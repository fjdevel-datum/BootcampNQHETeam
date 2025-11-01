import React, { useEffect, useState } from "react";
import { ArrowLeft, AlertTriangle, Calendar, FileText, X, Eye } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { toast } from "../../components/toast";

interface Incidence {
  id: number;
  tipo: string;
  recurso: string;
  fecha: string;
  detalles: string;
}

const IncidenceList: React.FC = () => {
  const navigate = useNavigate();
  const [incidences, setIncidences] = useState<Incidence[]>([]);
  const [selectedIncidence, setSelectedIncidence] = useState<Incidence | null>(null);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    const stored = localStorage.getItem("incidences");
    if (stored) {
      const data = JSON.parse(stored);
      setIncidences(data);
      
      if (data.length > 0) {
        toast.success("Incidencias cargadas", `${data.length} registro${data.length !== 1 ? 's' : ''} encontrado${data.length !== 1 ? 's' : ''}`);
      } else {
        toast.info("Sin incidencias", "No hay incidencias registradas");
      }
    }
  }, []);

  const handleVerDetalles = (incidence: Incidence) => {
    setSelectedIncidence(incidence);
    setShowModal(true);
  };

  const handleEliminar = (id: number) => {
    const updatedIncidences = incidences.filter(inc => inc.id !== id);
    localStorage.setItem("incidences", JSON.stringify(updatedIncidences));
    setIncidences(updatedIncidences);
    toast.success("Incidencia eliminada", "El registro ha sido eliminado correctamente");
  };

  const getIconoTipo = (tipo: string) => {
    switch (tipo) {
      case "Extravio": return "🔍";
      case "Daño": return "💔";
      case "Robo": return "🚨";
      case "Uso indebido": return "⚠️";
      case "Error": return "❌";
      default: return "📋";
    }
  };

  const getColorTipo = (tipo: string) => {
    switch (tipo) {
      case "Extravio": return "bg-yellow-100 text-yellow-800 border-yellow-300";
      case "Daño": return "bg-orange-100 text-orange-800 border-orange-300";
      case "Robo": return "bg-red-100 text-red-800 border-red-300";
      case "Uso indebido": return "bg-purple-100 text-purple-800 border-purple-300";
      case "Error": return "bg-blue-100 text-blue-800 border-blue-300";
      default: return "bg-gray-100 text-gray-800 border-gray-300";
    }
  };

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
                {incidences.length} incidencia{incidences.length !== 1 ? 's' : ''} registrada{incidences.length !== 1 ? 's' : ''}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Contenido */}
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {incidences.length === 0 ? (
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
            {incidences.map((inc) => (
              <div
                key={inc.id}
                className="bg-white rounded-xl p-4 md:p-6 shadow-md border border-gray-200 hover:shadow-lg transition-all"
              >
                <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                  <div className="flex-1">
                    <div className="flex items-start gap-3 mb-3">
                      <div className="text-3xl">{getIconoTipo(inc.tipo)}</div>
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-2">
                          <h3 className="text-lg font-bold text-gray-900">{inc.tipo}</h3>
                          <span className={`px-3 py-1 rounded-full text-xs font-semibold border ${getColorTipo(inc.tipo)}`}>
                            En revisión
                          </span>
                        </div>
                        <div className="space-y-2 text-sm text-gray-600">
                          <div className="flex items-center gap-2">
                            <FileText className="w-4 h-4 text-gray-400" />
                            <span className="font-medium">Recurso:</span>
                            <span>{inc.recurso}</span>
                          </div>
                          <div className="flex items-center gap-2">
                            <Calendar className="w-4 h-4 text-gray-400" />
                            <span className="font-medium">Fecha:</span>
                            <span>{new Date(inc.fecha).toLocaleDateString('es-ES', { 
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
                    <button
                      onClick={() => {
                        if (window.confirm('¿Estás seguro de eliminar esta incidencia?')) {
                          handleEliminar(inc.id);
                        }
                      }}
                      className="flex-1 md:flex-none flex items-center justify-center gap-2 bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg text-sm font-semibold transition-colors"
                    >
                      <X className="w-4 h-4" />
                      Eliminar
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Botón para nueva incidencia */}
        {incidences.length > 0 && (
          <button
            onClick={() => navigate("/incidences/new")}
            className="w-full mt-6 bg-button hover:bg-button-hover text-white font-bold py-3 rounded-lg shadow-md transition-all transform hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-2"
          >
            <AlertTriangle className="w-5 h-5" />
            Reportar Nueva Incidencia
          </button>
        )}
      </div>

      {/* Modal de detalles */}
      {showModal && selectedIncidence && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl p-6 max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="flex items-start justify-between mb-6">
              <div className="flex items-center gap-3">
                <div className="text-4xl">{getIconoTipo(selectedIncidence.tipo)}</div>
                <div>
                  <h2 className="text-2xl font-bold text-gray-900">{selectedIncidence.tipo}</h2>
                  <span className={`inline-block mt-1 px-3 py-1 rounded-full text-xs font-semibold border ${getColorTipo(selectedIncidence.tipo)}`}>
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
                  <h3 className="font-semibold text-gray-900">Recurso Comprometido</h3>
                </div>
                <p className="text-gray-700">{selectedIncidence.recurso}</p>
              </div>

              <div className="bg-gray-50 rounded-lg p-4">
                <div className="flex items-center gap-2 mb-2">
                  <Calendar className="w-5 h-5 text-gray-600" />
                  <h3 className="font-semibold text-gray-900">Fecha de Incidencia</h3>
                </div>
                <p className="text-gray-700">
                  {new Date(selectedIncidence.fecha).toLocaleDateString('es-ES', { 
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
                  <h3 className="font-semibold text-gray-900">Detalles</h3>
                </div>
                <p className="text-gray-700 whitespace-pre-wrap">{selectedIncidence.detalles}</p>
              </div>
            </div>

            <div className="mt-6 flex gap-3">
              <button
                onClick={() => setShowModal(false)}
                className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-700 font-semibold py-3 rounded-lg transition-colors"
              >
                Cerrar
              </button>
              <button
                onClick={() => {
                  if (window.confirm('¿Estás seguro de eliminar esta incidencia?')) {
                    handleEliminar(selectedIncidence.id);
                    setShowModal(false);
                  }
                }}
                className="flex-1 bg-red-500 hover:bg-red-600 text-white font-semibold py-3 rounded-lg transition-colors"
              >
                Eliminar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default IncidenceList;