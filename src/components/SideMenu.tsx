import React from "react";

interface SideMenuProps {
  isOpen: boolean;
  onClose: () => void;
  currentPage?: string;
  onNavigate?: (path: string) => void;
}

const SideMenu: React.FC<SideMenuProps> = ({ isOpen, onClose, currentPage, onNavigate }) => {
  const navigateTo = (path: string) => {
    if (onNavigate) {
      onNavigate(path);
    }
    onClose(); // Close menu after navigation
  };

  const handleLogout = () => {
    // Add your logout logic here
    console.log("Logging out...");
    onClose();
  };

  if (!isOpen) return null;

  return (
    <>
      <aside className="fixed top-0 left-0 w-[70%] max-w-xs h-full bg-black p-6 z-40 flex flex-col">
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
            Cerrar sesión
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