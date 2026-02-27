import { motion } from 'framer-motion';

interface LoadingSpinnerProps {
  size?: 'small' | 'medium' | 'large';
  color?: 'emeraude' | 'jaune' | 'light';
  className?: string;
}

const LoadingSpinner = ({ 
  size = 'medium', 
  color = 'emeraude', 
  className = '' 
}: LoadingSpinnerProps) => {
  const sizeClasses = {
    small: 'w-6 h-6',
    medium: 'w-10 h-10',
    large: 'w-16 h-16'
  };

  const colorClasses = {
    emeraude: 'border-[var(--color-emeraude)]',
    jaune: 'border-[var(--color-jaune)]',
    light: 'border-[var(--color-light)]'
  };

  return (
    <div className={`flex items-center justify-center ${className}`}>
      <motion.div
        className={`${sizeClasses[size]} ${colorClasses[color]} border-4 border-t-transparent rounded-full`}
        animate={{ rotate: 360 }}
        transition={{
          duration: 1,
          repeat: Infinity,
          ease: "linear"
        }}
      />
    </div>
  );
};

export default LoadingSpinner;
