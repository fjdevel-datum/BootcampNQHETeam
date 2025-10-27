import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ArrowLeft, CreditCard } from "lucide-react";
import Card from "../../components/Card";
import { fetchWithAuth } from "../../services/authService";
import { useLocation } from "react-router-dom";



interface Recurso {
  tarjetaId: number;
  tipoId: number | null;
  numeroTarjeta: string;
  fechaExpiracion: string | null;
  descripcion: string;
}

interface Colaborador {
  empleadoId: number;
  nombres: string;
  apellidos: string;
  documentoIdentidad: string;
  rol: string;
  empresaId: number;
}



const InfoColaborators: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { id } = useParams<{ id: string }>();
  const [colaborador, setColaborador] = useState<Colaborador | null>(null);
  const [recursos, setRecursos] = useState<Recurso[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const apiurl = import.meta.env.VITE_API_URL;

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);

        if (!id) {
          setError("ID de colaborador no válido");
          return;
        }

        const colabData: Colaborador = await fetchWithAuth(`${apiurl}/empleado/${id}`);
        setColaborador(colabData);

        try {
          const recursoData: Recurso[] = await fetchWithAuth(`${apiurl}/tarjeta/usuario/${id}`);
          setRecursos(recursoData);
        } catch (err: any) {
          if (err.message.includes("404") || err.message.includes("No se encontraron")) {
            setRecursos([]);
          } else throw err;
        }
      } catch (err: any) {
        setError(err.message || "No se pudo cargar la información del colaborador");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

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

const handleGoBack = () => {
  // Si venimos desde SeeCollaborators, usar -1 para mantener historial
  if (location.state?.fromSee) {
    navigate(-1);
  } else {
    // fallback seguro
    navigate("/admin/see-collaborators");
  }
};  const getTipoTarjeta = (tipoId: number | null): "VIATICO" | "CREDITO" | "CORPORATIVA" => {
    switch (tipoId) {
      case 1: return "VIATICO";
      case 2: return "CREDITO";
      default: return "CORPORATIVA";
    }
  };

  const handleCardClick = (tarjetaId: number) => {
  navigate(`/admin/edit-card/${tarjetaId}/${colaborador?.empleadoId}`, {
    state: { fromInfo: `/admin/info-colaborators/${colaborador?.empleadoId}` }
  });
};


  if (loading) {
    return (
      <div className="min-h-screen bg-background flex flex-col items-center justify-center p-6">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-button mb-4"></div>
        <p className="text-gray-600">Cargando información...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-background p-6">
        <button
          onClick={handleGoBack}
          className="flex items-center gap-2 text-black hover:text-activity transition mb-4"
        >
          <ArrowLeft className="w-5 h-5" />
          Volver
        </button>
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-700 text-center">{error}</p>
        </div>
      </div>
    );
  }

  if (!colaborador) {
    return (
      <div className="min-h-screen bg-background p-6">
        <p className="text-gray-500 text-center">No se encontró el colaborador</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background p-6 font-montserrat">
      {/* Header con botón volver */}
      <button
        onClick={handleGoBack}
        className="flex items-center gap-2 text-black hover:text-activity transition mb-6"
      >
        <ArrowLeft className="w-5 h-5" />
        Volver
      </button>

      {/* Información del colaborador */}
      <div className="bg-white rounded-2xl p-6 shadow-md mb-6">
        <div className="flex flex-col sm:flex-row items-center sm:items-start gap-4 mb-4">
          <div className="w-16 h-16 bg-activity rounded-full flex items-center justify-center">
            <span className="text-white font-bold text-2xl">
              {colaborador.nombres[0]}{colaborador.apellidos[0]}
            </span>
          </div>
          <div className="text-center sm:text-left">
            <h1 className="text-xl font-bold text-black">
              {colaborador.nombres} {colaborador.apellidos}
            </h1>
            <p className="text-gray-600 text-sm">{colaborador.rol}</p>
            {colaborador.documentoIdentidad && (
              <p className="text-gray-500 text-sm">DNI: {colaborador.documentoIdentidad}</p>
            )}
          </div>
        </div>
      </div>

      {/* Sección de recursos */}
      <div className="mb-6">
        <div className="flex items-center justify-between mb-4 flex-wrap gap-2">
          <h2 className="text-lg font-semibold text-black flex items-center gap-2">
            <CreditCard className="w-5 h-5" />
            Recursos Asignados
          </h2>
          <span className="px-3 py-1 bg-blue-100 text-blue-700 rounded-full text-sm font-medium">
            {recursos.length} tarjeta{recursos.length !== 1 ? 's' : ''}
          </span>
        </div>

        {recursos.length === 0 ? (
          <div className="bg-gray-50 rounded-xl p-8 text-center border-2 border-dashed border-gray-300">
            <CreditCard className="w-12 h-12 mx-auto text-gray-400 mb-3" />
            <p className="text-gray-500">No hay recursos asignados a este colaborador</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 justify-items-center">
            {recursos.map((r) => (
              <div 
                key={r.tarjetaId} 
                className="w-full max-w-sm cursor-pointer transform transition-all hover:scale-105 hover:shadow-xl"
                onClick={() => handleCardClick(r.tarjetaId)}
                role="button"
                tabIndex={0}
                onKeyPress={(e) => {
                  if (e.key === 'Enter' || e.key === ' ') {
                    handleCardClick(r.tarjetaId);
                  }
                }}
              >
                <Card
                  number={r.numeroTarjeta}
                  name={`${colaborador.nombres} ${colaborador.apellidos}`}
                  expirationDate={formatExpirationDate(r.fechaExpiracion)}
                  tipo={getTipoTarjeta(r.tipoId)}
                />
                <div className="mt-2 text-center">
                  <span className="text-xs text-gray-500 hover:text-button transition">
                    Click para editar
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Botones de acción */}
      <div className="space-y-3">
        <button
          onClick={() => navigate(`/admin/add-resource/${id}`)}
          className="w-full bg-button text-white font-semibold py-3 rounded-lg hover:bg-button-hover transition-all transform hover:scale-[1.02] active:scale-[0.98] shadow-md flex items-center justify-center gap-2"
        >
          <svg
            className="w-5 h-5"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Añadir Recurso
        </button>
      </div>
    </div>
  );
};

export default InfoColaborators;