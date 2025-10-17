import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

const AñadirRecurso: React.FC = () => {
  const { id } = useParams(); // ID del colaborador
  const navigate = useNavigate();
  const [form, setForm] = useState({
    numero: "",
    fechaExpiracion: "",
    montoMaximo: "",
    estado: "Activo",
    fechaAsignacion: "",
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await fetch(`http://localhost:4000/api/colaboradores/${id}/recursos`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });
      navigate(`/colaborador/${id}`);
    } catch (error) {
      console.error("Error al añadir recurso:", error);
    }
  };

  return (
    <div className="min-h-screen bg-background p-6 font-montserrat">
      <h1 className="text-black text-xl font-semibold mb-4">Añadir Recurso</h1>

      <form className="space-y-4" onSubmit={handleSubmit}>
        <div>
          <label className="block text-sm text-black font-medium mb-1">
            Número del recurso
          </label>
          <input
            type="text"
            name="numero"
            value={form.numero}
            onChange={handleChange}
            className="w-full border border-gray-300 p-2 rounded-lg"
          />
        </div>

        <div>
          <label className="block text-sm text-black font-medium mb-1">
            Fecha de expiración
          </label>
          <input
            type="date"
            name="fechaExpiracion"
            value={form.fechaExpiracion}
            onChange={handleChange}
            className="w-full border border-gray-300 p-2 rounded-lg"
          />
        </div>

        <div>
          <label className="block text-sm text-black font-medium mb-1">
            Monto máximo
          </label>
          <input
            type="number"
            name="montoMaximo"
            value={form.montoMaximo}
            onChange={handleChange}
            className="w-full border border-gray-300 p-2 rounded-lg"
          />
        </div>

        <div>
          <label className="block text-sm text-black font-medium mb-1">
            Fecha de asignación
          </label>
          <input
            type="date"
            name="fechaAsignacion"
            value={form.fechaAsignacion}
            onChange={handleChange}
            className="w-full border border-gray-300 p-2 rounded-lg"
          />
        </div>

        <div>
          <label className="block text-sm text-black font-medium mb-1">
            Estado
          </label>
          <select
            name="estado"
            value={form.estado}
            onChange={handleChange}
            className="w-full border border-gray-300 p-2 rounded-lg"
          >
            <option value="Activo">Activo</option>
            <option value="Inactivo">Inactivo</option>
          </select>
        </div>

        <button
          type="submit"
          className="w-full bg-button text-white font-semibold py-3 rounded-lg hover:bg-button-hover"
        >
          Guardar
        </button>
      </form>
    </div>
  );
};

export default AñadirRecurso;
