import React, { useState, useEffect } from "react";
import { ArrowLeft, AlertTriangle, FileText, Calendar, MessageSquare } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { toast } from "../../components/toast";
import { fetchWithAuth, getCurrentUserData } from "../../services/authService";

interface TipoIncidencia {
  tipoIncidenciaId: number;
  descripcion: string;
}

interface RecursoAsignado {
  recursoId: number;
  tarjetaId: number;
  estado: string;
  numeroTarjeta?: string;
  descripcionTarjeta?: string;
  tipoId?: number | null;
}

interface TarjetaData {
  tarjetaId: number;
  numeroTarjeta: string;
  descripcion: string;
  tipoId: number | null;
}

interface IncidenciaDTO {
  incidenciaId?: number;
  empleadoId: number;
  tipoIncidenciaId: number;
  recursoId: number;
  fechaIncidencia: string;
  descripcion: string;
}

export default function NewIncidence() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(true);
  const [tiposIncidencia, setTiposIncidencia] = useState<TipoIncidencia[]>([]);
  const [recursos, setRecursos] = useState<RecursoAsignado[]>([]);
  
  const [formData, setFormData] = useState<IncidenciaDTO>({
    empleadoId: 0,
    tipoIncidenciaId: 0,
    recursoId: 0,
    fechaIncidencia: new Date().toISOString().split('T')[0],
    descripcion: ""
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const apiurl = import.meta.env.VITE_API_URL;
        const userData = getCurrentUserData();
        
        if (!userData || !userData.empleadoId) {
          toast.error("Error de autenticación", "No se pudo identificar al usuario");
          navigate("/colaborators/user-cards");
          return;
        }

        setFormData(prev => ({ ...prev, empleadoId: userData.empleadoId }));

        // Obtener tipos de incidencia
        const tiposResponse = await fetchWithAuth(`${apiurl}/tipoIncidencia/lista`);
        setTiposIncidencia(tiposResponse);

        // Obtener tarjetas del usuario
        const tarjetasResponse: TarjetaData[] = await fetchWithAuth(
          `${apiurl}/tarjeta/usuario/${userData.empleadoId}`
        );

        // Obtener recursos asignados al empleado
        const recursosResponse = await fetchWithAuth(
          `${apiurl}/recursoAsignado/empleado/${userData.empleadoId}`
        );

        // Combinar recursos con información de tarjetas Y FILTRAR SOLO TARJETAS DE CRÉDITO
        const recursosConTarjeta = recursosResponse
          .map((recurso: RecursoAsignado) => {
            const tarjeta = tarjetasResponse.find((t: TarjetaData) => t.tarjetaId === recurso.tarjetaId);
            return {
              ...recurso,
              numeroTarjeta: tarjeta?.numeroTarjeta || `Tarjeta #${recurso.tarjetaId}`,
              descripcionTarjeta: tarjeta?.descripcion || '',
              tipoId: tarjeta?.tipoId
            };
          })
          .filter((recurso: RecursoAsignado) => 
            recurso.tipoId === 2  // Solo tarjetas de crédito (tipoId === 2)
          );

        setRecursos(recursosConTarjeta);

        if (recursosConTarjeta.length === 0) {
          toast.warning(
            "Sin tarjetas de crédito", 
            "No tienes tarjetas de crédito asignadas. Solo se pueden reportar incidencias de tarjetas de crédito."
          );
        } else {
          toast.success("Datos cargados", "Información obtenida correctamente");
        }
        
      } catch (error: any) {
        console.error(error);
        toast.error("Error al cargar", error.message || "No se pudieron cargar los datos necesarios");
      } finally {
        setLoadingData(false);
      }
    };

    fetchData();
  }, [navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validaciones
    if (!formData.tipoIncidenciaId || formData.tipoIncidenciaId === 0) {
      toast.error("Campo requerido", "Selecciona un tipo de incidencia");
      return;
    }

    if (!formData.recursoId || formData.recursoId === 0) {
      toast.error("Campo requerido", "Selecciona un recurso");
      return;
    }

    if (!formData.descripcion.trim()) {
      toast.error("Campo requerido", "Describe los detalles de la incidencia");
      return;
    }

    // Obtener el recurso seleccionado para mostrar en confirmación
    const recursoSeleccionado = recursos.find(r => r.recursoId === formData.recursoId);
    const tipoSeleccionado = tiposIncidencia.find(t => t.tipoIncidenciaId === formData.tipoIncidenciaId);

    // Pedir confirmación
    const confirmar = window.confirm(
      `¿Estás seguro de reportar esta incidencia?\n\n` +
      `Tipo: ${tipoSeleccionado?.descripcion}\n` +
      `Recurso: ${recursoSeleccionado?.numeroTarjeta}\n\n` +
      `⚠️ IMPORTANTE: El recurso será desactivado automáticamente.`
    );

    if (!confirmar) {
      return;
    }

    setLoading(true);

    try {
      const apiurl = import.meta.env.VITE_API_URL;
      
      // Paso 1: Crear la incidencia
      const incidenciaCreada = await fetchWithAuth(`${apiurl}/incidencia/crear`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      toast.success("Incidencia reportada", "El reporte ha sido registrado exitosamente");

      // Paso 2: Desactivar el recurso
      try {
        await fetchWithAuth(`${apiurl}/recursoAsignado/${formData.recursoId}/desactivar`, {
          method: "PUT",
        });
        
        toast.success("Recurso desactivado", "El recurso ha sido desactivado por seguridad");
      } catch (errorDesactivacion: any) {
        console.error("Error al desactivar recurso:", errorDesactivacion);
        toast.error(
          "Advertencia", 
          "La incidencia fue creada pero hubo un problema al desactivar el recurso. Contacta con tu administrador."
        );
      }

      // Esperar un momento para que el usuario vea los mensajes
      setTimeout(() => {
        navigate("/incidences/list");
      }, 2000);

    } catch (error: any) {
      console.error(error);
      toast.error("Error al crear", error.message || "No se pudo crear la incidencia");
    } finally {
      setLoading(false);
    }
  };

  if (loadingData) {
    return (
      <div className="flex flex-col items-center justify-center h-screen bg-background font-montserrat">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-button mb-4"></div>
        <p className="text-lg animate-pulse text-black font-semibold">Cargando formulario...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background font-montserrat">
      {/* Header */}
      <div className="bg-white shadow-sm">
        <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <button
            onClick={() => navigate(-1)}
            className="flex items-center gap-2 text-gray-700 hover:text-button transition-colors font-medium mb-4"
          >
            <ArrowLeft className="w-5 h-5" />
            Volver
          </button>
          <div className="flex items-center gap-3">
            <div className="p-3 bg-button rounded-xl">
              <AlertTriangle className="w-8 h-8 text-white" />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Reportar Incidencia</h1>
              <p className="text-sm text-gray-500">Registra un problema con tus tarjetas de crédito</p>
            </div>
          </div>
        </div>
      </div>

      {/* Formulario */}
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <form onSubmit={handleSubmit} className="bg-white rounded-xl shadow-lg p-6 space-y-6">
          
          {/* Alerta informativa */}
          <div className="bg-yellow-50 border border-yellow-400 rounded-lg p-4 flex items-start gap-3">
            <AlertTriangle className="w-5 h-5 text-yellow-600 flex-shrink-0 mt-0.5" />
            <div className="text-sm text-yellow-800">
              <p className="font-semibold mb-1">Importante</p>
              <p>Al reportar una incidencia, el recurso seleccionado será desactivado automáticamente por seguridad.</p>
            </div>
          </div>

          {/* Tipo de Incidencia */}
          <div>
            <label className="flex items-center gap-2 text-sm font-semibold text-gray-700 mb-2">
              <AlertTriangle className="w-4 h-4 text-button" />
              Tipo de Incidencia *
            </label>
            <select
              value={formData.tipoIncidenciaId}
              onChange={(e) => setFormData({ ...formData, tipoIncidenciaId: Number(e.target.value) })}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-button focus:border-transparent transition-all"
              required
            >
              <option value={0}>Selecciona un tipo de incidencia</option>
              {tiposIncidencia.map((tipo) => (
                <option key={tipo.tipoIncidenciaId} value={tipo.tipoIncidenciaId}>
                  {tipo.descripcion}
                </option>
              ))}
            </select>
          </div>

          {/* Recurso Comprometido */}
          <div>
            <label className="flex items-center gap-2 text-sm font-semibold text-gray-700 mb-2">
              <FileText className="w-4 h-4 text-button" />
              Recurso Comprometido * 
              <span className="text-xs text-gray-500 font-normal">(Solo tarjetas de crédito)</span>
            </label>
            <select
              value={formData.recursoId}
              onChange={(e) => setFormData({ ...formData, recursoId: Number(e.target.value) })}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-button focus:border-transparent transition-all"
              required
              disabled={recursos.length === 0}
            >
              <option value={0}>
                {recursos.length === 0 
                  ? "No hay tarjetas de crédito disponibles" 
                  : "Selecciona un recurso"}
              </option>
              {recursos.map((recurso) => (
                <option key={recurso.recursoId} value={recurso.recursoId}>
                  {recurso.numeroTarjeta}
                  {recurso.descripcionTarjeta && ` - ${recurso.descripcionTarjeta}`}
                  {recurso.estado && recurso.estado !== 'Activo' && ` (${recurso.estado})`}
                </option>
              ))}
            </select>
            {recursos.length === 0 && (
              <p className="text-xs text-red-600 mt-1">
                Solo puedes reportar incidencias de tarjetas de crédito corporativas.
              </p>
            )}
          </div>

          {/* Fecha de Incidencia */}
          <div>
            <label className="flex items-center gap-2 text-sm font-semibold text-gray-700 mb-2">
              <Calendar className="w-4 h-4 text-button" />
              Fecha de la Incidencia *
            </label>
            <input
              type="date"
              value={formData.fechaIncidencia}
              onChange={(e) => setFormData({ ...formData, fechaIncidencia: e.target.value })}
              max={new Date().toISOString().split('T')[0]}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-button focus:border-transparent transition-all"
              required
            />
          </div>

          {/* Descripción */}
          <div>
            <label className="flex items-center gap-2 text-sm font-semibold text-gray-700 mb-2">
              <MessageSquare className="w-4 h-4 text-button" />
              Descripción de la Incidencia *
            </label>
            <textarea
              value={formData.descripcion}
              onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
              placeholder="Describe detalladamente lo sucedido..."
              rows={6}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-button focus:border-transparent transition-all resize-none"
              required
            />
            <p className="text-xs text-gray-500 mt-1">
              {formData.descripcion.length} caracteres
            </p>
          </div>

          {/* Botones */}
          <div className="flex gap-4 pt-4">
            <button
              type="button"
              onClick={() => navigate(-1)}
              className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-700 font-semibold py-3 rounded-lg transition-colors"
              disabled={loading}
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading || recursos.length === 0}
              className="flex-1 bg-button hover:bg-button-hover text-white font-semibold py-3 rounded-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed shadow-md"
            >
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                  Procesando...
                </span>
              ) : recursos.length === 0 ? (
                "No hay tarjetas de crédito"
              ) : (
                "Reportar Incidencia"
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}