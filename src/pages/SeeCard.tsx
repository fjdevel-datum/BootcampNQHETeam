import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";

interface CardInfo {
  nombre: string;
  numero: string;
  asignacion: string;
  estado: string;
  montoMaximo: number;
  creditoConsumido: number;
}

const SeeCard: React.FC = () => {
  const navigate = useNavigate();
  const [cardData, setCardData] = useState<CardInfo | null>(null);

  useEffect(() => {

    setCardData({
      nombre: "Juan Perez",
      numero: "1234 5678 9101 1123",
      asignacion: "01/09/2025",
      estado: "Activo",
      montoMaximo: 1500,
      creditoConsumido: 750,
    });
  }, []);

  if (!cardData) {
    return (
      <div className="min-h-screen flex items-center justify-center text-black font-montserrat">
        Cargando tarjeta...
      </div>
    );
  }

  const porcentaje = Math.round(
    (cardData.creditoConsumido / cardData.montoMaximo) * 100
  );

  return (
    <div className="min-h-screen bg-background font-montserrat px-4 sm:px-6 lg:px-8">
      {/* Contenedor centralizado */}
      <div className="max-w-3xl mx-auto pt-12 sm:pt-20">
        {/* Botón volver */}
        <button
          onClick={() => navigate(-1)}
          className="flex items-center text-black font-bold mb-6 hover:text-activity transition-colors"
        >
          <ArrowLeft className="w-5 h-5 mr-2" /> Volver
        </button>

        {/* Título */}
        <h1 className="text-2xl sm:text-3xl font-bold text-black mb-8">
          Tarjeta del socio
        </h1>

        {/* Info de la tarjeta */}
        <div className="bg-background rounded-lg ">
          <p>
            <span className="font-semibold">Nombre del socio:</span>{" "}
            {cardData.nombre}
          </p>
          <p>
            <span className="font-semibold">Número de Tarjeta:</span>{" "}
            {cardData.numero}
          </p>
          <p>
            <span className="font-semibold">Fecha de Asignación:</span>{" "}
            {cardData.asignacion}
          </p>
          <p>
            <span className="font-semibold">Estado del recurso asignado:</span>{" "}
            {cardData.estado}
          </p>
          <p>
            <span className="font-semibold">Monto Máximo:</span> $
            {cardData.montoMaximo}
          </p>
          <p>
            <span className="font-semibold">Crédito consumido:</span> $
            {cardData.creditoConsumido}
          </p>
        </div>

        {/* Indicador de consumo */}
        <div className="mt-10 flex flex-col items-center">
          <p className="text-button font-bold text-3xl">{porcentaje}%</p>
          <p className="text-button font-bold text-2xl">
            → ${cardData.creditoConsumido}
          </p>
        </div>
      </div>
    </div>
  );
};

export default SeeCard;
