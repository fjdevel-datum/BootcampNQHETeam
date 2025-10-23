import React, { useState, useEffect } from "react";
import Card from "../../components/Card";
import SideMenu from "../../components/SideMenu";
import { useNavigate } from "react-router-dom";
import { fetchWithAuth, getCurrentUserData } from "../../services/authService";

interface CardData {
  tarjetaId: number;
  tipoTarjetaId: number | null;
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

  useEffect(() => {
    const fetchCards = async () => {
      try {
        setLoading(true);
        setError(null);

        // Obtener datos del usuario autenticado
        const userData = getCurrentUserData();

        if (!userData || !userData.empleadoId) {
          setError("No se pudo identificar al usuario");
          return;
        }

        console.log("Cargando tarjetas del empleado ID:", userData.empleadoId);

        // Llamar al endpoint con autenticación
        const data: CardData[] = await fetchWithAuth(
          `http://localhost:8080/tarjeta/usuario/${userData.empleadoId}`
        );

        console.log("Tarjetas obtenidas:", data);
        setCards(data);
      } catch (err: any) {
        console.error("Error al cargar tarjetas:", err);
        
        // Si el error es 404 (no hay tarjetas), no mostrar como error
        if (err.message.includes("404") || err.message.includes("No se encontraron")) {
          console.log("ℹEl usuario no tiene tarjetas registradas");
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

  const handleNavigate = (path: string) => {
    navigate(path);
  };

  // Función para obtener el nombre de la marca según el tipoTarjetaId
  const getBrandName = (tipoTarjetaId: number | null): string => {
    switch (tipoTarjetaId) {
      case 1:
        return "Visa";
      case 2:
        return "Mastercard";
      case 3:
        return "American Express";
      default:
        return "Tarjeta";
    }
  };

  // Función para formatear la fecha de expiración
  const formatExpirationDate = (fecha: string | null): string => {
    if (!fecha) return "";
    
    try {
      const date = new Date(fecha);
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const year = String(date.getFullYear()).slice(-2);
      return `${month}/${year}`;
    } catch {
      return "";
    }
  };

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
      <main className="px-4 py-6 md:px-8 max-w-3xl mx-auto">
        {/* Estado de carga */}
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

        {/* Lista de tarjetas */}
        {!loading && !error && (
          <>
            {cards.length === 0 ? (
              <div className="text-center py-12">
                <svg
                  className="w-16 h-16 mx-auto text-gray-400 mb-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"
                  />
                </svg>
                <p className="text-gray-500 text-lg mb-2">No tienes tarjetas registradas</p>
                <p className="text-gray-400 text-sm">
                  Las tarjetas asignadas aparecerán aquí
                </p>
              </div>
            ) : (
              <>
                <div className="space-y-4">
                  {cards.map((card) => (
                    <Card
                      key={card.tarjetaId}
                      number={card.numeroTarjeta}
                      name={card.descripcion || "Tarjeta corporativa"}
                      brand={getBrandName(card.tipoTarjetaId)}
                      expirationDate={formatExpirationDate(card.fechaExpiracion)}
                    />
                  ))}
                </div>

                {/* Información adicional */}
                <div className="mt-6 p-4 bg-blue-50 rounded-lg">
                  <p className="text-sm text-gray-700 text-center">
                    💳 Total de tarjetas: <span className="font-semibold">{cards.length}</span>
                  </p>
                </div>
              </>
            )}
          </>
        )}

        {/* Botón para ver detalles (opcional) */}
        {!loading && !error && cards.length > 0 && (
          <button
            onClick={() => navigate("/colaborators/see-card")}
            className="mt-8 w-full bg-button text-white font-semibold py-3 rounded-lg hover:bg-button-hover transition-all transform hover:scale-[1.02] active:scale-[0.98] shadow-md"
          >
            Ver detalles de tarjetas
          </button>
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