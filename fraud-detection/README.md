# MineTrace Fraud Detection Microservice

Isolation Forest anomaly detection service for MineTrace 2.0.

## Setup

```bash
cd fraud-detection
python -m venv venv
venv\Scripts\activate        # Windows
pip install -r requirements.txt
```

## Run

```bash
python main.py
# Service starts on http://localhost:8000
```

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /health | Check if service is running and model is trained |
| POST | /train | Train the Isolation Forest on all current batch data |
| POST | /analyze | Score a single batch (called automatically per-batch) |

## Workflow

1. Start this service before the Spring Boot backend.
2. In the MineTrace UI → Fraud & Risk → click **Train AI Model** (do this once after seeding data).
3. Click **Analyze All Batches** to score every batch using the trained model.
4. Re-train periodically as new batch data accumulates.

## Fallback

If this service is unavailable, Spring Boot falls back to rule-based scoring automatically. No data is lost.
