import { create } from 'zustand';
import { getUnseenNotifications, markNotificationAsSeen } from '../api/notificationApi';
import type { NotificationDto } from '../types/notification';

interface NotificationStore {
  notifications: NotificationDto[];
  unreadCount: number;
  loading: boolean;
  error: string | null;
  
  // Actions
  fetchNotifications: () => Promise<void>;
  markAsSeen: (id: number) => Promise<void>;
  clearError: () => void;
}

export const useNotificationStore = create<NotificationStore>((set, get) => ({
  notifications: [],
  unreadCount: 0,
  loading: false,
  error: null,

  fetchNotifications: async () => {
    set({ loading: true, error: null });
    try {
      const response = await getUnseenNotifications();
      const notifications = response.data || [];
      set({ 
        notifications,
        unreadCount: notifications.length,
        loading: false 
      });
    } catch (error: unknown) {
      const err = error as { message?: string };
      set({ 
        error: err.message || 'Erreur lors du chargement des notifications',
        loading: false 
      });
    }
  },

  markAsSeen: async (id: number) => {
    try {
      await markNotificationAsSeen(id);
      const { notifications } = get();
      const updatedNotifications = notifications.filter(n => n.id !== id);
      set({ 
        notifications: updatedNotifications,
        unreadCount: updatedNotifications.length 
      });
    } catch (error: unknown) {
      const err = error as { message?: string };
      set({ error: err.message || 'Erreur lors du marquage de la notification' });
    }
  },

  clearError: () => set({ error: null })
}));