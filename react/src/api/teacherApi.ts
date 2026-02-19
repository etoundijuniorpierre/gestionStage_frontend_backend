import { api, getAuthHeaders } from './api';

// Offres à valider pour le département de l'enseignant
export const getOffersToReviewByDepartment = async () => {
  return await api.get('/api/teacher/offerToReview', { headers: getAuthHeaders() });
};

// Valider une offre et sa convention
export const validateOfferAndConvention = async (
  id: number,
  validationData: { offerApproved: boolean; conventionApproved: boolean }
) => {
  if (!Number.isInteger(id) || id <= 0) {
    throw new Error('ID d\'offre invalide - doit être un entier positif');
  }
  try {
    return await api.put(`/api/teacher/offers/${id}/validate`, validationData, {
      headers: getAuthHeaders()
    });
  } catch (error: unknown) {
    const err = error as { response?: { status?: number; data?: { message?: string } } };
    if (err.response?.status === 400) {
      throw new Error('Offre déjà traitée ou données invalides');
    }
    if (err.response?.status === 401) {
      throw new Error('Session expirée. Veuillez vous reconnecter.');
    }
    if (err.response?.status === 403) {
      throw new Error('Vous n\'avez pas les droits pour valider cette offre');
    }
    throw new Error(err.response?.data?.message || 'Erreur lors de la validation de l\'offre');
  }
};

// Récupérer la liste des étudiants par département
export const getStudentsByDepartment = async () => {
  return await api.get('/api/teacher/listOfStudentByDepartment', { headers: getAuthHeaders() });
};

// Statistiques des stages par département
export const getInternshipStats = async () => {
  return await api.get('/api/teacher/internshipsByDepartment', {
    headers: getAuthHeaders()
  });
};

// Récupérer les offres approuvées par l'enseignant
export const getOffersApprovedByTeacher = async () => {
  return await api.get('/api/teacher/offersApprovedByTeacher', {
    headers: getAuthHeaders()
  });
};

// Récupérer les notifications de l'enseignant
export const getTeacherNotifications = async () => {
  return await api.get('/api/teacher/teacherNotifications', {
    headers: getAuthHeaders()
  });
};

// Récupérer les entreprises en partenariat (spécifique enseignant)
export const getTeacherEnterpriseInPartnership = async () => {
  return await api.get('/api/teacher/enterpriseInPartnership', {
    headers: getAuthHeaders()
  });
};

// Récupérer les candidatures d'un étudiant spécifique
export const getStudentApplications = async (studentId: number) => {
  return await api.get(`/api/teacher/student/${studentId}/applications`, {
    headers: getAuthHeaders()
  });
};

// Télécharger le CV étudiant
export const downloadStudentCV = async (applicationId: number) => {
  const response = await api.get(`/downloadFiles/cv/${applicationId}/download`, {
    headers: getAuthHeaders(),
    responseType: 'blob'
  });
  return response.data;
};

// Télécharger la lettre de motivation étudiant
export const downloadStudentCoverLetter = async (applicationId: number) => {
  const response = await api.get(`/downloadFiles/coverLetter/${applicationId}/download`, {
    headers: getAuthHeaders(),
    responseType: 'blob'
  });
  return response.data;
};

// Télécharger collectivement les documents de plusieurs candidatures
export const downloadBulkDocuments = async (applicationIds: number[]) => {
  const response = await api.get(`/downloadFiles/bulkDownload`, {
    headers: getAuthHeaders(),
    params: { ids: applicationIds.join(',') },
    responseType: 'blob'
  });
  return response.data;
};

// Télécharger collectivement les documents de plusieurs étudiants
export const downloadBulkDocumentsByStudents = async (studentIds: number[]) => {
  const response = await api.get(`/downloadFiles/bulkDownloadByStudents`, {
    headers: getAuthHeaders(),
    params: { studentIds: studentIds.join(',') },
    responseType: 'blob'
  });
  return response.data;
};
