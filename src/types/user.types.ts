export type UserRole = 'ADMIN' | 'CONTADOR' | 'EMPLEADO';

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

export interface UserProfile {
  uid: string;
  email: string | null;
  displayName: string | null;
  rol?: UserRole;
  empleadoId?: number;
  empresaId?: number;
}