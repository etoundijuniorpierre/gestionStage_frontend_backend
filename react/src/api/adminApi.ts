import { api } from './api';

// Récupérer les entreprises en attente de validation
export const getPendingEnterprises = (page = 0, size = 10) => {
  return api.get('/admin/approvalPendingEnterprise', { params: { page, size } });
};

// Approuver ou rejeter une entreprise
export const approveEnterprise = async (enterpriseId: number, approved: boolean) => {
  return api.put(`/admin/Enterprise/${enterpriseId}/approve?approved=${approved}`);
};

// Télécharger le rapport Excel des stages
export const downloadInternshipsExcel = async () => {
  return api.get('/admin/internships.xlsx', {
    responseType: 'blob'
  });
};

// Récupérer tous les enseignants
export const getAllTeachers = (page = 0, size = 10) => {
  return api.get('/admin/allTeachers', { params: { page, size } });
};

// Récupérer tous les étudiants
export const getAllStudents = (page = 0, size = 10) => {
  return api.get('/admin/allStudent', { params: { page, size } });
};

// Récupérer les entreprises en partenariat
export const getEnterpriseInPartnership = (page = 0, size = 10) => {
  return api.get('/admin/enterpriseInPartnership', { params: { page, size } });
};

// Supprimer le compte utilisateur (admin)
export const deleteUserAccount = async (userId: number) => {
  return api.delete(`/profile/account/${userId}`);
};

// Supprimer une entreprise (admin)
export const deleteEnterprise = async (enterpriseId: number) => {
  return api.delete(`/profile/account/${enterpriseId}`);
};

// Supprimer un étudiant (admin)
export const deleteStudent = async (studentId: number) => {
  return api.delete(`/profile/account/${studentId}`);
};
