import { useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { FiCheckCircle, FiXCircle, FiAlertCircle, FiInfo, FiX } from 'react-icons/fi';
import { useToastStore } from '../../store/toastStore';

const ToastContainer = () => {
  const { toasts, removeToast } = useToastStore();

  const icons = {
    success: <FiCheckCircle className="w-5 h-5" />,
    error: <FiXCircle className="w-5 h-5" />,
    warning: <FiAlertCircle className="w-5 h-5" />,
    info: <FiInfo className="w-5 h-5" />,
  };

  const colors = {
    success: 'bg-green-500 dark:bg-green-600',
    error: 'bg-red-500 dark:bg-red-600',
    warning: 'bg-yellow-500 dark:bg-yellow-600',
    info: 'bg-blue-500 dark:bg-blue-600',
  };

  return (
    <div className="fixed top-4 right-4 z-[1070] flex flex-col gap-3 max-w-md">
      <AnimatePresence>
        {toasts.map((toast) => (
          <Toast
            key={toast.id}
            id={toast.id}
            message={toast.message}
            type={toast.type}
            icon={icons[toast.type]}
            color={colors[toast.type]}
            onClose={() => removeToast(toast.id)}
          />
        ))}
      </AnimatePresence>
    </div>
  );
};

interface ToastProps {
  id: string;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
  icon: React.ReactNode;
  color: string;
  onClose: () => void;
}

const Toast = ({ id, message, type, icon, color, onClose }: ToastProps) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose();
    }, 5000);

    return () => clearTimeout(timer);
  }, [id, onClose]);

  return (
    <motion.div
      initial={{ opacity: 0, y: -20, scale: 0.95 }}
      animate={{ opacity: 1, y: 0, scale: 1 }}
      exit={{ opacity: 0, x: 100, scale: 0.95 }}
      transition={{ type: 'spring', duration: 0.3, bounce: 0.3 }}
      className={`
        ${color}
        text-white
        rounded-lg shadow-xl
        p-4 pr-12
        flex items-center gap-3
        min-w-[300px] max-w-md
        relative
        backdrop-blur-sm
      `}
    >
      <div className="flex-shrink-0">
        {icon}
      </div>
      
      <p className="flex-1 text-sm font-medium">
        {message}
      </p>
      
      <button
        onClick={onClose}
        className="
          absolute top-2 right-2
          p-1 rounded-md
          hover:bg-white/20
          transition-colors
        "
        aria-label="Close notification"
      >
        <FiX className="w-4 h-4" />
      </button>
      
      {/* Progress bar */}
      <motion.div
        initial={{ scaleX: 1 }}
        animate={{ scaleX: 0 }}
        transition={{ duration: 5, ease: 'linear' }}
        className="absolute bottom-0 left-0 right-0 h-1 bg-white/30 origin-left rounded-b-lg"
      />
    </motion.div>
  );
};

export default ToastContainer;
