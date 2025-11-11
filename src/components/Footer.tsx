// src/components/Footer.tsx
import React from "react";

const Footer: React.FC = () => {
  return (
    <footer className="w-full">
      {/* Franja roja personalizada */}
      <div className="w-full h-4 bg-button"></div>

      {/* Franja negra */}
      <div className="w-full h-6 bg-black"></div>
    </footer>
  );
};

export default Footer;
