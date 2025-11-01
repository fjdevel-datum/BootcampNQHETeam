import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "../../components/toast";
import { CreditCard } from "lucide-react";
import Card from "../../components/Card";
import SideMenu from "../../components/SideMenu";
import { fetchWithAuth, getCurrentUserData } from "../../services/authService";

interface CardData {
  tarjetaId: number;
  tipoId: number | null; 
  numeroTarjeta: string;
  fechaExpiracion: string | null;
  descripcion: string;
  estado: string;
}

const UserCards: React.FC = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [cards, setCards] = useState<CardData[]>([]);
  const [loading, setLoading] = useState(true);
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

  // Verificar si la tarjeta está inactiva
  const esTarjetaInactiva = (estado: string | undefined): boolean => {
    return estado?.toLowerCase() === 'inactivo';
  };

  // Determinar tipo de tarjeta según tipoTarjetaId
  const getTipoTarjeta = (tipoId: number | null): "VIATICO" | "CREDITO" | "CORPORATIVA" => {
    if (tipoId === null || tipoId === undefined) return "CORPORATIVA";
    switch (tipoId) {
      case 1: return "VIATICO";
      case 2: return "CREDITO";
      default: return "CORPORATIVA";
    }
  };

  // Obtener nombre del tipo de tarjeta
  const getNombreTipo = (tipoId: number | null): string => {
    const tipo = getTipoTarjeta(tipoId);
    switch (tipo) {
      case "VIATICO": return "Viático";
      case "CREDITO": return "Crédito";
      case "CORPORATIVA": return "Corporativa";
      default: return "Desconocido";
    }
  };

  useEffect(() => {
    const apiurl = import.meta.env.VITE_API_URL;
    const fetchCards = async () => {
      try {
        setLoading(true);
        const userData = getCurrentUserData();
        if (!userData || !userData.empleadoId) {
          toast.error("Error de autenticación", "No se pudo identificar al usuario");
          setLoading(false);
          return;
        }

        const data: CardData[] = await fetchWithAuth(
          `${apiurl}/tarjeta/usuario/${userData.empleadoId}`
        );

        setCards(data);

        if (data.length > 0) {
          const tarjetasInactivas = data.filter(card => esTarjetaInactiva(card.estado));
          if (tarjetasInactivas.length > 0) {
            toast.warning(
              "Tarjetas inactivas", 
              `Tienes ${tarjetasInactivas.length} tarjeta${tarjetasInactivas.length > 1 ? 's' : ''} inactiva${tarjetasInactivas.length > 1 ? 's' : ''}`
            );
          } else {
            toast.success("Tarjetas cargadas", `${data.length} tarjeta${data.length !== 1 ? 's' : ''} disponible${data.length !== 1 ? 's' : ''}`);
          }
        } else {
          toast.info("Sin tarjetas", "No tienes tarjetas asignadas aún");
        }
      } catch (err: any) {
        console.error("Error al cargar tarjetas:", err);
        toast.error("Error al cargar", err.message || "No se pudieron cargar las tarjetas");
      } finally {
        setLoading(false);
      }
    };

    fetchCards();
  }, []);

  const handleNavigate = (path: string) => navigate(path);

  const handleCardClick = (cardId: number) => {
    navigate(`/colaborators/see-card/${cardId}`);
  };

  return (
    <div className="min-h-screen bg-background font-montserrat relative">
      {/* Header */}
      <header className="relative flex items-center justify-between p-6 bg-white shadow-sm">
        <div className="flex items-center gap-3">
          <button
            onClick={toggleMenu}
            aria-label="Abrir menú"
            className="w-10 h-10 rounded-full bg-button hover:bg-button-hover text-white flex items-center justify-center font-bold shadow transition-colors"
          >
            ≡
          </button>
          <div>
            <h1 className="text-xl font-bold text-black">Mis Tarjetas</h1>
            <p className="text-sm text-gray-500">Gestiona tus recursos</p>
          </div>
        </div>
        
        {!loading && cards.length > 0 && (
          <div className="hidden sm:flex items-center gap-2 px-3 py-2 bg-blue-50 rounded-lg">
            <CreditCard className="w-4 h-4 text-blue-600" />
            <span className="text-sm font-semibold text-blue-600">{cards.length}</span>
          </div>
        )}
      </header>

      {/* Contenido */}
      <main className="px-4 py-8 md:px-8 max-w-7xl mx-auto">
        {loading && (
          <div className="flex flex-col justify-center items-center py-20">
            <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-button mb-4"></div>
            <p className="text-gray-600 font-medium">Cargando tarjetas...</p>
            <p className="text-gray-400 text-sm mt-2">Esto tomará solo un momento</p>
          </div>
        )}

        {!loading && (
          <>
            {cards.length === 0 ? (
              <div className="text-center py-20">
                <div className="inline-flex items-center justify-center w-20 h-20 bg-gray-100 rounded-full mb-6">
                  <CreditCard className="w-10 h-10 text-gray-400" />
                </div>
                <h2 className="text-2xl font-bold text-gray-800 mb-2">
                  No tienes tarjetas asignadas
                </h2>
                <p className="text-gray-500 mb-8 max-w-md mx-auto">
                  Cuando tu empresa te asigne tarjetas corporativas o viáticos, aparecerán aquí
                </p>
                <div className="flex flex-col sm:flex-row gap-3 justify-center">
                  <button
                    onClick={() => navigate("/colaborators/activities")}
                    className="px-6 py-3 bg-button hover:bg-button-hover text-white font-semibold rounded-lg transition-colors shadow-md"
                  >
                    Ver Actividades
                  </button>
                  <button
                    onClick={() => navigate("/colaborators/home")}
                    className="px-6 py-3 bg-gray-200 hover:bg-gray-300 text-gray-700 font-semibold rounded-lg transition-colors"
                  >
                    Ir al Inicio
                  </button>
                </div>
              </div>
            ) : (
              <>
                {/* Grid de tarjetas */}
                <div className="mb-8">
                  <h2 className="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
                    <CreditCard className="w-5 h-5 text-button" />
                    Tus Tarjetas
                  </h2>
                  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                    {cards.map((card) => {
                      const inactiva = esTarjetaInactiva(card.estado);
                      return (
                        <div 
                          key={card.tarjetaId}
                          className="relative cursor-pointer transform transition-all hover:scale-105 hover:shadow-2xl"
                          onClick={() => handleCardClick(card.tarjetaId)}
                        >
                          {inactiva && (
                            <div className="absolute top-2 right-2 z-10 bg-red-500 text-white text-xs font-bold px-2 py-1 rounded-full shadow-lg">
                              INACTIVA
                            </div>
                          )}
                          <Card
                            number={card.numeroTarjeta}
                            name={card.descripcion || "Tarjeta corporativa"}
                            expirationDate={formatExpirationDate(card.fechaExpiracion)}
                            tipo={getTipoTarjeta(card.tipoId)}
                          />
                          <div className="mt-3 text-center">
                            <span className="inline-block px-3 py-1 bg-gray-100 text-gray-700 text-xs font-medium rounded-full">
                              {getNombreTipo(card.tipoId)}
                            </span>
                            <p className="text-xs text-gray-500 mt-2 hover:text-button transition">
                              Click para ver detalles
                            </p>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>
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
