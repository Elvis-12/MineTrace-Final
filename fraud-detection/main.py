from fastapi import FastAPI
from pydantic import BaseModel
from sklearn.ensemble import IsolationForest
import numpy as np
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="MineTrace Fraud Detection Service")

# Isolation Forest model — trained lazily on first analyze-all call or with /train
model: IsolationForest | None = None

# Feature order must be consistent across /train and /analyze
FEATURES = [
    "initial_weight",
    "movement_count",
    "dispatch_count",
    "verification_count",
    "days_since_extraction",
    "has_license",
]


class BatchFeatures(BaseModel):
    batch_id: str
    initial_weight: float
    movement_count: int
    dispatch_count: int
    verification_count: int
    days_since_extraction: float
    has_license: bool


class TrainRequest(BaseModel):
    batches: list[BatchFeatures]


class AnalyzeResponse(BaseModel):
    batch_id: str
    anomaly_score: float          # 0.0 – 1.0  (higher = more anomalous)
    is_anomaly: bool
    risk_level: str               # LOW | MEDIUM | HIGH


def _to_vector(b: BatchFeatures) -> list[float]:
    return [
        b.initial_weight,
        float(b.movement_count),
        float(b.dispatch_count),
        float(b.verification_count),
        b.days_since_extraction,
        1.0 if b.has_license else 0.0,
    ]


def _score_to_risk(score: float) -> str:
    if score < 0.35:
        return "LOW"
    elif score < 0.65:
        return "MEDIUM"
    else:
        return "HIGH"


@app.get("/health")
def health():
    return {"status": "ok", "model_trained": model is not None}


@app.post("/train")
def train(request: TrainRequest):
    global model
    if len(request.batches) < 5:
        return {"trained": False, "reason": "Need at least 5 batches to train"}

    X = np.array([_to_vector(b) for b in request.batches])

    model = IsolationForest(
        n_estimators=100,
        contamination=0.1,   # assume ~10% of historical batches are anomalous
        random_state=42,
    )
    model.fit(X)
    logger.info("Isolation Forest trained on %d batches", len(request.batches))
    return {"trained": True, "n_samples": len(request.batches)}


@app.post("/analyze", response_model=AnalyzeResponse)
def analyze(batch: BatchFeatures):
    global model

    x = np.array([_to_vector(batch)])

    if model is None:
        # No model yet — use a minimal default trained on the single sample
        # (gives a neutral score; Spring Boot will apply rule-based fallback anyway)
        tmp = IsolationForest(n_estimators=10, contamination=0.1, random_state=42)
        tmp.fit(x)
        raw = tmp.decision_function(x)[0]
    else:
        raw = model.decision_function(x)[0]

    # decision_function returns values roughly in [-0.5, 0.5]
    # Map to [0, 1] where 1 = most anomalous
    score = float(np.clip(0.5 - raw, 0.0, 1.0))
    is_anomaly = score >= 0.5

    return AnalyzeResponse(
        batch_id=batch.batch_id,
        anomaly_score=round(score, 4),
        is_anomaly=is_anomaly,
        risk_level=_score_to_risk(score),
    )


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
