import React from "react";

interface CardProps {
  number: string; 
  name: string;
  brand?: string;
  expirationDate?: string;
}

const Card: React.FC<CardProps> = ({ number, name, brand = "Tarjeta", expirationDate }) => {
  // Función para obtener el color según la marca
  const getBrandColor = () => {
    switch (brand.toLowerCase()) {
      case "visa":
        return "bg-gradient-to-br from-blue-600 to-blue-800";
      case "mastercard":
        return "bg-gradient-to-br from-red-600 to-orange-600";
      case "american express":
        return "bg-gradient-to-br from-green-600 to-teal-600";
      default:
        return "bg-gradient-to-br from-gray-600 to-gray-800";
    }
  };

  // Función para enmascarar el número de tarjeta
  const maskCardNumber = (num: string) => {
    if (num.length < 4) return num;
    const lastFour = num.slice(-4);
    return `•••• •••• •••• ${lastFour}`;
  };

  return (
    <div className={`${getBrandColor()} rounded-2xl p-6 text-white shadow-lg transform transition-all hover:scale-105`}>
      {/* Header con marca */}
      <div className="flex justify-between items-start mb-8">
        <div>
          <p className="text-xs opacity-80 uppercase tracking-wider">Tarjeta Corporativa</p>
          <p className="text-sm font-semibold mt-1">{brand}</p>
        </div>
        <svg
          className="w-12 h-12 opacity-80"
          fill="currentColor"
          viewBox="0 0 24 24"
        >
          <path d="M20 8H4V6h16v2zm0 2H4v6h16v-6zm0 8H4v2h16v-2z" />
        </svg>
      </div>

      {/* Número de tarjeta */}
      <div className="mb-6">
        <p className="text-xl font-mono tracking-wider">
          {maskCardNumber(number)}
        </p>
      </div>

      {/* Footer con nombre y expiración */}
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