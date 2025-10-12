// src/App.tsx
import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
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
    <BrowserRouter>
  <div className="flex flex-col min-h-screen bg-background">        {/* Contenido dinámico */}
        <main className="flex-grow">
          <Routes>
            <Route path="/" element={<Splash />} />
            <Route path="/Login" element={<Login />} />
            <Route path="/Home" element={<Home />} />
            <Route path="/Activities" element={<Activities />} />
            <Route path="Activities/:id/Bills" element={<Bills />} />
            <Route path="/NewBill" element={<NewBill />} />
            <Route path="/BillCheck" element={<BillCheck />} />
            <Route path="/UserCards" element={<UserCards />} />
            <Route path="/SeeCard" element={<SeeCard />} />
            <Route path="/Activities/New" element={<NewActivity />} />
            <Route path="/incidences" element={<Incidences />} />
            <Route path="/incidences/list" element={<IncidenceList />} />
            <Route path="/incidences/new" element={<NewIncidence />} />

          </Routes>
        </main>

        {/* Footer siempre abajo */}
        <Footer />
      </div>
    </BrowserRouter>
  );
};

export default App;
