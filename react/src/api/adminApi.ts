import { api, getAuthHeaders } from './api';
import type { EnterpriseResponseDto } from '../types/enterprise';
import type { StudentResponseDto } from '../types/student';


// Récupérer les entreprises en attente de validation
export const getPendingEnterprises = async () => {
  return api.get<EnterpriseResponseDto[]>('/api/admin/approvalPendingEnterprise', {
    headers: getAuthHeaders()
  });
};

// Approuver ou rejeter une entreprise
export const approveEnterprise = async (enterpriseId: number, approved: boolean) => {
  if (!enterpriseId || enterpriseId <= 0) {
    throw new Error('ID d\'entreprise invalide');
  }
  return api.put<EnterpriseResponseDto>(`/api/admin/Enterprise/${enterpriseId}/approve?approved=${approved}`, {}, {
    headers: getAuthHeaders()
  });
};

// Télécharger le rapport Excel des stages
export const downloadInternshipsExcel = async () => {
  return api.get('/api/admin/internships.xlsx', {
    responseType: 'blob',
    headers: getAuthHeaders()
  });
};

// Récupérer tous les enseignants
export const getAllTeachers = async () => {
  return api.get('/api/admin/allTeachers', {
    headers: getAuthHeaders()
  });
};

// Récupérer tous les étudiants
export const getAllStudents = async () => {
  return api.get('/api/admin/allStudent', {
    headers: getAuthHeaders()
  });
};

// Récupérer les entreprises en partenariat
export const getEnterpriseInPartnership = async () => {
  return api.get('/api/admin/enterpriseInPartnership', {
    headers: getAuthHeaders()
  });
};

// Récupérer les enseignants avec pagination
export const getTeachersPagination = async (page: number, size: number) => {
  if (page < 0 || size <= 0) {
    throw new Error('Paramètres de pagination invalides');
  }
  return api.get(`/api/admin/teacherPagination?page=${page}&size=${size}`, {
    headers: getAuthHeaders()
  });
};

// Récupérer les étudiants avec pagination
export const getStudentsPagination = async (page: number, size: number) => {
  if (page < 0 || size <= 0) {
    throw new Error('Paramètres de pagination invalides');
  }
  return api.get(`/api/admin/studentPagination?page=${page}&size=${size}`, {
    headers: getAuthHeaders()
  });
};

// Supprimer une entreprise (admin)
export const deleteEnterprise = async (enterpriseId: number) => {
  return api.delete(`/updateProfile/deleteAccount/${enterpriseId}`, {
    headers: getAuthHeaders()
  });
};

// Supprimer un étudiant (admin)
export const deleteStudent = async (studentId: number) => {
  return api.delete(`/updateProfile/deleteAccount/${studentId}`, {
    headers: getAuthHeaders()
  });
};

// Récupérer un étudiant par ID (admin)
export const getStudentById = async (id: number): Promise<StudentResponseDto | null> => {
  if (!Number.isInteger(id) || id <= 0) {
    throw new Error('ID étudiant invalide');
  }
  const response = await getAllStudents();
  const students = response.data as StudentResponseDto[];
  return students.find((student: StudentResponseDto) => student.id === id) || null;
};
