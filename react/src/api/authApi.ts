import { api } from './api';
import type { LoginRequest, ResetPasswordRequestDto } from '../types/auth';

// Authentification
export const login = async (loginData: LoginRequest) => {
  try {
    const response = await api.post('/auth/login', loginData);
    return response; // Contient success, message, data (token, role, etc)
  } catch (error: any) {
    throw error;
  }
};

export const verifyCurrentPassword = async (password: string) => {
  try {
    return await api.post('/profile/verify-password', { password });
  } catch (error: any) {
    throw error;
  }
};

// Demander un token de réinitialisation
export const sendResetToken = async (email: string) => {
  try {
    return await api.post('/auth/reset-password/send-token', { email });
  } catch (error: any) {
    throw error;
  }
};

// Vérifier le token de réinitialisation
export const verifyResetToken = async (email: string, token: string) => {
  try {
    return await api.post('/auth/reset-password/verify-email', { email, token });
  } catch (error: any) {
    throw error;
  }
};

// Réinitialiser le mot de passe (après vérification)
export const resetPassword = async (resetData: ResetPasswordRequestDto) => {
  try {
    return await api.patch('/auth/reset-password', resetData);
  } catch (error: any) {
    throw error;
  }
};
