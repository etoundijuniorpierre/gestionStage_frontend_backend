import { useNavigate } from 'react-router-dom';
import NotificationBell from './NotificationBell';
import { useAuthStore } from '../store/authStore';

const HeaderActions = ({ logoutPath = '/login' }: { logoutPath?: string }) => {
  const navigate = useNavigate();
  const { logout } = useAuthStore();

  const handleLogout = () => {
    logout();
    navigate(logoutPath);
  };

  return (
    <div className="flex gap-8 items-center">
      <NotificationBell />
      <button
        onClick={handleLogout}
        className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded transition-colors duration-200 text-sm font-medium"
      >
        Déconnexion
      </button>
    </div>
  );
};

export default HeaderActions;
