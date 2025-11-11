// pages/admin/SeeIncidences.tsx
import React, { useEffect, useState } from 'react';
import { toast } from '../../components/toast';
import AdminSideMenu from './AdminSideMenu';
import { fetchWithAuth, getCurrentUserData } from '../../services/authService';

const apiurl = import.meta.env.VITE_API_URL;

interface Incidence {
  incidenciaId: number;
  empleadoId: number;
  nombreEmpleado: string;
  tipoIncidenciaId: number;
  tipoIncidenciaNombre: string;
  recursoId: number;
  numeroTarjeta: string;
  fechaIncidencia: string;
  descripcion: string;
}

const SeeIncidences: React.FC = () => {
  const [incidences, setIncidences] = useState<Incidence[]>([]);
  const [loading, setLoading] = useState(true);
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  useEffect(() => {
    const fetchIncidences = async () => {
      try {
        setLoading(true);
        const userData = getCurrentUserData();

        if (!userData?.empresaId) {
          toast.error('Error de autenticación', 'No se pudo identificar la empresa del usuario');
          setLoading(false);
          return;
        }

        const data: Incidence[] = await fetchWithAuth(`${apiurl}/incidencia/empresa/${userData.empresaId}`);
        setIncidences(data);
      } catch (err: any) {
        toast.error('Error al cargar', err.message || 'No se pudieron cargar las incidencias');
      } finally {
        setLoading(false);
      }
    };

    fetchIncidences();
  }, []);

  const formatDate = (fecha: string) => {
    if (!fecha) return 'N/A';
    
    try {
      const date = new Date(fecha);
      return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    } catch {
      return fecha;
    }
  };

  return (
    <div className="min-h-screen bg-background font-montserrat relative">
      {/* Header */}
      <header className="relative flex items-center p-6 bg-background">
        <button
          onClick={() => setIsMenuOpen(true)}
          aria-label="Abrir menú"
          className="w-10 h-10 rounded-full bg-button hover:bg-button-hover text-white flex items-center justify-center font-bold shadow transition-colors"
        >
          ≡
        </button>
        <h1 className="ml-4 text-xl font-semibold text-black">Incidencias de Empleados</h1>
      </header>

      <main className="p-6">
        {/* Estadísticas */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm">Total de Incidencias</p>
              <p className="text-3xl font-bold text-button">{incidences.length}</p>
            </div>
            <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center">
              <svg className="w-8 h-8 text-button" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
            </div>
          </div>
        </div>

        {/* Lista de incidencias */}
        {loading ? (
          <div className="flex justify-center items-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-button"></div>
          </div>
        ) : incidences.length === 0 ? (
          <div className="text-center py-12">
            <svg className="w-16 h-16 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p className="text-gray-500 text-lg">No hay incidencias registradas</p>
          </div>
        ) : (
          <div className="space-y-4">
            {incidences.map((incidence) => (
              <div
                key={incidence.incidenciaId}
                className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow border-l-4 border-button"
              >
                {/* Header de la incidencia */}
                <div className="flex justify-between items-start mb-4">
                  <div className="flex-1">
                    <div className="flex flex-wrap items-center gap-2 mb-2">
                      <span className="px-3 py-1 bg-red-100 text-red-800 text-xs font-semibold rounded-full">
                        {incidence.tipoIncidenciaNombre}
                      </span>
                      <span className="px-3 py-1 bg-gray-100 text-gray-700 text-xs font-semibold rounded-full">
                        Tarjeta: {incidence.numeroTarjeta}
                      </span>
                    </div>
                  </div>
                </div>

                {/* Información del empleado */}
                <div className="mb-4 pb-4 border-b border-gray-200">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-button rounded-full flex items-center justify-center">
                      <span className="text-white font-bold">
                        {incidence.nombreEmpleado.charAt(0).toUpperCase()}
                      </span>
                    </div>
                    <div>
                      <p className="font-semibold text-gray-900">{incidence.nombreEmpleado}</p>
                    </div>
                  </div>
                </div>

                {/* Descripción */}
                <div className="mb-4">
                  <p className="text-sm font-semibold text-gray-700 mb-1">Descripción:</p>
                  <p className="text-gray-700">{incidence.descripcion}</p>
                </div>

                {/* Fecha */}
                <div className="flex items-center text-sm text-gray-500">
                  <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                    />
                  </svg>
                  <span className="font-medium">Fecha de incidencia:</span>
                  <span className="ml-2">{formatDate(incidence.fechaIncidencia)}</span>
                </div>
              </div>
            ))}
          </div>
        )}

        {incidences.length > 0 && (
          <p className="text-gray-600 text-sm mt-4 text-center">
            Mostrando {incidences.length} incidencia{incidences.length !== 1 ? 's' : ''}
          </p>
        )}
      </main>

      <AdminSideMenu 
        isOpen={isMenuOpen} 
        onClose={() => setIsMenuOpen(false)}
        currentPage="incidences"
      />
    </div>
  );
};

export default SeeIncidences;