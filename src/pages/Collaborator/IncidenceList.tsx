import React, { useEffect, useState } from "react";
import { 
  ArrowLeft, 
  AlertTriangle, 
  Calendar, 
  FileText, 
  X, 
  Eye,
  Search,
  HeartCrack,
  ShieldAlert,
  TriangleAlert,
  Bug,
  ClipboardList
} from "lucide-react";
import { useNavigate } from "react-router-dom";
import { toast } from "../../components/toast";
import { fetchWithAuth, getCurrentUserData } from "../../services/authService";

interface Incidencia {
  incidenciaId: number;
  empleadoId: number;
  nombreEmpleado: string;
  tipoIncidenciaId: number;
  tipoIncidenciaNombre: string;
  recursoId: number;
  numeroTarjeta: string;
  fechaIncidencia: string;
  descripcion: string;
}

const IncidenceList: React.FC = () => {
  const navigate = useNavigate();
  const [incidencias, setIncidencias] = useState<Incidencia[]>([]);
  const [selectedIncidencia, setSelectedIncidencia] = useState<Incidencia | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchIncidencias();
  }, []);

  const fetchIncidencias = async () => {
    try {
      const apiurl = import.meta.env.VITE_API_URL;
      const userData = getCurrentUserData();
      
      if (!userData || !userData.empleadoId) {
        toast.error("Error de autenticación", "No se pudo identificar al usuario");
        navigate("/colaborators/user-cards");
        return;
      }

      const response: Incidencia[] = await fetchWithAuth(
        `${apiurl}/incidencia/empleado/${userData.empleadoId}`
      );
      
      setIncidencias(response);
      
      if (response.length > 0) {
        toast.success("Incidencias cargadas", `${response.length} registro${response.length !== 1 ? 's' : ''} encontrado${response.length !== 1 ? 's' : ''}`);
      } else {
        toast.info("Sin incidencias", "No hay incidencias registradas");
      }
    } catch (error: any) {
      console.error(error);
      toast.error("Error al cargar", error.message || "No se pudieron cargar las incidencias");
    } finally {
      setLoading(false);
    }
  };

  const handleVerDetalles = (incidencia: Incidencia) => {
    setSelectedIncidencia(incidencia);
    setShowModal(true);
  };


  const getIconoTipo = (tipoNombre: string) => {
    const tipo = tipoNombre?.toLowerCase() || "";
    if (tipo.includes("extravio") || tipo.includes("pérdida") || tipo.includes("perdida")) {
      return <Search className="w-8 h-8 text-yellow-600" />;
    }
    if (tipo.includes("daño") || tipo.includes("dano") || tipo.includes("deterioro")) {
      return <HeartCrack className="w-8 h-8 text-orange-600" />;
    }
    if (tipo.includes("robo") || tipo.includes("sustracción") || tipo.includes("sustraccion")) {
      return <ShieldAlert className="w-8 h-8 text-red-600" />;
    }
    if (tipo.includes("uso indebido") || tipo.includes("inadecuado") || tipo.includes("no autorizado")) {
      return <TriangleAlert className="w-8 h-8 text-purple-600" />;
    }
    if (tipo.includes("error") || tipo.includes("técnico") || tipo.includes("sistema")) {
      return <Bug className="w-8 h-8 text-blue-600" />;
    }
    return <ClipboardList className="w-8 h-8 text-gray-600" />;
  };

  const getColorTipo = (tipoNombre: string) => {
    const tipo = tipoNombre?.toLowerCase() || "";
    if (tipo.includes("extravio") || tipo.includes("pérdida") || tipo.includes("perdida")) {
      return "bg-yellow-100 text-yellow-800 border-yellow-300";
    }
    if (tipo.includes("daño") || tipo.includes("dano") || tipo.includes("deterioro")) {
      return "bg-orange-100 text-orange-800 border-orange-300";
    }
    if (tipo.includes("robo") || tipo.includes("sustracción") || tipo.includes("sustraccion")) {
      return "bg-red-100 text-red-800 border-red-300";
    }
    if (tipo.includes("uso indebido") || tipo.includes("inadecuado") || tipo.includes("no autorizado")) {
      return "bg-purple-100 text-purple-800 border-purple-300";
    }
    if (tipo.includes("error") || tipo.includes("técnico") || tipo.includes("sistema")) {
      return "bg-blue-100 text-blue-800 border-blue-300";
    }
    return "bg-gray-100 text-gray-800 border-gray-300";
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center h-screen bg-background font-montserrat">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-button mb-4"></div>
        <p className="text-lg animate-pulse text-black font-semibold">Cargando incidencias...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background font-montserrat relative">
      {/* Header */}
      <div className="bg-white shadow-sm">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <button
            onClick={() => navigate(-1)}
            className="flex items-center gap-2 text-gray-700 hover:text-button transition-colors font-medium mb-4"
          >
            <ArrowLeft className="w-5 h-5" />
            Volver
          </button>
          <div className="flex items-center gap-3">
            <div className="p-3 bg-button rounded-xl">
              <FileText className="w-8 h-8 text-white" />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Historial de Incidencias</h1>
              <p className="text-sm text-gray-500">
                {incidencias.length} incidencia{incidencias.length !== 1 ? 's' : ''} registrada{incidencias.length !== 1 ? 's' : ''}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Contenido */}
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {incidencias.length === 0 ? (
          <div className="text-center py-20">
            <div className="inline-flex items-center justify-center w-20 h-20 bg-gray-100 rounded-full mb-6">
              <AlertTriangle className="w-10 h-10 text-gray-400" />
            </div>
            <h2 className="text-2xl font-bold text-gray-800 mb-2">
              No hay incidencias registradas
            </h2>
            <p className="text-gray-500 mb-8 max-w-md mx-auto">
              Cuando reportes alguna incidencia, aparecerá aquí para que puedas hacer seguimiento
            </p>
            <button
              onClick={() => navigate("/incidences/new")}
              className="px-6 py-3 bg-button hover:bg-button-hover text-white font-semibold rounded-lg transition-colors shadow-md"
            >
              Reportar Primera Incidencia
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            {incidencias.map((inc) => (
              <div
                key={inc.incidenciaId}
                className="bg-white rounded-xl p-4 md:p-6 shadow-md border border-gray-200 hover:shadow-lg transition-all"
              >
                <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                  <div className="flex-1">
                    <div className="flex items-start gap-3 mb-3">
                      <div>{getIconoTipo(inc.tipoIncidenciaNombre)}</div>
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-2">
                          <h3 className="text-lg font-bold text-gray-900">{inc.tipoIncidenciaNombre}</h3>
                          <span className={`px-3 py-1 rounded-full text-xs font-semibold border ${getColorTipo(inc.tipoIncidenciaNombre)}`}>
                            Registrada
                          </span>
                        </div>
                        <div className="space-y-2 text-sm text-gray-600">
                          <div className="flex items-center gap-2">
                            <FileText className="w-4 h-4 text-gray-400" />
                            <span className="font-medium">Tarjeta:</span>
                            <span>{inc.numeroTarjeta}</span>
                          </div>
                          <div className="flex items-center gap-2">
                            <Calendar className="w-4 h-4 text-gray-400" />
                            <span className="font-medium">Fecha:</span>
                            <span>{new Date(inc.fechaIncidencia).toLocaleDateString('es-ES', { 
                              day: '2-digit', 
                              month: 'long', 
                              year: 'numeric' 
                            })}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="flex flex-row md:flex-col gap-2">
                    <button
                      onClick={() => handleVerDetalles(inc)}
                      className="flex-1 md:flex-none flex items-center justify-center gap-2 bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-semibold transition-colors"
                    >
                      <Eye className="w-4 h-4" />
                      Ver
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {incidencias.length > 0 && (
          <button
            onClick={() => navigate("/incidences/new")}
            className="w-full mt-6 bg-button hover:bg-button-hover text-white font-bold py-3 rounded-lg shadow-md transition-all transform hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-2"
          >
            <AlertTriangle className="w-5 h-5" />
            Reportar Nueva Incidencia
          </button>
        )}
      </div>

      {/* Modal */}
      {showModal && selectedIncidencia && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl p-6 max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="flex items-start justify-between mb-6">
              <div className="flex items-center gap-3">
                {getIconoTipo(selectedIncidencia.tipoIncidenciaNombre)}
                <div>
                  <h2 className="text-2xl font-bold text-gray-900">{selectedIncidencia.tipoIncidenciaNombre}</h2>
                  <span className={`inline-block mt-1 px-3 py-1 rounded-full text-xs font-semibold border ${getColorTipo(selectedIncidencia.tipoIncidenciaNombre)}`}>
                    En revisión
                  </span>
                </div>
              </div>
              <button
                onClick={() => setShowModal(false)}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <X className="w-6 h-6 text-gray-500" />
              </button>
            </div>

            <div className="space-y-4">
              <div className="bg-gray-50 rounded-lg p-4">
                <div className="flex items-center gap-2 mb-2">
                  <FileText className="w-5 h-5 text-gray-600" />
                  <h3 className="font-semibold text-gray-900">Tarjeta Comprometida</h3>
                </div>
                <p className="text-gray-700">{selectedIncidencia.numeroTarjeta}</p>
              </div>

              <div className="bg-gray-50 rounded-lg p-4">
                <div className="flex items-center gap-2 mb-2">
                  <Calendar className="w-5 h-5 text-gray-600" />
                  <h3 className="font-semibold text-gray-900">Fecha de Incidencia</h3>
                </div>
                <p className="text-gray-700">
                  {new Date(selectedIncidencia.fechaIncidencia).toLocaleDateString('es-ES', { 
                    weekday: 'long',
                    day: '2-digit', 
                    month: 'long', 
                    year: 'numeric' 
                  })}
                </p>
              </div>

              <div className="bg-gray-50 rounded-lg p-4">
                <div className="flex items-center gap-2 mb-2">
                  <AlertTriangle className="w-5 h-5 text-gray-600" />
                  <h3 className="font-semibold text-gray-900">Descripción</h3>
                </div>
                <p className="text-gray-700 whitespace-pre-wrap">{selectedIncidencia.descripcion}</p>
              </div>
            </div>

            <div className="mt-6 flex gap-3">
              <button
                onClick={() => setShowModal(false)}
                className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-700 font-semibold py-3 rounded-lg transition-colors"
              >
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default IncidenceList;