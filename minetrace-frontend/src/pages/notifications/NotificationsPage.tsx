import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Bell, CheckCheck, AlertTriangle, Truck, ShieldCheck, ShieldX } from 'lucide-react';
import { notificationApi } from '../../api/notificationApi';
import { formatDate } from '../../utils/formatDate';
import PageHeader from '../../components/ui/PageHeader';
import { cn } from '../../lib/utils';

const TYPE_CONFIG: Record<string, { icon: any; color: string; bg: string }> = {
  HIGH_RISK:      { icon: AlertTriangle, color: 'text-red-600',    bg: 'bg-red-50' },
  BATCH_FLAGGED:  { icon: ShieldX,       color: 'text-orange-600', bg: 'bg-orange-50' },
  BATCH_APPROVED: { icon: ShieldCheck,   color: 'text-green-600',  bg: 'bg-green-50' },
  MOVEMENT:       { icon: Truck,         color: 'text-blue-600',   bg: 'bg-blue-50' },
};

export default function NotificationsPage() {
  const queryClient = useQueryClient();

  const { data, isLoading } = useQuery({
    queryKey: ['notifications'],
    queryFn: () => notificationApi.getAll(),
  });

  const markReadMutation = useMutation({
    mutationFn: (id: number) => notificationApi.markRead(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] });
      queryClient.invalidateQueries({ queryKey: ['notificationCount'] });
    },
  });

  const markAllMutation = useMutation({
    mutationFn: () => notificationApi.markAllRead(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] });
      queryClient.invalidateQueries({ queryKey: ['notificationCount'] });
    },
  });

  const notifications = data?.data || [];
  const unread = notifications.filter((n: any) => !n.read).length;

  return (
    <div className="space-y-6">
      <PageHeader
        title="Notifications"
        subtitle="System alerts and activity updates."
        action={
          unread > 0 ? (
            <button
              onClick={() => markAllMutation.mutate()}
              disabled={markAllMutation.isPending}
              className="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
            >
              <CheckCheck className="h-4 w-4 mr-2" />
              Mark all as read
            </button>
          ) : undefined
        }
      />

      <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
        {isLoading ? (
          <div className="p-8 text-center text-gray-400">Loading...</div>
        ) : notifications.length === 0 ? (
          <div className="p-12 text-center">
            <Bell className="h-12 w-12 text-gray-300 mx-auto mb-3" />
            <p className="text-gray-500">No notifications yet.</p>
          </div>
        ) : (
          <ul className="divide-y divide-gray-100">
            {notifications.map((n: any) => {
              const cfg = TYPE_CONFIG[n.type] || { icon: Bell, color: 'text-gray-500', bg: 'bg-gray-50' };
              const Icon = cfg.icon;
              return (
                <li
                  key={n.id}
                  className={cn('flex items-start gap-4 px-6 py-4 transition-colors', !n.read && 'bg-blue-50/40')}
                >
                  <div className={cn('mt-0.5 flex h-9 w-9 shrink-0 items-center justify-center rounded-full', cfg.bg)}>
                    <Icon className={cn('h-5 w-5', cfg.color)} />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center justify-between gap-2">
                      <p className={cn('text-sm font-semibold text-gray-900', !n.read && 'font-bold')}>
                        {n.title}
                      </p>
                      {!n.read && (
                        <span className="inline-block h-2 w-2 rounded-full bg-blue-500 shrink-0" />
                      )}
                    </div>
                    <p className="mt-0.5 text-sm text-gray-600">{n.message}</p>
                    <p className="mt-1 text-xs text-gray-400">{formatDate(n.timestamp)}</p>
                  </div>
                  {!n.read && (
                    <button
                      onClick={() => markReadMutation.mutate(n.id)}
                      className="shrink-0 text-xs text-primary-600 hover:text-primary-900 font-medium mt-1"
                    >
                      Mark read
                    </button>
                  )}
                </li>
              );
            })}
          </ul>
        )}
      </div>
    </div>
  );
}
