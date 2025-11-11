import React, { useEffect, useState } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import { ArrowLeft, CreditCard, AlertCircle, Loader2, Calendar } from "lucide-react";
import { toast } from "../../components/toast";
import { fetchWithAuth } from "../../services/authService";

interface TarjetaData {
  tarjetaId: number;
  tipoId: number | null;
  numeroTarjeta: string;
  fechaExpiracion: string | null;
  descripcion: string;
  diaCorte?: number | null;
}

interface RecursoAsignadoData {
  recursoId: number;
  empleadoId: number;
  tarjetaId: number;
  fechaAsignacion: string | null;
  montoMaximo: number | null;
  estado: string;
}

interface LocationState {
  fromSee?: boolean;
  fromEdit?: boolean;
}

const EditCard: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation() as unknown as { state: LocationState };
  const { cardId, empleadoId } = useParams<{ cardId: string; empleadoId: string }>();
  
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  
  const [tarjetaData, setTarjetaData] = useState<TarjetaData | null>(null);
  const [recursoData, setRecursoData] = useState<RecursoAsignadoData | null>(null);
  
  const apiurl = import.meta.env.VITE_API_URL;
  const [formData, setFormData] = useState({
    fechaExpiracion: "",
    montoMaximo: "",
    estado: "Activo"
  });

  const esFechaVencida = (fecha: string): boolean => {
    if (!fecha) return false;
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    const fechaComparar = new Date(fecha + 'T00:00:00');
    return fechaComparar < hoy;
  };

  const esTarjetaCredito = tarjetaData?.tipoId === 2;

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);

        if (!cardId || !empleadoId) {
          toast.error("Parámetros inválidos", "No se pudo cargar la información");
          navigate(`/admin/info-colaborators/${empleadoId}`);
          return;
        }

        const tarjeta: TarjetaData = await fetchWithAuth(`${apiurl}/tarjeta/${cardId}`);
        setTarjetaData(tarjeta);

        const recurso: RecursoAsignadoData = await fetchWithAuth(
          `${apiurl}/recursoAsignado/tarjeta/${cardId}/empleado/${empleadoId}`
        );
        setRecursoData(recurso);

        const fechaFormateada = tarjeta.fechaExpiracion
          ? new Date(tarjeta.fechaExpiracion).toISOString().split("T")[0]
          : "";

        const estadoInicial = esFechaVencida(fechaFormateada) 
          ? "Inactivo" 
          : (recurso.estado || "Activo");

        setFormData({
          fechaExpiracion: fechaFormateada,
          montoMaximo: recurso.montoMaximo ? recurso.montoMaximo.toString() : "",
          estado: estadoInicial
        });

      } catch (err: any) {
        toast.error("Error al cargar", err.message || "No se pudo cargar la información de la tarjeta");
        navigate(`/admin/info-colaborators/${empleadoId}`);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [cardId, empleadoId]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;

    setFormData(prev => {
      const newFormData = { ...prev, [name]: value };

      if (name === "fechaExpiracion" && esFechaVencida(value)) {
        newFormData.estado = "Inactivo";
        toast.warning("Fecha vencida", "La tarjeta se marcará como inactiva automáticamente");
      }

      if (name === "estado" && value === "Activo" && esFechaVencida(newFormData.fechaExpiracion)) {
        toast.error("Acción no permitida", "No puede activar una tarjeta con fecha vencida");
        return prev;
      }

      return newFormData;
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);

    if (esFechaVencida(formData.fechaExpiracion) && formData.estado === "Activo") {
      toast.error("Validación fallida", "No se puede activar una tarjeta con fecha de expiración vencida");
      setSaving(false);
      return;
    }

    const loadingToast = toast.loading("Guardando cambios...");

    try {
      await fetchWithAuth(`${apiurl}/tarjeta/${cardId}/fecha-expiracion`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ fechaExpiracion: formData.fechaExpiracion || null })
      });

      await fetchWithAuth(`${apiurl}/recursoAsignado/${recursoData?.recursoId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          montoMaximo: formData.montoMaximo ? parseFloat(formData.montoMaximo) : null,
          estado: formData.estado
        })
      });

      toast.dismiss(loadingToast);
      toast.success("Tarjeta actualizada", "Los cambios se guardaron correctamente");

      setTimeout(() => {
        navigate(`/admin/info-colaborators/${empleadoId}`, {
          state: { fromEdit: true }
        });
      }, 1500);

    } catch (err: any) {
      toast.dismiss(loadingToast);
      toast.error("Error al actualizar", err.message || "No se pudieron guardar los cambios");
    } finally {
      setSaving(false);
    }
  };

  const handleGoBack = () => {
    if (location.state?.fromEdit) {
      navigate(-1);
    } else {
      navigate(`/admin/info-colaborators/${empleadoId}`);
    }
  };

  const getTipoTarjeta = (tipoId: number | null): string => {
    switch (tipoId) {
      case 1: return "VIÁTICO";
      case 2: return "CRÉDITO";
      case 3: return "CORPORATIVA";
      default: return "DESCONOCIDO";
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-background flex flex-col items-center justify-center p-6">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-button mb-4"></div>
        <p className="text-gray-600">Cargando información de la tarjeta...</p>
      </div>
    );
  }

  if (!tarjetaData || !recursoData) {
    return (
      <div className="min-h-screen bg-background p-6">
        <button
          onClick={handleGoBack}
          className="flex items-center gap-2 text-black hover:text-activity transition mb-4"
        >
          <ArrowLeft className="w-5 h-5" />
          Volver
        </button>
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-700 text-center">No se encontró la información de la tarjeta</p>
        </div>
      </div>
    );
  }

  const fechaVencida = esFechaVencida(formData.fechaExpiracion);

  return (
    <div className="min-h-screen bg-background p-6 font-montserrat">
      <button
        onClick={handleGoBack}
        className="flex items-center gap-2 text-black hover:text-activity transition mb-6"
        disabled={saving}
      >
        <ArrowLeft className="w-5 h-5" />
        Volver
      </button>

      <div className="flex items-center gap-3 mb-6">
        <CreditCard className="w-8 h-8 text-button" />
        <h1 className="text-2xl font-bold text-black">Editar Tarjeta</h1>
      </div>

      {fechaVencida && (
        <div className="bg-amber-50 border border-amber-200 rounded-lg p-4 mb-4 flex items-center gap-2">
          <AlertCircle className="w-5 h-5 text-amber-600 flex-shrink-0" />
          <div>
            <p className="text-amber-800 font-semibold">⚠️ Tarjeta Expirada</p>
            <p className="text-amber-700 text-sm">Esta tarjeta ha expirado y se encuentra desactivada. No puede activarse con una fecha vencida.</p>
          </div>
        </div>
      )}

      {/* BLOQUE INFORMACIÓN DE TARJETA */}
      <div className="bg-gray-50 rounded-2xl p-6 shadow-md mb-6 border border-gray-200">
        <h2 className="text-lg font-semibold text-black mb-4">Información de la Tarjeta</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Número de tarjeta */}
          <div>
            <p className="text-sm text-gray-500 mb-1">Número de Tarjeta</p>
            <p className="text-base font-medium text-gray-900">
              {tarjetaData.numeroTarjeta.replace(/(\d{4})/g, "$1 ").trim()}
            </p>
          </div>

          {/* Tipo de tarjeta */}
          <div>
            <p className="text-sm text-gray-500 mb-1">Tipo</p>
            <p className="text-base font-medium text-gray-900">{getTipoTarjeta(tarjetaData.tipoId)}</p>
          </div>

          {/* Día de corte solo si es tarjeta de crédito */}
          {esTarjetaCredito && tarjetaData.diaCorte && (
            <div>
              <p className="text-sm text-gray-500 mb-1 flex items-center gap-1">
                <Calendar className="w-4 h-4" />
                Día de Corte
              </p>
              <p className="text-base font-medium text-gray-900">
                Día {tarjetaData.diaCorte} de cada mes
              </p>
              <p className="text-xs text-gray-400 mt-1">Este valor no se puede modificar</p>
            </div>
          )}

          {/* Descripción */}
          {tarjetaData.descripcion && (
            <div className={esTarjetaCredito && tarjetaData.diaCorte ? "" : "md:col-span-2"}>
              <p className="text-sm text-gray-500 mb-1">Descripción</p>
              <p className="text-base font-medium text-gray-900">{tarjetaData.descripcion}</p>
            </div>
          )}
        </div>
      </div>

      {/* FORMULARIO DE EDICIÓN */}
      <form onSubmit={handleSubmit} className="bg-white rounded-2xl p-6 shadow-md space-y-6">
        <h2 className="text-lg font-semibold text-black mb-4">Editar Información</h2>

        {/* Fecha de expiración */}
        <div>
          <label htmlFor="fechaExpiracion" className="block text-sm font-semibold text-gray-700 mb-2">
            Fecha de Expiración {fechaVencida && <span className="text-red-500">⚠️ Vencida</span>}
          </label>
          <input
            type="date"
            id="fechaExpiracion"
            name="fechaExpiracion"
            value={formData.fechaExpiracion}
            onChange={handleChange}
            className={`w-full px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-button focus:border-transparent transition ${
              fechaVencida ? 'border-red-300 bg-red-50' : 'border-gray-300'
            }`}
            disabled={saving}
          />
          <p className="text-xs text-gray-500 mt-1">
            {fechaVencida 
              ? "Esta fecha ya pasó. Actualice a una fecha futura para poder activar la tarjeta."
              : "Fecha de expiración de la tarjeta"}
          </p>
        </div>

        {/* Monto máximo */}
        <div>
          <label htmlFor="montoMaximo" className="block text-sm font-semibold text-gray-700 mb-2">
            Monto Máximo
          </label>
          <div className="relative">
            <span className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-500">$</span>
            <input
              type="number"
              id="montoMaximo"
              name="montoMaximo"
              value={formData.montoMaximo}
              onChange={handleChange}
              step="0.01"
              min="0"
              placeholder="0.00"
              className="w-full pl-8 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-button focus:border-transparent transition"
              disabled={saving}
            />
          </div>
          <p className="text-xs text-gray-500 mt-1">Límite de gasto permitido para esta tarjeta</p>
        </div>

        {/* Estado */}
        <div>
          <label htmlFor="estado" className="block text-sm font-semibold text-gray-700 mb-2">
            Estado {fechaVencida && <span className="text-red-500">(Bloqueado)</span>}
          </label>
          <select
            id="estado"
            name="estado"
            value={formData.estado}
            onChange={handleChange}
            className={`w-full px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-button focus:border-transparent transition ${
              fechaVencida ? 'bg-gray-100 cursor-not-allowed' : ''
            }`}
            disabled={saving || fechaVencida}
          >
            <option value="Activo">Activo</option>
            <option value="Inactivo">Inactivo</option>
          </select>
          {fechaVencida ? (
            <p className="text-xs text-red-500 mt-1 flex items-center gap-1">
              <AlertCircle className="w-3 h-3" />
              No se puede activar una tarjeta expirada. Actualice la fecha primero.
            </p>
          ) : (
            <p className="text-xs text-gray-500 mt-1">
              Estado actual de la tarjeta asignada
            </p>
          )}
        </div>

        {/* Botones */}
        <div className="space-y-3 pt-4">
          <button
            type="submit"
            disabled={saving}
            className="w-full bg-button text-white font-semibold py-3 rounded-lg hover:bg-button-hover transition-all transform hover:scale-[1.02] active:scale-[0.98] shadow-md disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none flex items-center justify-center gap-2"
          >
            {saving ? (
              <>
                <Loader2 className="w-5 h-5 animate-spin" />
                Guardando...
              </>
            ) : (
              <>
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                </svg>
                Guardar Cambios
              </>
            )}
          </button>

          <button
            type="button"
            onClick={handleGoBack}
            disabled={saving}
            className="w-full bg-gray-200 text-gray-700 font-semibold py-3 rounded-lg hover:bg-gray-300 transition-all transform hover:scale-[1.02] active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
};

export default EditCard;
