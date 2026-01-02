import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { FiMail, FiLock } from 'react-icons/fi';
import { motion } from 'framer-motion';
import { useNavigate, Link } from 'react-router-dom';
import { login } from '../api/authApi';
import { useAuthStore } from '../store/authStore';
import { useToastStore } from '../store/toastStore';
import Button from './ui/Button';
import Input from './ui/Input';
import Card from './ui/Card';
import logo from '../assets/logo.png';

interface LoginFormInputs {
  email: string;
  password: string;
}

const LoginPage = () => {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors } } = useForm<LoginFormInputs>();
  const [isLoading, setIsLoading] = useState(false);
  const setAuth = useAuthStore((state) => state.login);
  const toast = useToastStore();

  const onSubmit = async (formData: LoginFormInputs) => {
    setIsLoading(true);
    
    try {
      const response = await login(formData);
      // 'response' corresponds to the 'ApiResponse' object returned by the interceptor
      if (response && response.success && response.data) {
        const { token, role, name } = response.data;
        
        if (token && role) {
          setAuth(token, role);
          toast.success(`Welcome back, ${name || 'User'}!`);
          
          // Redirection selon le rôle
          setTimeout(() => {
            if (role === 'STUDENT') {
              navigate('/etudiant/stages');
            } else if (role === 'TEACHER') {
              navigate('/enseignant/offres');
            } else if (role === 'ENTERPRISE') {
              navigate('/entreprise/offres');
            } else if (role === 'ADMIN') {
              navigate('/admin/dashboard');
            }
          }, 500);
        }
      }
    } catch (error: any) {
      console.error('Login error:', error);
      const errorMessage = error.response?.data?.message || 'Invalid credentials';
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-900 via-neutral-900 to-cyan-900 flex items-center justify-center p-4">
      {/* Background Effects */}
      <div className="absolute inset-0 overflow-hidden">
        <motion.div
          className="absolute top-1/4 left-1/4 w-96 h-96 bg-purple-500/20 rounded-full blur-3xl"
          animate={{
            scale: [1, 1.2, 1],
            opacity: [0.3, 0.5, 0.3],
          }}
          transition={{
            duration: 8,
            repeat: Infinity,
          }}
        />
        <motion.div
          className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-cyan-500/20 rounded-full blur-3xl"
          animate={{
            scale: [1.2, 1, 1.2],
            opacity: [0.3, 0.5, 0.3],
          }}
          transition={{
            duration: 8,
            repeat: Infinity,
            delay: 1,
          }}
        />
      </div>

      {/* Login Card */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-md relative z-10"
      >
        <Card variant="glass" className="backdrop-blur-xl">
          {/* Logo */}
          <div className="flex flex-col items-center mb-8">
            <motion.img
              src={logo}
              alt="Logo"
              className="w-32 h-32 object-contain mb-4"
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ type: 'spring', duration: 0.6 }}
            />
            <h1 className="text-3xl font-bold gradient-text">
              ELITE
            </h1>
            <p className="text-neutral-400 mt-2">Internship Management System</p>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <Input
              label="Email"
              type="email"
              placeholder="your.email@example.com"
              leftIcon={<FiMail className="w-5 h-5" />}
              error={errors.email?.message}
              {...register('email', {
                required: 'Email is required',
                pattern: {
                  value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                  message: 'Invalid email address',
                },
              })}
            />

            <Input
              label="Password"
              type="password"
              placeholder="••••••••"
              leftIcon={<FiLock className="w-5 h-5" />}
              error={errors.password?.message}
              {...register('password', {
                required: 'Password is required',
                minLength: {
                  value: 6,
                  message: 'Password must be at least 6 characters',
                },
              })}
            />

            <div className="flex items-center justify-between text-sm">
              <Link
                to="/reset-password"
                className="text-purple-400 hover:text-purple-300 transition-colors"
              >
                Forgot password?
              </Link>
              <Link
                to="/register"
                className="text-cyan-400 hover:text-cyan-300 transition-colors"
              >
                Create account
              </Link>
            </div>

            <Button
              type="submit"
              variant="gradient"
              size="lg"
              fullWidth
              isLoading={isLoading}
            >
              Sign In
            </Button>
          </form>

          {/* Footer */}
          <div className="mt-8 pt-6 border-t border-white/10 text-center">
            <p className="text-sm text-neutral-400">
              Secure authentication powered by JWT
            </p>
          </div>
        </Card>
      </motion.div>
    </div>
  );
};

export default LoginPage;