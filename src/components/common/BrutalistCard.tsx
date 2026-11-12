import React from 'react';

interface BrutalistCardProps {
  children: React.ReactNode;
  className?: string;
  bgColor?: 'white' | 'teal' | 'sage' | 'mustard' | 'terra' | 'cream' | 'ink';
  shadow?: 'none' | 'sm' | 'md' | 'lg';
  rounded?: 'xl' | '2xl' | '3xl' | 'full';
  onClick?: () => void;
}

export const BrutalistCard: React.FC<BrutalistCardProps> = ({
  children,
  className = '',
  bgColor = 'white',
  shadow = 'md',
  rounded = '3xl',
  onClick,
}) => {
  const bgStyles = {
    white: 'bg-white text-[#2B2620]',
    teal: 'bg-[#8FB5AD] text-[#2B2620]',
    sage: 'bg-[#9CCBA2] text-[#2B2620]',
    mustard: 'bg-[#DFA43C] text-[#2B2620]',
    terra: 'bg-[#D97B54] text-[#2B2620]',
    cream: 'bg-[#F7EFE1] text-[#2B2620]',
    ink: 'bg-[#2B2620] text-white',
  }[bgColor];

  const shadowStyles = {
    none: '',
    sm: 'shadow-[0_3px_10px_rgba(43,38,32,0.16)]',
    md: 'shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)]',
    lg: 'shadow-[0_12px_28px_-8px_rgba(43,38,32,0.25)]',
  }[shadow];

  const roundedStyles = {
    xl: 'rounded-xl',
    '2xl': 'rounded-2xl',
    '3xl': 'rounded-[24px]',
    full: 'rounded-full',
  }[rounded];

  return (
    <div
      onClick={onClick}
      className={`${bgStyles} ${shadowStyles} ${roundedStyles} ${
        onClick ? 'cursor-pointer transition-transform hover:-translate-y-1' : ''
      } ${className}`}
    >
      {children}
    </div>
  );
};
