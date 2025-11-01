import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import { toast } from "../../components/toast";
import ConfirmModal from "../../components/ConfirmModal";

interface BillData {
  lugar: string;
  tipo: string;
  fecha: string;
  monto: string | number;
  moneda: string;
  metodoPago: string;
  photo?: string;
}

const BillCheck: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const initialData: BillData = location.state?.bill || {
    lugar: "",
    tipo: "Alimentación",
    fecha: new Date().toISOString().split("T")[0],
    monto: 0,
    moneda: "USD",
    metodoPago: "Efectivo",
    photo: "",
  };

  const [formData, setFormData] = useState<BillData>(initialData);
  const [showSaveModal, setShowSaveModal] = useState(false);
  const [showBackModal, setShowBackModal] = useState(false);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === "monto" ? parseFloat(value) || 0 : value,
    }));
  };

  const handleSaveConfirm = () => {
    // Validaciones
    if (!formData.lugar.trim()) {
      toast.error("Campo requerido", "Por favor ingrese el lugar del gasto");
      setShowSaveModal(false);
      return;
    }

    if (!formData.monto || parseFloat(formData.monto.toString()) <= 0) {
      toast.error("Monto inválido", "El monto debe ser mayor a 0");
      setShowSaveModal(false);
      return;
    }

    const loadingToast = toast.loading("Guardando gasto...");

    // Simular guardado (reemplazar con tu API call)
    setTimeout(() => {
      console.log("💾 Gasto guardado:", formData);
      
      toast.dismiss(loadingToast);
      toast.success(
        "Gasto guardado",
        `${formData.tipo} - $${parseFloat(formData.monto.toString()).toFixed(2)} ${formData.moneda}`
      );

      setShowSaveModal(false);
      
      setTimeout(() => {
        navigate(-1);
      }, 1000);
    }, 800);
  };

  const handleBackConfirm = () => {
    toast.info("Gasto cancelado", "El gasto no fue registrado");
    setShowBackModal(false);
    navigate(-1);
  };

  return (
    <div className="min-h-screen bg-background relative">
      {/* Flecha arriba del todo */}
      <button
        onClick={() => setShowBackModal(true)}
        className="absolute top-4 left-4 text-black hover:text-red-600 transition"
      >
        <ArrowLeft className="w-7 h-7" />
      </button>

      {/* Contenedor del formulario */}
      <div className="flex justify-center pt-16 px-6">
        <div className="bg-background shadow-lg rounded-xl p-6 w-full max-w-md">
          <h2 className="text-xl font-bold mb-6 text-center">Nuevo Gasto</h2>

          {formData.photo && (
            <img
              src={formData.photo}
              alt="Comprobante"
              className="w-full h-40 object-contain mb-4 rounded-lg border"
            />
          )}

          {/* Lugar */}
          <label className="block mb-2 text-sm font-medium">Lugar: *</label>
          <input
            type="text"
            name="lugar"
            value={formData.lugar}
            onChange={handleChange}
            className="w-full border rounded-lg px-3 py-2 mb-4 focus:ring-2 focus:ring-button focus:border-button outline-none"
            placeholder="Ejemplo: Subway"
            required
          />

          {/* Tipo */}
          <label className="block mb-2 text-sm font-medium">Tipo de gasto: *</label>
          <select
            name="tipo"
            value={formData.tipo}
            onChange={handleChange}
            className="w-full border rounded-lg px-3 py-2 mb-4 focus:ring-2 focus:ring-button focus:border-button outline-none"
          >
            <option value="Alimentación">Alimentación</option>
            <option value="Transporte">Transporte</option>
            <option value="Hospedaje">Hospedaje</option>
            <option value="Entretenimiento">Entretenimiento</option>
            <option value="Otros">Otros</option>
          </select>

          {/* Fecha */}
          <label className="block mb-2 text-sm font-medium">Fecha: *</label>
          <input
            type="date"
            name="fecha"
            value={formData.fecha}
            onChange={handleChange}
            className="w-full border rounded-lg px-3 py-2 mb-4 focus:ring-2 focus:ring-button focus:border-button outline-none"
            max={new Date().toISOString().split("T")[0]}
          />

          {/* Monto */}
          <label className="block mb-2 text-sm font-medium">Monto Total: *</label>
          <input
            type="text"
            name="monto"
            value={formData.monto}
            onChange={(e) => {
              const value = e.target.value;
              if (/^\d*\.?\d{0,2}$/.test(value)) {
                setFormData({ ...formData, monto: value });
              }
            }}
            inputMode="decimal"
            placeholder="0.00"
            className="w-full border rounded-lg px-3 py-2 mb-4 no-arrows focus:ring-2 focus:ring-button focus:border-button outline-none"
            required
          />

          {/* Moneda */}
          <label className="block mb-2 text-sm font-medium">Moneda: *</label>
          <select
            name="moneda"
            value={formData.moneda}
            onChange={handleChange}
            className="w-full border rounded-lg px-3 py-2 mb-4 focus:ring-2 focus:ring-button focus:border-button outline-none"
          >
            <option value="USD">USD - Dólar</option>
            <option value="EUR">EUR - Euro</option>
            <option value="MXN">MXN - Peso Mexicano</option>
            <option value="CRC">CRC - Colón</option>
          </select>

          {/* Método de pago */}
          <label className="block mb-2 text-sm font-medium">Método de Pago: *</label>
          <select
            name="metodoPago"
            value={formData.metodoPago}
            onChange={handleChange}
            className="w-full border rounded-lg px-3 py-2 mb-6 focus:ring-2 focus:ring-button focus:border-button outline-none"
          >
            <option value="Efectivo">Efectivo</option>
            <option value="Tarjeta Crédito">Tarjeta Crédito</option>
            <option value="Tarjeta Débito">Tarjeta Débito</option>
            <option value="Transferencia">Transferencia</option>
          </select>

          {/* Botón guardar */}
          <button
            onClick={() => setShowSaveModal(true)}
            className="w-full py-3 bg-red-600 hover:bg-red-700 text-white rounded-lg font-semibold transition-colors"
          >
            Guardar Gasto
          </button>
        </div>
      </div>

      {/* Modal Guardar */}
      {showSaveModal && (
        <ConfirmModal
          message="¿Está seguro que los datos están correctos?"
          onConfirm={handleSaveConfirm}
          onCancel={() => setShowSaveModal(false)}
        />
      )}

      {/* Modal Regresar */}
      {showBackModal && (
        <ConfirmModal
          message="¿Está seguro que no quiere registrar el gasto?"
          onConfirm={handleBackConfirm}
          onCancel={() => setShowBackModal(false)}
        />
      )}
    </div>
  );
};

export default BillCheck;