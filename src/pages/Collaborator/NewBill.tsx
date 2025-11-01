import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft, Receipt, MapPin, Calendar, DollarSign, CreditCard, Sparkles } from "lucide-react";
import { toast } from "../../components/toast";

interface GastoOCR {
  lugar: string;
  tipoGasto: string;
  fecha: string;
  monto: string;
  metodoPago: string;
}

const NewBill: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState<GastoOCR>({
    lugar: "",
    tipoGasto: "",
    fecha: "",
    monto: "",
    metodoPago: "",
  });
  const [loading, setLoading] = useState(false);

  //const apiurl = import.meta.env.VITE_API_URL;
  const handleGoBack = () => navigate(-1);

  // Simula la respuesta del backend/OCR
  const simularOCR = () => {
    const loadingToast = toast.loading("Procesando datos OCR...");
    
    setTimeout(() => {
      const datos: GastoOCR = {
        lugar: "Subway",
        tipoGasto: "Alimentación",
        fecha: "2025-11-08",
        monto: "50.00",
        metodoPago: "Tarjeta Crédito",
      };
      setFormData(datos);
      
      toast.dismiss(loadingToast);
      toast.success("Datos cargados", "Información extraída del comprobante");
    }, 1500);
  };

  const handleGuardar = () => {
    // Validaciones
    if (!formData.lugar.trim()) {
      toast.error("Campo requerido", "Por favor ingresa el lugar del gasto");
      return;
    }

    if (!formData.tipoGasto) {
      toast.error("Campo requerido", "Selecciona el tipo de gasto");
      return;
    }

    if (!formData.fecha) {
      toast.error("Campo requerido", "Selecciona la fecha del gasto");
      return;
    }

    if (!formData.monto || parseFloat(formData.monto) <= 0) {
      toast.error("Monto inválido", "El monto debe ser mayor a 0");
      return;
    }

    if (!formData.metodoPago) {
      toast.error("Campo requerido", "Selecciona el método de pago");
      return;
    }

    setLoading(true);
    const loadingToast = toast.loading("Guardando gasto...");

    // Simular guardado
    setTimeout(() => {
      toast.dismiss(loadingToast);
      toast.success(
        "Gasto registrado",
        `${formData.tipoGasto} - $${parseFloat(formData.monto).toFixed(2)}`
      );
      
      setTimeout(() => {
        navigate(-1);
      }, 1000);
    }, 800);
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
              <p className="text-sm text-gray-500">Registra un nuevo comprobante</p>
            </div>
          </div>
        </div>
      </div>

      {/* Contenido */}
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white shadow-lg rounded-2xl p-6 md:p-8">
          <div className="space-y-6">
            {/* Lugar */}
            <div>
              <label className="flex items-center gap-2 font-semibold text-gray-700 mb-2">
                <MapPin className="w-4 h-4 text-button" />
                Lugar del gasto *
              </label>
              <input
                type="text"
                value={formData.lugar}
                onChange={(e) => setFormData({ ...formData, lugar: e.target.value })}
                className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-button focus:border-button outline-none transition-all"
                placeholder="Ej: Subway, Hotel Marriott"
                disabled={loading}
              />
            </div>

            {/* Tipo de gasto */}
            <div>
              <label className="flex items-center gap-2 font-semibold text-gray-700 mb-2">
                <Receipt className="w-4 h-4 text-button" />
                Tipo de gasto *
              </label>
              <select
                value={formData.tipoGasto}
                onChange={(e) => setFormData({ ...formData, tipoGasto: e.target.value })}
                className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-button focus:border-button outline-none transition-all bg-white"
                disabled={loading}
              >
                <option value="">Selecciona una categoría</option>
                <option value="Alimentación">🍽️ Alimentación</option>
                <option value="Transporte">🚗 Transporte</option>
                <option value="Hospedaje">🏨 Hospedaje</option>
                <option value="Entretenimiento">🎭 Entretenimiento</option>
                <option value="Material de oficina">📎 Material de oficina</option>
                <option value="Otros">📋 Otros</option>
              </select>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Fecha */}
              <div>
                <label className="flex items-center gap-2 font-semibold text-gray-700 mb-2">
                  <Calendar className="w-4 h-4 text-button" />
                  Fecha *
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

              {/* Monto */}
              <div>
                <label className="flex items-center gap-2 font-semibold text-gray-700 mb-2">
                  <DollarSign className="w-4 h-4 text-button" />
                  Monto Total *
                </label>
                <input
                  type="text"
                  value={formData.monto}
                  onChange={(e) => {
                    const value = e.target.value;
                    if (/^\d*\.?\d{0,2}$/.test(value)) {
                      setFormData({ ...formData, monto: value });
                    }
                  }}
                  className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-button focus:border-button outline-none transition-all"
                  placeholder="0.00"
                  inputMode="decimal"
                  disabled={loading}
                />
              </div>
            </div>

            {/* Método de pago */}
            <div>
              <label className="flex items-center gap-2 font-semibold text-gray-700 mb-2">
                <CreditCard className="w-4 h-4 text-button" />
                Método de Pago *
              </label>
              <select
                value={formData.metodoPago}
                onChange={(e) => setFormData({ ...formData, metodoPago: e.target.value })}
                className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-button focus:border-button outline-none transition-all bg-white"
                disabled={loading}
              >
                <option value="">Selecciona el método</option>
                <option value="Efectivo">💵 Efectivo</option>
                <option value="Tarjeta Crédito">💳 Tarjeta de Crédito</option>
                <option value="Tarjeta Débito">💳 Tarjeta de Débito</option>
                <option value="Transferencia">🏦 Transferencia</option>
              </select>
            </div>
          </div>

          {/* Botones de acción */}
          <div className="mt-8 space-y-3">
            <button
              onClick={handleGuardar}
              disabled={loading}
              className="w-full bg-button hover:bg-button-hover text-white font-bold py-3 px-4 rounded-lg transition-all transform hover:scale-[1.02] active:scale-[0.98] shadow-md disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none flex items-center justify-center gap-2"
            >
              {loading ? (
                <>
                  <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                  Guardando...
                </>
              ) : (
                <>
                  <Receipt className="w-5 h-5" />
                  Guardar Gasto
                </>
              )}
            </button>

            <button
              onClick={simularOCR}
              disabled={loading}
              className="w-full bg-gradient-to-r from-purple-500 to-purple-600 hover:from-purple-600 hover:to-purple-700 text-white font-semibold py-3 px-4 rounded-lg transition-all transform hover:scale-[1.02] active:scale-[0.98] shadow-md disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none flex items-center justify-center gap-2"
            >
              <Sparkles className="w-5 h-5" />
              Simular OCR (Demo)
            </button>
          </div>

          <p className="text-xs text-gray-500 text-center mt-6">
            <span className="text-red-500">*</span> Campos obligatorios
          </p>
        </div>

        {/* Información adicional */}
        <div className="mt-6 bg-blue-50 border-l-4 border-blue-500 p-4 rounded-lg">
          <div className="flex items-start gap-3">
            <Sparkles className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
            <div>
              <h4 className="font-semibold text-blue-900 mb-1">💡 Tip: OCR Automático</h4>
              <p className="text-sm text-blue-700">
                Usa el botón "Simular OCR" para ver cómo los datos se extraerían automáticamente de un comprobante escaneado.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default NewBill;