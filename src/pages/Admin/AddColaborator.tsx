import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  ArrowLeft,
  UserPlus,
  AlertCircle,
  Loader2,
  Mail,
  Lock,
  User,
  FileText,
  Building2,
  //Briefcase,
} from "lucide-react";
import { fetchWithAuth, getCurrentUserData } from "../../services/authService";

interface CentroCosto {
  centroId: number;
  nombreCentro: string;
  descripcion: string;
  responsable: string;
}

interface FormData {
  email: string;
  password: string;
  confirmPassword: string;
  nombres: string;
  apellidos: string;
  documentoIdentidad: string;
  centroId: string;
  rol: "ADMIN" | "EMPLEADO" | "CONTADOR" | "";
}

interface FormErrors {
  email?: string;
  password?: string;
  confirmPassword?: string;
  nombres?: string;
  apellidos?: string;
  documentoIdentidad?: string;
  centroId?: string;
  rol?: string;
}

const AddColaborator: React.FC = () => {
  const navigate = useNavigate();
  const apiurl = import.meta.env.VITE_API_URL;
  const [empresaId, setEmpresaId] = useState<number | null>(null);
  const [centros, setCentros] = useState<CentroCosto[]>([]);
  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const [formData, setFormData] = useState<FormData>({
    email: "",
    password: "",
    confirmPassword: "",
    nombres: "",
    apellidos: "",
    documentoIdentidad: "",
    centroId: "",
    rol: "",
  });

  const [formErrors, setFormErrors] = useState<FormErrors>({});
  const [touched, setTouched] = useState<Record<string, boolean>>({});

  //  Obtener empresaId usando el servicio de autenticación
  useEffect(() => {
    const initializeData = async () => {
      try {
        setLoadingData(true);
        setError(null);

        // Obtener datos del usuario actual
        const userData = getCurrentUserData();

        if (!userData || !userData.empresaId) {
          setError("No se pudo identificar la empresa del usuario");
          return;
        }

        console.log("EmpresaId obtenido:", userData.empresaId);
        setEmpresaId(userData.empresaId);

      } catch (err: any) {
        console.error("Error al obtener datos del usuario:", err);
        setError(err.message || "Error al cargar datos del usuario");
      } finally {
        setLoadingData(false);
      }
    };

    initializeData();
  }, []);

  // Cargar centros de costo una vez que tengamos empresaId
  useEffect(() => {
    if (!empresaId) return;

    const fetchCentros = async () => {
      try {
        console.log("Cargando centros de costo...");

        // Usar fetchWithAuth del servicio
        const data: CentroCosto[] = await fetchWithAuth(
          `${apiurl}/centroCosto/lista`
        );

        console.log("Centros de costo cargados:", data.length);
        setCentros(data);

      } catch (err: any) {
        console.error("Error al cargar centros:", err);
        setError(err.message || "No se pudieron cargar los centros de costo");
      }
    };

    fetchCentros();
  }, [empresaId]);

  // Validaciones básicas
  const validarEmail = (email: string) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  
  const validarPassword = (password: string) =>
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/.test(password);

  const validarFormulario = (): boolean => {
    const errors: FormErrors = {};

    if (!formData.email.trim()) {
      errors.email = "El email es requerido";
    } else if (!validarEmail(formData.email)) {
      errors.email = "Email inválido";
    }

    if (!formData.password) {
      errors.password = "La contraseña es requerida";
    } else if (!validarPassword(formData.password)) {
      errors.password =
        "Debe tener 8 caracteres, una mayúscula, una minúscula y un número";
    }

    if (!formData.confirmPassword) {
      errors.confirmPassword = "Confirme la contraseña";
    } else if (formData.password !== formData.confirmPassword) {
      errors.confirmPassword = "Las contraseñas no coinciden";
    }

    if (!formData.nombres.trim()) errors.nombres = "Los nombres son requeridos";
    if (!formData.apellidos.trim())
      errors.apellidos = "Los apellidos son requeridos";
    if (!formData.documentoIdentidad.trim())
      errors.documentoIdentidad = "El documento de identidad es requerido";
    if (!formData.centroId)
      errors.centroId = "Debe seleccionar un centro de costo";
    if (!formData.rol) errors.rol = "Debe seleccionar un rol";

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleBlur = (field: string) => {
    setTouched((prev) => ({ ...prev, [field]: true }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validarFormulario()) {
      setError("Por favor corrija los errores del formulario");
      return;
    }

    if (!empresaId) {
      setError("No se pudo obtener el ID de empresa del usuario logueado");
      return;
    }

    setLoading(true);
    setError(null);
    setSuccessMessage(null);

    try {
      const requestData = {
        email: formData.email.trim(),
        password: formData.password,
        nombres: formData.nombres.trim(),
        apellidos: formData.apellidos.trim(),
        documentoIdentidad: formData.documentoIdentidad.trim(),
        empresaId: empresaId,
        centroId: Number(formData.centroId),
        rol: formData.rol,
      };

      console.log("Enviando registro:", requestData);

      // Usar fetchWithAuth para la petición
      await fetchWithAuth(
        `${apiurl}/auth/registro`,
        {
          method: "POST",
          body: JSON.stringify(requestData),
        }
      );

      setSuccessMessage("Colaborador creado exitosamente");
      setFormData({
        email: "",
        password: "",
        confirmPassword: "",
        nombres: "",
        apellidos: "",
        documentoIdentidad: "",
        centroId: "",
        rol: "",
      });

      setTimeout(() => navigate("/admin/see-collaborators"), 2000);
    } catch (err: any) {
      console.error("Error al crear colaborador:", err);
      setError(err.message || "Error al registrar colaborador");
    } finally {
      setLoading(false);
    }
  };

  const handleGoBack = () => navigate("/admin/see-collaborators");

  //  Mostrar loader mientras carga datos iniciales
  if (loadingData) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <Loader2 className="w-10 h-10 animate-spin text-button mx-auto mb-4" />
          <p className="text-gray-600">Cargando datos...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background p-6 font-montserrat">
      <button
        onClick={handleGoBack}
        className="flex items-center gap-2 text-black hover:text-activity transition mb-6"
        disabled={loading}
      >
        <ArrowLeft className="w-5 h-5" />
        Volver
      </button>

      <div className="flex items-center gap-3 mb-6">
        <UserPlus className="w-8 h-8 text-button" />
        <h1 className="text-2xl font-bold text-black">Agregar Colaborador</h1>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-4 flex items-center gap-2">
          <AlertCircle className="w-5 h-5 text-red-600" />
          <p className="text-red-700">{error}</p>
        </div>
      )}

      {successMessage && (
        <div className="bg-green-50 border border-green-200 rounded-lg p-4 mb-4 flex items-center gap-2">
          <svg
            className="w-5 h-5 text-green-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M5 13l4 4L19 7"
            />
          </svg>
          <p className="text-green-700">{successMessage}</p>
        </div>
      )}

      <form onSubmit={handleSubmit} className="bg-white rounded-2xl p-6 shadow-md space-y-6">
        {/* Información personal */}
        <div>
          <h2 className="text-lg font-semibold text-black mb-4 flex items-center gap-2">
            <User className="w-5 h-5" />
            Información Personal
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Nombres */}
            <div>
              <label htmlFor="nombres" className="block text-sm font-semibold text-gray-700 mb-2">
                Nombres <span className="text-red-500">*</span>
              </label>
              <div className="relative">
                <User className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                <input
                  type="text"
                  id="nombres"
                  name="nombres"
                  value={formData.nombres}
                  onChange={handleChange}
                  onBlur={() => handleBlur("nombres")}
                  disabled={loading}
                  className={`w-full pl-10 pr-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-button focus:border-transparent transition ${
                    touched.nombres && formErrors.nombres
                      ? "border-red-500 bg-red-50"
                      : "border-gray-300"
                  }`}
                  placeholder="Juan"
                />
              </div>
              {touched.nombres && formErrors.nombres && (
                <p className="text-xs text-red-500 mt-1 flex items-center gap-1">
                  <AlertCircle className="w-3 h-3" />
                  {formErrors.nombres}
                </p>
              )}
            </div>

            {/* Apellidos */}
            <div>
              <label htmlFor="apellidos" className="block text-sm font-semibold text-gray-700 mb-2">
                Apellidos <span className="text-red-500">*</span>
              </label>
              <div className="relative">
                <User className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                <input
                  type="text"
                  id="apellidos"
                  name="apellidos"
                  value={formData.apellidos}
                  onChange={handleChange}
                  onBlur={() => handleBlur("apellidos")}
                  disabled={loading}
                  className={`w-full pl-10 pr-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-button focus:border-transparent transition ${
                    touched.apellidos && formErrors.apellidos
                      ? "border-red-500 bg-red-50"
                      : "border-gray-300"
                  }`}
                  placeholder="Pérez"
                />
              </div>
              {touched.apellidos && formErrors.apellidos && (
                <p className="text-xs text-red-500 mt-1 flex items-center gap-1">
                  <AlertCircle className="w-3 h-3" />
                  {formErrors.apellidos}
                </p>
              )}
            </div>

            {/* Documento de Identidad */}
            <div>
              <label htmlFor="documentoIdentidad" className="block text-sm font-semibold text-gray-700 mb-2">
                Documento de Identidad <span className="text-red-500">*</span>
              </label>
              <div className="relative">
                <FileText className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                <input
                  type="text"
                  id="documentoIdentidad"
                  name="documentoIdentidad"
                  value={formData.documentoIdentidad}
                  onChange={handleChange}
                  onBlur={() => handleBlur("documentoIdentidad")}
                  disabled={loading}
                  className={`w-full pl-10 pr-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-button focus:border-transparent transition ${
                    touched.documentoIdentidad && formErrors.documentoIdentidad
                      ? "border-red-500 bg-red-50"
                      : "border-gray-300"
                  }`}
                  placeholder="00000000-0"
                />
              </div>
              {touched.documentoIdentidad && formErrors.documentoIdentidad && (
                <p className="text-xs text-red-500 mt-1 flex items-center gap-1">
                  <AlertCircle className="w-3 h-3" />
                  {formErrors.documentoIdentidad}
                </p>
              )}
            </div>

            {/* Email */}
            <div>
              <label htmlFor="email" className="block text-sm font-semibold text-gray-700 mb-2">
                Email <span className="text-red-500">*</span>
              </label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  onBlur={() => handleBlur("email")}
                  disabled={loading}
                  className={`w-full pl-10 pr-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-button focus:border-transparent transition ${
                    touched.email && formErrors.email
                      ? "border-red-500 bg-red-50"
                      : "border-gray-300"
                  }`}
                  placeholder="correo@ejemplo.com"
                />
              </div>
              {touched.email && formErrors.email && (
                <p className="text-xs text-red-500 mt-1 flex items-center gap-1">
                  <AlertCircle className="w-3 h-3" />
                  {formErrors.email}
                </p>
              )}
            </div>
          </div>
        </div>

        {/* Seguridad */}
        <div>
          <h2 className="text-lg font-semibold text-black mb-4 flex items-center gap-2">
            <Lock className="w-5 h-5" />
            Seguridad
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Contraseña */}
            <div>
              <label htmlFor="password" className="block text-sm font-semibold text-gray-700 mb-2">
                Contraseña <span className="text-red-500">*</span>
              </label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                <input
                  type="password"
                  id="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  onBlur={() => handleBlur("password")}
                  disabled={loading}
                  className={`w-full pl-10 pr-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-button focus:border-transparent transition ${
                    touched.password && formErrors.password
                      ? "border-red-500 bg-red-50"
                      : "border-gray-300"
                  }`}
                  placeholder="••••••••"
                />
              </div>
              {touched.password && formErrors.password && (
                <p className="text-xs text-red-500 mt-1 flex items-center gap-1">
                  <AlertCircle className="w-3 h-3" />
                  {formErrors.password}
                </p>
              )}
            </div>

            {/* Confirmar Contraseña */}
            <div>
              <label htmlFor="confirmPassword" className="block text-sm font-semibold text-gray-700 mb-2">
                Confirmar Contraseña <span className="text-red-500">*</span>
              </label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                <input
                  type="password"
                  id="confirmPassword"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  onBlur={() => handleBlur("confirmPassword")}
                  disabled={loading}
                  className={`w-full pl-10 pr-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-button focus:border-transparent transition ${
                    touched.confirmPassword && formErrors.confirmPassword
                      ? "border-red-500 bg-red-50"
                      : "border-gray-300"
                  }`}
                  placeholder="••••••••"
                />
              </div>
              {touched.confirmPassword && formErrors.confirmPassword && (
                <p className="text-xs text-red-500 mt-1 flex items-center gap-1">
                  <AlertCircle className="w-3 h-3" />
                  {formErrors.confirmPassword}
                </p>
              )}
            </div>
          </div>
        </div>

        {/* Información Organizacional */}
        <div>
          <h2 className="text-lg font-semibold text-black mb-4 flex items-center gap-2">
            <Building2 className="w-5 h-5" />
            Información Organizacional
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Centro de Costo */}
            <div>
              <label htmlFor="centroId" className="block text-sm font-semibold text-gray-700 mb-2">
                Centro de Costo <span className="text-red-500">*</span>
              </label>
              <select
                id="centroId"
                name="centroId"
                value={formData.centroId}
                onChange={handleChange}
                onBlur={() => handleBlur("centroId")}
                disabled={loading || centros.length === 0}
                className={`w-full px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-button focus:border-transparent transition ${
                  touched.centroId && formErrors.centroId
                    ? "border-red-500 bg-red-50"
                    : "border-gray-300"
                }`}
              >
                <option value="">Seleccione un centro de costo</option>
                {centros.map((centro) => (
                  <option key={centro.centroId} value={centro.centroId}>
                    {centro.nombreCentro}
                  </option>
                ))}
              </select>
              {touched.centroId && formErrors.centroId && (
                <p className="text-xs text-red-500 mt-1 flex items-center gap-1">
                  <AlertCircle className="w-3 h-3" />
                  {formErrors.centroId}
                </p>
              )}
            </div>

            {/* Rol */}
            <div>
              <label htmlFor="rol" className="block text-sm font-semibold text-gray-700 mb-2">
                Rol <span className="text-red-500">*</span>
              </label>
              <select
                id="rol"
                name="rol"
                value={formData.rol}
                onChange={handleChange}
                onBlur={() => handleBlur("rol")}
                disabled={loading}
                className={`w-full px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-button focus:border-transparent transition ${
                  touched.rol && formErrors.rol
                    ? "border-red-500 bg-red-50"
                    : "border-gray-300"
                }`}
              >
                <option value="">Seleccione un rol</option>
                <option value="ADMIN">Administrador</option>
                <option value="EMPLEADO">Empleado</option>
                <option value="CONTADOR">Contador</option>
              </select>
              {touched.rol && formErrors.rol && (
                <p className="text-xs text-red-500 mt-1 flex items-center gap-1">
                  <AlertCircle className="w-3 h-3" />
                  {formErrors.rol}
                </p>
              )}
            </div>
          </div>
        </div>

        {/* Botón */}
        <button
          type="submit"
          disabled={loading}
          className="w-full bg-button text-white font-semibold py-3 rounded-lg hover:bg-button-hover transition-all flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {loading ? (
            <Loader2 className="w-5 h-5 animate-spin" />
          ) : (
            <UserPlus className="w-5 h-5" />
          )}
          {loading ? "Creando..." : "Crear Colaborador"}
        </button>
      </form>
    </div>
  );
};

export default AddColaborator;