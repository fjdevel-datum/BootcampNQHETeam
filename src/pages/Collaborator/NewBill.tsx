import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import {
  ArrowLeft, Receipt, Calendar, DollarSign,
  Sparkles, Image as ImageIcon, Loader2,
  Coins, CreditCard, Tag
} from "lucide-react";
import { toast } from "../../components/toast";
import { fetchWithAuth, getCurrentUserData } from "../../services/authService";

interface GeminiData {
  Nombre_Pagador: string;
  Fecha: string;
  Monto_Total: number;
  Numero_Tarjeta: string;
  Descripcion_Item: string;
  Cantidad_Item: number;
}

interface LocationState {
  draftId?: string;
  geminiData?: GeminiData;
  imageUrl?: string;
  actividadId?: string;
}

interface OCRStorageData {
  draftId: string;
  geminiData: GeminiData;
  imageUrl: string | null;
  timestamp: number;
  actividadId?: string;
}

interface Moneda {
  monedaId: number;
  codigoISO: string;
  nombreMoneda: string;
  simbolo: string;
}

interface Tarjeta {
  tarjetaId: number;
  tipoTarjetaId: number | null;
  numeroTarjeta: string;
  fechaExpiracion: string | null;
  descripcion: string;
  diaCorte: number;
}

interface TipoGasto {
  tipoGastoId: number;
  nombre: string;
  descripcion: string;
}

