import { create } from 'zustand';


const ROLE_MAP: Record<string, string> = {
  'ETUDIANT': 'STUDENT',
  'ENSEIGNANT': 'TEACHER',
  'ENTREPRISE': 'ENTERPRISE',
  'ADMIN': 'ADMIN',
  'STUDENT': 'STUDENT',
  'TEACHER': 'TEACHER',
  'ENTERPRISE': 'ENTERPRISE',
};

const normalizeRole = (role: string | null): string | null => {
  if (!role) return null;
  const upper = role.toUpperCase();
  return ROLE_MAP[upper] || upper;
};

interface AuthState {
  token: string | null;
  role: string | null;
  login: (token: string, role: string) => void;
  logout: () => void;
  sync: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  token: localStorage.getItem('token'),
  role: normalizeRole(localStorage.getItem('role')),
  login: (token, role) => {
    const normalized = normalizeRole(role) || '';
    localStorage.setItem('token', token);
    localStorage.setItem('role', normalized);
    set({ token, role: normalized });
  },
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    set({ token: null, role: null });
  },
  sync: () => {
    const token = localStorage.getItem('token');
    const role = normalizeRole(localStorage.getItem('role'));
    set({ token, role });
  }
}));

// Synchronisation multi-onglets (logout/login partout)
if (typeof window !== 'undefined') {
  window.addEventListener('storage', (event) => {
    if (event.key === 'token' || event.key === 'role') {
      const { sync } = useAuthStore.getState();
      sync();
    }
  });
}
