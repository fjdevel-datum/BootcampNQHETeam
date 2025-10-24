import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
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
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    numeroTarjeta: "",
    tipoId: null as number | null,
    fechaExpiracion: "",
    descripcion: "",
    montoMaximo: "",
  });

  // Cargar colaborador
  useEffect(() => {
    const fetchColaborador = async () => {
      if (!id) return;
      try {
        const data: Colaborador = await fetchWithAuth(
          `http://localhost:8080/empleado/${id}`
        );
        setColaborador(data);
      } catch {
        setError("No se pudo cargar la información del colaborador");
      }
    };
    fetchColaborador();
  }, [id]);

  // Cargar tipos de tarjeta
  useEffect(() => {
    const fetchTipos = async () => {
      try {
        const data: TipoTarjeta[] = await fetchWithAuth(
          "http://localhost:8080/tipoTarjeta/lista"
        );
        setTiposTarjeta(data);
      } catch {
        setError("No se pudieron cargar los tipos de tarjeta");
      }
    };
    fetchTipos();
  }, []);

  const esViatico = formData.tipoId === 1;

  const handleChange = (field: string, value: string | number) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (error) setError(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.tipoId || !formData.numeroTarjeta || !formData.fechaExpiracion || !formData.descripcion || !formData.montoMaximo) {
      setError("Todos los campos son obligatorios");
      return;
    }

    setLoading(true);
    try {
      // Crear tarjeta
      const tarjetaCreada = await fetchWithAuth(
        "http://localhost:8080/tarjeta/crearTarjeta",
        {
          method: "POST",
          body: JSON.stringify({
            numeroTarjeta: formData.numeroTarjeta,
            tipoId: formData.tipoId,
            fechaExpiracion: formData.fechaExpiracion,
            descripcion: formData.descripcion,
          }),
        }
      );

      // Asignar tarjeta al colaborador
      await fetchWithAuth("http://localhost:8080/recursoAsignado/crear", {
        method: "POST",
        body: JSON.stringify({
          tarjetaId: tarjetaCreada.tarjetaId,
          empleadoId: parseInt(id!),
          fechaAsignacion: new Date().toISOString().split("T")[0],
          montoMaximo: parseFloat(formData.montoMaximo),
          estado: "Activo",
        }),
      });

      alert(
        esViatico
          ? `Viático asignado a ${colaborador?.nombres} - $${parseFloat(formData.montoMaximo).toFixed(2)}`
          : `Tarjeta de crédito asignada a ${colaborador?.nombres} - $${parseFloat(formData.montoMaximo).toFixed(2)}`
      );

      navigate(`/admin/info-colaborators/${id}`);
    } catch (err: any) {
      console.error(err);
      setError(err.message || "Error al asignar el recurso");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-4 bg-gray-50 relative">
      <button
        onClick={() => navigate(-1)}
        className="absolute top-6 left-6"
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

        {error && (
          <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded">
            <p className="text-red-700 text-center text-sm">{error}</p>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Tipo de tarjeta */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">Tipo de Tarjeta *</label>
            <select
              value={formData.tipoId || ""}
              onChange={(e) => handleChange("tipoId", parseInt(e.target.value))}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
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
              className="w-full border border-gray-300 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
              maxLength={16}
            />
          </div>

          {/* Fecha de expiración */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              {esViatico ? "Fecha de expiración de Viático" : "Fecha de expiración de Tarjeta de Crédito"} *
            </label>
            <input
              type="date"
              value={formData.fechaExpiracion}
              onChange={(e) => handleChange("fechaExpiracion", e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
              min={new Date().toISOString().split("T")[0]}
            />
          </div>

          {/* Descripción */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">Descripción *</label>
            <input
              type="text"
              value={formData.descripcion}
              onChange={(e) => handleChange("descripcion", e.target.value)}
              placeholder={esViatico ? "Ej: Viaje a Ciudad de México" : "Ej: Tarjeta corporativa"}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
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
              className="w-full border border-gray-300 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-button text-white font-bold py-3 rounded-lg hover:bg-blue-700 transition"
          >
            {loading ? "Asignando..." : esViatico ? "Asignar Viático" : "Asignar Tarjeta"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default AddResource;
