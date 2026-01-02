import { api } from './api';
import type { OfferRequestDto } from '../types/offer';

// Créer une nouvelle offre (sans convention)
export const createOffer = async (offer: OfferRequestDto) => {
  try {
    return await api.post('/enterprise/createOffer', offer);
  } catch (error: any) {
    throw error;
  }
};

// Ajouter la convention PDF à une offre existante
export const addConventionToOffer = async (offerId: number, pdfConvention: File) => {
  const formData = new FormData();
  formData.append('pdfConvention', pdfConvention);
  return await api.post(`/enterprise/${offerId}/convention`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};

// Récupérer toutes les candidatures de l'entreprise
export const getEnterpriseApplications = (page = 0, size = 10) =>
  api.get('/enterprise/Applications', { params: { page, size } });

// Récupérer la liste des offres de l'entreprise
export const getEnterpriseOffers = (page = 0, size = 10) =>
  api.get('/enterprise/listOfOffers', { params: { page, size } });

// Récupérer le logo de l'entreprise (courant)
export const getEnterpriseLogo = () =>
  api.get('/profile/photo/logo', { responseType: 'blob' });

// Uploader une photo de profil / logo
export const uploadProfilePhoto = async (photo: File) => {
  const formData = new FormData();
  formData.append('photo', photo);
  return await api.post('/profile/photo/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};

// Télécharger le CV d'un candidat
export const downloadCandidateCV = async (studentId: number) => {
  return await api.get(`/files/cv/${studentId}`, { responseType: 'blob' });
};

// Télécharger la lettre de motivation d'un candidat
export const downloadCandidateCoverLetter = async (studentId: number) => {
  return await api.get(`/files/cover-letter/${studentId}`, { responseType: 'blob' });
};

// Valider ou rejeter une candidature
export const validateApplication = async (applicationId: number, approved: boolean) => {
  return await api.put(`/enterprise/application/${applicationId}/validate?approved=${approved}`);
};

// Télécharger la convention d'une offre
export const downloadConvention = async (offerId: number) => {
  return await api.get(`/files/convention/${offerId}`, { responseType: 'blob' });
};

// Récupérer les informations de l'entreprise connectée
export const getCurrentEnterpriseInfo = async () => {
  try {
    return await api.get('/enterprise/info');
  } catch (error: any) {
    throw error;
  }
};

// Mettre à jour le contact
export const updateContact = async (contact: string) => {
  return await api.patch('/enterprise/updateContact', { contact });
};

// Mettre à jour la localisation
export const updateLocation = async (location: string) => {
  return await api.patch('/enterprise/updateLocation', { location });
};

// Mettre à jour le logo
export const updateLogo = async (enterpriseId: number, file: File) => {
  const formData = new FormData();
  formData.append('photo', file); // In backend ProfilePhotoController uses @RequestParam("photo")
  return await api.post('/profile/photo/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};



