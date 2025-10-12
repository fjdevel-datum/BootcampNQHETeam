import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";

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

  const handleGoBack = () => navigate(-1);

  // 🔹 Simula la respuesta del backend/OCR
  const simularOCR = () => {
    const datos: GastoOCR = {
      lugar: "Subway",
      tipoGasto: "Alimentación",
      fecha: "2025-11-08",
      monto: "50.00",
      metodoPago: "Tarjeta Credito xxxx22",
    };
    setFormData(datos);
  };

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center justify-center p-6 relative font-montserrat">
      {/* 🔹 Botón de regresar arriba a la izquierda */}
      <button
        onClick={handleGoBack}
        className="absolute top-6 left-6 flex items-center gap-2 text-black hover:text-red-600 transition"
      >
        <ArrowLeft className="w-5 h-5" />
      </button>

      {/* 🔹 Contenedor del formulario */}
      <div className="w-full max-w-md bg-white shadow-md rounded-2xl p-6 mt-12">
        <h1 className="text-xl font-bold text-center mb-6 text-red-600">
          Nuevo Gasto
        </h1>

        <div className="space-y-4">
          <div>
            <label className="block font-semibold text-black mb-1">Lugar:</label>
            <input
              type="text"
              value={formData.lugar}
              onChange={(e) =>
                setFormData({ ...formData, lugar: e.target.value })
              }
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-red-600"
            />
          </div>

          <div>
            <label className="block font-semibold text-black mb-1">
              Tipo de gasto:
            </label>
            <input
              type="text"
              value={formData.tipoGasto}
              onChange={(e) =>
                setFormData({ ...formData, tipoGasto: e.target.value })
              }
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-red-600"
            />
          </div>

          <div>
            <label className="block font-semibold text-black mb-1">Fecha:</label>
            <input
              type="date"
              value={formData.fecha}
              onChange={(e) =>
                setFormData({ ...formData, fecha: e.target.value })
              }
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-red-600"
            />
          </div>

          <div>
            <label className="block font-semibold text-black mb-1">
              Monto Total:
            </label>
            <input
              type="text"
              value={formData.monto}
              onChange={(e) =>
                setFormData({ ...formData, monto: e.target.value })
              }
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-red-600"
            />
          </div>

          <div>
            <label className="block font-semibold text-black mb-1">
              Método de Pago:
            </label>
            <input
              type="text"
              value={formData.metodoPago}
              onChange={(e) =>
                setFormData({ ...formData, metodoPago: e.target.value })
              }
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-red-600"
            />
          </div>
        </div>

        {/* 🔹 Botones de acción */}
        <button
          onClick={() => alert("Gasto guardado")}
          className="w-full bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded-lg mt-6 transition-colors"
        >
          Guardar Gasto
        </button>

        <button
          onClick={simularOCR}
          className="w-full bg-gray-200 hover:bg-gray-300 text-black font-semibold py-2 px-4 rounded-lg mt-3 transition-colors"
        >
          Simular datos del OCR
        </button>
      </div>
    </div>
  );
};

export default NewBill;
