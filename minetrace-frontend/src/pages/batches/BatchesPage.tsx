import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import toast from "react-hot-toast";
import {
  Plus,
  Loader2,
  Download,
  FileSpreadsheet,
  Trash2,
  Pencil,
  QrCode,
  FileText,
  MapPin,
  List,
} from "lucide-react";
import LocationSelect from "../../components/ui/LocationSelect";
import PageHeader from "../../components/ui/PageHeader";
import DataTable, { Column } from "../../components/ui/DataTable";
import Modal from "../../components/ui/Modal";
import ConfirmDialog from "../../components/ui/ConfirmDialog";
import StatusBadge from "../../components/ui/StatusBadge";
import RiskBadge from "../../components/ui/RiskBadge";
import { batchApi } from "../../api/batchApi";
import { mineApi } from "../../api/mineApi";
import { useAuthStore } from "../../store/authStore";
import { formatDate } from "../../utils/formatDate";
import { exportToCsv } from "../../utils/exportCsv";
import { exportTablePdf } from "../../utils/exportPdf";
import { ROUTES } from "../../constants/routes";
import { STATUS_COLORS, RISK_COLORS } from "../../constants/statusColors";

const MINERAL_TYPES = [
  "Coltan",
  "Cassiterite",
  "Wolframite",
  "Gold",
  "Tin",
  "Lithium",
  "Other",
];

const batchSchema = z.object({
  mineralType: z.enum([
    "Coltan",
    "Cassiterite",
    "Wolframite",
    "Gold",
    "Tin",
    "Lithium",
    "Other",
  ]),
  initialWeight: z.number().min(0.001, "Weight must be greater than 0"),
  extractionDate: z.string().min(1, "Extraction date is required"),
  mineId: z.string().min(1, "Mine is required"),
  destination: z.string().min(1, "Destination is required"),
  notes: z.string().optional(),
});

type BatchForm = z.infer<typeof batchSchema>;

