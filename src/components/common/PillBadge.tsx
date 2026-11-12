import React from 'react';

interface PillBadgeProps {
  children: React.ReactNode;
  variant?: 'sage' | 'mustard' | 'teal' | 'terra' | 'white' | 'ink' | 'outline';
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
    sage: 'bg-[#9CCBA2] text-[#2B2620]',
    mustard: 'bg-[#DFA43C] text-[#2B2620]',
    teal: 'bg-[#8FB5AD] text-[#2B2620]',
    terra: 'bg-[#D97B54] text-[#2B2620]',
    white: 'bg-white text-[#2B2620]',
    ink: 'bg-[#2B2620] text-white',
    outline: 'bg-transparent text-[#2B2620]',
  }[active ? 'sage' : variant];

  const sizeStyles = {
    sm: 'text-xs px-2.5 py-1',
    md: 'text-sm px-3.5 py-1.5',
    lg: 'text-base px-5 py-2 font-bold',
  }[size];

  return (
    <span
      onClick={onClick}
      className={`inline-flex items-center gap-1.5 font-bold rounded-full select-none transition-all ${variantStyles} ${sizeStyles} ${
        onClick
          ? 'cursor-pointer hover:-translate-y-0.5 active:translate-y-0.5 shadow-[0_3px_10px_rgba(43,38,32,0.16)]'
          : ''
      } ${className}`}
    >
      {children}
    </span>
  );
};
