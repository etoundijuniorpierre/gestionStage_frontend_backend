import { api } from './api';

// Offres à valider pour le département de l'enseignant
export const getOffersToReviewByDepartment = async (page = 0, size = 10) => {
  try {
    return await api.get('/teacher/offerToReview', { params: { page, size } });
  } catch (error) {
    throw error;
  }
};

// Valider une offre et sa convention
export const validateOfferAndConvention = async (
  id: number,
  validationData: { offerApproved: boolean; conventionApproved: boolean }
) => {
  try {
    return await api.put(`/teacher/offers/${id}/validate`, validationData);
  } catch (error: any) {
    throw error;
  }
};

// Récupérer la liste des étudiants par département
export const getStudentsByDepartment = async (page = 0, size = 10) => {
  try {
    return await api.get('/teacher/listOfStudentByDepartment', { params: { page, size } });
  } catch (error) {
    throw error;
  }
};

// Récupérer les entreprises en attente de validation (via Admin)
export const getPendingEnterprises = async (page = 0, size = 10) => {
  try {
    return await api.get('/admin/approvalPendingEnterprise', { params: { page, size } });
  } catch (error) {
    throw error;
  }
};

// Approuver ou rejeter une entreprise (via Admin)
export const approveEnterprise = async (enterpriseId: number, approved: boolean) => {
  try {
    return await api.put(`/admin/Enterprise/${enterpriseId}/approve?approved=${approved}`);
  } catch (error) {
    throw error;
  }
};

// Statistiques des stages par département
export const getInternshipStats = async () => {
  try {
    return await api.get('/teacher/internshipsByDepartment');
  } catch (error) {
    throw error;
  }
};

// Récupérer les offres approuvées par l'enseignant
export const getOffersApprovedByTeacher = async (page = 0, size = 10) => {
  try {
    return await api.get('/teacher/offersApprovedByTeacher', { params: { page, size } });
  } catch (error) {
    throw error;
  }
};

// Récupérer les entreprises en partenariat
export const getEnterpriseInPartnership = async (page = 0, size = 10) => {
  try {
    return await api.get('/teacher/enterpriseInPartnership', { params: { page, size } });
  } catch (error) {
    throw error;
  }
};

// Télécharger une convention
export const downloadConvention = async (offerId: number) => {
  return await api.get(`/files/convention/${offerId}`, { responseType: 'blob' });
};

// Télécharger le CV d'un candidat
export const downloadStudentCV = async (studentId: number) => {
  return await api.get(`/files/cv/${studentId}`, { responseType: 'blob' });
};

// Supprimer le compte utilisateur
export const deleteUserAccount = async (userId: number) => {
  return await api.delete(`/profile/account/${userId}`);
};

// Vérifier le mot de passe
export const verifyPassword = async (password: string) => {
  return await api.post('/profile/verify-password', { password });
};


