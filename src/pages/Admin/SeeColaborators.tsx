import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { User } from "lucide-react";
import { toast } from "../../components/toast";
import { fetchWithAuth, getCurrentUserData, logoutUser } from "../../services/authService";

const apiurl = import.meta.env.VITE_API_URL;

interface Colaborador {
  empleadoId: number;
  nombres: string;
  apellidos: string;
  documentoIdentidad: string;
  rol: string;
  empresaId: number;
}

interface SideMenuProps {
  isOpen: boolean;
  onClose: () => void;
}

const AdminSideMenu: React.FC<SideMenuProps> = ({ isOpen, onClose }) => {
  const navigate = useNavigate();
  const userData = getCurrentUserData();

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
        {/* User Info */}
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

        {/* Spacer para empujar el botón hacia abajo */}
        <div className="flex-1"></div>

        {/* Logout Button */}
        <div className="mt-auto pt-6 border-t border-gray-700">
          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-between py-3 px-4 text-white font-semibold bg-red-600 hover:bg-red-700 rounded-md font-montserrat transition-colors"
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

      {/* Overlay */}
      <div onClick={onClose} className="fixed inset-0 bg-black/40 z-30" />
    </>
  );
};

const SeeCollaborators: React.FC = () => {
  const navigate = useNavigate();
  const [colaboradores, setColaboradores] = useState<Colaborador[]>([]);
  const [busqueda, setBusqueda] = useState("");
  const [loading, setLoading] = useState(true);
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    const fetchColaboradores = async () => {
      try {
        setLoading(true);

        // Obtener datos del usuario actual
        const userData = getCurrentUserData();

        if (!userData || !userData.empresaId) {
          toast.error("Error de autenticación", "No se pudo identificar la empresa del usuario");
          setLoading(false);
          return;
        }

        console.log("Cargando empleados:", userData.empresaId);

        // Llamar al endpoint con autenticación
        const data: Colaborador[] = await fetchWithAuth(
          `${apiurl}/empleado/empresa/${userData.empresaId}`
        );

        console.log("Empleados obtenidos:", data);
        setColaboradores(data);
        
        if (data.length > 0) {
        }
      } catch (err: any) {
        console.error("Error al cargar colaboradores:", err);
        toast.error("Error al cargar", err.message || "No se pudieron cargar los colaboradores");
      } finally {
        setLoading(false);
      }
    };

    fetchColaboradores();
  }, []);

  const filtrados = colaboradores.filter((c) =>
    `${c.nombres} ${c.apellidos}`.toLowerCase().includes(busqueda.toLowerCase())
  );

  const toggleMenu = () => setMenuOpen((prev) => !prev);

  return (
    <div className="min-h-screen bg-background font-montserrat relative">
      {/* Header con botón de menú */}
      <header className="relative flex items-center p-6 bg-background">
        <button
          onClick={toggleMenu}
          aria-label="Abrir menú"
          className="w-10 h-10 rounded-full bg-button hover:bg-button-hover text-white flex items-center justify-center font-bold shadow transition-colors"
        >
          ≡
        </button>
        <h1 className="ml-4 text-xl font-semibold text-black">Colaboradores</h1>
      </header>

      {/* Main Content */}
      <main className="p-6">
        {/* Buscador */}
        <input
          type="text"
          placeholder="Buscar colaborador..."
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
          className="w-full p-3 border border-gray-300 rounded-lg mb-6 focus:ring-2 focus:ring-button focus:border-button outline-none transition-all"
        />

        {/* Estado de carga */}
        {loading && (
          <div className="flex justify-center items-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-button"></div>
          </div>
        )}

        {/* Lista de colaboradores */}
        {!loading && (
          <>
            {filtrados.length === 0 ? (
              <div className="text-center py-12">
                <User size={48} className="mx-auto text-gray-400 mb-3" />
                <p className="text-gray-500 text-lg">
                  {busqueda 
                    ? "No se encontraron colaboradores con ese nombre" 
                    : colaboradores.length === 0 
                      ? "No hay colaboradores registrados aún"
                      : "No se encontraron colaboradores"}
                </p>
                {busqueda && (
                  <p className="text-gray-400 text-sm mt-2">
                    Intenta con otro término de búsqueda
                  </p>
                )}
              </div>
            ) : (
              <div className="space-y-3">
                {filtrados.map((colab) => (
                  <button
                    key={colab.empleadoId}
                    onClick={() => navigate(`/admin/info-colaborators/${colab.empleadoId}`, {
                      state: { fromSee: true }
                    })}
                    className="bg-activity text-white flex items-center justify-between p-4 rounded-lg w-full hover:opacity-90 transition-opacity shadow-md"
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 bg-white rounded-full flex items-center justify-center">
                        <User size={20} className="text-activity" />
                      </div>
                      <div className="text-left">
                        <p className="font-semibold">
                          {colab.nombres} {colab.apellidos}
                        </p>
                        <p className="text-sm opacity-80">{colab.rol}</p>
                      </div>
                    </div>
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
                        d="M9 5l7 7-7 7"
                      />
                    </svg>
                  </button>
                ))}
              </div>
            )}

            {/* Contador de resultados */}
            {filtrados.length > 0 && (
              <p className="text-gray-600 text-sm mt-4 text-center">
                Mostrando {filtrados.length} de {colaboradores.length} colaborador
                {colaboradores.length !== 1 ? "es" : ""}
              </p>
            )}
          </>
        )}

        {/* Botón agregar colaborador */}
        {!loading && (
          <button
            onClick={() => navigate("/admin/add-colaborator")}
            className="mt-8 w-full bg-button text-white font-semibold py-3 rounded-lg hover:bg-button-hover transition-all transform hover:scale-[1.02] active:scale-[0.98] shadow-md"
          >
            Agregar Colaborador
          </button>
        )}
      </main>

      {/* Sidebar */}
      <AdminSideMenu isOpen={menuOpen} onClose={() => setMenuOpen(false)} />
    </div>
  );
};

export default SeeCollaborators;