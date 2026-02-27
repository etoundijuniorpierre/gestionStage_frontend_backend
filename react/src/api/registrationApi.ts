import { api } from './api';
import type { StudentRegistrationRequestDto } from '../types/student';
import type { EnterpriseRegistrationRequestDto } from '../types/enterprise';
import type { TeacherRegistrationRequestDto } from '../types/teacher';
import type { TokenVerificationRequestDto } from '../types/auth';

// Inscription étudiant
export const registerStudent = async (studentData: StudentRegistrationRequestDto) => {
  if (!studentData.email || !studentData.password || !studentData.name) {
    throw new Error('Email, mot de passe et nom requis');
  }
  const response = await api.post('/registration/registerStudent', studentData);
  
  // Gérer les comptes existants
  if (response.status === 400 && typeof response.data === 'string') {
    // Le backend retourne une chaîne de caractères pour les comptes existants
    const message = response.data;
    if (message.includes('already exists but is inactive')) {
      throw new Error(message);
    }
  }
  
  return response;
};

// Inscription entreprise
export const registerEnterprise = async (enterpriseData: EnterpriseRegistrationRequestDto) => {
  if (!enterpriseData.email || !enterpriseData.password || !enterpriseData.name) {
    throw new Error('Email, mot de passe et nom requis');
  }
  const formData = new FormData();
  Object.entries(enterpriseData).forEach(([key, value]) => {
    if (key === 'logo' && value) {
      formData.append(key, value as File);
    } else if (typeof value !== 'undefined') {
      formData.append(key, String(value));
    }
  });
  const response = await api.post('/registration/registerEnterprise', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
  
  // Gérer les comptes existants
  if (response.status === 400 && typeof response.data === 'string') {
    // Le backend retourne une chaîne de caractères pour les comptes existants
    const message = response.data;
    if (message.includes('already exists but is inactive')) {
      throw new Error(message);
    }
  }
  
  return response;
};

// Inscription enseignant
export const registerTeacher = async (teacherData: TeacherRegistrationRequestDto) => {
  if (!teacherData.email || !teacherData.password || !teacherData.name || !teacherData.firstName || !teacherData.department) {
    throw new Error('Email, mot de passe, nom, prénom et département requis');
  }
  const response = await api.post('/registration/registerTeacher', teacherData);
  
  // Gérer les comptes existants
  if (response.status === 400 && typeof response.data === 'string') {
    // Le backend retourne une chaîne de caractères pour les comptes existants
    const message = response.data;
    if (message.includes('already exists but is inactive')) {
      throw new Error(message);
    }
    // Afficher le message d'erreur exact du backend pour le diagnostic
    console.error('Backend error:', message);
    throw new Error(message);
  }
  
  return response;
};

// Vérification d'email
export const verifyEmail = async (verifyData: TokenVerificationRequestDto) => {
  if (!verifyData.email || !verifyData.token) {
    throw new Error('Email et token requis');
  }
  return await api.post('/registration/verifyEmail', verifyData);
};

// Renvoyer le token de vérification
export const resendToken = async (email: string) => {
  if (!email || email.trim() === '') {
    throw new Error('Email requis');
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    throw new Error('Format d\'email invalide');
  }
  return await api.post('/registration/resendToken', null, { params: { email } });
};
