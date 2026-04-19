import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShieldCheck, ShieldX, Eye, AlertTriangle, CheckCircle, Clock } from 'lucide-react';
import { batchApi } from '../../api/batchApi';
import { ROUTES } from '../../constants/routes';
import PageHeader from '../../components/ui/PageHeader';
import toast from 'react-hot-toast';

interface Batch {
  id: string;
  batchCode: string;
  mineralType: string;
  initialWeight: number;
  status: string;
  riskLevel: string;
  mineName: string;
  anomalyScore: number;
  inspectorApproved: boolean | null;
  inspectorNote: string | null;
  inspectedBy: string | null;
}

export default function InspectorDashboardPage() {
  const navigate = useNavigate();
  const [batches, setBatches] = useState<Batch[]>([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState<string | null>(null);
  const [noteModal, setNoteModal] = useState<{ id: string; approved: boolean } | null>(null);
  const [note, setNote] = useState('');

  useEffect(() => {
    fetchPending();
  }, []);

  const fetchPending = async () => {
    try {
      setLoading(true);
      const res = await batchApi.getPendingInspection();
      setBatches(res.data);
    } catch {
      toast.error('Failed to load batches');
    } finally {
      setLoading(false);
    }
  };

  const handleInspect = async () => {
    if (!noteModal) return;
    setActionLoading(noteModal.id);
    try {
      await batchApi.inspect(noteModal.id, noteModal.approved, note);
      toast.success(noteModal.approved ? 'Batch approved' : 'Batch flagged for compliance');
      setNoteModal(null);
      setNote('');
      fetchPending();
    } catch {
      toast.error('Action failed');
    } finally {
      setActionLoading(null);
    }
  };

  const riskColor = (level: string) => {
    if (level === 'HIGH') return 'bg-red-100 text-red-700';
    if (level === 'MEDIUM') return 'bg-yellow-100 text-yellow-700';
    return 'bg-green-100 text-green-700';
  };

  const stats = {
    pending: batches.length,
    highRisk: batches.filter(b => b.riskLevel === 'HIGH').length,
    flagged: batches.filter(b => b.status === 'FLAGGED').length,
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Inspector Dashboard"
        subtitle="Review high-risk and flagged batches requiring compliance verification"
      />

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-white rounded-xl border border-gray-200 p-5 flex items-center gap-4">
          <div className="p-3 rounded-lg bg-blue-50"><Clock className="h-6 w-6 text-blue-600" /></div>
          <div>
            <p className="text-sm text-gray-500">Pending Review</p>
            <p className="text-2xl font-bold text-gray-900">{stats.pending}</p>
          </div>
        </div>
        <div className="bg-white rounded-xl border border-gray-200 p-5 flex items-center gap-4">
          <div className="p-3 rounded-lg bg-red-50"><AlertTriangle className="h-6 w-6 text-red-600" /></div>
          <div>
            <p className="text-sm text-gray-500">High Risk</p>
            <p className="text-2xl font-bold text-gray-900">{stats.highRisk}</p>
          </div>
        </div>
        <div className="bg-white rounded-xl border border-gray-200 p-5 flex items-center gap-4">
          <div className="p-3 rounded-lg bg-orange-50"><ShieldX className="h-6 w-6 text-orange-600" /></div>
          <div>
            <p className="text-sm text-gray-500">Flagged</p>
            <p className="text-2xl font-bold text-gray-900">{stats.flagged}</p>
          </div>
        </div>
      </div>

      {/* Batch list */}
      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-100">
          <h2 className="text-base font-semibold text-gray-900">Batches Pending Inspection</h2>
        </div>

        {loading ? (
          <div className="p-12 text-center text-gray-400">Loading...</div>
        ) : batches.length === 0 ? (
          <div className="p-12 text-center">
            <CheckCircle className="h-12 w-12 text-green-400 mx-auto mb-3" />
            <p className="text-gray-500">No batches pending inspection</p>
          </div>
        ) : (
          <div className="divide-y divide-gray-100">
            {batches.map(batch => (
              <div key={batch.id} className="px-6 py-4 flex flex-col sm:flex-row sm:items-center gap-4">
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 flex-wrap">
                    <span className="font-semibold text-gray-900">{batch.batchCode}</span>
                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${riskColor(batch.riskLevel)}`}>
                      {batch.riskLevel} RISK
                    </span>
                    <span className="text-xs px-2 py-0.5 rounded-full bg-gray-100 text-gray-600">
                      {batch.status}
                    </span>
                  </div>
                  <p className="text-sm text-gray-500 mt-1">
                    {batch.mineralType} · {batch.initialWeight} kg · {batch.mineName}
                  </p>
                  <p className="text-sm text-gray-500">
                    Anomaly Score: <span className="font-medium text-gray-700">{(batch.anomalyScore * 100).toFixed(0)}%</span>
                  </p>
                </div>

                <div className="flex items-center gap-2 shrink-0">
                  <button
                    onClick={() => navigate(`/batches/${batch.id}`)}
                    className="inline-flex items-center gap-1.5 px-3 py-1.5 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 text-gray-700"
                  >
                    <Eye className="h-4 w-4" /> View
                  </button>
                  <button
                    onClick={() => { setNoteModal({ id: batch.id, approved: true }); setNote(''); }}
                    disabled={actionLoading === batch.id}
                    className="inline-flex items-center gap-1.5 px-3 py-1.5 text-sm bg-green-600 hover:bg-green-700 text-white rounded-lg disabled:opacity-50"
                  >
                    <ShieldCheck className="h-4 w-4" /> Approve
                  </button>
                  <button
                    onClick={() => { setNoteModal({ id: batch.id, approved: false }); setNote(''); }}
                    disabled={actionLoading === batch.id}
                    className="inline-flex items-center gap-1.5 px-3 py-1.5 text-sm bg-red-600 hover:bg-red-700 text-white rounded-lg disabled:opacity-50"
                  >
                    <ShieldX className="h-4 w-4" /> Flag
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Note Modal */}
      {noteModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="bg-white rounded-xl shadow-xl w-full max-w-md p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-1">
              {noteModal.approved ? 'Approve Batch' : 'Flag Batch'}
            </h3>
            <p className="text-sm text-gray-500 mb-4">
              {noteModal.approved
                ? 'Add an optional compliance note before approving.'
                : 'Describe the compliance issue found.'}
            </p>
            <textarea
              rows={3}
              value={note}
              onChange={e => setNote(e.target.value)}
              placeholder={noteModal.approved ? 'Compliance note (optional)...' : 'Reason for flagging (required)...'}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
            <div className="flex gap-3 mt-4 justify-end">
              <button
                onClick={() => setNoteModal(null)}
                className="px-4 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                onClick={handleInspect}
                disabled={!noteModal.approved && !note.trim()}
                className={`px-4 py-2 text-sm text-white rounded-lg disabled:opacity-50 ${
                  noteModal.approved ? 'bg-green-600 hover:bg-green-700' : 'bg-red-600 hover:bg-red-700'
                }`}
              >
                {noteModal.approved ? 'Confirm Approval' : 'Confirm Flag'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
