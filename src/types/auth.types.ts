import type { UserRole } from './user.types';

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterData {
  email: string;
  password: string;
  confirmPassword: string;
  name?: string;
  nombres: string;
  apellidos: string;
  documentoIdentidad: string;
  empresaId: number;
  centroId: number;
  rol: UserRole;
}

export interface AuthResponse {
  success: boolean;
  user?: {
    uid: string;
    email: string | null;
    displayName: string | null;
    rol?: UserRole;
    empleadoId?: number;
    empresaId?: number;
  };
  userData?: UserData;
  error?: string;
  code?: string;
}

export interface UserData {
  token: string;
  uid: string;
  email: string;
  nombres: string;
  apellidos: string;
  displayName: string;
  rol: UserRole;
  photoURL?: string;
  empleadoId: number;
  empresaId: number;
  centroId: number;
  mensaje: string;
}
