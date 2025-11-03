import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { FileSpreadsheet, Info, ChevronLeft, CreditCard, Calendar } from "lucide-react";
import { toast } from "../../components/toast";
import { fetchWithAuth, getCurrentUserData } from "../../services/authService";

interface RecursoConFechasDTO {
  recursoId: number;
  tarjetaId: number;
  numeroTarjeta: string;
  descripcionTarjeta: string;
  tipoTarjeta: string;
  estadoRecurso: string;
  fechaInicioReporte: string;
  fechaFinalReporte: string;
  diaCorte: number | null;
  fechaAsignacion: string;
  fechaExpiracion: string;
}

const GenerateReport = () => {
  const [recursos, setRecursos] = useState<RecursoConFechasDTO[]>([]);
  const [selectedRecurso, setSelectedRecurso] = useState<RecursoConFechasDTO | null>(null);
  const [fechaInicio, setFechaInicio] = useState("");
  const [fechaFinal, setFechaFinal] = useState("");
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);

  const navigate = useNavigate();
  const apiUrl = import.meta.env.VITE_API_URL;

  const fetchRecursos = async () => {
    try {
      setLoading(true);
      const userData = getCurrentUserData();
      if (!userData?.empleadoId) {
        toast.error("Error de autenticación", "Por favor inicia sesión nuevamente");
        navigate("/login");
        return;
      }

      const data: RecursoConFechasDTO[] = await fetchWithAuth(
        `${apiUrl}/recursoAsignado/empleado/${userData.empleadoId}/para-reporte`
      );

      setRecursos(data);
    } catch (err: any) {
      toast.error("Error al cargar recursos", err.message || "No se pudieron obtener los recursos");
      setRecursos([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRecursos();
  }, []);

  useEffect(() => {
    if (!selectedRecurso) {
      setFechaInicio("");
      setFechaFinal("");
      return;
    }
    setFechaInicio(selectedRecurso.fechaInicioReporte);
    setFechaFinal(selectedRecurso.fechaFinalReporte);
  }, [selectedRecurso]);

  const handleGenerateReport = async () => {
    if (!selectedRecurso) {
      toast.error("Selecciona un recurso", "Debes elegir un recurso para generar el reporte");
      return;
    }
    if (!fechaInicio || !fechaFinal) {
      toast.error("Fechas incompletas", "Debes definir un rango de fechas");
      return;
    }

    setGenerating(true);
    try {
      const url = `${apiUrl}/gastos/reporteTarjeta/excel?tarjetaId=${selectedRecurso.tarjetaId}&fechaInicio=${fechaInicio}&fechaFinal=${fechaFinal}`;
      const token = localStorage.getItem("token");

      if (!token) {
        toast.error("Sin autenticación", "Por favor inicia sesión nuevamente");
        navigate("/login");
        return;
      }

      const response = await fetch(url, {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Accept": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        },
      });

      if (!response.ok) {
        const errorText = await response.text();
        if (response.status === 401) {
          toast.error("Sesión expirada", "Por favor inicia sesión nuevamente");
          navigate("/login");
          return;
        }
        throw new Error(`Error HTTP ${response.status}: ${errorText}`);
      }

      const blob = await response.blob();
      if (blob.size === 0) throw new Error("El archivo descargado está vacío");

      const downloadUrl = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = downloadUrl;
      link.download = `ReporteTarjeta_T${selectedRecurso.tarjetaId}_${fechaFinal}.xlsx`;
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(downloadUrl);

      toast.success("Reporte generado", "El reporte se descargó correctamente");
    } catch (err: any) {
      toast.error("Error al generar reporte", err.message || "No se pudo generar el reporte");
    } finally {
      setGenerating(false);
    }
  };

  const formatFecha = (fechaStr: string) =>
    new Date(fechaStr).toLocaleDateString("es-ES", { day: "2-digit", month: "short", year: "numeric" });

  if (loading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-button mx-auto mb-4"></div>
          <p className="text-black">Cargando recursos...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background p-6 font-montserrat">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="mb-6 flex items-center gap-4">
          <button onClick={() => navigate(-1)} className="p-2 hover:bg-red-100 rounded-full transition">
            <ChevronLeft size={24} className="text-button" />
          </button>
          <div>
            <h1 className="text-3xl font-bold text-black">Generar Reporte de Gastos</h1>
            <p className="text-gray-600 text-sm mt-1">Selecciona un recurso para generar el reporte</p>
          </div>
        </div>

        {/* Lista de recursos */}
        <div className="mb-6">
          <h2 className="text-lg font-semibold text-black mb-3 flex items-center gap-2">
            <CreditCard size={20} className="text-button" />
            Selecciona un recurso
          </h2>

          {recursos.length === 0 ? (
            <div className="bg-white p-8 rounded-lg shadow text-center">
              <CreditCard size={48} className="mx-auto text-gray-400 mb-3" />
              <p className="text-black font-medium">No tienes recursos asignados</p>
              <p className="text-gray-500 text-sm mt-2">Contacta a tu administrador</p>
            </div>
          ) : (
            <div className="space-y-3">
              {recursos.map((recurso) => (
                <div
                  key={recurso.recursoId}
                  onClick={() => setSelectedRecurso(recurso)}
                  className={`bg-white p-5 rounded-lg shadow cursor-pointer transition-all ${
                    selectedRecurso?.recursoId === recurso.recursoId
                      ? "ring-2 ring-button bg-red-50"
                      : "hover:shadow-md"
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="p-2 bg-red-100 rounded-lg">
                        <CreditCard className="text-button" size={24} />
                      </div>
                      <div>
                        <p className="font-semibold text-black">
                          {recurso.tipoTarjeta} ****{recurso.numeroTarjeta.slice(-4)}
                        </p>
                        <p className="text-sm text-gray-500">{recurso.descripcionTarjeta}</p>
                        <div className="flex gap-3 mt-2 text-xs text-gray-500">
                          {recurso.diaCorte && (
                            <span className="px-2 py-1 bg-red-100 text-button rounded">
                              Día de corte: {recurso.diaCorte}
                            </span>
                          )}
                          <span>Asignado: {formatFecha(recurso.fechaAsignacion)}</span>
                          <span>Expira: {formatFecha(recurso.fechaExpiracion)}</span>
                        </div>
                      </div>
                    </div>
                    <span className="px-3 py-1 rounded-full text-xs font-semibold bg-green-100 text-green-800">
                      {recurso.estadoRecurso}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Fechas */}
        {selectedRecurso && (
          <div className="bg-white p-5 rounded-lg shadow mb-6">
            <h2 className="text-lg font-semibold text-black mb-3 flex items-center gap-2">
              <Calendar size={20} className="text-button" />
              Período del reporte
            </h2>
            <div className="grid grid-cols-2 gap-4 mb-3">
              <div>
                <label className="block text-sm font-medium text-black mb-2">Fecha inicio</label>
                <input
                  type="date"
                  value={fechaInicio}
                  onChange={(e) => setFechaInicio(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-button"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-black mb-2">Fecha final</label>
                <input
                  type="date"
                  value={fechaFinal}
                  onChange={(e) => setFechaFinal(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-button"
                />
              </div>
            </div>
            <div className="p-3 bg-red-50 border border-red-200 rounded-md">
              <p className="text-sm text-button flex items-start gap-2">
                <Info size={16} className="mt-0.5 flex-shrink-0" />
                <span>
                  {selectedRecurso.tipoTarjeta.toLowerCase().includes("crédito")
                    ? `Las fechas se calcularon según el día de corte (${selectedRecurso.diaCorte}). Puedes ajustarlas manualmente.`
                    : "Las fechas corresponden al período de asignación. Puedes ajustarlas manualmente."}
                </span>
              </p>
            </div>
          </div>
        )}

        {/* Botón generar */}
        <button
          onClick={(e) => {
            e.preventDefault();
            handleGenerateReport();
          }}
          disabled={!selectedRecurso || generating}
          className={`w-full py-4 rounded-lg font-semibold flex items-center justify-center gap-2 transition ${
            !selectedRecurso || generating
              ? "bg-gray-300 text-gray-500 cursor-not-allowed"
              : "bg-button text-white hover:bg-button-hover shadow-lg"
          }`}
        >
          {generating ? (
            <>
              <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
              Generando reporte...
            </>
          ) : (
            <>
              <FileSpreadsheet size={20} />
              Generar Reporte Excel
            </>
          )}
        </button>
      </div>
    </div>
  );
};

export default GenerateReport;
