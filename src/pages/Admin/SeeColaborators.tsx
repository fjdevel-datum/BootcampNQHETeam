import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { User } from "lucide-react";

interface Colaborador {
  id: number;
  nombre: string;
  apellido: string;
}

const VerColaboradores: React.FC = () => {
  const navigate = useNavigate();
  const [colaboradores, setColaboradores] = useState<Colaborador[]>([]);
  const [busqueda, setBusqueda] = useState("");

  useEffect(() => {
    const fetchColaboradores = async () => {
      try {
        const res = await fetch("http://localhost:4000/api/colaboradores");
        const data = await res.json();
        setColaboradores(data);
      } catch (error) {
        console.error("Error al obtener colaboradores:", error);
      }
    };
    fetchColaboradores();
  }, []);

  const filtrados = colaboradores.filter((c) =>
    `${c.nombre} ${c.apellido}`.toLowerCase().includes(busqueda.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-background p-6 font-montserrat">
      <h1 className="text-black text-xl font-semibold mb-4">Colaboradores</h1>

      <input
        type="text"
        placeholder="Buscar colaborador"
        value={busqueda}
        onChange={(e) => setBusqueda(e.target.value)}
        className="w-full p-3 border border-gray-300 rounded-lg mb-4"
      />

      <div className="space-y-3">
        {filtrados.map((colab) => (
          <button
            key={colab.id}
            onClick={() => navigate(`/colaborador/${colab.id}`)}
            className="bg-activity text-white flex items-center justify-between p-3 rounded-lg w-full hover:opacity-90"
          >
            <span>
              {colab.nombre} {colab.apellido}
            </span>
            <User size={20} />
          </button>
        ))}
      </div>

      <button
        onClick={() => navigate("/agregar-colaborador")}
        className="mt-6 w-full bg-button text-white font-semibold py-3 rounded-lg hover:bg-button-hover"
      >
        Agregar colaborador
      </button>
    </div>
  );
};

export default VerColaboradores;
