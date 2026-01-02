import { api } from './api';

// Récupérer le profil de l'utilisateur connecté
export const getCurrentStudentInfo = async () => {
  try {
    return await api.get('/student/profile');
  } catch (error: any) {
    throw error;
  }
};

// Récupérer le statut de l'étudiant
export const getStudentStatus = async () => {
  try {
    return await api.get('/student/status');
  } catch (error: any) {
    throw error;
  }
};

// Récupérer les offres approuvées pour l'étudiant
export const getApprovedOffers = async (page = 0, size = 10) => {
  try {
    return await api.get('/student/offersByApprovedStatus', { params: { page, size } });
  } catch (error: any) {
    throw error;
  }
};

// Filtrer les offres
export const filterOffers = async (paying?: boolean, remote?: boolean, page = 0, size = 10) => {
  try {
    return await api.get('/student/filter', { params: { paying, remote, page, size } });
  } catch (error: any) {
    throw error;
  }
};

// Créer une candidature
export const createApplication = async (offerId: number, applicationData: FormData) => {
  try {
    return await api.post(`/student/${offerId}/createApplication`, applicationData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  } catch (error: any) {
    throw error;
  }
};

// Récupérer les candidatures en attente de l'étudiant
export const getPendingApplicationsOfStudent = async (page = 0, size = 10) => {
  try {
    return await api.get('/student/pendingApplicationsOfStudent', { params: { page, size } });
  } catch (error: any) {
    throw error;
  }
};

// Récupérer les candidatures approuvées de l'étudiant
export const getApplicationsApprovedOfStudent = async (page = 0, size = 10) => {
  try {
    return await api.get('/student/applicationsApprovedOfStudent', { params: { page, size } });
  } catch (error: any) {
    throw error;
  }
};

// Mettre à jour le statut de l'étudiant pour une candidature
export const updateStudentStatus = async (applicationId: number, applicationAccepted: boolean) => {
  try {
    return await api.put(`/student/${applicationId}/updateStudentStatus?applicationAccepted=${applicationAccepted}`);
  } catch (error: any) {
    throw error;
  }
};

// Mettre à jour les langages
export const updateLanguages = async (language: string) => {
  try {
    return await api.patch('/student/updateLanguages', { language });
  } catch (error: any) {
    throw error;
  }
};

// Mettre à jour le lien GitHub
export const updateGithubLink = async (github: string) => {
  try {
    return await api.patch('/student/updateGithubLink', { github });
  } catch (error: any) {
    throw error;
  }
};

// Mettre à jour le lien LinkedIn  
export const updateLinkedinLink = async (linkedin: string) => {
  try {
    return await api.patch('/student/updateLinkedinLink', { linkedin });
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

// Télécharger un CV
export const downloadCV = async (studentId: number) => {
  try {
    return await api.get(`/files/cv/${studentId}`, { responseType: 'blob' });
  } catch (error: any) {
    throw error;
  }
};

// Télécharger une lettre de motivation
export const downloadCoverLetter = async (studentId: number) => {
  try {
    return await api.get(`/files/cover-letter/${studentId}`, { responseType: 'blob' });
  } catch (error: any) {
    throw error;
  }
};

// Télécharger une convention
export const downloadConvention = async (offerId: number) => {
  try {
    return await api.get(`/files/convention/${offerId}`, { responseType: 'blob' });
  } catch (error: any) {
    throw error;
  }
};

// Supprimer une candidature
export const deleteApplication = async (applicationId: number) => {
  try {
    return await api.delete(`/student/${applicationId}`);
  } catch (error: any) {
    throw error;
  }
};



