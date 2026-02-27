import { useSearchParams } from 'react-router-dom';
import { useState, useEffect } from 'react';
import RegisterStep4Code from './RegisterStep4Code';
import { AnimatePresence, motion } from 'framer-motion';
import logo from '../assets/logo.png';

const VerificationPage = () => {
  const [searchParams] = useSearchParams();
  const email = searchParams.get('email') || '';
  const message = searchParams.get('message') || '';
  const [showNotification, setShowNotification] = useState(false);

  useEffect(() => {
    if (message) {
      setShowNotification(true);
      // Masquer la notification après 5 secondes
      const timer = setTimeout(() => {
        setShowNotification(false);
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [message]);

  return (
    <div className="min-h-screen flex flex-col justify-center items-center bg-login-gradient">
      {/* Notification d'erreur */}
      {showNotification && (
        <div className="fixed top-4 right-4 max-w-md bg-red-50 border border-red-200 rounded-lg p-4 shadow-lg z-50 animate-fade-in">
          <div className="flex items-start">
            <div className="flex-shrink-0">
              <svg className="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
            </div>
            <div className="ml-3">
              <h3 className="text-sm font-medium text-red-800">Compte inactif</h3>
              <div className="mt-2 text-sm text-red-700">
                <p>{decodeURIComponent(message)}</p>
              </div>
            </div>
            <div className="ml-auto pl-3">
              <button
                onClick={() => setShowNotification(false)}
                className="inline-flex text-red-400 hover:text-red-600 focus:outline-none"
              >
                <svg className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      )}
      
      <div className="flex flex-col items-center justify-center mb-2">
        <img src={logo} alt="Logo" className="max-w-[280px] w-full" />
        <p className="text-[#e1d3c1] text-center text-5xl tracking-[0.8em] ml-[35px]">ELITE</p>
      </div>
      <h1 className="text-[#b79056] mb-[-20px] mt-2 text-center mx-auto text-2xl">VÉRIFICATION</h1><br/>
      <div className="w-full max-w-[380px] border border-[3px] p-4 border-[#B79056]">
        <AnimatePresence mode="wait" initial={false}>
          <motion.div
            key="verification"
            initial={{ opacity: 0, x: 40 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -40 }}
            transition={{ duration: 0.35, ease: 'easeInOut' }}
          >
            <RegisterStep4Code email={email} />
          </motion.div>
        </AnimatePresence>
      </div>
    </div>
  );
};

export default VerificationPage;
