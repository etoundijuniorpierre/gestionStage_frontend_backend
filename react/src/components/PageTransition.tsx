import { motion, AnimatePresence } from 'framer-motion';
import { useLocation } from 'react-router-dom';
import LoadingSpinner from './LoadingSpinner';

interface PageTransitionProps {
  children: React.ReactNode;
  isLoading?: boolean;
}

const pageVariants = {
  initial: {
    opacity: 0,
    y: 20,
    scale: 0.95
  },
  in: {
    opacity: 1,
    y: 0,
    scale: 1
  },
  out: {
    opacity: 0,
    y: -20,
    scale: 1.05
  }
};

const pageTransition = {
  type: 'tween' as const,
  ease: 'anticipate' as const,
  duration: 0.4
};

export default function PageTransition({ children, isLoading = false }: PageTransitionProps) {
  const location = useLocation();

  return (
    <AnimatePresence mode="wait">
      {isLoading ? (
        <motion.div
          key="loading"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          className="flex items-center justify-center min-h-[60vh]"
        >
          <LoadingSpinner size="large" color="emeraude" />
        </motion.div>
      ) : (
        <motion.div
          key={location.pathname}
          initial="initial"
          animate="in"
          exit="out"
          variants={pageVariants}
          transition={pageTransition}
        >
          {children}
        </motion.div>
      )}
    </AnimatePresence>
  );
}
