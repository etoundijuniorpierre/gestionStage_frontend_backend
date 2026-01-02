import { api } from './api';
import type { OfferResponseDto } from '../types/offer';

// Recherche le détail d'offre en parcourant les listes disponibles selon le rôle
export async function getStageDetail(offerId: number): Promise<OfferResponseDto> {
  const tryEndpoints = [
    () => api.get('/student/offersByApprovedStatus'),
    () => api.get('/teacher/offerToReview'),
    () => api.get('/enterprise/listOfOffers')
  ];

  for (const fetcher of tryEndpoints) {
    try {
      const { data } = await fetcher();
      if (Array.isArray(data)) {
        const found = data.find((o: OfferResponseDto) => Number(o.id) === Number(offerId));
        if (found) {
          return found as OfferResponseDto;
        }
      }
    } catch (error) {
      console.log('Erreur endpoint:', error);
    }
  }

  throw new Error(`Offre avec l'ID ${offerId} non trouvée`);
}

// Télécharge la convention de stage
export async function downloadConvention(offerId: number): Promise<Blob> {
  try {
    const { data } = await api.get(`/files/convention/${offerId}`, {
      responseType: 'blob'
    });
    return data;
  } catch (error: any) {
    throw error;
  }
}

// Soumet une candidature avec CV et lettre de motivation
export async function submitApplication(offerId: number, cvFile: File, coverLetterFile: File): Promise<void> {
  try {
    const formData = new FormData();
    formData.append('cv', cvFile);
    formData.append('coverLetter', coverLetterFile);

    await api.post(`/student/${offerId}/createApplication`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  } catch (error: any) {
    throw error;
  }
}
