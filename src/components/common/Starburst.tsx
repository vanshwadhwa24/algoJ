import React from 'react';

interface StarburstProps {
  className?: string;
  size?: number;
  color?: string;
}

export const Starburst: React.FC<StarburstProps> = ({
  className = '',
  size = 48,
  color = '#1A1A1A',
}) => {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 100 100"
      fill={color}
      className={`select-none pointer-events-none transition-transform duration-300 ${className}`}
      aria-hidden="true"
    >
      <polygon points="50,0 59,32 90,15 72,43 100,50 72,57 90,85 59,68 50,100 41,68 10,85 28,57 0,50 28,43 10,15 41,32" />
    </svg>
  );
};
