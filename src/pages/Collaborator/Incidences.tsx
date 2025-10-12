import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import SideMenu from "../../components/SideMenu";

const Incidences: React.FC = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const navigate = useNavigate();

  const toggleMenu = () => setMenuOpen((v) => !v);
  const handleNavigation = (path: string) => navigate(path);

  return (
    <div className="min-h-screen bg-background font-montserrat relative">
      {/* Header */}
      <header className="relative flex items-center p-6 bg-background">
        <button
          onClick={toggleMenu}
          aria-label="Abrir menú"
          className="w-10 h-10 rounded-full bg-button hover:bg-button-hover text-white flex items-center justify-center font-bold shadow transition-colors"
        >
          ≡
        </button>
      </header>

      <main className="px-4 py-10 md:px-8 max-w-md mx-auto text-center">
        <h1 className="text-xl font-bold text-black mb-8">
          Registro de incidencias
        </h1>

        <div className="flex flex-col space-y-4">
          <button
            onClick={() => navigate("/incidences/new")}
            className="w-full py-3 bg-button hover:bg-button-hover text-white font-bold rounded-md shadow"
          >
            Reportar incidencia
          </button>

          <button
            onClick={() => navigate("/incidences/list")}
            className="w-full py-3 bg-button hover:bg-button-hover text-white font-bold rounded-md shadow"
          >
            Ver registro de incidencias
          </button>
        </div>
      </main>

      <SideMenu
        isOpen={menuOpen}
        onClose={() => setMenuOpen(false)}
        currentPage="incidences"
        onNavigate={handleNavigation}
      />
    </div>
  );
};

export default Incidences;
