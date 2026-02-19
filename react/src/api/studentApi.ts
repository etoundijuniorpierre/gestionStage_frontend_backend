import axios from 'axios';
import { api, getAuthHeaders } from './api';

const handleApiError = (error: unknown, defaultMessage: string): never => {
  if (axios.isAxiosError(error)) {
    if (error.response?.status === 401) {
      throw new Error('Session expirée. Veuillez vous reconnecter.');
    }
    throw new Error(error.response?.data?.message || defaultMessage);
  }
  throw new Error(defaultMessage);
};

// Récupérer le profil de l'utilisateur connecté
export const getCurrentStudentInfo = async () => {
  try {
    return await api.get('/api/student/profile');
  } catch (error) {
    handleApiError(error, 'Erreur lors de la récupération du profil');
  }
};

// Récupérer le statut de l'étudiant
export const getStudentStatus = async () => {
  try {
    return await api.get('/api/student/status');
  } catch (error) {
    handleApiError(error, 'Erreur lors de la récupération du statut');
  }
};

// Récupérer les offres approuvées pour l'étudiant
export const getApprovedOffers = async () => {
  try {
    return await api.get('/api/student/offersByApprovedStatus');
  } catch (error) {
    handleApiError(error, 'Erreur lors de la récupération des offres');
  }
};

// Filtrer les offres
export const filterOffers = async (paying?: boolean, remote?: boolean) => {
  try {
    const params = new URLSearchParams();
    if (paying !== undefined) params.append('paying', paying.toString());
    if (remote !== undefined) params.append('remote', remote.toString());
    
    return await api.get(`/api/student/filter?${params.toString()}`, {
      headers: getAuthHeaders()
    });
  } catch (error) {
    handleApiError(error, 'Erreur lors du filtrage des offres');
  }
};

// Récupérer les notifications de l'étudiant
export const getStudentNotifications = async () => {
  try {
    return await api.get('/api/student/StudentNotifications', {
      headers: getAuthHeaders()
    });
  } catch (error) {
    handleApiError(error, 'Erreur lors de la récupération des notifications');
  }
};

// Créer une candidature
export const createApplication = async (offerId: number, applicationData: FormData) => {
  try {
    if (!offerId || offerId <= 0) {
      throw new Error('ID d\'offre invalide');
    }
    if (!applicationData) {
      throw new Error('Données de candidature requises');
    }
    return await api.post(`/api/student/${offerId}/createApplication`, applicationData, {
      headers: getAuthHeaders()
    });
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.status === 409) {
      throw new Error('Vous avez déjà postulé pour cette offre');
    }
    handleApiError(error, 'Erreur lors de la création de la candidature');
  }
};

// Récupérer les candidatures en attente de l'étudiant
export const getPendingApplicationsOfStudent = async () => {
  try {
    return await api.get('/api/student/pendingApplicationsOfStudent', {
      headers: getAuthHeaders()
    });
  } catch (error) {
    handleApiError(error, 'Erreur lors de la récupération des candidatures');
  }
};

// Récupérer les candidatures approuvées de l'étudiant
export const getApplicationsApprovedOfStudent = async () => {
  try {
    return await api.get('/api/student/applicationsApprovedOfStudent', {
      headers: getAuthHeaders()
    });
  } catch (error) {
    handleApiError(error, 'Erreur lors de la récupération des candidatures approuvées');
  }
};

// Mettre à jour le statut de l'étudiant pour une candidature
export const updateStudentStatus = async (applicationId: number, applicationAccepted: boolean) => {
  try {
    if (!applicationId || applicationId <= 0) {
      throw new Error('ID de candidature invalide');
    }
    return await api.put(`/api/student/${applicationId}/updateStudentStatus?applicationAccepted=${applicationAccepted}`, {}, {
      headers: getAuthHeaders()
    });
  } catch (error) {
    handleApiError(error, 'Erreur lors de la mise à jour du statut');
  }
};

// Mettre à jour les langages
export const updateLanguages = async (language: string) => {
  try {
    if (!language || language.trim() === '') {
      throw new Error('Langage requis');
    }
    return await api.patch('/api/student/updateLanguages', { language }, {
      headers: getAuthHeaders()
    });
  } catch (error) {
    handleApiError(error, 'Erreur lors de la mise à jour du langage');
  }
};

// Mettre à jour le lien GitHub
export const updateGithubLink = async (github: string) => {
  try {
    if (!github || github.trim() === '') {
      throw new Error('Lien GitHub requis');
    }
    return await api.patch('/api/student/updateGithubLink', { github }, {
      headers: getAuthHeaders()
    });
  } catch (error) {
    handleApiError(error, 'Erreur lors de la mise à jour du lien GitHub');
  }
};

// Mettre à jour le lien LinkedIn  
export const updateLinkedinLink = async (linkedin: string) => {
  try {
    if (!linkedin || linkedin.trim() === '') {
      throw new Error('Lien LinkedIn requis');
    }
    return await api.patch('/api/student/updateLinkedinLink', { linkedin }, {
      headers: getAuthHeaders()
    });
  } catch (error) {
    handleApiError(error, 'Erreur lors de la mise à jour du lien LinkedIn');
  }
};

// Mettre à jour le profil complet
export const updateStudentProfile = async (profileData: Record<string, unknown>) => {
  try {
    return await api.patch('/api/student/updateProfile', profileData, {
      headers: getAuthHeaders()
    });
  } catch (error) {
    handleApiError(error, 'Erreur lors de la mise à jour du profil');
  }
};



// Supprimer une candidature
export const deleteApplication = async (applicationId: number) => {
  try {
    if (!applicationId || applicationId <= 0) {
      throw new Error('ID de candidature invalide');
    }
    return await api.delete(`/api/student/${applicationId}`, {
      headers: getAuthHeaders()
    });
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.status === 404) {
      throw new Error('Candidature non trouvée');
    }
    handleApiError(error, 'Erreur lors de la suppression de la candidature');
  }
};



