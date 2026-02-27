import { NavLink } from 'react-router-dom';
import { motion } from 'framer-motion';
import { usePageLoading } from '../hooks/usePageLoading';

interface AnimatedNavLinkProps {
  to: string;
  label: string;
  color: 'emeraude' | 'jaune';
  className?: string;
  onClick?: () => void;
}

const linkClass = "relative text-lg font-light font-[var(--font-family-poiret)] tracking-wider px-1 pb-1 transition-colors duration-200";

const AnimatedNavLink = ({ to, label, color, className = '', onClick }: AnimatedNavLinkProps) => {
  const { setIsLoading } = usePageLoading();

  const handleClick = () => {
    // Démarrer le chargement immédiatement au clic
    setIsLoading(true);
    if (onClick) onClick();
  };

  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        `${linkClass} ${className} ${isActive ? `text-[var(--color-${color})]` : "text-[var(--color-light)]"}`
      }
      onClick={handleClick}
    >
      {({ isActive }) => (
        <motion.span
          className="relative inline-block pb-1"
          whileHover="hover"
          initial="rest"
          animate="rest"
        >
          {label}
          <motion.span
            className="absolute left-1/2 -translate-x-1/2 bottom-0 rounded-full"
            style={{
              width: "100%",
              minWidth: 24,
              height: "2px",
              transformOrigin: "center",
              display: "block",
            }}
            variants={{
              rest: {
                scaleX: isActive ? 1 : 0,
                opacity: isActive ? 1 : 0,
                backgroundColor: isActive ? `var(--color-${color})` : `var(--color-${color})`,
              },
              hover: {
                scaleX: 1,
                opacity: 1,
                backgroundColor: "var(--color-light)",
              },
            }}
            transition={{ type: "spring", stiffness: 250, damping: 25 }}
          />
        </motion.span>
      )}
    </NavLink>
  );
};

export default AnimatedNavLink;
