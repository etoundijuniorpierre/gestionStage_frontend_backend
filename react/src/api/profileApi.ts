import { api, getAuthHeaders } from './api';

// Mettre à jour le mot de passe
export const updatePassword = async (password: string) => {
  if (!password || password.trim() === '') {
    throw new Error('Mot de passe requis');
  }
  if (password.length < 6) {
    throw new Error('Le mot de passe doit contenir au moins 6 caractères');
  }
  
  try {
    const headers = getAuthHeaders();
    console.log('Headers pour updatePassword:', headers);
    
    const response = await api.patch('/updateProfile/updatePassword', { password }, {
      headers
    });
    return response;
  } catch (error: unknown) {
      const err = error as { response?: { status?: number; data?: { message?: string } }; message?: string };
    console.error('Erreur updatePassword:', error);
    console.error('Status:', err.response?.status);
    console.error('Data:', err.response?.data);
    
    if (err.response?.status === 401 || err.response?.status === 403) {
      throw new Error('Session expirée. Veuillez vous reconnecter.');
    }
    throw new Error(err.response?.data?.message || err.message || 'Erreur lors de la mise à jour du mot de passe');
  }
};

// Mettre à jour l'email
export const updateEmail = async (email: string) => {
  try {
    if (!email || email.trim() === '') {
      throw new Error('Email requis');
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      throw new Error('Format d\'email invalide');
    }
    return await api.patch('/updateProfile/updateEmail', { email }, {
      headers: getAuthHeaders()
    });
  } catch (error: unknown) {
      const err = error as { response?: { status?: number; data?: { message?: string } }; message?: string };
    if (err.response?.status === 409) {
      throw new Error('Cet email est déjà utilisé');
    }
    if (err.response?.status === 401) {
      throw new Error('Session expirée. Veuillez vous reconnecter.');
    }
    throw new Error(err.response?.data?.message || 'Erreur lors de la mise à jour de l\'email');
  }
};

// Récupérer l'email de l'utilisateur
export const getUserEmail = async () => {
  try {
    return await api.get('/updateProfile/getUserEmail', {
      headers: getAuthHeaders()
    });
  } catch (error: unknown) {
      const err = error as { response?: { status?: number; data?: { message?: string } }; message?: string };
    if (err.response?.status === 401) {
      throw new Error('Session expirée. Veuillez vous reconnecter.');
    }
    throw new Error(err.response?.data?.message || 'Erreur lors de la récupération de l\'email');
  }
};

// Supprimer le compte utilisateur
export const deleteUserAccount = async () => {
  try {
    return await api.delete('/updateProfile/deleteUserAccount', {
      headers: getAuthHeaders()
    });
  } catch (error: unknown) {
      const err = error as { response?: { status?: number; data?: { message?: string } }; message?: string };
    if (err.response?.status === 401) {
      throw new Error('Session expirée. Veuillez vous reconnecter.');
    }
    if (err.response?.status === 403) {
      throw new Error('Opération non autorisée');
    }
    throw new Error(err.response?.data?.message || 'Erreur lors de la suppression du compte');
  }
};

// Vérifier le mot de passe
export const verifyPassword = async (password: string) => {
  try {
    if (!password || password.trim() === '') {
      throw new Error('Mot de passe requis');
    }
    return await api.put('/updateProfile/verifyPassword', { password }, {
      headers: getAuthHeaders()
    });
  } catch (error: unknown) {
      const err = error as { response?: { status?: number; data?: { message?: string } }; message?: string };
    if (err.response?.status === 401) {
      throw new Error('Mot de passe incorrect');
    }
    if (err.response?.status === 403) {
      throw new Error('Session expirée. Veuillez vous reconnecter.');
    }
    throw new Error(err.response?.data?.message || 'Erreur lors de la vérification du mot de passe');
  }
};