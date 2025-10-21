import React, { useState, useEffect } from "react";
import SideMenu from "../../components/SideMenu"; 
import { useNavigate } from "react-router-dom";
import ActivityCard from "../../components/ActivityCard";
import { fetchWithAuth, getCurrentUserData } from "../../services/authService";

interface Activity {
  actividadId: number;
  nombre: string;
  estado: string;
  empleadoId: number;
}

const Activities: React.FC = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [activities, setActivities] = useState<Activity[]>([]);
  const [search, setSearch] = useState("");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
  const [showSortOptions, setShowSortOptions] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();
  const toggleMenu = () => setMenuOpen((v) => !v);

  const handleNavigation = (path: string) => {
    navigate(path);
  };

  // 🔹 Cargar actividades desde el endpoint usando el usuario autenticado
  useEffect(() => {
    const fetchActivities = async () => {
      try {
        setLoading(true);
        setError(null);

        // Obtener datos del usuario actual
        const userData = getCurrentUserData();
        
        if (!userData || !userData.empleadoId) {
          setError("No se pudo identificar al usuario");
          setLoading(false);
          return;
        }

        console.log("📋 Cargando actividades para empleado ID:", userData.empleadoId);

        // Llamar al endpoint con autenticación
        const data: Activity[] = await fetchWithAuth(
          `http://localhost:8080/actividad/empleado/${userData.empleadoId}`
        );

        console.log("✅ Actividades obtenidas:", data);

        setActivities(data);
      } catch (err: any) {
        console.error("❌ Error al cargar actividades:", err);
        setError(err.message || "No se pueden cargar las actividades");
      } finally {
        setLoading(false);
      }
    };

    fetchActivities();
  }, []);

  const filteredAndSortedActivities = activities
    .filter((act) =>
      act.nombre.toLowerCase().includes(search.toLowerCase())
    )
    .sort((a, b) => {
      // Como no tienes fecha, ordenar por nombre o ID
      if (sortOrder === "asc") {
        return a.actividadId - b.actividadId;
      }
      return b.actividadId - a.actividadId;
    });

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

      {/* Body */}
      <main className="px-4 py-6 md:px-8 max-w-3xl mx-auto">
        {/* Barra superior */}
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-3 mb-4">
          <h1 className="text-lg md:text-xl font-bold text-black">
            Actividades
          </h1>
          <div className="flex gap-2 md:ml-auto relative">
            <button
              onClick={() => setShowSortOptions(!showSortOptions)}
              className="px-3 py-1 border border-gray-300 rounded-md text-sm bg-white hover:bg-gray-100 shadow-sm"
            >
              Ordenar {sortOrder === "asc" ? "⬆" : "⬇"}
            </button>

            {showSortOptions && (
              <div className="absolute top-10 right-0 bg-white border border-gray-300 rounded-md shadow-md z-10">
                <button
                  className="block w-full px-4 py-2 hover:bg-gray-100 text-left"
                  onClick={() => { setSortOrder("asc"); setShowSortOptions(false); }}
                >
                  Más Antiguas
                </button>
                <button
                  className="block w-full px-4 py-2 hover:bg-gray-100 text-left"
                  onClick={() => { setSortOrder("desc"); setShowSortOptions(false); }}
                >
                  Más Recientes
                </button>
              </div>
            )}
          </div>
        </div>

        {/* Input buscar */}
        <input
          type="text"
          placeholder="Buscar una Actividad"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-button bg-white shadow-sm"
        />

        {/* Estado de carga o error */}
        <div className="mt-6">
          {loading && (
            <div className="flex justify-center items-center py-8">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-button"></div>
            </div>
          )}
          {error && (
            <div className="bg-red-50 border border-red-200 rounded-lg p-4">
              <p className="text-red-700">{error}</p>
            </div>
          )}
          {!loading && !error && filteredAndSortedActivities.length === 0 && (
            <div className="text-center py-8">
              <p className="text-gray-500 text-lg">No hay actividades registradas</p>
            </div>
          )}
        </div>

        {/* Lista de actividades */}
        <div className="space-y-3 mt-4">
          {!loading && !error && filteredAndSortedActivities.map((act) => (
            <ActivityCard
              key={act.actividadId}
              title={act.nombre}
              date={act.estado} // Mostrar el estado como "fecha"
              onClick={() => navigate(`/colaborators/activities/${act.actividadId}/bills`)}
            />
          ))}
        </div>

        {/* Botones grandes */}
        {!loading && !error && (
          <div className="flex flex-col space-y-3 mt-8">
            <button
              onClick={() => navigate("/colaborators/activities/new")}
              className="w-full py-3 bg-button hover:bg-button-hover text-white font-bold rounded-md shadow transition-colors"
            >
              Nueva Actividad
            </button>

            <button 
              className="w-full py-3 bg-button hover:bg-button-hover text-white font-bold rounded-md shadow transition-colors"
              onClick={() => console.log("Generar reporte")}
            >
              Generar reporte
            </button>
          </div>
        )}
      </main>

      <SideMenu
        isOpen={menuOpen}
        onClose={() => setMenuOpen(false)}
        currentPage="activities"
        onNavigate={handleNavigation}
      />
    </div>
  );
};

export default Activities;