export default function BatchesPage() {
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { user } = useAuthStore();

  const canRegister = user?.role === "ADMIN" || user?.role === "MINE_OFFICER";

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingBatch, setEditingBatch] = useState<any>(null);
  const [qrModalData, setQrModalData] = useState<{
    isOpen: boolean;
    batchCode: string;
    qrCodeData?: string;
  }>({ isOpen: false, batchCode: "" });
  const [deleteDialog, setDeleteDialog] = useState<{
    isOpen: boolean;
    batchId: string;
    batchCode: string;
  }>({
    isOpen: false,
    batchId: "",
    batchCode: "",
  });
  const [viewMode, setViewMode] = useState<"list" | "location">("list");

  // Filters
  const [statusFilter, setStatusFilter] = useState("");
  const [mineralFilter, setMineralFilter] = useState("");
  const [mineFilter, setMineFilter] = useState(
    searchParams.get("mineId") || "",
  );
  const [riskFilter, setRiskFilter] = useState("");
  const [dateFrom, setDateFrom] = useState("");
  const [dateTo, setDateTo] = useState("");

  const { data: batchesData, isLoading: isLoadingBatches } = useQuery({
    queryKey: ["batches"],
    queryFn: () => batchApi.getAll(),
  });

  const { data: minesData } = useQuery({
    queryKey: ["mines"],
    queryFn: () => mineApi.getAll(),
    enabled: canRegister || true, // Need for filters too
  });

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<BatchForm>({
    resolver: zodResolver(batchSchema),
    defaultValues: {
      extractionDate: new Date().toISOString().split("T")[0],
    },
  });

  const createMutation = useMutation({
    mutationFn: (data: BatchForm) => {
      const mine = minesData?.data.find((m: any) => m.id === data.mineId);
      return batchApi.create({
        ...data,
        mineName: mine?.name,
        createdBy: user?.fullName,
      });
    },
    onSuccess: (res) => {
      queryClient.invalidateQueries({ queryKey: ["batches"] });
      toast.success("Batch registered successfully");
      setIsModalOpen(false);
      reset();
      setQrModalData({
        isOpen: true,
        batchCode: res.data.batchCode,
        qrCodeData: res.data.qrCodeData,
      });
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Failed to register batch");
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: BatchForm }) =>
      batchApi.update(id, {
        mineralType: data.mineralType,
        initialWeight: data.initialWeight,
        extractionDate: data.extractionDate,
        mineId: data.mineId,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["batches"] });
      toast.success("Batch updated successfully");
      setEditingBatch(null);
      reset({ extractionDate: new Date().toISOString().split("T")[0] });
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Failed to update batch");
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => batchApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["batches"] });
      toast.success("Batch deleted successfully");
      setDeleteDialog({ isOpen: false, batchId: "", batchCode: "" });
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Failed to delete batch");
      setDeleteDialog({ isOpen: false, batchId: "", batchCode: "" });
    },
  });

  const onSubmit = (data: BatchForm) => {
    if (editingBatch) {
      updateMutation.mutate({ id: editingBatch.id, data });
    } else {
      createMutation.mutate(data);
    }
  };

  const handleEdit = (batch: any) => {
    setEditingBatch(batch);
    setValue("mineralType", batch.mineralType);
    setValue("initialWeight", batch.initialWeight);
    setValue(
      "extractionDate",
      batch.extractionDate?.split("T")[0] ||
        new Date().toISOString().split("T")[0],
    );
    setValue("mineId", batch.mineId || "");
    setIsModalOpen(true);
  };

  const downloadQR = () => {
    const link = document.createElement("a");
    link.download = `${qrModalData.batchCode}-QR.png`;
    link.href = `${import.meta.env.VITE_API_URL || "http://localhost:8082"}/api/batches/${qrModalData.batchCode}/qr`;
    link.click();
  };

  const columns: Column<any>[] = [
    { key: '_index', label: '#', render: (_row: any, index: number) => <span className="text-gray-400">{index + 1}</span> },
    {
      key: "batchCode",
      label: "Batch Code",
      sortable: true,
      render: (row) => (
        <span
          className="font-mono font-medium text-primary-600 hover:text-primary-800 cursor-pointer hover:underline"
          onClick={(e) => {
            e.stopPropagation();
            navigate(ROUTES.BATCH_DETAIL(row.id));
          }}
        >
          {row.batchCode}
        </span>
      ),
    },
    { key: "mineralType", label: "Mineral Type", sortable: true },
    {
      key: "initialWeight",
      label: "Initial Weight (kg)",
      sortable: true,
      render: (row) =>
        row.initialWeight.toLocaleString(undefined, {
          minimumFractionDigits: 2,
          maximumFractionDigits: 3,
        }),
    },
    {
      key: "status",
      label: "Status",
      sortable: true,
      render: (row) => <StatusBadge status={row.status} />,
    },
    {
      key: "riskLevel",
      label: "Risk Level",
      sortable: true,
      render: (row) => <RiskBadge level={row.riskLevel} />,
    },
    { key: "mineName", label: "Mine (Source)", sortable: true },
    { key: "destination", label: "Destination", sortable: true },
    {
      key: "extractionDate",
      label: "Extraction Date",
      sortable: true,
      render: (row) => formatDate(row.extractionDate).split(" ")[0], // Just date
    },
    { key: "createdBy", label: "Created By", sortable: true },
    {
      key: "createdAt",
      label: "Date Registered",
      sortable: true,
      render: (row) => formatDate(row.createdAt),
    },
    {
      key: "actions",
      label: "Actions",
      render: (row) => (
        <div className="flex items-center gap-3">
          <button
            type="button"
            onClick={(e) => {
              e.stopPropagation();
              navigate(ROUTES.BATCH_DETAIL(row.id));
            }}
            className="text-sm font-medium text-primary-600 hover:text-primary-900 transition-colors"
          >
            View Details
          </button>
          <button
            type="button"
            onClick={(e) => {
              e.stopPropagation();
              setQrModalData({ isOpen: true, batchCode: row.batchCode });
            }}
            className="text-sm font-medium text-gray-600 hover:text-gray-900 transition-colors"
            title="Show QR Code"
          >
            <QrCode className="h-4 w-4" />
          </button>
          {canRegister && (
            <button
              type="button"
              onClick={(e) => {
                e.stopPropagation();
                handleEdit(row);
              }}
              className="text-sm font-medium text-blue-600 hover:text-blue-900 transition-colors"
              title="Edit Batch"
            >
              <Pencil className="h-4 w-4" />
            </button>
          )}
          {user?.role === "ADMIN" && (
            <button
              type="button"
              onClick={(e) => {
                e.stopPropagation();
                setDeleteDialog({
                  isOpen: true,
                  batchId: row.id,
                  batchCode: row.batchCode,
                });
              }}
              className="text-sm font-medium text-red-600 hover:text-red-900 transition-colors"
              title="Delete Batch"
            >
              <Trash2 className="h-4 w-4" />
            </button>
          )}
        </div>
      ),
    },
  ];

  let filteredBatches = batchesData?.data || [];

  if (statusFilter) {
    filteredBatches = filteredBatches.filter(
      (b: any) => b.status === statusFilter,
    );
  }
  if (mineralFilter) {
    filteredBatches = filteredBatches.filter(
      (b: any) => b.mineralType === mineralFilter,
    );
  }
  if (mineFilter) {
    filteredBatches = filteredBatches.filter(
      (b: any) => b.mineId === mineFilter,
    );
  }
  if (riskFilter) {
    filteredBatches = filteredBatches.filter(
      (b: any) => b.riskLevel === riskFilter,
    );
  }
  if (dateFrom) {
    filteredBatches = filteredBatches.filter(
      (b: any) => new Date(b.createdAt) >= new Date(dateFrom),
    );
  }
  if (dateTo) {
    const to = new Date(dateTo);
    to.setHours(23, 59, 59, 999);
    filteredBatches = filteredBatches.filter(
      (b: any) => new Date(b.createdAt) <= to,
    );
  }

  // Filter mines for dropdown based on role
  let availableMines = minesData?.data || [];
  if (user?.role === "MINE_OFFICER") {
    availableMines = availableMines.filter(
      (m: any) => m.organizationName === user.organizationName,
    );
  }

  // Location grouping: group batches by district
  const locationGroups = (() => {
    const allBatches: any[] = batchesData?.data || [];
    const mines: any[] = minesData?.data || [];
    const mineMap = new Map(mines.map((m: any) => [m.id, m]));
    const groups: Record<
      string,
      { district: string; province: string; batches: any[]; inStock: number }
    > = {};

    for (const batch of allBatches) {
      const mine = mineMap.get(batch.mineId);
      const district = mine?.district || "Unknown";
      const province = mine?.province || "Unknown";
      if (!groups[district]) {
        groups[district] = { district, province, batches: [], inStock: 0 };
      }
      groups[district].batches.push(batch);
      if (batch.status !== "SOLD" && batch.status !== "IN_TRANSIT") {
        groups[district].inStock += batch.initialWeight;
      }
    }
    return Object.values(groups).sort((a, b) =>
      a.district.localeCompare(b.district),
    );
  })();

  return (
    <div className="space-y-6">
      <PageHeader
        title="Mineral Batches"
        subtitle="Track and manage registered mineral batches."
        action={
          <div className="flex gap-3">
            <button
              onClick={() => {
                const exportData = filteredBatches.map((b: any) => ({
                  "Batch Code": b.batchCode,
                  "Mineral Type": b.mineralType,
                  "Initial Weight (kg)": b.initialWeight,
                  Status: b.status,
                  "Risk Level": b.riskLevel,
                  "Mine Name": b.mineName,
                  "Extraction Date": formatDate(b.extractionDate).split(" ")[0],
                  "Created By": b.createdBy,
                  "Date Registered": formatDate(b.createdAt),
                }));
                exportToCsv(exportData, "minetrace-batches");
              }}
              className="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
            >
              <FileSpreadsheet className="h-4 w-4 mr-2" />
              Export CSV
            </button>
            <button
              onClick={() => {
                const exportData = filteredBatches.map((b: any) => ({
                  "Batch Code": b.batchCode,
                  "Mineral Type": b.mineralType,
                  "Initial Weight (kg)": b.initialWeight,
                  Status: b.status,
                  "Risk Level": b.riskLevel,
                  Mine: b.mineName,
                  "Extraction Date": formatDate(b.extractionDate).split(" ")[0],
                  "Created By": b.createdBy,
                  "Date Registered": formatDate(b.createdAt),
                }));
                exportTablePdf(
                  "Mineral Batches Register",
                  "All registered mineral batches and their current status",
                  exportData,
                  "minetrace-batches",
                );
              }}
              className="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
            >
              <FileText className="h-4 w-4 mr-2" />
              Export PDF
            </button>
            {canRegister && (
              <button
                onClick={() => {
                  setEditingBatch(null);
                  reset({
                    extractionDate: new Date().toISOString().split("T")[0],
                  });
                  setIsModalOpen(true);
                }}
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
              >
                <Plus className="h-4 w-4 mr-2" />
                Register Batch
              </button>
            )}
          </div>
        }
      />

      <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-200 mb-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
          <div>
            <label className="block text-xs font-medium text-gray-700 mb-1">
              Status
            </label>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="block w-full border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 sm:text-sm py-1.5 px-3"
            >
              <option value="">All</option>
              {Object.keys(STATUS_COLORS).map((s) => (
                <option key={s} value={s}>
                  {s.replace("_", " ")}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-700 mb-1">
              Mineral
            </label>
            <select
              value={mineralFilter}
              onChange={(e) => setMineralFilter(e.target.value)}
              className="block w-full border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 sm:text-sm py-1.5 px-3"
            >
              <option value="">All</option>
              {MINERAL_TYPES.map((m) => (
                <option key={m} value={m}>
                  {m}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-700 mb-1">
              Mine
            </label>
            <select
              value={mineFilter}
              onChange={(e) => setMineFilter(e.target.value)}
              className="block w-full border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 sm:text-sm py-1.5 px-3"
            >
              <option value="">All</option>
              {(minesData?.data || []).map((m: any) => (
                <option key={m.id} value={m.id}>
                  {m.name}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-700 mb-1">
              Risk Level
            </label>
            <select
              value={riskFilter}
              onChange={(e) => setRiskFilter(e.target.value)}
              className="block w-full border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 sm:text-sm py-1.5 px-3"
            >
              <option value="">All</option>
              {Object.keys(RISK_COLORS).map((r) => (
                <option key={r} value={r}>
                  {r}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-700 mb-1">
              From Date
            </label>
            <input
              type="date"
              value={dateFrom}
              onChange={(e) => setDateFrom(e.target.value)}
              className="block w-full border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 sm:text-sm py-1.5 px-3"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-700 mb-1">
              To Date
            </label>
            <input
              type="date"
              value={dateTo}
              onChange={(e) => setDateTo(e.target.value)}
              className="block w-full border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 sm:text-sm py-1.5 px-3"
            />
          </div>
        </div>
      </div>

      {/* View toggle */}
      <div className="flex items-center gap-2">
        <button
          type="button"
          onClick={() => setViewMode("list")}
          className={`inline-flex items-center px-3 py-1.5 text-sm font-medium rounded-md border transition-colors ${viewMode === "list" ? "bg-primary-600 text-white border-primary-600" : "bg-white text-gray-700 border-gray-300 hover:bg-gray-50"}`}
        >
          <List className="h-4 w-4 mr-1.5" />
          List View
        </button>
        <button
          type="button"
          onClick={() => setViewMode("location")}
          className={`inline-flex items-center px-3 py-1.5 text-sm font-medium rounded-md border transition-colors ${viewMode === "location" ? "bg-primary-600 text-white border-primary-600" : "bg-white text-gray-700 border-gray-300 hover:bg-gray-50"}`}
        >
          <MapPin className="h-4 w-4 mr-1.5" />
          By Location
        </button>
      </div>

      {viewMode === "list" ? (
        <DataTable
          columns={columns}
          data={filteredBatches}
          loading={isLoadingBatches}
          onRowClick={(row) => navigate(ROUTES.BATCH_DETAIL(row.id))}
          searchPlaceholder="Search by batch code..."
        />
      ) : (
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  District
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Province
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Total Batches
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  In Stock (kg)
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {locationGroups.map((group) => (
                <tr
                  key={group.district}
                  className="hover:bg-gray-50 transition-colors"
                >
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-semibold text-gray-900 flex items-center gap-2">
                    <MapPin className="h-4 w-4 text-primary-500" />
                    {group.district}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {group.province}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {group.batches.length}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-green-700">
                    {group.inStock.toLocaleString(undefined, {
                      minimumFractionDigits: 2,
                      maximumFractionDigits: 2,
                    })}{" "}
                    kg
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    <button
                      type="button"
                      onClick={() => {
                        const mine = (minesData?.data || []).find(
                          (m: any) => m.district === group.district,
                        );
                        if (mine) setMineFilter(mine.id);
                        setViewMode("list");
                      }}
                      className="text-primary-600 hover:text-primary-900 font-medium"
                    >
                      View Batches
                    </button>
                  </td>
                </tr>
              ))}
              {locationGroups.length === 0 && (
                <tr>
                  <td
                    colSpan={5}
                    className="px-6 py-12 text-center text-gray-500"
                  >
                    No batches found
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      <ConfirmDialog
        isOpen={deleteDialog.isOpen}
        onCancel={() =>
          setDeleteDialog({ isOpen: false, batchId: "", batchCode: "" })
        }
        onConfirm={() => deleteMutation.mutate(deleteDialog.batchId)}
        title="Delete Batch"
        message={`Are you sure you want to permanently delete batch "${deleteDialog.batchCode}"? All associated records will be lost.`}
        confirmLabel="Delete"
        danger
        loading={deleteMutation.isPending}
      />

      {/* Register Batch Modal */}
      {canRegister && (
        <Modal
          isOpen={isModalOpen}
          onClose={() => {
            setIsModalOpen(false);
            setEditingBatch(null);
          }}
          title={editingBatch ? "Edit Mineral Batch" : "Register Mineral Batch"}
          size="md"
          footer={
            <>
              <button
                type="button"
                onClick={() => {
                  setIsModalOpen(false);
                  setEditingBatch(null);
                }}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={handleSubmit(onSubmit)}
                disabled={
                  isSubmitting ||
                  createMutation.isPending ||
                  updateMutation.isPending
                }
                className="inline-flex items-center px-4 py-2 text-sm font-medium text-white border border-transparent rounded-lg bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50"
              >
                {(isSubmitting ||
                  createMutation.isPending ||
                  updateMutation.isPending) && (
                  <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                )}
                {editingBatch ? "Save Changes" : "Register Batch"}
              </button>
            </>
          }
        >
          <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Mineral Type *
                </label>
                <select
                  {...register("mineralType")}
                  className={`mt-1 block w-full pl-3 pr-10 py-2 text-base border ${errors.mineralType ? "border-red-300" : "border-gray-300"} focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm rounded-md`}
                >
                  <option value="">Select type</option>
                  {MINERAL_TYPES.map((m) => (
                    <option key={m} value={m}>
                      {m}
                    </option>
                  ))}
                </select>
                {errors.mineralType && (
                  <p className="mt-1 text-sm text-red-600">
                    {errors.mineralType.message}
                  </p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Initial Weight (kg) *
                </label>
                <input
                  type="number"
                  step="0.001"
                  min="0.001"
                  {...register("initialWeight", { valueAsNumber: true })}
                  className={`mt-1 block w-full px-3 py-2 border ${errors.initialWeight ? "border-red-300" : "border-gray-300"} rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm`}
                />
                {errors.initialWeight && (
                  <p className="mt-1 text-sm text-red-600">
                    {errors.initialWeight.message}
                  </p>
                )}
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Extraction Date *
                </label>
                <input
                  type="date"
                  {...register("extractionDate")}
                  className={`mt-1 block w-full px-3 py-2 border ${errors.extractionDate ? "border-red-300" : "border-gray-300"} rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm`}
                />
                {errors.extractionDate && (
                  <p className="mt-1 text-sm text-red-600">
                    {errors.extractionDate.message}
                  </p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Mine of Origin *
                </label>
                <select
                  {...register("mineId")}
                  className={`mt-1 block w-full pl-3 pr-10 py-2 text-base border ${errors.mineId ? "border-red-300" : "border-gray-300"} focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm rounded-md`}
                >
                  <option value="">Select mine</option>
                  {availableMines.map((m: any) => (
                    <option key={m.id} value={m.id}>
                      {m.name}
                    </option>
                  ))}
                </select>
                {errors.mineId && (
                  <p className="mt-1 text-sm text-red-600">
                    {errors.mineId.message}
                  </p>
                )}
              </div>
            </div>

            {/* Source auto-filled from selected mine */}
            {(() => {
              const selectedMine = availableMines.find(
                (m: any) => m.id === watch("mineId"),
              );
              return selectedMine?.district ? (
                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    Source (District)
                  </label>
                  <div className="mt-1 px-3 py-2 bg-gray-50 border border-gray-200 rounded-md text-sm text-gray-700 font-medium">
                    {selectedMine.district}
                    {selectedMine.province ? ` — ${selectedMine.province}` : ""}
                  </div>
                  <p className="mt-1 text-xs text-gray-400">
                    Auto-filled from the selected mine's location
                  </p>
                </div>
              ) : null;
            })()}

            <LocationSelect
              label="Destination"
              required
              value={watch("destination") || ""}
              onChange={(val) => setValue("destination", val)}
              error={errors.destination?.message}
            />

            <div>
              <label className="block text-sm font-medium text-gray-700">
                Notes (Optional)
              </label>
              <textarea
                {...register("notes")}
                rows={3}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
              />
            </div>
          </form>
        </Modal>
      )}

      {/* QR Code Display Modal */}
      <Modal
        isOpen={qrModalData.isOpen}
        onClose={() => setQrModalData({ isOpen: false, batchCode: "" })}
        title="Batch Registered Successfully"
        size="sm"
        footer={
          <button
            type="button"
            onClick={() => setQrModalData({ isOpen: false, batchCode: "" })}
            className="w-full inline-flex justify-center items-center px-4 py-2 text-sm font-medium text-white border border-transparent rounded-lg bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
          >
            Close
          </button>
        }
      >
        <div className="flex flex-col items-center justify-center py-6">
          <p className="text-sm text-gray-500 mb-4 text-center">
            Scan this QR code to quickly access batch details during transit and
            verification.
          </p>
          <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100 mb-4">
            {qrModalData.qrCodeData ? (
              <img
                src={qrModalData.qrCodeData}
                alt={`QR for ${qrModalData.batchCode}`}
                width={200}
                height={200}
              />
            ) : (
              <div className="w-[200px] h-[200px] flex items-center justify-center text-gray-400 text-sm">
                Loading QR...
              </div>
            )}
          </div>
          <p className="font-mono text-lg font-bold text-gray-900 mb-6">
            {qrModalData.batchCode}
          </p>

          <button
            onClick={downloadQR}
            className="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
          >
            <Download className="h-4 w-4 mr-2" />
            Download QR Code (PNG)
          </button>
        </div>
      </Modal>
    </div>
  );
}
