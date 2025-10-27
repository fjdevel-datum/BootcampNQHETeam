import React, { useState, useEffect } from "react";
import Card from "../../components/Card";
import SideMenu from "../../components/SideMenu";
import { useNavigate } from "react-router-dom";
import { fetchWithAuth, getCurrentUserData } from "../../services/authService";

interface CardData {
  tarjetaId: number;
  tipoId: number | null; 
  numeroTarjeta: string;
  fechaExpiracion: string | null;
  descripcion: string;
}

const UserCards: React.FC = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [cards, setCards] = useState<CardData[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const toggleMenu = () => setMenuOpen((v) => !v);

  // Formatear fecha de expiración
  const formatExpirationDate = (fecha: string | null): string => {
    if (!fecha) return "";
    try {
      const date = new Date(fecha);
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const year = String(date.getFullYear()).slice(-2);
      return `${month}/${year}`;
    } catch {
      return "";
    }
  };

  // Determinar tipo de tarjeta según tipoTarjetaId
  const getTipoTarjeta = (tipoId: number | null): "VIATICO" | "CREDITO" | "CORPORATIVA" => {
    console.log("getTipoTarjeta recibió:", tipoId, typeof tipoId);
    
    // Manejar null/undefined
    if (tipoId === null || tipoId === undefined) {
      return "CORPORATIVA";
    }
    
    // Convertir a número por si viene como string
    const tipo = Number(tipoId);
    
    switch (tipo) {
      case 1: return "VIATICO";
      case 2: return "CREDITO";
      default: return "CORPORATIVA";
    }
  };

  useEffect(() => {
    const apiurl = import.meta.env.VITE_API_URL;
    const fetchCards = async () => {
      try {
        setLoading(true);
        setError(null);

        // Obtener datos del usuario logueado
        const userData = getCurrentUserData();
        if (!userData || !userData.empleadoId) {
          setError("No se pudo identificar al usuario");
          return;
        }

        const data: CardData[] = await fetchWithAuth(
          `${apiurl}/tarjeta/usuario/${userData.empleadoId}`
        );
        
        // Debug: verificar los datos recibidos
        console.log("Tarjetas recibidas:", data);
        data.forEach(card => {
          console.log(`Tarjeta ${card.numeroTarjeta}:`, {
            tipoId: card.tipoId,
            descripcion: card.descripcion
          });
        });
        
        setCards(data);
      } catch (err: any) {
        console.error("Error al cargar tarjetas:", err);
        if (err.message.includes("404") || err.message.includes("No se encontraron")) {
          setCards([]);
        } else {
          setError(err.message || "No se pudieron cargar las tarjetas");
        }
      } finally {
        setLoading(false);
      }
    };

    fetchCards();
  }, []);

  const handleNavigate = (path: string) => navigate(path);

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
        <h1 className="ml-4 text-xl font-semibold text-black">Mis Tarjetas</h1>
      </header>

      {/* Contenido */}
      <main className="px-4 py-6 md:px-8 max-w-5xl mx-auto">
        {/* Loading */}
        {loading && (
          <div className="flex flex-col justify-center items-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-button mb-4"></div>
            <p className="text-gray-600">Cargando tarjetas...</p>
          </div>
        )}

        {/* Error */}
        {error && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
            <p className="text-red-700 text-center">{error}</p>
          </div>
        )}

        {!loading && !error && (
          <>
            {cards.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-500 text-lg mb-2">No tienes tarjetas registradas</p>
                <p className="text-gray-400 text-sm">
                  Las tarjetas asignadas aparecerán aquí
                </p>
              </div>
            ) : (
              <>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 justify-items-center">
                  {cards.map((card) => (
                    <Card
                      key={card.tarjetaId}
                      number={card.numeroTarjeta}
                      name={card.descripcion || "Tarjeta corporativa"}
                      expirationDate={formatExpirationDate(card.fechaExpiracion)}
                      tipo={getTipoTarjeta(card.tipoId)}
                    />
                  ))}
                </div>

                <div className="mt-6 p-4 bg-blue-50 rounded-lg text-center">
                  <p className="text-sm text-gray-700">
                     Total de recursos: <span className="font-semibold">{cards.length}</span>
                  </p>
                </div>

                <button
                  onClick={() => navigate("/colaborators/see-card")}
                  className="mt-8 w-full bg-button text-white font-semibold py-3 rounded-lg hover:bg-button-hover transition-all shadow-md"
                >
                  Ver detalles de tarjetas
                </button>
              </>
            )}
          </>
        )}
      </main>

      {/* SideMenu */}
      <SideMenu
        isOpen={menuOpen}
        onClose={() => setMenuOpen(false)}
        currentPage="cards"
        onNavigate={handleNavigate}
      />
    </div>
  );
};

export default UserCards;