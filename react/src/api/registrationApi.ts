import { api } from './api';
import type { StudentRegistrationRequestDto } from '../types/student';
import type { EnterpriseRegistrationRequestDto } from '../types/enterprise';
import type { TeacherRegistrationRequestDto } from '../types/teacher';
import type { TokenVerificationRequestDto } from '../types/auth';

// Inscription étudiant
export const registerStudent = async (studentData: StudentRegistrationRequestDto) => {
  try {
    return await api.post('/registration/registerStudent', studentData);
  } catch (error) {
    throw error;
  }
};

// Inscription entreprise
export const registerEnterprise = async (enterpriseData: EnterpriseRegistrationRequestDto) => {
  const formData = new FormData();
  Object.entries(enterpriseData).forEach(([key, value]) => {
    if (key === 'logo' && value) {
      formData.append(key, value as File);
    } else if (value !== undefined && value !== null) {
      formData.append(key, String(value));
    }
  });
  return await api.post('/registration/registerEnterprise', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};

// Inscription enseignant
export const registerTeacher = async (teacherData: TeacherRegistrationRequestDto) => {
  try {
    return await api.post('/registration/registerTeacher', teacherData);
  } catch (error) {
    throw error;
  }
};

// Vérification d'email
export const verifyEmail = async (verifyData: TokenVerificationRequestDto) => {
  try {
    return await api.post('/registration/verifyEmail', verifyData);
  } catch (error) {
    throw error;
  }
};

// Renvoyer le token de vérification
export const resendToken = async (email: string) => {
  try {
    return await api.post('/registration/resendToken', null, { params: { email } });
  } catch (error) {
    throw error;
  }
};
