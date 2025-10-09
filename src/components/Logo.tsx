// src/assets/Logo.tsx
import React from "react";
import logo from "../assets/logo.png";


interface LogoProps {
  width?: string;
  height?: string;
  className?: string;
}

const Logo: React.FC<LogoProps> = ({ 
  width = "w-42", 
  height = "h-42", 
  className = "" 
}) => { 
  return (
    <div className={`flex flex-col items-center ${className}`}>
      <img
        src={logo}
        alt="Datum Redsoft Logo"
        className={`${width} ${height} object-contain transition-transform duration-300 hover:scale-105`}
        onError={(e) => {
          // Fallback en caso de que la imagen no cargue
          const target = e.target as HTMLImageElement;
          target.style.display = 'none';
          const fallback = target.nextElementSibling as HTMLElement;
          if (fallback) fallback.style.display = 'block';
        }}
      />
      
      {/* Fallback text (se muestra si la imagen falla) */}
      <div className="hidden font-montserrat">
        <h1 className="text-3xl font-bold text-gray-700">
          DATUM <span className="text-button">REDSOFT</span>
        </h1>
      </div>
    </div>
  );
};

export default Logo;