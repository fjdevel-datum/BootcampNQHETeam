import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import { fetchWithAuth } from "../../services/authService";

interface Colaborador {
  empleadoId: number;
  nombres: string;
  apellidos: string;
}

interface FormData {
  numeroTarjeta: string;
  tipoTarjetaId: number | null;
  fechaExpiracion: string;
  descripcion: string;
  empleadoId: number | null;
}

const AddResource: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [colaborador, setColaborador] = useState<Colaborador | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState<FormData>({
    numeroTarjeta: "",
    tipoTarjetaId: null,
    fechaExpiracion: "",
    descripcion: "",
    empleadoId: null,
  });

  useEffect(() => {
    const fetchColaborador = async () => {
      try {
        if (!id) return;

        console.log("👤 Cargando colaborador ID:", id);

        const data: Colaborador = await fetchWithAuth(
          `http://localhost:8080/empleado/${id}`
        );

        console.log("✅ Colaborador obtenido:", data);
        setColaborador(data);
        setFormData(prev => ({ ...prev, empleadoId: data.empleadoId }));
      } catch (err: any) {
        console.error("❌ Error al cargar colaborador:", err);
        setError("No se pudo cargar la información del colaborador");
      }
    };

    fetchColaborador();
  }, [id]);

  const handleChange = (field: keyof FormData, value: string | number) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
    if (error) setError(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validaciones
    if (!formData.numeroTarjeta || formData.numeroTarjeta.length < 13) {
      setError("El número de tarjeta debe tener al menos 13 dígitos");
      return;
    }

    if (!formData.tipoTarjetaId) {
      setError("Seleccione el tipo de tarjeta");
      return;
    }

    if (!formData.fechaExpiracion) {
      setError("La fecha de expiración es requerida");
      return;
    }

    if (!formData.descripcion.trim()) {
      setError("La descripción es requerida");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      console.log("💳 Creando tarjeta con datos:", formData);

      const response = await fetchWithAuth(
        "http://localhost:8080/tarjeta/crearTarjeta",
        {
          method: "POST",
          body: JSON.stringify(formData),
        }
      );

      console.log("✅ Tarjeta creada exitosamente:", response);

      alert(`✅ Recurso asignado exitosamente a ${colaborador?.nombres}`);
      navigate(`/admin/info-colaborators/${id}`);
    } catch (err: any) {
      console.error("❌ Error al crear tarjeta:", err);
      setError(err.message || "No se pudo asignar el recurso");
    } finally {
      setLoading(false);
    }
  };

  const handleGoBack = () => navigate(-1);

  return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center p-4 font-montserrat relative">
      {/* Botón volver */}
      <button
        onClick={handleGoBack}
        className="absolute top-6 left-6 flex items-center gap-2 text-black hover:text-activity transition"
        disabled={loading}
      >
        <ArrowLeft className="w-5 h-5" />
        <span className="font-medium">Volver</span>
      </button>

      <div className="w-full max-w-md bg-white shadow-lg rounded-2xl p-6 mt-12">
        <h1 className="text-2xl font-bold text-center mb-2 text-activity">
          Añadir Recurso
        </h1>
        {colaborador && (
          <p className="text-center text-gray-600 mb-6">
            Para: <span className="font-semibold">{colaborador.nombres} {colaborador.apellidos}</span>
          </p>
        )}

        {/* Mensaje de error */}
        {error && (
          <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg">
            <p className="text-sm text-red-700 text-center">{error}</p>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Número de tarjeta */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              Número de Tarjeta <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={formData.numeroTarjeta}
              onChange={(e) => {
                const value = e.target.value.replace(/\D/g, ""); // Solo números
                if (value.length <= 16) {
                  handleChange("numeroTarjeta", value);
                }
              }}
              placeholder="1234567890123456"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-activity focus:border-activity outline-none"
              disabled={loading}
              maxLength={16}
              required
            />
            <p className="text-xs text-gray-500 mt-1">
              {formData.numeroTarjeta.length}/16 dígitos
            </p>
          </div>

          {/* Tipo de tarjeta */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              Tipo de Tarjeta <span className="text-red-500">*</span>
            </label>
            <select
              value={formData.tipoTarjetaId || ""}
              onChange={(e) => handleChange("tipoTarjetaId", parseInt(e.target.value))}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-activity focus:border-activity outline-none"
              disabled={loading}
              required
            >
              <option value="">Seleccione un tipo</option>
              <option value="1">Visa</option>
              <option value="2">Mastercard</option>
              <option value="3">American Express</option>
            </select>
          </div>

          {/* Fecha de expiración */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              Fecha de Expiración <span className="text-red-500">*</span>
            </label>
            <input
              type="date"
              value={formData.fechaExpiracion}
              onChange={(e) => handleChange("fechaExpiracion", e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-activity focus:border-activity outline-none"
              disabled={loading}
              min={new Date().toISOString().split('T')[0]}
              required
            />
          </div>

          {/* Descripción */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              Descripción <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={formData.descripcion}
              onChange={(e) => handleChange("descripcion", e.target.value)}
              placeholder="Ej: Tarjeta corporativa principal"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-activity focus:border-activity outline-none"
              disabled={loading}
              maxLength={100}
              required
            />
          </div>

          {/* Botón Guardar */}
          <button
            type="submit"
            disabled={loading || !colaborador}
            className="w-full bg-button hover:bg-button-hover text-white font-bold py-3 px-4 rounded-lg transition-all transform hover:scale-[1.02] active:scale-[0.98] shadow-md disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
          >
            {loading ? (
              <span className="flex items-center justify-center gap-2">
                <svg className="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Asignando...
              </span>
            ) : (
              "Asignar Recurso"
            )}
          </button>

          <p className="text-xs text-gray-500 text-center mt-4">
            <span className="text-red-500">*</span> Campos obligatorios
          </p>
        </form>
      </div>
    </div>
  );
};

export default AddResource;