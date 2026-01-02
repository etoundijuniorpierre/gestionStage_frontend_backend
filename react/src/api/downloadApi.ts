import { api } from './api';

// Télécharger un CV par ID de candidature
export const downloadCV = async (applicationId: number) => {
  try {
    return await api.get(`/files/cv/${applicationId}`, {
      responseType: 'blob'
    });
  } catch (error: any) {
    throw error;
  }
};

// Télécharger une lettre de motivation par ID de candidature
export const downloadCoverLetter = async (applicationId: number) => {
  try {
    return await api.get(`/files/cover-letter/${applicationId}`, {
      responseType: 'blob'
    });
  } catch (error: any) {
    throw error;
  }
};

// Télécharger une convention par ID d'offre
export const downloadConvention = async (offerId: number) => {
  try {
    return await api.get(`/files/convention/${offerId}`, {
      responseType: 'blob'
    });
  } catch (error: any) {
    throw error;
  }
};

// Fonction utilitaire pour télécharger un fichier blob
export const downloadBlob = (blob: Blob, filename: string) => {
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
};
