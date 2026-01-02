import axios from 'axios';

export const api = axios.create({
  baseURL: '/api', // Use the proxy
  withCredentials: true,
});

// Interceptor de requête pour gestion automatique des headers
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor de réponse pour gestion des erreurs et standardisation
api.interceptors.response.use(
  (response) => {
    // Si c'est un blob (téléchargement), on retourne tel quel
    if (response.config.responseType === 'blob') {
      return response;
    }

    // Si on reçoit notre ApiResponse standard
    const { data } = response;
    if (data && typeof data === 'object' && 'success' in data) {
      if (!data.success) {
        return Promise.reject({
          response: {
            data: {
              message: data.message || 'Une erreur est survenue'
            }
          }
        });
      }
      // On retourne l'objet ApiResponse complet car il contient success, message, data et pagination
      return data;
    }

    return response.data;
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
};
