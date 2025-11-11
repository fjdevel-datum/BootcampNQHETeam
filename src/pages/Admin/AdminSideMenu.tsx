import React from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "../../components/toast";
import { logoutUser } from "../../services/authService";
import { getCurrentUserData } from "../../services/authService";

interface SideMenuProps {
  isOpen: boolean;
  onClose: () => void;
  currentPage?: string;
}

const AdminSideMenu: React.FC<SideMenuProps> = ({ isOpen, onClose, currentPage }) => {
  const navigate = useNavigate();
  const userData = getCurrentUserData();

  const navigateTo = (path: string) => {
    navigate(path);
    onClose();
  };

  const handleLogout = async () => {
    const loadingToast = toast.loading("Cerrando sesión...");

    try {
      const result = await logoutUser();

      toast.dismiss(loadingToast);

      if (result.success) {
        toast.success("Sesión cerrada", "Hasta pronto");
        setTimeout(() => {
          navigate("/login");
        }, 1000);
      } else {
        toast.error("Error al cerrar sesión", result.error || "Inténtalo nuevamente");
      }
    } catch (error: any) {
      toast.dismiss(loadingToast);
      toast.error("Error inesperado", error.message || "No se pudo cerrar la sesión");
    }

    onClose();
  };

  if (!isOpen) return null;

  return (
    <>
      <aside className="fixed top-0 left-0 w-[70%] max-w-xs h-full bg-black p-6 z-40 flex flex-col">
        {/* Información del usuario */}
        <div className="mb-6 pb-4 border-b border-gray-700">
          <div className="flex items-center space-x-3">
            <div className="w-12 h-12 bg-red-600 rounded-full flex items-center justify-center">
              <span className="text-white font-bold text-lg">
                {userData?.nombres?.[0]?.toUpperCase() || "A"}
              </span>
            </div>
            <div className="flex-1 overflow-hidden">
              <p className="text-white font-semibold truncate">
                {userData?.nombres} {userData?.apellidos}
              </p>
              <p className="text-gray-400 text-sm truncate">
                {userData?.email || "Administrador"}
              </p>
              <span className="inline-block mt-1 px-2 py-0.5 text-xs bg-red-600 text-white rounded">
                {userData?.rol}
              </span>
            </div>
          </div>
        </div>

        {/* Navegación */}
        <nav className="mt-8 space-y-3 flex-1">
          <button
            onClick={() => navigateTo('/admin/see-collaborators')}
            className={`w-full text-left py-3 px-4 text-white font-semibold rounded-md font-montserrat transition-colors ${
              currentPage === 'collaborators' 
                ? 'bg-red-800' 
                : 'bg-red-600 hover:bg-red-700'
            }`}
          >
            Ver Colaboradores
          </button>
          <button
            onClick={() => navigateTo('/admin/see-incidences')}
            className={`w-full text-left py-3 px-4 text-white font-semibold rounded-md font-montserrat transition-colors ${
              currentPage === 'incidences' 
                ? 'bg-red-800' 
                : 'bg-red-600 hover:bg-red-700'
            }`}
          >
            Ver Incidencias
          </button>
        </nav>

        {/* Cerrar sesión button separated at bottom */}
        <div className="mt-auto pt-6 border-t border-gray-700">
          <button
            onClick={handleLogout}
            className="w-full text-left py-3 px-4 text-white font-semibold bg-red-600 hover:bg-red-700 rounded-md font-montserrat transition-colors flex items-center justify-between"
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
      <div onClick={onClose} className="fixed inset-0 bg-black/40 z-30" />
    </>
  );
};

export default AdminSideMenu;