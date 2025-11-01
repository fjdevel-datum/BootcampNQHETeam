import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { AlertTriangle, FileText, TrendingUp } from "lucide-react";
import { toast } from "../../components/toast";
import SideMenu from "../../components/SideMenu";

const Incidences: React.FC = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [totalIncidences, setTotalIncidences] = useState(0);
  const navigate = useNavigate();

  const toggleMenu = () => setMenuOpen((v) => !v);
  const handleNavigation = (path: string) => navigate(path);

  useEffect(() => {
    const stored = localStorage.getItem("incidences");
    if (stored) {
      const incidences = JSON.parse(stored);
      setTotalIncidences(incidences.length);
      
      if (incidences.length > 0) {
        toast.info("Incidencias pendientes", `Tienes ${incidences.length} incidencia${incidences.length !== 1 ? 's' : ''} registrada${incidences.length !== 1 ? 's' : ''}`);
      }
    }
  }, []);

  return (
    <div className="min-h-screen bg-background font-montserrat relative">
      {/* Header */}
      <header className="bg-white shadow-sm">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <button
                onClick={toggleMenu}
                aria-label="Abrir menú"
                className="w-10 h-10 rounded-full bg-button hover:bg-button-hover text-white flex items-center justify-center font-bold shadow transition-colors"
              >
                ≡
              </button>
              <div>
                <h1 className="text-xl font-bold text-black">Incidencias</h1>
                <p className="text-sm text-gray-500">Gestiona tus reportes</p>
              </div>
            </div>

            {totalIncidences > 0 && (
              <div className="hidden sm:flex items-center gap-2 px-3 py-2 bg-red-50 rounded-lg">
                <AlertTriangle className="w-4 h-4 text-red-600" />
                <span className="text-sm font-semibold text-red-600">{totalIncidences}</span>
              </div>
            )}
          </div>
        </div>
      </header>

      <main className="px-4 py-8 md:px-8 max-w-4xl mx-auto">
        {/* Estadísticas */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
          <div className="bg-white rounded-xl p-4 shadow-md border-l-4 border-red-500">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-500 mb-1">Total Reportadas</p>
                <p className="text-2xl font-bold text-gray-900">{totalIncidences}</p>
              </div>
              <AlertTriangle className="w-8 h-8 text-red-500" />
            </div>
          </div>

          <div className="bg-white rounded-xl p-4 shadow-md border-l-4 border-yellow-500">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-500 mb-1">En Revisión</p>
                <p className="text-2xl font-bold text-gray-900">{totalIncidences}</p>
              </div>
              <TrendingUp className="w-8 h-8 text-yellow-500" />
            </div>
          </div>

          <div className="bg-white rounded-xl p-4 shadow-md border-l-4 border-green-500">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-500 mb-1">Resueltas</p>
                <p className="text-2xl font-bold text-gray-900">0</p>
              </div>
              <FileText className="w-8 h-8 text-green-500" />
            </div>
          </div>
        </div>

        {/* Botones de acción principales */}
        <div className="space-y-4">
          <div className="bg-gradient-to-r from-red-500 to-red-600 rounded-2xl p-6 shadow-xl text-white">
            <div className="flex items-start justify-between mb-4">
              <div className="flex-1">
                <h2 className="text-xl font-bold mb-2">Reportar Incidencia</h2>
                <p className="text-red-100 text-sm">
                  ¿Tienes un problema con algún recurso? Repórtalo aquí para recibir asistencia inmediata.
                </p>
              </div>
              <AlertTriangle className="w-12 h-12 opacity-50" />
            </div>
            <button
              onClick={() => navigate("/incidences/new")}
              className="w-full bg-white hover:bg-gray-100 text-red-600 font-bold py-3 rounded-lg shadow-md transition-all transform hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-2"
            >
              <AlertTriangle className="w-5 h-5" />
              Nueva Incidencia
            </button>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-md border-2 border-gray-200 hover:border-button transition-all">
            <div className="flex items-start justify-between mb-4">
              <div className="flex-1">
                <h2 className="text-xl font-bold text-gray-900 mb-2">Ver Registro</h2>
                <p className="text-gray-600 text-sm">
                  Consulta el historial completo de tus incidencias reportadas y su estado actual.
                </p>
              </div>
              <FileText className="w-10 h-10 text-gray-400" />
            </div>
            <button
              onClick={() => navigate("/incidences/list")}
              className="w-full bg-button hover:bg-button-hover text-white font-bold py-3 rounded-lg shadow-md transition-all transform hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-2"
            >
              <FileText className="w-5 h-5" />
              Ver Historial ({totalIncidences})
            </button>
          </div>
        </div>

        {/* Información adicional */}
        <div className="mt-8 bg-blue-50 border-l-4 border-blue-500 p-4 rounded-lg">
          <div className="flex items-start gap-3">
            <AlertTriangle className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
            <div>
              <h4 className="font-semibold text-blue-900 mb-2">💡 ¿Cuándo reportar una incidencia?</h4>
              <ul className="text-sm text-blue-700 space-y-1">
                <li>• Extravío o pérdida de tarjetas corporativas</li>
                <li>• Daños físicos en recursos asignados</li>
                <li>• Transacciones no autorizadas o sospechosas</li>
                <li>• Errores en asignaciones o montos</li>
                <li>• Cualquier anomalía que requiera atención</li>
              </ul>
            </div>
          </div>
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