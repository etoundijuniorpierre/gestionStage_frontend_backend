import { api } from './api';

// Mettre à jour le mot de passe
export const updatePassword = async (password: string) => {
  try {
    return await api.patch('/profile/password', { password });
  } catch (error: any) {
    throw error;
  }
};

// Mettre à jour l'email
export const updateEmail = async (email: string) => {
  try {
    return await api.patch('/profile/email', { email });
  } catch (error: any) {
    throw error;
  }
};

// Récupérer l'email de l'utilisateur
export const getUserEmail = async () => {
  try {
    return await api.get('/profile/email');
  } catch (error: any) {
    throw error;
  }
};

// Supprimer le compte utilisateur (Admin)
export const deleteUserAccount = async (userId: number) => {
  try {
    return await api.delete(`/profile/account/${userId}`);
  } catch (error: any) {
    throw error;
  }
};

// Vérifier le mot de passe
export const verifyPassword = async (password: string) => {
  try {
    return await api.post('/profile/verify-password', { password });
  } catch (error: any) {
    throw error;
  }
};
