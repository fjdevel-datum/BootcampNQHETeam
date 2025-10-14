// src/App.tsx
import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Splash from "./pages/Common/Splash";
import Login from "./pages/Common/Login";
import Home from "./pages/Collaborator/Home";
import Activities from "./pages/Collaborator/Activities";
import Bills from "./pages/Collaborator/Bills";
import Footer from "./components/Footer";
import NewBill from "./pages/Collaborator/NewBill";
import BillCheck from "./pages/Collaborator/BillCheck";
import UserCards from "./pages/Collaborator/UserCards";
import SeeCard from "./pages/Collaborator/SeeCard";
import NewActivity from "./pages/Collaborator/NewActivity";
import IncidenceList from "./pages/Collaborator/IncidenceList";
import Incidences from "./pages/Collaborator/Incidences";
import NewIncidence from "./pages/Collaborator/NewIncidence";

const App: React.FC = () => {
  return (
  <AuthProvider>
    <BrowserRouter>
  <div className="flex flex-col min-h-screen bg-background">        {/* Contenido dinámico */}
        <main className="flex-grow">
          <Routes>
            <Route path="/" element={<Splash />} />
            <Route path="/Login" element={<Login />} />
            <Route path="/Home" element={<ProtectedRoute><Home /></ProtectedRoute>} />
            <Route path="/Activities" element={<ProtectedRoute><Activities /></ProtectedRoute>} />
            <Route path="Activities/:id/Bills" element={<ProtectedRoute><Bills /></ProtectedRoute>} />
            <Route path="/NewBill" element={<ProtectedRoute><NewBill /></ProtectedRoute>} />
            <Route path="/BillCheck" element={<ProtectedRoute><BillCheck /></ProtectedRoute>} />
            <Route path="/UserCards" element={<ProtectedRoute><UserCards /></ProtectedRoute>} />
            <Route path="/SeeCard" element={<ProtectedRoute><SeeCard /></ProtectedRoute>} />
            <Route path="/Activities/New" element={<ProtectedRoute><NewActivity /></ProtectedRoute>} />
            <Route path="/incidences" element={<ProtectedRoute><Incidences /></ProtectedRoute>} />
            <Route path="/incidences/list" element={<ProtectedRoute><IncidenceList /></ProtectedRoute>} />
            <Route path="/incidences/new" element={<ProtectedRoute><NewIncidence /></ProtectedRoute>} />
            <Route path="*" element={<Navigate to="/" replace />} />

          </Routes>
        </main>

        {/* Footer siempre abajo */}
        <Footer />
      </div>
    </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
