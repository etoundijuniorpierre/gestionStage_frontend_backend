import { api } from './api';

export const getAllStudents = async (page = 0, size = 10) => {
  try {
    return await api.get('/admin/allStudent', { params: { page, size } });
  } catch (error: any) {
    throw error;
  }
};

// Supprimer un étudiant
export const deleteStudent = async (studentId: number) => {
  try {
    return await api.delete(`/profile/account/${studentId}`);
  } catch (error: any) {
    throw error;
  }
};
