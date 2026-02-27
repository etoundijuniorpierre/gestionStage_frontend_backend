import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';

interface ProtectedRouteProps {
  allowedRoles?: string[]; // Ex: ['etudiant', 'enseignant']
  redirectTo?: string;
}

export default function ProtectedRoute({ allowedRoles, redirectTo = '/login' }: ProtectedRouteProps) {
  const { token, role } = useAuthStore();

  // Normalisation plus robuste du rôle
  const normalizedRole = (role || '').trim().toUpperCase();
  const normalizedAllowedRoles = allowedRoles?.map(r => (r || '').trim().toUpperCase()) || [];

  console.log(`🔒 ProtectedRoute: role brut="${role}", normalized="${normalizedRole}"`);
  console.log(`🔒 ProtectedRoute: allowed=${allowedRoles}, normalizedAllowed=${normalizedAllowedRoles}`);
  console.log(`🔒 ProtectedRoute: token=${!!token}`);
  console.log(`🔒 ProtectedRoute: includes check=${normalizedAllowedRoles.includes(normalizedRole)}`);

  if (!token) {
    console.log('🔒 ProtectedRoute: Pas de token → redirection vers login');
    return <Navigate to={redirectTo} replace />;
  }

  if (allowedRoles && !normalizedAllowedRoles.includes(normalizedRole)) {
    console.log(`🔒 ProtectedRoute: Rôle "${normalizedRole}" non autorisé pour [${normalizedAllowedRoles.join(', ')}] → redirection vers /`);
    return <Navigate to="/" replace />;
  }

  console.log(`✅ ProtectedRoute: Accès autorisé pour rôle "${normalizedRole}"`);
  return <Outlet />;
}