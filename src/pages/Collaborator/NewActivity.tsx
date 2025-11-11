import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import { toast } from "../../components/toast";
import { fetchWithAuth, getCurrentUserData } from "../../services/authService";

interface Activity {
  actividadId?: number | null;
  nombre: string;
  fechaInicio: string;
  fechaFinal?: string;
  estado?: string;
  empleadoId?: number | null;
}

const NewActivity: React.FC = () => {
  const navigate = useNavigate();
  const apiurl = import.meta.env.VITE_API_URL;
  
  const [formData, setFormData] = useState<Activity>({
    nombre: "",
    fechaInicio: "",
    fechaFinal: "",
    estado: "Pendiente",
    empleadoId: null,
  });

  const [loading, setLoading] = useState(false);

  // Obtener el empleadoId del usuario logueado
  useEffect(() => {
    const userData = getCurrentUserData();
    
    if (!userData || !userData.empleadoId) {
      toast.error("Error de autenticación", "No se pudo identificar al usuario");
      setTimeout(() => navigate("/colaborators/activities"), 2000);
      return;
    }

    setFormData(prev => ({
      ...prev,
      empleadoId: userData.empleadoId
    }));

    console.log("👤 Empleado ID cargado:", userData.empleadoId);
  }, [navigate]);

  const handleGoBack = () => navigate(-1);

  const handleGuardar = async () => {
    // Validaciones
    if (!formData.nombre || !formData.nombre.trim()) {
      toast.error("Campo requerido", "Por favor ingrese el nombre de la actividad");
      return;
    }

    if (!formData.fechaInicio) {
      toast.error("Campo requerido", "Por favor seleccione la fecha de inicio");
      return;
    }

    if (formData.fechaFinal && formData.fechaFinal < formData.fechaInicio) {
      toast.error("Fecha inválida", "La fecha final no puede ser anterior a la fecha de inicio");
      return;
    }

    if (!formData.empleadoId) {
      toast.error("Error de sesión", "Por favor, inicie sesión nuevamente");
      return;
    }

    setLoading(true);
    const loadingToast = toast.loading("Creando actividad...");

    try {
      console.log("📝 Creando actividad con datos:", formData);

      // Llamar al endpoint con autenticación
      const data = await fetchWithAuth(
        `${apiurl}/actividad`,
        {
          method: "POST",
          body: JSON.stringify(formData),
        }
      );

      console.log("✅ Actividad creada exitosamente:", data);

      toast.dismiss(loadingToast);
      toast.success(
        "Actividad creada",
        `"${data.nombre}" se registró correctamente`
      );

      setTimeout(() => {
        navigate("/colaborators/activities");
      }, 1500);

    } catch (error: any) {
      console.error("❌ Error al crear actividad:", error);
      toast.dismiss(loadingToast);
      toast.error(
        "Error al crear", 
        error.message || "No se pudo crear la actividad. Intente nuevamente"
      );
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field: keyof Activity, value: string) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center p-4 font-montserrat relative">
      {/* Botón volver */}
      <button
        onClick={handleGoBack}
        className="absolute top-6 left-6 flex items-center gap-2 text-black hover:text-activity transition-colors"
        disabled={loading}
      >
        <ArrowLeft className="w-5 h-5" />
        <span className="font-medium">Volver</span>
      </button>

      <div className="w-full max-w-md bg-white shadow-lg rounded-2xl p-6 mt-12">
        <h1 className="text-2xl font-bold text-center mb-6 text-activity">
          Nueva Actividad
        </h1>

        <div className="space-y-4">
          {/* Nombre */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              Nombre de la actividad <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={formData.nombre}
              onChange={(e) => handleChange("nombre", e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-activity focus:border-activity outline-none transition-all"
              placeholder="Ej: Desarrollo de módulo de reportes"
              required
              disabled={loading}
              maxLength={100}
            />
          </div>

          {/* Fecha inicio */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              Fecha de inicio <span className="text-red-500">*</span>
            </label>
            <input
              type="date"
              value={formData.fechaInicio}
              onChange={(e) => handleChange("fechaInicio", e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-activity focus:border-activity outline-none transition-all"
              required
              disabled={loading}
              min={new Date().toISOString().split('T')[0]}
            />
          </div>

          {/* Fecha final */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              Fecha final <span className="text-gray-400">(opcional)</span>
            </label>
            <input
              type="date"
              value={formData.fechaFinal}
              onChange={(e) => handleChange("fechaFinal", e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-activity focus:border-activity outline-none transition-all"
              disabled={loading}
              min={formData.fechaInicio || new Date().toISOString().split('T')[0]}
            />
            <p className="text-xs text-gray-500 mt-1">
              Deja este campo vacío si la actividad no tiene fecha límite
            </p>
          </div>

          {/* Estado */}
          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              Estado
            </label>
            <select
              value={formData.estado}
              onChange={(e) => handleChange("estado", e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-activity focus:border-activity outline-none transition-all"
              disabled={loading}
            >
              <option value="Pendiente">Pendiente</option>
              <option value="En Progreso">En Progreso</option>
              <option value="Completado">Completado</option>
              <option value="Cancelado">Cancelado</option>
            </select>
          </div>
        </div>

        {/* Botón Guardar */}
        <button
          onClick={handleGuardar}
          disabled={loading || !formData.empleadoId}
          className="w-full bg-button hover:bg-button-hover text-white font-bold py-3 px-4 rounded-lg mt-6 transition-all transform hover:scale-[1.02] active:scale-[0.98] shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
        >
          {loading ? (
            <span className="flex items-center justify-center gap-2">
              <svg className="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Guardando...
            </span>
          ) : (
            "Guardar Actividad"
          )}
        </button>

        {/* Texto de ayuda */}
        <p className="text-xs text-gray-500 text-center mt-4">
          <span className="text-red-500">*</span> Campos obligatorios
        </p>
      </div>
    </div>
  );
};

export default NewActivity;