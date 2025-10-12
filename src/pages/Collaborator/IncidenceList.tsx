import React, { useEffect, useState } from "react";
import { ArrowLeft } from "lucide-react";
import { useNavigate } from "react-router-dom";

interface Incidence {
  id: number;
  tipo: string;
  recurso: string;
  fecha: string;
  detalles: string;
}

const IncidenceList: React.FC = () => {
  const navigate = useNavigate();
  const [incidences, setIncidences] = useState<Incidence[]>([]);

  useEffect(() => {
    const stored = localStorage.getItem("incidences");
    if (stored) setIncidences(JSON.parse(stored));
  }, []);

  return (
    <div className="min-h-screen bg-background flex flex-col items-center p-4 font-montserrat relative">
      <button
        onClick={() => navigate(-1)}
        className="absolute top-6 left-6 flex items-center gap-2 text-black hover:text-button transition"
      >
        <ArrowLeft className="w-5 h-5" />
      </button>

      <h1 className="text-xl font-bold text-black mt-12 mb-6">
        Incidencias Reportadas
      </h1>

      <div className="w-full max-w-md space-y-3">
        {incidences.length === 0 ? (
          <p className="text-gray-500 text-center">
            No hay incidencias registradas.
          </p>
        ) : (
          incidences.map((inc) => (
            <div
              key={inc.id}
              className="flex justify-between items-center border border-gray-300 rounded-lg p-4 bg-white shadow-sm"
            >
              <div>
                <p className="font-semibold text-black">{inc.tipo}</p>
                <p className="text-sm text-gray-500">{inc.fecha}</p>
              </div>
              <button
                onClick={() => alert(`Detalles:\n${inc.detalles}`)}
                className="bg-gray-200 hover:bg-gray-300 text-black px-3 py-1 rounded-md text-sm font-semibold"
              >
                Ver
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default IncidenceList;
