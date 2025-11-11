// src/pages/Common/Login.tsx

import React, { useState } from "react";
import type { ChangeEvent, FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import Logo from "../../components/Logo";
import { loginUser, getErrorMessage } from "../../services/authService";

type LoginForm = {
  email: string;
  password: string;
};

const Login: React.FC = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState<LoginForm>({ email: "", password: "" });
  const [error, setError] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
    if (error) setError("");
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    
    try {
      const result = await loginUser({
        email: form.email,
        password: form.password,
      });

      if (result.success && result.userData) {
        const { rol, nombres, apellidos } = result.userData;
        
        console.log("Login exitoso");
        console.log("Usuario:", nombres, apellidos);
        console.log("Rol:", rol);
        
        // Redirigir según el rol del usuario
        switch (rol) {
          case "ADMIN":
            console.log("→ Redirigiendo a Admin - See Collaborators");
            navigate("/admin/see-collaborators");
            break;
            
          case "CONTADOR":
            console.log("→ Redirigiendo a Accountancy Dashboard");
            navigate("/accountancy/dashboard");
            break;
            
          case "EMPLEADO":
            console.log("→ Redirigiendo a Colaborators Home");
            navigate("/colaborators/home");
            break;
            
          default:
            console.warn("Rol desconocido:", rol);
            setError("Rol de usuario no reconocido");
        }
      } else {
        setError(getErrorMessage(result.code) || result.error || "Error al iniciar sesión");
        console.error("Error de login:", result.error);
      }
    } catch (err: any) {
      setError(err.message || "Error inesperado al iniciar sesión");
      console.error("Error en login:", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex justify-center items-center min-h-[70vh] bg-background font-montserrat px-4">
      <div className="w-full max-w-sm p-8 bg-white shadow-lg rounded-2xl border border-gray-100">
        <div className="flex justify-center mb-8">
          <Logo width="w-32" height="h-32" />
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Mensaje de error */}
          {error && (
            <div className="p-3 bg-red-50 border border-red-200 rounded-lg">
              <p className="text-sm text-red-700 text-center">{error}</p>
            </div>
          )}

          {/* Email */}
          <div>
            <label className="sr-only">Email</label>
            <input
              type="email"
              name="email"
              placeholder="Email"
              value={form.email}
              onChange={handleChange}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg text-gray-700 placeholder-gray-400 focus:ring-2 focus:ring-button focus:border-button transition-all duration-200 outline-none"
              required
            />
          </div>

          {/* Password */}
          <div>
            <label className="sr-only">Contraseña</label>
            <input
              type="password"
              name="password"
              placeholder="Contraseña"
              value={form.password}
              onChange={handleChange}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg text-gray-700 placeholder-gray-400 focus:ring-2 focus:ring-button focus:border-button transition-all duration-200 outline-none"
              required
            />
          </div>

          {/* Botón Login */}
          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 bg-button text-white font-semibold rounded-lg hover:bg-button-hover transition-all duration-200 transform hover:scale-[1.02] active:scale-[0.98] shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
          >
            {loading ? "Iniciando sesión..." : "Iniciar Sesión"}
          </button>
        </form>

        <div className="mt-6 text-center">
          <a 
            href="#" 
            className="text-sm text-gray-600 hover:text-button transition-colors duration-200"
          >
            ¿Olvidaste tu contraseña?
          </a>
        </div>
      </div>
    </div>
  );
};

export default Login;