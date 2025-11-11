import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { User } from "lucide-react";
import { toast } from "../../components/toast";
import { fetchWithAuth, getCurrentUserData } from "../../services/authService";
import AdminSideMenu from "./AdminSideMenu";

const apiurl = import.meta.env.VITE_API_URL;

interface Colaborador {
  empleadoId: number;
  nombres: string;
  apellidos: string;
  documentoIdentidad: string;
  rol: string;
  empresaId: number;
}

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
        const userData = getCurrentUserData();

        if (!userData?.empresaId) {
          toast.error("Error de autenticación", "No se pudo identificar la empresa del usuario");
          setLoading(false);
          return;
        }

        const data: Colaborador[] = await fetchWithAuth(`${apiurl}/empleado/empresa/${userData.empresaId}`);
        setColaboradores(data);
      } catch (err: any) {
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

  return (
    <div className="min-h-screen bg-background font-montserrat relative">
      {/* Header */}
      <header className="relative flex items-center p-6 bg-background">
        <button
          onClick={() => setMenuOpen(true)}
          aria-label="Abrir menú"
          className="w-10 h-10 rounded-full bg-button hover:bg-button-hover text-white flex items-center justify-center font-bold shadow transition-colors"
        >
          ≡
        </button>
        <h1 className="ml-4 text-xl font-semibold text-black">Colaboradores</h1>
      </header>

      <main className="p-6">
        <input
          type="text"
          placeholder="Buscar colaborador..."
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
          className="w-full p-3 border border-gray-300 rounded-lg mb-6 focus:ring-2 focus:ring-button focus:border-button outline-none transition-all"
        />

        {loading ? (
          <div className="flex justify-center items-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-button"></div>
          </div>
        ) : filtrados.length === 0 ? (
          <div className="text-center py-12">
            <User size={48} className="mx-auto text-gray-400 mb-3" />
            <p className="text-gray-500 text-lg">
              {busqueda
                ? "No se encontraron colaboradores con ese nombre"
                : "No hay colaboradores registrados aún"}
            </p>
          </div>
        ) : (
          <div className="space-y-3">
            {filtrados.map((colab) => (
              <button
                key={colab.empleadoId}
                onClick={() =>
                  navigate(`/admin/info-colaborators/${colab.empleadoId}`, { state: { fromSee: true } })
                }
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
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                </svg>
              </button>
            ))}
          </div>
        )}

        {filtrados.length > 0 && (
          <p className="text-gray-600 text-sm mt-4 text-center">
            Mostrando {filtrados.length} de {colaboradores.length} colaborador
            {colaboradores.length !== 1 ? "es" : ""}
          </p>
        )}

        {!loading && (
          <button
            onClick={() => navigate("/admin/add-colaborator")}
            className="mt-8 w-full bg-button text-white font-semibold py-3 rounded-lg hover:bg-button-hover transition-all transform hover:scale-[1.02] active:scale-[0.98] shadow-md"
          >
            Agregar Colaborador
          </button>
        )}
      </main>

      <AdminSideMenu isOpen={menuOpen} onClose={() => setMenuOpen(false)} />
    </div>
  );
};

export default SeeCollaborators;
