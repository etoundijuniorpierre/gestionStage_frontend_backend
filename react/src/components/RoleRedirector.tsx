import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';

const RoleRedirector = () => {
  const { token, role } = useAuthStore();

  console.log(`🔄 RoleRedirector: token=${!!token}, role=${role}`);

  if (!token) {
    console.log('🔄 RoleRedirector: Pas de token → login');
    return <Navigate to="/login" replace />;
  }
  if (role === 'STUDENT') {
    console.log('🔄 RoleRedirector: STUDENT → /etudiant/stages');
    return <Navigate to="/etudiant/stages" replace />;
  }
  if (role === 'TEACHER') {
    console.log('🔄 RoleRedirector: TEACHER → /enseignant/offres');
    return <Navigate to="/enseignant/offres" replace />;
  }
  if (role === 'ENTERPRISE') {
    console.log('🔄 RoleRedirector: ENTERPRISE → /entreprise/offres');
    return <Navigate to="/entreprise/offres" replace />;
  }
  if (role === 'ADMIN') {
    console.log('🔄 RoleRedirector: ADMIN → /admin/dashboard');
    return <Navigate to="/admin/dashboard" replace />;
  }
  console.log(`🔄 RoleRedirector: Rôle non reconnu ${role} → login`);
  return <Navigate to="/login" replace />;
};

export default RoleRedirector;

