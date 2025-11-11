import React from "react";

interface CardProps {
  number: string;
  name: string;
  expirationDate?: string;
  tipo?: "VIATICO" | "CREDITO" | "CORPORATIVA";
}

const Card: React.FC<CardProps> = ({ number, name, expirationDate, tipo = "CORPORATIVA" }) => {
  const maskCardNumber = (num: string) => {
    if (num.length < 4) return num;
    const lastFour = num.slice(-4);
    return `•••• •••• •••• ${lastFour}`;
  };

  // Colores según tipo de tarjeta
  const bgColor =
    tipo === "CREDITO" ? "bg-black" :
    tipo === "VIATICO" ? "bg-button" :
    "bg-gray-700";

  const textColor = "text-white";

  const headerText =
    tipo === "CREDITO" ? "Tarjeta de Crédito" :
    tipo === "VIATICO" ? "Viático" :
    "Tarjeta Corporativa";

  return (
    <div className={`${bgColor} rounded-2xl p-6 shadow-lg transform transition-all hover:scale-105 ${textColor} w-full`}>
      <div className="flex justify-between items-start mb-8">
        <p className="text-xs opacity-80 uppercase tracking-wider">{headerText}</p>
        <svg className="w-12 h-12 opacity-80" fill="currentColor" viewBox="0 0 24 24">
          <path d="M20 8H4V6h16v2zm0 2H4v6h16v-6zm0 8H4v2h16v-2z" />
        </svg>
      </div>

      <div className="mb-6">
        <p className="text-xl font-mono tracking-wider text-center">{maskCardNumber(number)}</p>
      </div>

      <div className="flex justify-between items-end">
        <div>
          <p className="text-xs opacity-80">Titular</p>
          <p className="text-sm font-semibold mt-1">{name}</p>
        </div>
        {expirationDate && (
          <div className="text-right">
            <p className="text-xs opacity-80">Válida hasta</p>
            <p className="text-sm font-semibold mt-1">{expirationDate}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Card;
