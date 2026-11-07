import React from 'react';

interface PillBadgeProps {
  children: React.ReactNode;
  variant?: 'mint' | 'yellow' | 'lavender' | 'orange' | 'white' | 'ink' | 'outline';
  size?: 'sm' | 'md' | 'lg';
  className?: string;
  onClick?: () => void;
  active?: boolean;
}

export const PillBadge: React.FC<PillBadgeProps> = ({
  children,
  variant = 'white',
  size = 'md',
  className = '',
  onClick,
  active = false,
}) => {
  const variantStyles = {
    mint: 'bg-[#8FD6B0] text-[#1A1A1A]',
    yellow: 'bg-[#F5D876] text-[#1A1A1A]',
    lavender: 'bg-[#C9B8F5] text-[#1A1A1A]',
    orange: 'bg-[#F5A868] text-[#1A1A1A]',
    white: 'bg-white text-[#1A1A1A]',
    ink: 'bg-[#1A1A1A] text-white',
    outline: 'bg-transparent text-[#1A1A1A]',
  }[active ? 'mint' : variant];

  const sizeStyles = {
    sm: 'text-xs px-2.5 py-1',
    md: 'text-sm px-3.5 py-1.5',
    lg: 'text-base px-5 py-2 font-bold',
  }[size];

  return (
    <span
      onClick={onClick}
      className={`inline-flex items-center gap-1.5 font-bold rounded-full border-[2px] border-[#1A1A1A] select-none transition-all ${variantStyles} ${sizeStyles} ${
        onClick
          ? 'cursor-pointer hover:-translate-y-0.5 active:translate-y-0.5 active:shadow-none shadow-[2px_2px_0px_#1A1A1A]'
          : ''
      } ${className}`}
    >
      {children}
    </span>
  );
};
