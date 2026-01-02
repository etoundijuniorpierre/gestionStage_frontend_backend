import { api } from './api';

// Récupérer les notifications non vues
export const getUnseenNotifications = async () => {
  try {
    return await api.get('/notifications/unseen');
  } catch (error: any) {
    throw error;
  }
};

// Marquer une notification comme vue
export const markNotificationAsSeen = async (notificationId: number) => {
  try {
    return await api.put(`/notifications/${notificationId}/mark-as-seen`);
  } catch (error: any) {
    throw error;
  }
};

// Supprimer des notifications (Si ajouté au backend plus tard)
export const deleteNotifications = async (notificationIds: number[]) => {
  try {
    // Non implémenté dans le backend actuel
    throw new Error('Fonctionnalité non disponible');
  } catch (error: any) {
    throw error;
  }
};
