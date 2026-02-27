import { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';

export const usePageLoading = () => {
  const [isLoading, setIsLoading] = useState(false);
  const location = useLocation();

  useEffect(() => {
    // Démarrer le chargement au changement de route
    setIsLoading(true);

    // Simuler un temps de chargement minimum pour voir l'animation
    const timer = setTimeout(() => {
      setIsLoading(false);
    }, 300); // 300ms minimum pour voir le loader

    return () => clearTimeout(timer);
  }, [location.pathname]);

  return { isLoading, setIsLoading };
};
