import React, { useState, useEffect } from "react";
import Card from "../../components/Card";
import SideMenu from "../../components/SideMenu";
import { useNavigate } from "react-router-dom";

interface CardData {
  id: number;
  number: string;
  description?: string;
  brand?: string; 
}

const Tarjetas: React.FC = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [cards, setCards] = useState<CardData[]>([]);
  const navigate = useNavigate();

  const toggleMenu = () => setMenuOpen((v) => !v);

  useEffect(() => {
    const userId = localStorage.getItem("userId"); 

    if (!userId) {
      console.error("No se encontró el ID del usuario");
      return;
    }

    fetch(`http://localhost:8080/tarjeta/usuario/${userId}`)
      .then((res) => {
        if (!res.ok) throw new Error("Error al cargar las tarjetas");
        return res.json();
      })
      .then((data) => {
        const formattedCards: CardData[] = data.map((t: any) => ({
          id: t.tarjetaId,
          number: t.numeroTarjeta,
          brand: t.tipoTarjetaNombre, // ✅ tomamos exactamente el valor del backend
          description: t.descripcion,
        }));
        setCards(formattedCards);
      })
      .catch((err) => console.error("Error al obtener tarjetas:", err));
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
                name={card.description ?? ""}
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
