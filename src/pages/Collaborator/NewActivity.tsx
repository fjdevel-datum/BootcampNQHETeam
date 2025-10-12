import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";

interface Activity {
  id: number;
  title: string;
  date: string;
  endDate?: string;
}

const NewActivity: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState<Activity>({
    id: Date.now(),
    title: "",
    date: "",
    endDate: "",
  });

  const handleGoBack = () => navigate(-1);

  const handleGuardar = () => {
    if (!formData.title || !formData.date) {
      alert("Por favor complete los campos obligatorios.");
      return;
    }

    // 🔹 Obtener actividades actuales desde localStorage
    const stored = localStorage.getItem("activities");
    const activities: Activity[] = stored ? JSON.parse(stored) : [];

    const newList = [...activities, formData];
    localStorage.setItem("activities", JSON.stringify(newList));

    alert("✅ Actividad agregada correctamente");
    navigate("/activities"); // 🔹 Redirige de regreso
  };

  return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center p-4 font-montserrat relative">
      <button
        onClick={handleGoBack}
        className="absolute top-6 left-6 flex items-center gap-2 text-black hover:text-activity transition"
      >
        <ArrowLeft className="w-5 h-5" />
      </button>

      {/* 🔹 Contenido principal */}
      <div className="w-full max-w-md bg-background shadow-md rounded-2xl p-6 mt-12">
        <h1 className="text-black font-bold text-center mb-6 text-activity">
          Nueva Actividad
        </h1>

        <div className="space-y-4">
          {/* Nombre */}
          <div>
            <label className="block font-semibold text-black mb-1">
              Nombre:
            </label>
            <input
              type="text"
              value={formData.title}
              onChange={(e) =>
                setFormData({ ...formData, title: e.target.value })
              }
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-activity"
              placeholder="Ingrese el nombre de la actividad"
              required
            />
          </div>

          {/* Fecha inicio */}
          <div>
            <label className="block font-semibold text-black mb-1">
              Fecha de inicio:
            </label>
            <input
              type="date"
              value={formData.date}
              onChange={(e) =>
                setFormData({ ...formData, date: e.target.value })
              }
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-activity"
              required
            />
          </div>

          {/* Fecha final (opcional) */}
          <div>
            <label className="block font-semibold text-black mb-1">
              Fecha final (opcional):
            </label>
            <input
              type="date"
              value={formData.endDate}
              onChange={(e) =>
                setFormData({ ...formData, endDate: e.target.value })
              }
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-activity"
            />
          </div>
        </div>

        <button
          onClick={handleGuardar}
          className="w-full bg-button hover:bg-button-hover text-white font-bold py-2 px-4 rounded-lg mt-6 transition-colors"
        >
          Guardar Actividad
        </button>
      </div>
    </div>
  );
};

export default NewActivity;
