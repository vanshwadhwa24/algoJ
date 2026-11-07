import React from 'react';

interface BrutalistCardProps {
  children: React.ReactNode;
  className?: string;
  bgColor?: 'white' | 'lavender' | 'mint' | 'yellow' | 'orange' | 'cream' | 'ink';
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
    white: 'bg-white text-[#1A1A1A]',
    lavender: 'bg-[#C9B8F5] text-[#1A1A1A]',
    mint: 'bg-[#8FD6B0] text-[#1A1A1A]',
    yellow: 'bg-[#F5D876] text-[#1A1A1A]',
    orange: 'bg-[#F5A868] text-[#1A1A1A]',
    cream: 'bg-[#FAF6EC] text-[#1A1A1A]',
    ink: 'bg-[#1A1A1A] text-white',
  }[bgColor];

  const shadowStyles = {
    none: '',
    sm: 'shadow-[2px_2px_0px_#1A1A1A]',
    md: 'shadow-[4px_4px_0px_#1A1A1A]',
    lg: 'shadow-[6px_6px_0px_#1A1A1A]',
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
      className={`border-[2.5px] border-[#1A1A1A] ${bgStyles} ${shadowStyles} ${roundedStyles} ${
        onClick ? 'cursor-pointer transition-transform hover:-translate-y-1' : ''
      } ${className}`}
    >
      {children}
    </div>
  );
};
