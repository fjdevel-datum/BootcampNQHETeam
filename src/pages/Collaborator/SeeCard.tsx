import React, { useEffect, useState } from "react";
import { ArrowLeft, CreditCard, Calendar, DollarSign, TrendingUp, AlertTriangle } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "../../components/toast";
import { fetchWithAuth, getCurrentUserData } from "../../services/authService";

interface InformacionRecurso {
  empleadoId: number;
  nombreEmpleado: string;
  numeroTarjeta: string;
  fechaAsignada: string;
  estadoRecurso: string;
  montoMaximo: number;
  montoActual: number;
  porcentaje: number;
  resto: number;
  diaCorte?: number;           
  fechaExpiracion?: string;    
  tipoTarjeta?: string;        
}

export default function SeeCard() {
  const { cardId } = useParams<{ cardId: string }>();
  const [data, setData] = useState<InformacionRecurso | null>(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const apiurl = import.meta.env.VITE_API_URL;
        const userData = getCurrentUserData();
        if (!userData || !userData.empleadoId) {
          toast.error("Error de autenticación", "No se pudo identificar al usuario");
          setLoading(false);
          navigate("/colaborators/user-cards");
          return;
        }
        if (!cardId) {
          toast.error("Error", "No se especificó la tarjeta");
          setLoading(false);
          navigate("/colaborators/user-cards");
          return;
        }
        const json: InformacionRecurso = await fetchWithAuth(
          `${apiurl}/InformacionRecurso/empleado/${userData.empleadoId}/tarjeta/${cardId}`
        );
        setData(json);
        toast.success("Información cargada", "Datos obtenidos correctamente");
      } catch (error: any) {
        console.error(error);
        toast.error("Error al cargar", error.message || "No se pudo cargar la información del recurso");
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [cardId, navigate]);

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center h-screen bg-background font-montserrat">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-button mb-4"></div>
        <p className="text-lg animate-pulse text-black font-semibold">Cargando información...</p>
        <p className="text-sm text-gray-500 mt-2">Obteniendo detalles de tu tarjeta</p>
      </div>
    );
  }

  if (!data) {
    return (
      <div className="flex flex-col items-center justify-center h-screen bg-background font-montserrat">
        <AlertTriangle className="w-16 h-16 text-yellow-500 mb-4" />
        <p className="text-xl font-semibold mb-2 text-black">No se encontró información</p>
        <p className="text-sm text-gray-500 mb-6">No hay datos disponibles para esta tarjeta</p>
        <button
          onClick={() => navigate(-1)}
          className="px-6 py-3 bg-button hover:bg-button-hover rounded-lg text-white font-semibold transition-colors flex items-center gap-2 shadow-lg"
        >
          <ArrowLeft className="w-5 h-5" /> Volver
        </button>
      </div>
    );
  }

  const isCardActive = () => {
    if (!data.estadoRecurso) return false;
    const estadoLower = data.estadoRecurso.toLowerCase();
    return estadoLower === "activo" || estadoLower === "activa";
  };

  const getProgressBarGradient = () => {
    if (data.porcentaje < 50) return "from-green-400 to-green-600";
    if (data.porcentaje < 80) return "from-yellow-400 to-yellow-600";
    return "from-button to-red-600";
  };

  return (
    <div className="min-h-screen bg-background font-montserrat flex flex-col items-center justify-center p-4 md:p-6">
      <div className="w-full max-w-2xl bg-white rounded-2xl shadow-2xl border border-gray-200 overflow-hidden">
        {/* Header */}
        <div className="bg-gradient-to-r from-button to-red-600 p-6 flex items-center gap-4">
          <button
            onClick={() => navigate(-1)}
            className="text-white hover:bg-white/20 p-2 rounded-lg transition-colors"
            aria-label="Volver"
          >
            <ArrowLeft className="w-6 h-6" />
          </button>
          <div className="flex-1">
            <h1 className="text-2xl font-bold text-white">Detalle de Tarjeta</h1>
            <p className="text-sm text-white/90 mt-1">Información y estado del recurso</p>
          </div>
          <CreditCard className="w-10 h-10 text-white/80" />
        </div>

        {/* Contenido principal */}
        <div className="p-6 space-y-6">
          {/* Información del empleado */}
          <div className="bg-gray-50 rounded-xl p-5 border border-gray-200">
            <h2 className="text-lg font-semibold text-black mb-4 flex items-center gap-2">
              <div className="w-2 h-2 bg-button rounded-full"></div>
              Información del Titular
            </h2>
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-gray-600 text-sm">Empleado:</span>
                <span className="font-semibold text-black">{data.nombreEmpleado}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-600 text-sm">Número de tarjeta:</span>
                <span className="font-semibold flex items-center text-button">
                  <CreditCard className="w-4 h-4 mr-2" /> {data.numeroTarjeta}
                </span>
              </div>
            </div>
          </div>

          {/* Estado y fecha */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="bg-gray-50 rounded-xl p-5 border border-gray-200">
              <div className="flex items-center justify-between">
                <span className="text-gray-600 text-sm">Estado:</span>
                <span
                  className={`font-semibold px-3 py-1 rounded-full text-sm ${
                    isCardActive()
                      ? "bg-green-100 text-green-700"
                      : "bg-red-100 text-button"
                  }`}
                >
                  {data.estadoRecurso || "Sin estado"}
                </span>
              </div>
            </div>

            <div className="bg-gray-50 rounded-xl p-5 border border-gray-200">
              <div className="flex items-center justify-between">
                <span className="text-gray-600 text-sm">Fecha de asignación:</span>
                <span className="flex items-center font-semibold text-button">
                  <Calendar className="w-4 h-4 mr-2" /> {data.fechaAsignada}
                </span>
              </div>
            </div>
          </div>

          {/* NUEVA SECCIÓN: Información adicional de la tarjeta */}
          {(data.fechaExpiracion || (data.tipoTarjeta === 'Tarjeta de Crédito' && data.diaCorte)) && (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {data.fechaExpiracion && (
                <div className="bg-gray-50 rounded-xl p-5 border border-gray-200">
                  <div className="flex items-center justify-between">
                    <span className="text-gray-600 text-sm">Fecha de expiración:</span>
                    <span className="flex items-center font-semibold text-purple-600">
                      <Calendar className="w-4 h-4 mr-2" /> {data.fechaExpiracion}
                    </span>
                  </div>
                </div>
              )}

              {data.tipoTarjeta === 'Tarjeta de Crédito' && data.diaCorte && (
                <div className="bg-gray-50 rounded-xl p-5 border border-gray-200">
                  <div className="flex items-center justify-between">
                    <span className="text-gray-600 text-sm">Día de corte:</span>
                    <span className="font-semibold text-blue-600">
                      Día {data.diaCorte} de cada mes
                    </span>
                  </div>
                </div>
              )}
            </div>
          )}

          {/* Montos */}
          <div className="bg-gradient-to-br from-gray-50 to-gray-100 rounded-xl p-6 border border-gray-200">
            <h2 className="text-lg font-semibold text-black mb-5 flex items-center gap-2">
              <DollarSign className="w-5 h-5 text-yellow-600" />
              Información Financiera
            </h2>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
              <div className="bg-white rounded-lg p-4 border border-yellow-200 shadow-sm">
                <p className="text-gray-600 text-xs mb-1">Monto Máximo</p>
                <p className="text-2xl font-bold text-yellow-600">
                  ${data.montoMaximo.toFixed(2)}
                </p>
              </div>

              <div className="bg-white rounded-lg p-4 border border-green-200 shadow-sm">
                <p className="text-gray-600 text-xs mb-1">Disponible</p>
                <p className="text-2xl font-bold text-green-600">
                  ${data.montoActual.toFixed(2)}
                </p>
              </div>

              <div className="bg-white rounded-lg p-4 border border-red-200 shadow-sm">
                <p className="text-gray-600 text-xs mb-1">% Gastado</p>
                <p className="text-2xl font-bold text-button flex items-center gap-1">
                  <TrendingUp className="w-5 h-5" /> {data.porcentaje}%
                </p>
              </div>
            </div>

            {/* Barra de progreso */}
            <div>
              <div className="flex justify-between items-center mb-2">
                <span className="text-sm text-gray-600">Progreso de consumo</span>
                <span className="text-sm font-semibold text-black">{data.porcentaje}%</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-4 overflow-hidden shadow-inner">
                <div
                  className={`h-4 bg-gradient-to-r ${getProgressBarGradient()} transition-all duration-500 ease-out shadow-lg`}
                  style={{ width: `${Math.min(data.porcentaje, 100)}%` }}
                ></div>
              </div>
              <p className="text-center mt-3 text-sm text-gray-600">
                Has utilizado <span className="font-semibold text-black">${(data.montoMaximo - data.montoActual).toFixed(2)}</span> de tu límite
              </p>
            </div>
          </div>

          {/* Alertas */}
          {data.porcentaje >= 80 && (
            <div className="bg-red-50 border border-button rounded-xl p-4 flex items-start gap-3">
              <AlertTriangle className="w-6 h-6 text-button flex-shrink-0 mt-0.5" />
              <div>
                <h3 className="font-semibold text-button mb-1">Alerta de Consumo</h3>
                <p className="text-sm text-gray-700">
                  Has utilizado más del 80% de tu límite. Considera gestionar tus gastos.
                </p>
              </div>
            </div>
          )}

          {!isCardActive() && (
            <div className="bg-yellow-50 border border-yellow-400 rounded-xl p-4 flex items-start gap-3">
              <AlertTriangle className="w-6 h-6 text-yellow-600 flex-shrink-0 mt-0.5" />
              <div>
                <h3 className="font-semibold text-yellow-700 mb-1">Tarjeta Inactiva</h3>
                <p className="text-sm text-gray-700">
                  Esta tarjeta está inactiva. Contacta con tu administrador para activarla.
                </p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}