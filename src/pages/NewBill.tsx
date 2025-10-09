import React, { useState } from "react";

interface GastoOCR {
  lugar: string;
  tipoGasto: string;
  fecha: string;
  monto: string;
  metodoPago: string;
}

const NewBill: React.FC = () => {
  const [formData, setFormData] = useState<GastoOCR>({
    lugar: "",
    tipoGasto: "",
    fecha: "",
    monto: "",
    metodoPago: "",
  });

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
    <div className="min-h-screen bg-gray-100 flex flex-col items-center p-6">
      <div className="w-full max-w-md bg-white shadow-md rounded-xl p-6">
        <h1 className="text-xl font-bold text-center mb-6">Nuevo Gasto</h1>

        <div className="space-y-4">
          <div>
            <label className="block font-semibold">Lugar:</label>
            <input
              type="text"
              value={formData.lugar}
              onChange={(e) =>
                setFormData({ ...formData, lugar: e.target.value })
              }
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>

          <div>
            <label className="block font-semibold">Tipo de gasto:</label>
            <input
              type="text"
              value={formData.tipoGasto}
              onChange={(e) =>
                setFormData({ ...formData, tipoGasto: e.target.value })
              }
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>

          <div>
            <label className="block font-semibold">Fecha:</label>
            <input
              type="date"
              value={formData.fecha}
              onChange={(e) =>
                setFormData({ ...formData, fecha: e.target.value })
              }
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>

          <div>
            <label className="block font-semibold">Monto Total:</label>
            <input
              type="text"
              value={formData.monto}
              onChange={(e) =>
                setFormData({ ...formData, monto: e.target.value })
              }
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>

          <div>
            <label className="block font-semibold">Método de Pago:</label>
            <input
              type="text"
              value={formData.metodoPago}
              onChange={(e) =>
                setFormData({ ...formData, metodoPago: e.target.value })
              }
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>
        </div>

        <button
          onClick={() => alert("Gasto guardado")}
          className="w-full bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded-lg mt-6"
        >
          Guardar Gasto
        </button>

        {/* 🔹 Solo para pruebas mientras no esté el backend */}
        <button
          onClick={simularOCR}
          className="w-full bg-gray-200 hover:bg-gray-300 text-black font-semibold py-2 px-4 rounded-lg mt-3"
        >
          Simular datos del OCR
        </button>
      </div>
    </div>
  );
};

export default NewBill;
