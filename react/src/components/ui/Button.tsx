import { ButtonHTMLAttributes, ReactNode } from 'react';
import { motion } from 'framer-motion';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  children: ReactNode;
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger' | 'success';
  size?: 'sm' | 'md' | 'lg';
  fullWidth?: boolean;
  isLoading?: boolean;
  leftIcon?: ReactNode;
  rightIcon?: ReactNode;
}

const Button = ({
  children,
  variant = 'primary',
  size = 'md',
  fullWidth = false,
  isLoading = false,
  leftIcon,
  rightIcon,
  disabled,
  className = '',
  ...props
}: ButtonProps) => {
  const baseStyles = `
    inline-flex items-center justify-center
    font-medium rounded-lg
    transition-all duration-200
    focus:outline-none focus:ring-2 focus:ring-offset-2
    disabled:opacity-50 disabled:cursor-not-allowed
    ${fullWidth ? 'w-full' : ''}
  `;

  const variants = {
    primary: `
      bg-blue-600 hover:bg-blue-700 active:bg-blue-800
      text-white
      focus:ring-blue-500
      dark:bg-blue-500 dark:hover:bg-blue-600
    `,
    secondary: `
      bg-purple-600 hover:bg-purple-700 active:bg-purple-800
      text-white
      focus:ring-purple-500
      dark:bg-purple-500 dark:hover:bg-purple-600
    `,
    outline: `
      bg-transparent hover:bg-neutral-100 active:bg-neutral-200
      text-neutral-900 dark:text-neutral-100
      border-2 border-neutral-300 dark:border-neutral-700
      focus:ring-neutral-500
      dark:hover:bg-neutral-800 dark:active:bg-neutral-700
    `,
    ghost: `
      bg-transparent hover:bg-neutral-100 active:bg-neutral-200
      text-neutral-700 dark:text-neutral-300
      focus:ring-neutral-500
      dark:hover:bg-neutral-800 dark:active:bg-neutral-700
    `,
    danger: `
      bg-red-600 hover:bg-red-700 active:bg-red-800
      text-white
      focus:ring-red-500
      dark:bg-red-500 dark:hover:bg-red-600
    `,
    success: `
      bg-green-600 hover:bg-green-700 active:bg-green-800
      text-white
      focus:ring-green-500
      dark:bg-green-500 dark:hover:bg-green-600
    `,
  };

  const sizes = {
    sm: 'px-3 py-1.5 text-sm gap-1.5',
    md: 'px-4 py-2 text-base gap-2',
    lg: 'px-6 py-3 text-lg gap-2.5',
  };

  return (
    <motion.button
      whileTap={{ scale: disabled || isLoading ? 1 : 0.98 }}
      className={`
        ${baseStyles}
        ${variants[variant]}
        ${sizes[size]}
        ${className}
      `}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading ? (
        <>
          <svg
            className="animate-spin h-5 w-5"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            />
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            />
          </svg>
          <span>Loading...</span>
        </>
      ) : (
        <>
          {leftIcon && <span className="flex-shrink-0">{leftIcon}</span>}
          <span>{children}</span>
          {rightIcon && <span className="flex-shrink-0">{rightIcon}</span>}
        </>
      )}
    </motion.button>
  );
};

export default Button;
