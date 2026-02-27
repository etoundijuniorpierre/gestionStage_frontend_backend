import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { FiMail, FiEye, FiEyeOff } from 'react-icons/fi';
import { create } from 'zustand';
import logo from '../assets/logo.png';
import { login } from '../api/authApi';
import { useAuthStore } from '../store/authStore';
import { useNavigate, Link } from 'react-router-dom';

interface LoginState {
  error: string;
  setError: (msg: string) => void;
  clearError: () => void;
}

const useLoginStore = create<LoginState>((set) => ({
  error: '',
  setError: (msg: string) => set((state) => ({ ...state, error: msg })),
  clearError: () => set((state) => ({ ...state, error: '' })),
}));

interface LoginFormInputs {
  email: string;
  password: string;
}

const LoginPage = () => {
  const navigate = useNavigate();
  const { register, handleSubmit } = useForm<LoginFormInputs>();
  const [showPassword, setShowPassword] = useState(false);
  const { error, setError, clearError } = useLoginStore();
  const setAuth = useAuthStore((state) => state.login);


  const onSubmit = async (data: LoginFormInputs) => {
    try {
      const response = await login(data);
      const { token, role } = response.data;
      
      console.log(`🔑 LoginPage: role=${role}, token=${!!token}`);
      
      if (token && role) {
        setAuth(token, role);
        clearError();
        
        // Vérifier stockage
        const storedRole = localStorage.getItem('role');
        console.log(`💾 LoginPage: role stocké=${storedRole}`);
        
        // Redirection selon rôle
        if (role === 'STUDENT') {
          console.log('🎯 LoginPage: STUDENT → /etudiant/stages');
          navigate('/etudiant/stages');
        } else if (role === 'TEACHER') {
          console.log('🎯 LoginPage: TEACHER → /enseignant/offres');
          navigate('/enseignant/offres');
        } else if (role === 'ENTERPRISE' || role?.toUpperCase() === 'ENTERPRISE') {
          console.log('🎯 LoginPage: ENTERPRISE → /entreprise/offres');
          navigate('/entreprise/offres');
        } else if (role === 'ADMIN') {
          console.log('🎯 LoginPage: ADMIN → /admin/dashboard');
          navigate('/admin/dashboard');
        } else {
          console.log(`❌ LoginPage: Rôle non reconnu ${role} → /`);
          navigate('/');
        }
      } else {
        console.log('Token ou role manquant');
        setError('Réponse de connexion invalide');
      }
    } catch (error) {
      console.log('Erreur login:', error);
      const err = error as { message?: string; userStatus?: { email: string; status: string; message: string } };
      
      // Gérer les comptes inactifs - erreur personnalisée avec userStatus
      if (err.userStatus?.status === 'INACTIF') {
        setError(err.userStatus.message);
        // Rediriger vers la page de vérification existante avec l'email et le message
        setTimeout(() => {
          navigate(`/verification?email=${encodeURIComponent(err.userStatus!.email)}&message=${encodeURIComponent(err.userStatus!.message)}`);
        }, 2000);
      } else {
        setError('Identifiants incorrects');
      }
    }
  };

  return (
    <div className="min-h-screen bg-login-gradient flex flex-col items-center justify-center px-4">
      {/* Logo */}
      <div className="flex flex-col items-center justify-center mb-20">
        <img src={logo} alt="Logo" className="max-w-[280px] w-full" />
        <p className="text-[#e1d3c1] text-center text-5xl tracking-[0.8em] ml-[35px]">ELITE</p>
      </div>

      {/* Formulaire */}
      <form className="w-full max-w-sm" onSubmit={handleSubmit(onSubmit)}>
        <div className="text-xs text-red-400 min-h-[1.5em] text-left mb-1">
          {error && 'Identifiants incorrects'}
        </div>

        <label className="flex justify-between items-center text-[#e2e2e2] mb-1" htmlFor="email">
          <span>Email</span>
          <FiMail className="text-xl" />
        </label>
        <input
          id="email"
          type="email"
          autoComplete="email"
          placeholder="-"
          className="w-full mb-4 border text-center border-gray-300 bg-[#e1d3c1] rounded focus:outline-none"
          {...register('email', { required: 'Email requis' })}
        />

        <label className="flex justify-between items-center text-[#e2e2e2] mb-1" htmlFor="password">
          <span>Mot de passe</span>
          <button
            type="button"
            tabIndex={-1}
            className="focus:outline-none cursor-pointer"
            onClick={() => setShowPassword((v) => !v)}
          >
            {showPassword ? <FiEyeOff className="text-xl" /> : <FiEye className="text-xl" />}
          </button>
        </label>
        <input
          id="password"
          type={showPassword ? 'text' : 'password'}
          autoComplete="current-password"
          placeholder="-"
          className="w-full mb-2 bg-[#e1d3c1] text-center border border-gray-300 rounded focus:outline-none"
          {...register('password', { required: 'Mot de passe requis' })}
        />

        <button
          type="submit"
          className="w-full bg-[#58693e] cursor-pointer text-white py-1 mt-5 rounded transition-colors hover:bg-[#4a5a32]"
        >
          Se connecter
        </button>

        <div className="flex justify-between items-center mt-3">
          <Link
            to="/reset-password"
            className="text-xs text-[#e1d3c1] hover:text-white hover:underline transition-colors"
          >
            Mot de passe oublié ?
          </Link>
          <Link
            to="/register"
            className="text-xs text-white hover:underline"
          >
            Créer un compte ?
          </Link>
        </div>
      </form>
    </div>
  );
};

export default LoginPage;
