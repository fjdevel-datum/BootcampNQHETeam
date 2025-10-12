import React, { useState, useEffect } from "react";
import SideMenu from "../../components/SideMenu"; 
import { useNavigate } from "react-router-dom";
import ActivityCard from "../../components/ActivityCard";

interface Activity {
  id: number;
  title: string;
  date: string;
}

const Activities: React.FC = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [activities, setActivities] = useState<Activity[]>([]);
  const [search, setSearch] = useState("");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
  const [showSortOptions, setShowSortOptions] = useState(false);

  const navigate = useNavigate();
  const toggleMenu = () => setMenuOpen((v) => !v);

  const handleNavigation = (path: string) => {
    navigate(path);
  };

  useEffect(() => {
    const mockData: Activity[] = [
      { id: 1, title: "Reunión de negocios", date: "2025-01-18" },
      { id: 2, title: "Viaje a Guatemala", date: "2025-01-22" },
      { id: 3, title: "Cena con Cliente", date: "2024-12-30" },
    ];
    setActivities(mockData);
  }, []);

  const filteredAndSortedActivities = activities
    .filter((act) =>
      act.title.toLowerCase().includes(search.toLowerCase())
    )
    .sort((a, b) => {
      if (sortOrder === "asc") return a.date.localeCompare(b.date);
      return b.date.localeCompare(a.date);
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
                  Antiguo → Reciente
                </button>
                <button
                  className="block w-full px-4 py-2 hover:bg-gray-100 text-left"
                  onClick={() => { setSortOrder("desc"); setShowSortOptions(false); }}
                >
                  Reciente → Antiguo
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

        {/* Lista de actividades */}
        <div className="space-y-3 mt-6">
          {filteredAndSortedActivities.length === 0 && (
            <p className="text-gray-500">No se encontraron actividades.</p>
          )}
          {filteredAndSortedActivities.map((act) => (
            <ActivityCard
              key={act.id}
              title={act.title}
              date={act.date}
              // 🔹 Navega a la pantalla de gastos
              onClick={() => navigate(`/activities/${act.id}/bills`)}
            />
          ))}
        </div>

        {/* Botones grandes */}
        <div className="flex flex-col space-y-3 mt-8">
          <button
            onClick={() => navigate("/activities/new")}
            className="w-full py-3 bg-button hover:bg-button-hover text-white font-bold rounded-md shadow">
            Nueva Actividad
          </button>

          <button className="w-full py-3 bg-button hover:bg-button-hover text-white font-bold rounded-md shadow">
            Generar reporte
          </button>
        </div>
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
