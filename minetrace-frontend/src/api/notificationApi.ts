import api from './axios';

export const notificationApi = {
  getAll: () => api.get('/api/notifications'),
  getUnreadCount: () => api.get('/api/notifications/unread-count'),
  markRead: (id: number) => api.put(`/api/notifications/${id}/read`),
  markAllRead: () => api.put('/api/notifications/read-all'),
};
