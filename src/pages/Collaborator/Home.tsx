import React, { useState } from "react";
import Logo from "../../components/Logo";
import profile from "../../assets/profile.png";
import SideMenu from "../../components/SideMenu"; 
import { useNavigate } from "react-router-dom";
const Home: React.FC = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const navigate = useNavigate();
  const toggleMenu = () => setMenuOpen((v) => !v);


  const handleNavigation = (path: string) => {
    navigate(path); 
  };

  return (
    <div className="min-h-screen bg-background font-montserrat relative">
      {/* Header */}
      <header className="relative flex items-center justify-center p-6">
        <button
          onClick={toggleMenu}
          aria-label="Abrir menú"
          className="absolute left-6 top-6 z-30 w-10 h-10 rounded-full bg-red-600 hover:bg-red-700 text-white flex items-center justify-center font-bold shadow font-montserrat transition-colors"
        >
          ≡
        </button>

        <div className="relative z-10 flex flex-col items-center px-4">
          <div className="transform transition-all duration-1000 animate-pulse">
            <Logo width="w-48" height="h-48" />
          </div>
        </div>

        <button
          aria-label="Ver perfil"
          className="absolute right-6 top-6 z-30 w-10 h-10 rounded-full bg-red-600 hover:bg-red-700 overflow-hidden shadow transition-colors flex items-center justify-center"
        >
          <img
            src={profile}
            alt="Perfil de usuario"
            className="w-8 h-8 object-cover"
          />
        </button>
      </header>

      {/* Body */}
      <main className="flex-1 flex flex-col items-center justify-center px-4">
        <h1 className="text-2xl md:text-3xl font-bold text-black font-montserrat mb-2">
          Bienvenido usuario
        </h1>
        <p className="text-3xl md:text-4xl font-bold text-gray-700 tracking-wide">
          EasyCheck<span className="text-red-600">✔</span>
        </p>
      </main>

      <SideMenu
        isOpen={menuOpen}
        onClose={() => setMenuOpen(false)}
        currentPage="home"
        onNavigate={handleNavigation}
      />
    </div>
  );
};

export default Home;
