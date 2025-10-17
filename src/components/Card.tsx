import React from "react";
import { useNavigate } from "react-router-dom";
import { CreditCard, User } from "lucide-react";

interface CardProps {
  number: string;
  name: string;
  brand?: string; // ✅ ahora acepta cualquier string que venga del backend
}

const Card: React.FC<CardProps> = ({ number, name, brand }) => {
  const navigate = useNavigate();
  const normalizedBrand = brand?.toLowerCase();

  return (
    <div className="bg-red-500 rounded-2xl p-4 shadow-md flex flex-col justify-between w-full max-w-sm mx-auto">
      <div className="flex justify-between items-center">
        <p className="text-white font-montserrat text-lg tracking-wider">
          {number}
        </p>

        <div className="text-white">
          {/* ✅ puedes cambiar los íconos si quieres mostrar logos distintos */}
          {normalizedBrand?.includes("visa") && <CreditCard size={28} />}
          {normalizedBrand?.includes("master") && <CreditCard size={28} />}
          {!normalizedBrand && <CreditCard size={28} />}
        </div>
      </div>

      <div className="flex justify-between items-center mt-6">
        <p className="text-white font-montserrat">{name}</p>
        <User size={28} className="text-white" />
      </div>

      <button
        onClick={() => navigate("/SeeCard")}
        className="mt-4 bg-black text-white font-semibold py-2 px-4 rounded-xl hover:bg-button-hover transition w-full"
      >
        Ver información
      </button>
    </div>
  );
};

export default Card;