const NewBill: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const state = location.state as LocationState;

  const [formData, setFormData] = useState({
    draftId: "",
    descripcion: "",
    fecha: "",
    totalGasto: "",
    totalMonedaBase: "",
    actividadId: "",
    monedaId: "",
    tarjetaId: "",
    recursoId: "",
    tipoGastoId: "",
  });

  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(true);
  const [loadingConversion, setLoadingConversion] = useState(false);
  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const [fromOCR, setFromOCR] = useState(false);

  const [monedas, setMonedas] = useState<Moneda[]>([]);
  const [tarjetas, setTarjetas] = useState<Tarjeta[]>([]);
  const [tiposGasto, setTiposGasto] = useState<TipoGasto[]>([]);

  const apiUrl = import.meta.env.VITE_API_URL;
  const userData = getCurrentUserData();

  // 🔹 Cargar catálogos
  useEffect(() => {
    const loadCatalogs = async () => {
      try {
        setLoadingData(true);
        const monedasData = await fetchWithAuth(`${apiUrl}/moneda/lista`);
        console.log("✅ Monedas cargadas:", monedasData);
        setMonedas(monedasData);

        if (userData?.empleadoId) {
          try {
            const tarjetasData = await fetchWithAuth(`${apiUrl}/tarjeta/usuario/${userData.empleadoId}`);
            setTarjetas(tarjetasData);
          } catch (error: any) {
            if (error.message?.includes('404')) setTarjetas([]);
          }
        }

        const tiposData = await fetchWithAuth(`${apiUrl}/tipoGasto/lista`);
        setTiposGasto(tiposData);

      } catch (error) {
        console.error("❌ Error al cargar datos:", error);
        toast.error("Error al cargar datos", "No se pudieron cargar las opciones disponibles");
      } finally {
        setLoadingData(false);
      }
    };
    loadCatalogs();
  }, [apiUrl]);

  // 🔹 OCR
  useEffect(() => {
    if (state?.actividadId) {
      setFormData(prev => ({ ...prev, actividadId: state.actividadId! }));
    }
    if (state?.draftId && state?.geminiData) {
      loadOCRData(state.draftId, state.geminiData, state.imageUrl);
      return;
    }
    const savedData = sessionStorage.getItem('ocrData');
    if (savedData) {
      const ocrData: OCRStorageData = JSON.parse(savedData);
      const fiveMinutesAgo = Date.now() - (5 * 60 * 1000);
      if (ocrData.timestamp < fiveMinutesAgo) {
        sessionStorage.removeItem('ocrData');
        return;
      }
      loadOCRData(ocrData.draftId, ocrData.geminiData, ocrData.imageUrl);
      if (ocrData.actividadId) {
        setFormData(prev => ({ ...prev, actividadId: ocrData.actividadId! }));
      }
      sessionStorage.removeItem('ocrData');
    }
  }, [state]);

  const loadOCRData = (draftId: string, geminiData: GeminiData, imageUrl?: string | null) => {
    const monto = geminiData.Monto_Total?.toString() || "";
    setFormData(prev => ({
      ...prev,
      draftId,
      descripcion: geminiData.Descripcion_Item || "",
      fecha: geminiData.Fecha || "",
      totalGasto: monto,
      totalMonedaBase: monto,
    }));
    if (imageUrl) setImagePreview(imageUrl);
    setFromOCR(true);
    toast.success("Datos cargados", "Información extraída del comprobante. Completa los campos necesarios.");
  };

  const handleGoBack = () => {
    sessionStorage.removeItem('ocrData');
    navigate(-1);
  };

  // 🔹 Convertir monto usando el endpoint del backend
  const convertirMoneda = async (monto: string, monedaId: string) => {
    if (!monto || !monedaId || !userData?.empleadoId) {
      setFormData(prev => ({ ...prev, totalMonedaBase: monto }));
      return;
    }

    const monedaSeleccionada = monedas.find(m => m.monedaId.toString() === monedaId);
    if (!monedaSeleccionada) {
      setFormData(prev => ({ ...prev, totalMonedaBase: monto }));
      return;
    }

    setLoadingConversion(true);
    
    try {
      const url = `${apiUrl}/moneda/convertir?empleadoId=${userData.empleadoId}&monedaGastoCodigo=${monedaSeleccionada.codigoISO}&monto=${parseFloat(monto)}`;
      console.log("🔄 Convirtiendo moneda:", url);

      const response = await fetchWithAuth(url);
      console.log("✅ Respuesta conversión:", response);

      if (response && response.montoMonedaBase !== undefined) {
        setFormData(prev => ({
          ...prev,
          totalMonedaBase: response.montoMonedaBase.toFixed(2)
        }));
        console.log(`💰 Conversión: ${monto} ${monedaSeleccionada.codigoISO} = ${response.montoMonedaBase.toFixed(2)} USD`);
      } else {
        // Si no hay conversión, usar el monto original
        setFormData(prev => ({ ...prev, totalMonedaBase: monto }));
      }
    } catch (error) {
      console.error("❌ Error al convertir moneda:", error);
      // En caso de error, mantener el monto original
      setFormData(prev => ({ ...prev, totalMonedaBase: monto }));
      toast.warning("Conversión no disponible", "Se usará el monto original");
    } finally {
      setLoadingConversion(false);
    }
  };

  // 🔹 Manejar cambio de monto
  const handleTotalGastoChange = (value: string) => {
    if (!/^\d*\.?\d{0,2}$/.test(value)) return;
    
    setFormData(prev => ({ ...prev, totalGasto: value }));
    
    // Convertir automáticamente si hay moneda seleccionada
    if (formData.monedaId && value) {
      convertirMoneda(value, formData.monedaId);
    } else {
      setFormData(prev => ({ ...prev, totalMonedaBase: value }));
    }
  };

  // 🔹 Manejar cambio de moneda
  const handleMonedaChange = (monedaId: string) => {
    setFormData(prev => ({ ...prev, monedaId }));
    
    // Reconvertir el monto existente con la nueva moneda
    if (formData.totalGasto) {
      convertirMoneda(formData.totalGasto, monedaId);
    }
  };

  const handleTarjetaChange = async (tarjetaId: string) => {
    setFormData({ ...formData, tarjetaId, recursoId: "" });
    if (!tarjetaId) return;

    try {
      if (!userData?.empleadoId) {
        toast.error("Error", "No se pudo identificar al empleado");
        return;
      }

      const recursoData = await fetchWithAuth(
        `${apiUrl}/recursoAsignado/tarjeta/${tarjetaId}/empleado/${userData.empleadoId}`
      );

      if (recursoData && recursoData.recursoId) {
        setFormData(prev => ({ ...prev, recursoId: recursoData.recursoId.toString() }));
        toast.success("Recurso asignado", "Se vinculó la tarjeta con el recurso");
      } else {
        toast.warning("Advertencia", "No se encontró el ID del recurso");
      }
    } catch (error: any) {
      console.error("❌ Error al obtener recurso:", error);
      toast.error("Error", `No se pudo obtener el recurso: ${error.message}`);
    }
  };

  const getUltimos4Digitos = (numeroTarjeta: string): string => {
    if (!numeroTarjeta) return "****";
    return numeroTarjeta.slice(-4);
  };

  const handleGuardar = async () => {
    if (!formData.descripcion.trim()) return toast.error("Campo requerido", "Por favor ingresa la descripción del gasto");
    if (!formData.fecha) return toast.error("Campo requerido", "Selecciona la fecha del gasto");
    if (!formData.totalGasto || parseFloat(formData.totalGasto) <= 0) return toast.error("Monto inválido", "El monto debe ser mayor a 0");
    if (!formData.monedaId) return toast.error("Campo requerido", "Selecciona una moneda");
    if (!formData.tarjetaId) return toast.error("Campo requerido", "Selecciona una tarjeta");
    if (!formData.recursoId) return toast.error("Error", "No se pudo obtener el recurso asociado a la tarjeta");
    if (!formData.tipoGastoId) return toast.error("Campo requerido", "Selecciona un tipo de gasto");
    if (!formData.actividadId) return toast.error("Error", "No se pudo identificar la actividad asociada");
    if (!formData.draftId) return toast.error("Error", "No hay draftId. Debes escanear un comprobante primero.");

    setLoading(true);
    const loadingToast = toast.loading("Guardando gasto...");

    try {
      const gastoDraftDTO = {
        draftId: formData.draftId,
        monedaGasto: parseInt(formData.monedaId),
        recursoId: parseInt(formData.recursoId),
        tipoGastoId: parseInt(formData.tipoGastoId),
        actividadId: parseInt(formData.actividadId),
        totalMonedaBase: parseFloat(formData.totalMonedaBase),
      };

      console.log("📤 Enviando DTO al backend:", gastoDraftDTO);

      const token = localStorage.getItem('token');
      const headers: HeadersInit = { 'Content-Type': 'application/json' };
      if (token) headers['Authorization'] = `Bearer ${token}`;

      const response = await fetch(`${apiUrl}/ocr/guardarGasto/save`, {
        method: "POST",
        headers: headers,
        body: JSON.stringify(gastoDraftDTO),
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error("❌ Error del servidor:", errorText);
        throw new Error(errorText);
      }

      const savedGasto = await response.json();
      console.log("✅ Gasto guardado exitosamente:", savedGasto);

      toast.dismiss(loadingToast);
      toast.success("Gasto registrado", `${formData.descripcion} - $${parseFloat(formData.totalGasto).toFixed(2)}`);

      setTimeout(() => navigate(-1), 1000);

    } catch (err) {
      console.error("❌ Error al guardar:", err);
      toast.dismiss(loadingToast);
      toast.error("Error al guardar", err instanceof Error ? err.message : "No se pudo guardar el gasto");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background relative font-montserrat">
      {/* Header */}
      <div className="bg-white shadow-sm">
        <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <button
            onClick={handleGoBack}
            className="flex items-center gap-2 text-gray-700 hover:text-button transition-colors font-medium mb-4"
            disabled={loading}
          >
            <ArrowLeft className="w-5 h-5" />
            Volver
          </button>
          <div className="flex items-center gap-3">
            <div className="p-3 bg-button rounded-xl">
              <Receipt className="w-8 h-8 text-white" />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Nuevo Gasto</h1>
              <p className="text-sm text-gray-500">
                {fromOCR ? "Datos extraídos con OCR" : "Registra un nuevo comprobante"}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Contenido */}
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Vista previa de la imagen */}
        {imagePreview && (
          <div className="bg-white shadow-lg rounded-2xl p-6 mb-6">
            <div className="flex items-center gap-2 mb-3">
              <ImageIcon className="w-5 h-5 text-button" />
              <h3 className="font-semibold text-gray-700">Comprobante Escaneado</h3>
            </div>
            <img 
              src={imagePreview} 
              alt="Comprobante" 
              className="w-full max-h-64 object-contain rounded-lg bg-gray-100"
            />
          </div>
        )}

        {/* Loading state */}
        {loadingData ? (
          <div className="bg-white shadow-lg rounded-2xl p-12 flex flex-col items-center justify-center">
            <Loader2 className="w-12 h-12 text-button animate-spin mb-4" />
            <p className="text-gray-600 font-medium">Cargando opciones disponibles...</p>
          </div>
        ) : (
          <div className="bg-white shadow-lg rounded-2xl p-6 md:p-8">
            <div className="space-y-6">
              {/* Descripción */}
              <div>
                <label className="flex items-center gap-2 font-semibold text-gray-700 mb-2">
                  <Receipt className="w-4 h-4 text-button" />
                  Descripción del Gasto *
                </label>
                <input
                  type="text"
                  value={formData.descripcion}
                  onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
                  className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-button focus:border-button outline-none transition-all"
                  placeholder="Ej: Almuerzo de negocios"
                  disabled={loading}
                />
              </div>

              {/* Fecha */}
              <div>
                <label className="flex items-center gap-2 font-semibold text-gray-700 mb-2">
                  <Calendar className="w-4 h-4 text-button" />
                  Fecha del Gasto *
                </label>
                <input
                  type="date"
                  value={formData.fecha}
                  onChange={(e) => setFormData({ ...formData, fecha: e.target.value })}
                  className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-button focus:border-button outline-none transition-all"
                  max={new Date().toISOString().split('T')[0]}
                  disabled={loading}
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* Total Gasto */}
                <div>
                  <label className="flex items-center gap-2 font-semibold text-gray-700 mb-2">
                    <DollarSign className="w-4 h-4 text-button" />
                    Monto Total *
                  </label>
                  <input
                    type="text"
                    value={formData.totalGasto}
                    onChange={(e) => handleTotalGastoChange(e.target.value)}
                    className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-button focus:border-button outline-none transition-all"
                    placeholder="0.00"
                    inputMode="decimal"
                    disabled={loading}
                  />
                  <p className="text-xs text-gray-500 mt-1">
                    Monto en {monedas.find(m => m.monedaId.toString() === formData.monedaId)?.nombreMoneda || "moneda seleccionada"}
                  </p>
                </div>

                {/* Total Moneda Base */}
                <div>
                  <label className="flex items-center gap-2 font-semibold text-gray-700 mb-2">
                    <DollarSign className="w-4 h-4 text-button" />
                    Monto en Moneda Base *
                    {loadingConversion && <Loader2 className="w-3 h-3 animate-spin ml-2" />}
                  </label>
                  <input
                    type="text"
                    value={formData.totalMonedaBase}
                    className="w-full border border-gray-300 bg-gray-50 rounded-lg px-4 py-3"
                    disabled
                  />
                  <p className="text-xs text-gray-500 mt-1">
                    {formData.monedaId && formData.totalGasto ? (
                      `Conversión automática aplicada`
                    ) : (
                      'Selecciona una moneda para ver la conversión'
                    )}
                  </p>
                </div>
              </div>

              {/* Selectores */}
              <div className="bg-blue-50 border-l-4 border-blue-500 p-4 rounded-lg space-y-4">
                <h3 className="font-semibold text-blue-900 mb-3 flex items-center gap-2">
                  <Tag className="w-4 h-4" />
                  Información Adicional
                </h3>
                
                {/* Moneda */}
                <div>
                  <label className="flex items-center gap-2 font-semibold text-gray-700 mb-2">
                    <Coins className="w-4 h-4 text-button" />
                    Moneda *
                  </label>
                  <select
                    value={formData.monedaId}
                    onChange={(e) => handleMonedaChange(e.target.value)}
                    className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-button focus:border-button outline-none transition-all bg-white"
                    disabled={loading}
                  >
                    <option value="">Selecciona una moneda</option>
                    {monedas.map((moneda) => (
                      <option key={moneda.monedaId} value={moneda.monedaId}>
                        {moneda.nombreMoneda} ({moneda.codigoISO})
                      </option>
                    ))}
                  </select>
                </div>

                {/* Tarjeta */}
                <div>
                  <label className="flex items-center gap-2 font-semibold text-gray-700 mb-2">
                    <CreditCard className="w-4 h-4 text-button" />
                    Tarjeta *
                  </label>
                  <select
                    value={formData.tarjetaId}
                    onChange={(e) => handleTarjetaChange(e.target.value)}
                    className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-button focus:border-button outline-none transition-all bg-white"
                    disabled={loading}
                  >
                    <option value="">Selecciona una tarjeta</option>
                    {tarjetas.map((tarjeta) => (
                      <option key={tarjeta.tarjetaId} value={tarjeta.tarjetaId}>
                        {tarjeta.descripcion} - **** {getUltimos4Digitos(tarjeta.numeroTarjeta)}
                      </option>
                    ))}
                  </select>
                  {tarjetas.length === 0 && (
                    <p className="text-xs text-amber-600 mt-1">
                      No tienes tarjetas registradas
                    </p>
                  )}
                </div>

                {/* Tipo de Gasto */}
                <div>
                  <label className="flex items-center gap-2 font-semibold text-gray-700 mb-2">
                    <Tag className="w-4 h-4 text-button" />
                    Tipo de Gasto *
                  </label>
                  <select
                    value={formData.tipoGastoId}
                    onChange={(e) => setFormData({ ...formData, tipoGastoId: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-button focus:border-button outline-none transition-all bg-white"
                    disabled={loading}
                  >
                    <option value="">Selecciona un tipo</option>
                    {tiposGasto.map((tipo) => (
                      <option key={tipo.tipoGastoId} value={tipo.tipoGastoId}>
                        {tipo.nombre}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            </div>

            {/* Botón de guardar */}
            <div className="mt-8">
              <button
                onClick={handleGuardar}
                disabled={loading || !formData.draftId || loadingConversion}
                className="w-full bg-button hover:bg-button-hover text-white font-bold py-3 px-4 rounded-lg transition-all transform hover:scale-[1.02] active:scale-[0.98] shadow-md disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none flex items-center justify-center gap-2"
              >
                {loading ? (
                  <>
                    <Loader2 className="w-5 h-5 animate-spin" />
                    Guardando...
                  </>
                ) : loadingConversion ? (
                  <>
                    <Loader2 className="w-5 h-5 animate-spin" />
                    Convirtiendo...
                  </>
                ) : (
                  <>
                    <Receipt className="w-5 h-5" />
                    Guardar Gasto
                  </>
                )}
              </button>
            </div>

            <p className="text-xs text-gray-500 text-center mt-6">
              <span className="text-red-500">*</span> Campos obligatorios
            </p>
          </div>
        )}

        {/* Información OCR */}
        {fromOCR && !loadingData && (
          <div className="mt-6 bg-green-50 border-l-4 border-green-500 p-4 rounded-lg">
            <div className="flex items-start gap-3">
              <Sparkles className="w-5 h-5 text-green-600 mt-0.5 flex-shrink-0" />
              <div>
                <h4 className="font-semibold text-green-900 mb-1">✅ Datos Extraídos con OCR</h4>
                <p className="text-sm text-green-700">
                  La información fue extraída automáticamente. Selecciona las opciones necesarias para guardar.
                </p>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default NewBill;