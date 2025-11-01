import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import { toast } from "../../components/toast"; 
import { fetchWithAuth } from "../../services/authService";

interface Colaborador {
  empleadoId: number;
  nombres: string;
  apellidos: string;
}

interface TipoTarjeta {
  tipoId: number;
  tipo: string;
  descripcion: string;
}

const AddResource: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();

  const [colaborador, setColaborador] = useState<Colaborador | null>(null);
  const [tiposTarjeta, setTiposTarjeta] = useState<TipoTarjeta[]>([]);
  const [loading, setLoading] = useState(false);

  const apiurl = import.meta.env.VITE_API_URL;

  const [formData, setFormData] = useState({
    numeroTarjeta: "",
    tipoId: null as number | null,
    fechaExpiracion: "",
    descripcion: "",
    montoMaximo: "",
    diaCorte: "", // NUEVO CAMPO
  });

  useEffect(() => {
    const fetchColaborador = async () => {
      if (!id) return;
      try {
        const data: Colaborador = await fetchWithAuth(
          `${apiurl}/empleado/${id}`
        );
        setColaborador(data);
      } catch {
        toast.error("Error al cargar", "No se pudo cargar la información del colaborador");
      }
    };
    fetchColaborador();
  }, [id]);

  useEffect(() => {
    const fetchTipos = async () => {
      try {
        const data: TipoTarjeta[] = await fetchWithAuth(
          `${apiurl}/tipoTarjeta/lista`
        );
        setTiposTarjeta(data);
      } catch {
        toast.error("Error al cargar", "No se pudieron cargar los tipos de tarjeta");
      }
    };
    fetchTipos();
  }, []);

  const esViatico = formData.tipoId === 1;
  const esTarjetaCredito = formData.tipoId === 2; // Para mostrar día de corte solo en crédito

  const handleChange = (field: string, value: string | number) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.tipoId || !formData.numeroTarjeta || !formData.fechaExpiracion || !formData.descripcion || !formData.montoMaximo) {
      toast.error("Campos incompletos", "Todos los campos son obligatorios");
      return;
    }

    // Validar día de corte para tarjetas de crédito
    if (esTarjetaCredito && (!formData.diaCorte || parseInt(formData.diaCorte) < 1 || parseInt(formData.diaCorte) > 31)) {
      toast.error("Día de corte inválido", "El día de corte debe estar entre 1 y 31");
      return;
    }

    setLoading(true);
    const loadingToast = toast.loading(
      esViatico ? "Asignando viático..." : "Asignando tarjeta..."
    );

    try {
      // Crear tarjeta
      const tarjetaCreada = await fetchWithAuth(
        `${apiurl}/tarjeta/crearTarjeta`,
        {
          method: "POST",
          body: JSON.stringify({
            numeroTarjeta: formData.numeroTarjeta,
            tipoId: formData.tipoId,
            fechaExpiracion: formData.fechaExpiracion,
            descripcion: formData.descripcion,
            diaCorte: esTarjetaCredito ? parseInt(formData.diaCorte) : null, // Solo enviar si es crédito
          }),
        }
      );

      // Asignar tarjeta al colaborador
      await fetchWithAuth(`${apiurl}/recursoAsignado/crear`, {
        method: "POST",
        body: JSON.stringify({
          tarjetaId: tarjetaCreada.tarjetaId,
          empleadoId: parseInt(id!),
          fechaAsignacion: new Date().toISOString().split("T")[0],
          montoMaximo: parseFloat(formData.montoMaximo),
          estado: "Activo",
        }),
      });

      toast.dismiss(loadingToast);
      
      toast.success(
        esViatico ? "Viático asignado" : "Tarjeta asignada",
        `${colaborador?.nombres} - $${parseFloat(formData.montoMaximo).toFixed(2)}`
      );

      setTimeout(() => {
        navigate(`/admin/info-colaborators/${id}`);
      }, 1500);

    } catch (err: any) {
      toast.dismiss(loadingToast);
      console.error(err);
      toast.error("Error al asignar", err.message || "No se pudo completar la operación");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-4 bg-background relative">
      <button
        onClick={() => navigate(-1)}
        className="absolute top-6 left-6 hover:bg-gray-200 p-2 rounded-lg transition"
        disabled={loading}
      >
        <ArrowLeft className="w-5 h-5" />
      </button>

      <div className="w-full max-w-md bg-white p-6 rounded-2xl shadow-lg mt-12">
        <h1 className="text-2xl font-bold text-center mb-4">Asignar Recurso</h1>
        {colaborador && (
          <p className="text-center text-gray-600 mb-4">
            Para: <span className="font-semibold">{colaborador.nombres} {colaborador.apellidos}</span>
          </p>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Tipo de tarjeta */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">Tipo de Tarjeta *</label>
            <select
              value={formData.tipoId || ""}
              onChange={(e) => handleChange("tipoId", parseInt(e.target.value))}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-button"
              disabled={loading}
            >
              <option value="">Seleccione un tipo</option>
              {tiposTarjeta.map(t => (
                <option key={t.tipoId} value={t.tipoId}>{t.tipo}</option>
              ))}
            </select>
          </div>

          {/* Número de tarjeta */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">Número de Tarjeta *</label>
            <input
              type="text"
              value={formData.numeroTarjeta}
              onChange={(e) => handleChange("numeroTarjeta", e.target.value.replace(/\D/g, ""))}
              placeholder="1234567890123456"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-button"
              maxLength={16}
              disabled={loading}
            />
          </div>

          {/* Fecha de expiración */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              {esViatico ? "Fecha de expiración de Viático" : "Fecha de expiración de Tarjeta"} *
            </label>
            <input
              type="date"
              value={formData.fechaExpiracion}
              onChange={(e) => handleChange("fechaExpiracion", e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-button"
              min={new Date().toISOString().split("T")[0]}
              disabled={loading}
            />
          </div>

          {/* Día de corte - SOLO PARA TARJETAS DE CRÉDITO */}
          {esTarjetaCredito && (
            <div>
              <label className="block font-semibold text-gray-700 mb-1">Día de Corte *</label>
              <input
                type="number"
                value={formData.diaCorte}
                onChange={(e) => handleChange("diaCorte", e.target.value)}
                placeholder="Ej: 15"
                min="1"
                max="31"
                className="w-full border border-gray-300 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-button"
                disabled={loading}
              />
              <p className="text-xs text-gray-500 mt-1">Día del mes en que se realiza el corte de la tarjeta (1-31)</p>
            </div>
          )}

          {/* Descripción */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">Descripción *</label>
            <input
              type="text"
              value={formData.descripcion}
              onChange={(e) => handleChange("descripcion", e.target.value)}
              placeholder={esViatico ? "Ej: Viaje a Ciudad de México" : "Ej: Tarjeta corporativa"}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-button"
              disabled={loading}
            />
          </div>

          {/* Monto máximo */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              {esViatico ? "Monto del Viático" : "Límite de Crédito"} *
            </label>
            <input
              type="number"
              value={formData.montoMaximo}
              onChange={(e) => handleChange("montoMaximo", e.target.value)}
              placeholder="0.00"
              min="0.01"
              step="0.01"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-button"
              disabled={loading}
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-button text-white font-bold py-3 rounded-lg hover:bg-button-hover transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? "Asignando..." : esViatico ? "Asignar Viático" : "Asignar Tarjeta"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default AddResource;