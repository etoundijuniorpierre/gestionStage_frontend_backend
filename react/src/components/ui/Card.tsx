import { HTMLAttributes, ReactNode } from 'react';
import { motion } from 'framer-motion';

interface CardProps extends HTMLAttributes<HTMLDivElement> {
  variant?: 'default' | 'glass' | 'gradient' | 'elevated';
  hover?: boolean;
  children: ReactNode;
}

const Card = ({ 
  variant = 'default', 
  hover = false, 
  children, 
  className = '',
  ...props 
}: CardProps) => {
  const baseStyles = `
    rounded-xl p-6
    transition-all duration-300
  `;

  const variants = {
    default: `
      bg-white dark:bg-neutral-900
      border border-neutral-200 dark:border-neutral-800
      shadow-md
    `,
    glass: `
      bg-white/10 dark:bg-white/5
      backdrop-blur-lg
      border border-white/20 dark:border-white/10
      shadow-xl
    `,
    gradient: `
      bg-gradient-to-br from-purple-500/10 via-pink-500/10 to-cyan-500/10
      dark:from-purple-500/20 dark:via-pink-500/20 dark:to-cyan-500/20
      border border-purple-500/20 dark:border-purple-500/30
      shadow-lg
    `,
    elevated: `
      bg-white dark:bg-neutral-900
      shadow-2xl
      border border-neutral-100 dark:border-neutral-800
    `,
  };

  const hoverStyles = hover
    ? `
      hover:shadow-2xl hover:-translate-y-1
      hover:border-purple-500/30
      cursor-pointer
    `
    : '';

  return (
    <motion.div
      className={`
        ${baseStyles}
        ${variants[variant]}
        ${hoverStyles}
        ${className}
      `}
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      whileHover={hover ? { scale: 1.02 } : {}}
      {...props}
    >
      {children}
    </motion.div>
  );
};

export default Card;
