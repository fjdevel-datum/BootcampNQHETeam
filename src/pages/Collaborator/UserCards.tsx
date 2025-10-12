import React, { useState, useEffect } from "react";
import Card from "../../components/Card";
import SideMenu from "../../components/SideMenu";
import { useNavigate } from "react-router-dom";

interface CardData {
  id: number;
  number: string;
  name: string;
  brand: "visa" | "mastercard";
}

const Tarjetas: React.FC = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [cards, setCards] = useState<CardData[]>([]);
  const navigate = useNavigate();

  const toggleMenu = () => setMenuOpen((v) => !v);

  useEffect(() => {
    // 🔹 Simulación: aquí iría tu fetch al backend
    const mockCards: CardData[] = [
      { id: 1, number: "1234 5678 9101 1123", name: "Juan Perez", brand: "mastercard" },
      { id: 2, number: "3211 1091 8765 4321", name: "Juan Perez", brand: "visa" },
      { id: 3, number: "4532 6789 1122 9876", name: "Maria Lopez", brand: "visa" },
    ];
    setCards(mockCards);

    // Ejemplo backend real:
    // fetch("/api/cards")
    //   .then(res => res.json())
    //   .then(data => setCards(data));
  }, []);

  return (
    <div className="min-h-screen bg-background font-montserrat relative">
      {/* Header */}
      <header className="relative flex items-center p-6 bg-background">
        <button
          onClick={toggleMenu}
          aria-label="Abrir menú"
          className="w-10 h-10 rounded-full bg-button hover:bg-button-hover text-white flex items-center justify-center font-bold shadow transition-colors"
        >
          ≡
        </button>
      </header>

      {/* Contenido */}
      <main className="px-4 py-6 md:px-8 max-w-3xl mx-auto">
        <h1 className="text-lg md:text-xl font-bold text-black mb-6">
          Tarjetas del socio
        </h1>

        <div className="space-y-6">
          {cards.length === 0 ? (
            <p className="text-gray-500">No hay tarjetas disponibles.</p>
          ) : (
            cards.map((card) => (
              <Card
                key={card.id}
                number={card.number}
                name={card.name}
                brand={card.brand}
              />
            ))
          )}
        </div>
      </main>

      {/* SideMenu */}
      <SideMenu
        isOpen={menuOpen}
        onClose={() => setMenuOpen(false)}
        currentPage="cards"
        onNavigate={(path) => navigate(path)}
      />
    </div>
  );
};

export default Tarjetas;
