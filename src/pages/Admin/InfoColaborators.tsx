import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Card from "../../components/Card";

interface Recurso {
  id: number;
  numero: string;
  nombre: string;
  brand: string;
}

interface Colaborador {
  id: number;
  nombre: string;
  apellido: string;
}

const InfoColaborador: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [colaborador, setColaborador] = useState<Colaborador | null>(null);
  const [recursos, setRecursos] = useState<Recurso[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const colabRes = await fetch(`http://localhost:4000/api/colaboradores/${id}`);
        const recursoRes = await fetch(`http://localhost:4000/api/colaboradores/${id}/recursos`);
        const colabData = await colabRes.json();
        const recursoData = await recursoRes.json();
        setColaborador(colabData);
        setRecursos(recursoData);
      } catch (error) {
        console.error("Error al obtener datos del colaborador:", error);
      }
    };
    fetchData();
  }, [id]);

  if (!colaborador) return <div className="p-6">Cargando...</div>;

  return (
    <div className="min-h-screen bg-background p-6 font-montserrat">
      <h1 className="text-black text-xl font-semibold mb-4">
        Información de {colaborador.nombre} {colaborador.apellido}
      </h1>

      <div className="space-y-4">
        {recursos.map((r) => (
          <Card key={r.id} number={r.numero} name={r.nombre} brand={r.brand} />
        ))}
      </div>

      <div className="space-y-3 mt-6">
        <button
          onClick={() => navigate(`/añadir-recurso/${id}`)}
          className="w-full bg-button text-white font-semibold py-3 rounded-lg hover:bg-button-hover"
        >
          Añadir recurso
        </button>
        <button
          className="w-full bg-button text-white font-semibold py-3 rounded-lg hover:bg-button-hover"
        >
          Editar información
        </button>
      </div>
    </div>
  );
};

export default InfoColaborador;
