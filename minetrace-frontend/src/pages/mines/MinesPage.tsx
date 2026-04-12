import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import toast from 'react-hot-toast';
import { Mountain, Loader2, Package, FileSpreadsheet, Trash2, Pencil, FileText } from 'lucide-react';
import PageHeader from '../../components/ui/PageHeader';
import DataTable, { Column } from '../../components/ui/DataTable';
import Modal from '../../components/ui/Modal';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import { mineApi } from '../../api/mineApi';
import { organizationApi } from '../../api/organizationApi';
import { useAuthStore } from '../../store/authStore';
import { formatDate } from '../../utils/formatDate';
import { exportToCsv } from '../../utils/exportCsv';
import { exportTablePdf } from '../../utils/exportPdf';
import { ROUTES } from '../../constants/routes';
import { cn } from '../../lib/utils';

const mineSchema = z.object({
  name: z.string().min(2, 'Mine name is required'),
  location: z.string().min(2, 'Location is required'),
  province: z.string().optional(),
  district: z.string().optional(),
  licenseNumber: z.string().optional(),
  organizationId: z.string().min(1, 'Organization is required'),
});

type MineForm = z.infer<typeof mineSchema>;

export default function MinesPage() {
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const { user } = useAuthStore();
  const isAdmin = user?.role === 'ADMIN';

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingMine, setEditingMine] = useState<any>(null);
  const [confirmDialog, setConfirmDialog] = useState<{ isOpen: boolean; mineId: string; action: 'activate' | 'deactivate' | null }>({
    isOpen: false,
    mineId: '',
    action: null,
  });
  const [deleteDialog, setDeleteDialog] = useState<{ isOpen: boolean; mineId: string; mineName: string }>({
    isOpen: false, mineId: '', mineName: ''
  });
  const [provinceFilter, setProvinceFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  const { data: minesData, isLoading: isLoadingMines } = useQuery({
    queryKey: ['mines'],
    queryFn: () => mineApi.getAll(),
  });

  const { data: orgsData } = useQuery({
    queryKey: ['organizations'],
    queryFn: () => organizationApi.getAll(),
    enabled: isAdmin, // Only fetch orgs if admin (needed for creation)
  });

  const { register, handleSubmit, reset, setValue, formState: { errors, isSubmitting } } = useForm<MineForm>({
    resolver: zodResolver(mineSchema),
  });

  const createMutation = useMutation({
    mutationFn: (data: MineForm) => {
      const org = orgsData?.data.find((o: any) => o.id === data.organizationId);
      return mineApi.create({ ...data, organizationName: org?.name });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['mines'] });
      toast.success('Mine registered successfully');
      setIsModalOpen(false);
      reset();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to register mine');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: MineForm }) => {
      const org = orgsData?.data.find((o: any) => o.id === data.organizationId);
      return mineApi.update(id, { ...data, organizationName: org?.name });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['mines'] });
      toast.success('Mine updated successfully');
      setEditingMine(null);
      reset();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to update mine');
    },
  });

  const toggleStatusMutation = useMutation({
    mutationFn: (id: string) => mineApi.toggleActive(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['mines'] });
      toast.success(`Mine status updated successfully`);
      setConfirmDialog({ isOpen: false, mineId: '', action: null });
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to update mine status');
      setConfirmDialog({ isOpen: false, mineId: '', action: null });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => mineApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['mines'] });
      toast.success('Mine deleted successfully');
      setDeleteDialog({ isOpen: false, mineId: '', mineName: '' });
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to delete mine');
      setDeleteDialog({ isOpen: false, mineId: '', mineName: '' });
    },
  });

  const onSubmit = (data: MineForm) => {
    if (editingMine) {
      updateMutation.mutate({ id: editingMine.id, data });
    } else {
      createMutation.mutate(data);
    }
  };

  const handleEdit = (mine: any) => {
    setEditingMine(mine);
    setValue('name', mine.name);
    setValue('location', mine.location);
    setValue('province', mine.province || '');
    setValue('district', mine.district || '');
    setValue('licenseNumber', mine.licenseNumber || '');
    setValue('organizationId', mine.organizationId || '');
  };

  const handleToggleStatus = (id: string, currentActive: boolean) => {
    setConfirmDialog({
      isOpen: true,
      mineId: id,
      action: currentActive ? 'deactivate' : 'activate',
    });
  };

  const columns: Column<any>[] = [
    { key: 'name', label: 'Name', sortable: true },
    { key: 'location', label: 'Location', sortable: true },
    { key: 'province', label: 'Province', sortable: true },
    { key: 'district', label: 'District', sortable: true },
    { key: 'licenseNumber', label: 'License Number', sortable: true },
    { key: 'organizationName', label: 'Organization', sortable: true },
    { 
      key: 'active', 
      label: 'Status', 
      sortable: true,
      render: (row) => (
        <span className={cn(
          "inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium",
          row.active ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"
        )}>
          {row.active ? 'ACTIVE' : 'INACTIVE'}
        </span>
      )
    },
    { 
      key: 'createdAt', 
      label: 'Date Registered', 
      sortable: true,
      render: (row) => formatDate(row.createdAt)
    },
    {
      key: 'actions',
      label: 'Actions',
      render: (row) => (
        <div className="flex items-center gap-3">
          <button
            onClick={(e) => {
              e.stopPropagation();
              navigate(`${ROUTES.BATCHES}?mineId=${row.id}`);
            }}
            className="text-sm font-medium text-primary-600 hover:text-primary-900 transition-colors flex items-center"
            title="View Batches"
          >
            <Package className="h-4 w-4 mr-1" />
            Batches
          </button>

          {isAdmin && (
            <button
              onClick={(e) => {
                e.stopPropagation();
                handleEdit(row);
              }}
              className="text-sm font-medium text-blue-600 hover:text-blue-900 transition-colors"
              title="Edit Mine"
            >
              <Pencil className="h-4 w-4" />
            </button>
          )}
          {isAdmin && (
            <button
              onClick={(e) => {
                e.stopPropagation();
                handleToggleStatus(row.id, row.active);
              }}
              className={cn(
                "text-sm font-medium transition-colors",
                row.active ? "text-red-600 hover:text-red-900" : "text-green-600 hover:text-green-900"
              )}
            >
              {row.active ? 'Deactivate' : 'Activate'}
            </button>
          )}
          {isAdmin && (
            <button
              onClick={(e) => {
                e.stopPropagation();
                setDeleteDialog({ isOpen: true, mineId: row.id, mineName: row.name });
              }}
              className="text-sm font-medium text-red-600 hover:text-red-900 transition-colors"
              title="Delete Mine"
            >
              <Trash2 className="h-4 w-4" />
            </button>
          )}
        </div>
      )
    }
  ];

  let filteredMines = minesData?.data || [];
  if (provinceFilter) {
    filteredMines = filteredMines.filter((m: any) => m.province === provinceFilter);
  }
  if (statusFilter) {
    const isActive = statusFilter === 'ACTIVE';
    filteredMines = filteredMines.filter((m: any) => m.active === isActive);
  }

  // Unique provinces for filter
  const provinces = Array.from(new Set((minesData?.data || []).map((m: any) => m.province).filter(Boolean)));

  return (
    <div className="space-y-6">
      <PageHeader 
        title="Mines" 
        subtitle="Manage registered extraction sites and concessions."
        action={
          <div className="flex gap-3">
            <button
              onClick={() => {
                const exportData = filteredMines.map((m: any) => ({
                  'Name': m.name,
                  'Location': m.location,
                  'Province': m.province || 'N/A',
                  'District': m.district || 'N/A',
                  'License Number': m.licenseNumber || 'N/A',
                  'Organization': m.organizationName,
                  'Status': m.active ? 'ACTIVE' : 'INACTIVE',
                  'Date Registered': formatDate(m.createdAt),
                }));
                exportToCsv(exportData, 'minetrace-mines');
              }}
              className="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
            >
              <FileSpreadsheet className="h-4 w-4 mr-2" />
              Export CSV
            </button>
            <button
              onClick={() => {
                const exportData = filteredMines.map((m: any) => ({
                  'Name': m.name,
                  'Location': m.location,
                  'Province': m.province || 'N/A',
                  'District': m.district || 'N/A',
                  'License Number': m.licenseNumber || 'N/A',
                  'Organization': m.organizationName,
                  'Status': m.active ? 'ACTIVE' : 'INACTIVE',
                  'Date Registered': formatDate(m.createdAt),
                }));
                exportTablePdf('Mines Register', 'All registered extraction sites and concessions', exportData, 'minetrace-mines');
              }}
              className="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
            >
              <FileText className="h-4 w-4 mr-2" />
              Export PDF
            </button>
            {isAdmin && (
              <button
                onClick={() => {
                  setEditingMine(null);
                  reset();
                  setIsModalOpen(true);
                }}
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
              >
                <Mountain className="h-4 w-4 mr-2" />
                Register Mine
              </button>
            )}
          </div>
        }
      />

      <DataTable 
        columns={columns} 
        data={filteredMines} 
        loading={isLoadingMines}
        searchPlaceholder="Search mines by name or location..."
        filters={
          <>
            <select
              value={provinceFilter}
              onChange={(e) => setProvinceFilter(e.target.value)}
              className="block w-full sm:w-auto pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm rounded-md"
            >
              <option value="">All Provinces</option>
              {provinces.map((p: any) => (
                <option key={p} value={p}>{p}</option>
              ))}
            </select>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="block w-full sm:w-auto pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm rounded-md"
            >
              <option value="">All Statuses</option>
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
            </select>
          </>
        }
      />

      {/* Register / Edit Mine Modal */}
      {isAdmin && (
        <Modal
          isOpen={isModalOpen || !!editingMine}
          onClose={() => { setIsModalOpen(false); setEditingMine(null); reset(); }}
          title={editingMine ? 'Edit Mine' : 'Register New Mine'}
          size="md"
          footer={
            <>
              <button
                type="button"
                onClick={() => { setIsModalOpen(false); setEditingMine(null); reset(); }}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={handleSubmit(onSubmit)}
                disabled={isSubmitting || createMutation.isPending || updateMutation.isPending}
                className="inline-flex items-center px-4 py-2 text-sm font-medium text-white border border-transparent rounded-lg bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50"
              >
                {(isSubmitting || createMutation.isPending || updateMutation.isPending) && <Loader2 className="w-4 h-4 mr-2 animate-spin" />}
                {editingMine ? 'Save Changes' : 'Register'}
              </button>
            </>
          }
        >
          <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
            <div>
              <label className="block text-sm font-medium text-gray-700">Mine Name *</label>
              <input
                type="text"
                {...register('name')}
                className={`mt-1 block w-full px-3 py-2 border ${errors.name ? 'border-red-300' : 'border-gray-300'} rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm`}
              />
              {errors.name && <p className="mt-1 text-sm text-red-600">{errors.name.message}</p>}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">Location *</label>
              <input
                type="text"
                {...register('location')}
                className={`mt-1 block w-full px-3 py-2 border ${errors.location ? 'border-red-300' : 'border-gray-300'} rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm`}
              />
              {errors.location && <p className="mt-1 text-sm text-red-600">{errors.location.message}</p>}
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Province</label>
                <input
                  type="text"
                  {...register('province')}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">District</label>
                <input
                  type="text"
                  {...register('district')}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">License Number</label>
                <input
                  type="text"
                  {...register('licenseNumber')}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Organization *</label>
                <select
                  {...register('organizationId')}
                  className={`mt-1 block w-full pl-3 pr-10 py-2 text-base border ${errors.organizationId ? 'border-red-300' : 'border-gray-300'} focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm rounded-md`}
                >
                  <option value="">Select organization</option>
                  {orgsData?.data.map((org: any) => (
                    <option key={org.id} value={org.id}>{org.name}</option>
                  ))}
                </select>
                {errors.organizationId && <p className="mt-1 text-sm text-red-600">{errors.organizationId.message}</p>}
              </div>
            </div>
          </form>
        </Modal>
      )}

      <ConfirmDialog
        isOpen={deleteDialog.isOpen}
        onCancel={() => setDeleteDialog({ isOpen: false, mineId: '', mineName: '' })}
        onConfirm={() => deleteMutation.mutate(deleteDialog.mineId)}
        title="Delete Mine"
        message={`Are you sure you want to permanently delete "${deleteDialog.mineName}"? This cannot be undone.`}
        confirmLabel="Delete"
        danger
        loading={deleteMutation.isPending}
      />

      {/* Confirm Status Change Dialog */}
      <ConfirmDialog
        isOpen={confirmDialog.isOpen}
        onCancel={() => setConfirmDialog({ isOpen: false, mineId: '', action: null })}
        onConfirm={() => toggleStatusMutation.mutate(confirmDialog.mineId)}
        title={confirmDialog.action === 'activate' ? 'Activate Mine' : 'Deactivate Mine'}
        message={`Are you sure you want to ${confirmDialog.action} this mine?`}
        confirmLabel={confirmDialog.action === 'activate' ? 'Activate' : 'Deactivate'}
        danger={confirmDialog.action === 'deactivate'}
        loading={toggleStatusMutation.isPending}
      />
    </div>
  );
}
