import React, { useState } from "react";
import { ArrowLeft } from "lucide-react";
import { useNavigate } from "react-router-dom";

interface Incidence {
  id: number;
  tipo: string;
  recurso: string;
  fecha: string;
  detalles: string;
}

const NewIncidence: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState<Incidence>({
    id: Date.now(),
    tipo: "",
    recurso: "",
    fecha: "",
    detalles: "",
  });

  const handleGuardar = () => {
    if (!formData.tipo || !formData.recurso || !formData.fecha) {
      alert("Por favor completa los campos obligatorios.");
      return;
    }

    const stored = localStorage.getItem("incidences");
    const incidences: Incidence[] = stored ? JSON.parse(stored) : [];
    const newList = [...incidences, formData];
    localStorage.setItem("incidences", JSON.stringify(newList));

    alert("✅ Incidencia registrada correctamente");
    navigate("/incidences/list");
  };

  return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center p-4 font-montserrat relative">
      <button
        onClick={() => navigate(-1)}
        className="absolute top-6 left-6 flex items-center gap-2 text-black hover:text-button transition"
      >
        <ArrowLeft className="w-5 h-5" />
      </button>

      <div className="w-full max-w-md bg-white shadow-md rounded-2xl p-6">
        <h1 className="text-black font-bold text-center mb-6 text-button">
          Nueva Incidencia
        </h1>

        <div className="space-y-4">
          <div>
            <label className="block font-semibold mb-1">Tipo de incidencia:</label>
            <select
              value={formData.tipo}
              onChange={(e) => setFormData({ ...formData, tipo: e.target.value })}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-button bg-white"
            >
              <option value="">Seleccionar tipo</option>
              <option value="Extravio">Extravio</option>
              <option value="Daño">Daño</option>
              <option value="Error">Error</option>
            </select>
          </div>

          <div>
            <label className="block font-semibold mb-1">Recurso comprometido:</label>
            <input
              type="text"
              value={formData.recurso}
              onChange={(e) => setFormData({ ...formData, recurso: e.target.value })}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-button"
              placeholder="Ej: 1234 5678 9101 1123"
            />
          </div>

          <div>
            <label className="block font-semibold mb-1">Fecha de incidencia:</label>
            <input
              type="date"
              value={formData.fecha}
              onChange={(e) => setFormData({ ...formData, fecha: e.target.value })}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-button"
            />
          </div>

          <div>
            <label className="block font-semibold mb-1">Detalles:</label>
            <textarea
              value={formData.detalles}
              onChange={(e) => setFormData({ ...formData, detalles: e.target.value })}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 h-24 focus:ring-2 focus:ring-button"
              placeholder="Describe brevemente la incidencia"
            />
          </div>
        </div>

        <button
          onClick={handleGuardar}
          className="w-full bg-button hover:bg-button-hover text-white font-bold py-2 px-4 rounded-lg mt-6 transition-colors"
        >
          Levantar Incidencia
        </button>
      </div>
    </div>
  );
};

export default NewIncidence;
