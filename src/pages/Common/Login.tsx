// src/pages/Login.tsx
import React, { useState } from "react";
import type { ChangeEvent, FormEvent } from "react";
import Logo from "../../components/Logo";

interface LoginForm {
  email: string;
  password: string;
}

const Login: React.FC = () => {
  const [form, setForm] = useState<LoginForm>({ email: "", password: "" });

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    console.log("Datos login:", form);
  };

  return (
<div className="flex justify-center items-center min-h-[70vh] bg-background font-montserrat px-4">
      <div className="w-full max-w-sm p-8 bg-white shadow-lg rounded-2xl border border-gray-100">
        <div className="flex justify-center mb-8">
          <Logo width="w-32" height="h-32" />
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
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
            className="w-full py-3 bg-button text-white font-semibold rounded-lg hover:bg-button-hover transition-all duration-200 transform hover:scale-[1.02] active:scale-[0.98] shadow-md hover:shadow-lg"
          >
            Iniciar Sesión
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
