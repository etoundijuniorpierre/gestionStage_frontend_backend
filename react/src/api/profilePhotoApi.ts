```typescript
import { api } from './api';

// Uploader une photo de profil (Student) ou Logo (Enterprise)
export const uploadProfilePhoto = async (photo: File) => {
  try {
    const formData = new FormData();
    formData.append('photo', photo);
    
    return await api.post('/profile/photo/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  } catch (error: any) {
    throw error;
  }
};

// Récupérer le logo de l'entreprise connectée
export const getEnterpriseLogo = async () => {
  try {
    return await api.get('/profile/photo/logo', {
      responseType: 'blob'
    });
  } catch (error: any) {
    throw error;
  }
};

// Récupérer la photo d'un étudiant par ID
export const getStudentPhoto = async (studentId: number) => {
  try {
    return await api.get(`/profile/photo/student/${studentId}`, {
      responseType: 'blob'
    });
  } catch (error: any) {
    throw error;
  }
};
```