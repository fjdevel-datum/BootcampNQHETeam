import React from "react";
import { useNavigate } from "react-router-dom";
import { logoutUser } from "../services/authService";
import { useAuth } from "../context/AuthContext";

interface SideMenuProps {
  isOpen: boolean;
  onClose: () => void;
  currentPage?: string;
  onNavigate?: (path: string) => void;
}

const SideMenu: React.FC<SideMenuProps> = ({ isOpen, onClose, currentPage, onNavigate }) => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const navigateTo = (path: string) => {
    if (onNavigate) {
      onNavigate(path);
    } else {
      navigate(path);
    }
    onClose(); // Close menu after navigation
  };

  const handleLogout = async () => {
   try {
      const result = await logoutUser();
      if (result.success) {
        console.log("Sesión cerrada exitosamente");
        navigate("/login");
      } else {
        console.error("Error al cerrar sesión:", result.error);
      }
    } catch (error) {
      console.error("Error inesperado al cerrar sesión:", error);
    } 
    onClose();
  };

  if (!isOpen) return null;

  return (
    <>
      <aside className="fixed top-0 left-0 w-[70%] max-w-xs h-full bg-black p-6 z-40 flex flex-col">
         <div className="mb-6 pb-4 border-b border-gray-700">
          <div className="flex items-center space-x-3">
            <div className="w-12 h-12 bg-red-600 rounded-full flex items-center justify-center">
              <span className="text-white font-bold text-lg">
                {user?.displayName?.[0]?.toUpperCase() || user?.email?.[0]?.toUpperCase() || "U"}
              </span>
            </div>
            <div className="flex-1 overflow-hidden">
              <p className="text-white font-semibold truncate">
                {user?.displayName || "Usuario"}
              </p>
              <p className="text-gray-400 text-sm truncate">
                {user?.email}
              </p>
            </div>
          </div>
        </div>
        <nav className="mt-8 space-y-3 flex-1">
          <button 
            onClick={() => navigateTo('/UserCards')}
            className={`w-full text-left py-3 px-4 text-white font-semibold rounded-md font-montserrat transition-colors ${
              currentPage === 'cards' 
                ? 'bg-red-800' 
                : 'bg-red-600 hover:bg-red-700'
            }`}
          >
            Tarjetas
          </button>
          <button 
            onClick={() => navigateTo('/activities')}
            className={`w-full text-left py-3 px-4 text-white font-semibold rounded-md font-montserrat transition-colors ${
              currentPage === 'activities' 
                ? 'bg-red-800' 
                : 'bg-red-600 hover:bg-red-700'
            }`}
          >
            Actividades
          </button>
          <button 
            onClick={() => navigateTo('/incidences')}
            className={`w-full text-left py-3 px-4 text-white font-semibold rounded-md font-montserrat transition-colors ${
              currentPage === 'incidencias' 
                ? 'bg-red-800' 
                : 'bg-red-600 hover:bg-red-700'
            }`}
          >
            Incidencias
          </button>
        </nav>
        
        {/* Cerrar sesión button separated at bottom */}
        <div className="mt-auto pt-6 border-t border-gray-700">
          <button 
            onClick={handleLogout}
            className="w-full text-left py-3 px-4 text-white font-semibold bg-red-600 hover:bg-red-700 rounded-md font-montserrat transition-colors"
          >
           <span>Cerrar sesión</span>
            <svg 
              className="w-5 h-5" 
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path 
                strokeLinecap="round" 
                strokeLinejoin="round" 
                strokeWidth={2} 
                d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" 
              />
            </svg>
          </button>
        </div>
      </aside>

      {/* Overlay para cerrar menú al click fuera */}
      <div
        onClick={onClose}
        className="fixed inset-0 bg-black/40 z-30"
      />
    </>
  );
};

export default SideMenu;