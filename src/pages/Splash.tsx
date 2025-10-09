// src/pages/Splash.tsx
import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Logo from "../components/Logo";

const Splash: React.FC = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const timer = setTimeout(() => {
      navigate("/Login");
    }, 2000);
    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <div className="flex flex-col justify-center items-center min-h-[70vh] bg-background font-montserrat relative overflow-hidden px-4">
      {/* Animación de fondo sutil */}
      <div className="absolute inset-0 bg-gradient-to-br from-background to-gray-100 opacity-50"></div>
      
      {/* Contenido principal */}
      <div className="relative z-10 flex flex-col items-center px-4">
        <div className="transform transition-all duration-1000 animate-pulse">
          <Logo width="w-48" height="h-48" />
        </div>

        <div className="mt-10 text-center">
          <p className="text-3xl md:text-4xl font-bold text-gray-700 tracking-wide">
            EasyCheck<span className="text-button">✔</span>
          </p>
          <p className="text-sm md:text-base text-gray-500 mt-2 opacity-75">
            Cargando...
          </p>
        </div>

        {/* Spinner */}
        <div className="mt-8 relative">
          <div className="animate-spin rounded-full h-8 w-8 border-3 border-gray-300"></div>
          <div className="animate-spin rounded-full h-8 w-8 border-3 border-transparent border-t-button absolute top-0 left-0"></div>
        </div>
      </div>

      {/* Elementos decorativos flotantes */}
      <div className="absolute top-10 left-10 w-20 h-20 bg-button opacity-5 rounded-full animate-bounce" style={{ animationDuration: '3s' }}></div>
      <div className="absolute bottom-20 right-10 w-16 h-16 bg-button opacity-10 rounded-full animate-bounce" style={{ animationDuration: '4s' }}></div>
      <div className="absolute top-1/3 right-20 w-12 h-12 bg-button opacity-8 rounded-full animate-bounce" style={{ animationDuration: '5s' }}></div>
    </div>
  );
};

export default Splash;
