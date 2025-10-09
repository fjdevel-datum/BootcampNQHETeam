// src/App.tsx
import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Splash from "./pages/Splash";
import Login from "./pages/Login";
import Home from "./pages/Home";
import Activities from "./pages/Activities";
import Bills from "./pages/Bills";
import Footer from "./components/Footer";
import NewBill from "./pages/NewBill";
import BillCheck from "./pages/BillCheck";
import UserCards from "./pages/UserCards";
import SeeCard from "./pages/SeeCard";

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

          </Routes>
        </main>

        {/* Footer siempre abajo */}
        <Footer />
      </div>
    </BrowserRouter>
  );
};

export default App;
