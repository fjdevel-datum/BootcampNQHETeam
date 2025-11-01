// src/App.tsx
import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { Toaster } from "sonner"; // ✅ Import necesario
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Footer from "./components/Footer";

// ========== PÁGINAS COMUNES ==========
import Splash from "./pages/Common/Splash";
import Login from "./pages/Common/Login";

// ========== PÁGINAS DE ADMIN ==========
import AddResource from "./pages/Admin/AddResource";
import InfoColaborators from "./pages/Admin/InfoColaborators";
import SeeCollaborators from "./pages/Admin/SeeColaborators";
import EditCard from "./pages/Admin/EditCard";
import AddColaborator from "./pages/Admin/AddColaborator";

// ========== PÁGINAS DE COLABORADOR (EMPLEADO) ==========
import Activities from "./pages/Collaborator/Activities";
import BillCheck from "./pages/Collaborator/BillCheck";
import Bills from "./pages/Collaborator/Bills";
import Home from "./pages/Collaborator/Home";
import IncidenceList from "./pages/Collaborator/IncidenceList";
import Incidences from "./pages/Collaborator/Incidences";
import NewActivity from "./pages/Collaborator/NewActivity";
import NewBill from "./pages/Collaborator/NewBill";
import NewIncidence from "./pages/Collaborator/NewIncidence";
import SeeCard from "./pages/Collaborator/SeeCard";
import UserCards from "./pages/Collaborator/UserCards";

// ========== COMPONENTE DE NO AUTORIZADO ==========
const Unauthorized: React.FC = () => (
  <div className="flex flex-col items-center justify-center min-h-[60vh] p-8">
    <div className="text-center">
      <h1 className="text-4xl font-bold text-red-600 mb-4">🚫 Acceso Denegado</h1>
      <p className="text-gray-600 mb-6">No tienes permisos para acceder a esta página.</p>
      <a
        href="/login"
        className="px-6 py-3 bg-button text-white rounded-lg hover:bg-button-hover transition-colors"
      >
        Volver al inicio
      </a>
    </div>
  </div>
);

const App: React.FC = () => {
  return (
    <>
      {/* ✅ Toaster debe estar dentro de un fragment o componente raíz */}
      <Toaster
        position="top-center"
        expand={true}
        richColors
        closeButton
        duration={3000}
        toastOptions={{
          style: {
            fontFamily: "Montserrat, sans-serif",
          },
          classNames: {
            toast: "shadow-2xl",
            title: "font-semibold",
            description: "text-sm",
            success: "bg-green-500 text-white border-green-600",
            error: "bg-button text-white border-button",
            warning: "bg-yellow-500 text-white border-yellow-600",
            info: "bg-activity text-white border-activity",
          },
        }}
      />

      <AuthProvider>
        <BrowserRouter>
          <div className="flex flex-col min-h-screen bg-background">
            {/* Contenido dinámico */}
            <main className="flex-grow">
              <Routes>
                {/* ========== RUTAS PÚBLICAS ========== */}
                <Route path="/" element={<Splash />} />
                <Route path="/login" element={<Login />} />
                <Route path="/unauthorized" element={<Unauthorized />} />

                {/* ========== RUTAS DE ADMIN ========== */}
                <Route
                  path="/admin/info-colaborators/:id"
                  element={
                    <ProtectedRoute allowedRoles={["ADMIN"]}>
                      <InfoColaborators />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/admin/add-resource/:id"
                  element={
                    <ProtectedRoute allowedRoles={["ADMIN"]}>
                      <AddResource />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/admin/see-collaborators"
                  element={
                    <ProtectedRoute allowedRoles={["ADMIN"]}>
                      <SeeCollaborators />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/admin/edit-card/:cardId/:empleadoId"
                  element={
                    <ProtectedRoute allowedRoles={["ADMIN"]}>
                      <EditCard />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/admin/add-colaborator"
                  element={
                    <ProtectedRoute allowedRoles={["ADMIN"]}>
                      <AddColaborator />
                    </ProtectedRoute>
                  }
                />

                {/* ========== RUTAS DE COLABORADOR (EMPLEADO) ========== */}
                <Route
                  path="/colaborators/home"
                  element={
                    <ProtectedRoute allowedRoles={["EMPLEADO"]}>
                      <Home />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/colaborators/activities"
                  element={
                    <ProtectedRoute allowedRoles={["EMPLEADO"]}>
                      <Activities />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/colaborators/activities/:id/bills"
                  element={
                    <ProtectedRoute allowedRoles={["EMPLEADO"]}>
                      <Bills />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/colaborators/bill-check"
                  element={
                    <ProtectedRoute allowedRoles={["EMPLEADO"]}>
                      <BillCheck />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/colaborators/new-bill"
                  element={
                    <ProtectedRoute allowedRoles={["EMPLEADO"]}>
                      <NewBill />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/colaborators/user-cards"
                  element={
                    <ProtectedRoute allowedRoles={["EMPLEADO"]}>
                      <UserCards />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/colaborators/see-card/:cardId"
                  element={
                    <ProtectedRoute allowedRoles={["EMPLEADO"]}>
                      <SeeCard />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/colaborators/activities/new"
                  element={
                    <ProtectedRoute allowedRoles={["EMPLEADO"]}>
                      <NewActivity />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/colaborators/incidences"
                  element={
                    <ProtectedRoute allowedRoles={["EMPLEADO"]}>
                      <Incidences />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/colaborators/incidences/list"
                  element={
                    <ProtectedRoute allowedRoles={["EMPLEADO"]}>
                      <IncidenceList />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/colaborators/incidences/new"
                  element={
                    <ProtectedRoute allowedRoles={["EMPLEADO"]}>
                      <NewIncidence />
                    </ProtectedRoute>
                  }
                />

                {/* ========== RUTAS LEGACY (compatibilidad) ========== */}
                <Route path="/home" element={<Navigate to="/colaborators/home" replace />} />
                <Route path="/activities" element={<Navigate to="/colaborators/activities" replace />} />
                <Route path="/activities/:id/bills" element={<Navigate to="/colaborators/activities/:id/bills" replace />} />
                <Route path="/billcheck" element={<Navigate to="/colaborators/bill-check" replace />} />
                <Route path="/newbill" element={<Navigate to="/colaborators/new-bill" replace />} />
                <Route path="/usercards" element={<Navigate to="/colaborators/user-cards" replace />} />
                <Route path="/seecard" element={<Navigate to="/colaborators/see-card" replace />} />
                <Route path="/activities/new" element={<Navigate to="/colaborators/activities/new" replace />} />
                <Route path="/incidences" element={<Navigate to="/colaborators/incidences" replace />} />
                <Route path="/incidences/list" element={<Navigate to="/colaborators/incidences/list" replace />} />
                <Route path="/incidences/new" element={<Navigate to="/colaborators/incidences/new" replace />} />

                {/* ========== RUTA CATCH-ALL ==========
                    Redirige rutas no encontradas */}
                <Route path="*" element={<Navigate to="/" replace />} />
              </Routes>
            </main>

            {/* Footer siempre abajo */}
            <Footer />
          </div>
        </BrowserRouter>
      </AuthProvider>
    </>
  );
};

export default App;
