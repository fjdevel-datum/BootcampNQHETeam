import {
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
  signOut,
  onAuthStateChanged,
  sendPasswordResetEmail,
  updateProfile,
  type User as FirebaseUser
} from 'firebase/auth';
import { auth } from '../firebase';
import type { 
  AuthResponse, 
  LoginCredentials, 
  RegisterData,
  UserData 
} from '../types/auth.types';
import type { UserRole } from '../types/user.types';

const ALLOWED_DOMAINS = ['gmail.com'];
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export const isCompanyEmail = (email: string): boolean => {
  const domain = email.split('@')[1]?.toLowerCase();
  return ALLOWED_DOMAINS.includes(domain);
};

/**
 * Registrar nuevo usuario en Firebase y Backend
 */
export const registerUser = async (data: RegisterData): Promise<AuthResponse> => {
  try {
    if (!isCompanyEmail(data.email)) {
      return {
        success: false,
        error: `Solo se permiten correos de: ${ALLOWED_DOMAINS.join(', ')}`,
        code: 'auth/invalid-company-email'
      };
    }

    if (data.password !== data.confirmPassword) {
      return {
        success: false,
        error: 'Las contraseñas no coinciden',
        code: 'auth/passwords-dont-match'
      };
    }

    if (data.password.length < 6) {
      return {
        success: false,
        error: 'La contraseña debe tener al menos 6 caracteres',
        code: 'auth/weak-password'
      };
    }

    // Crear usuario en Firebase primero
    const userCredential = await createUserWithEmailAndPassword(
      auth,
      data.email,
      data.password
    );

    const displayName = `${data.nombres} ${data.apellidos}`;
    
    if (displayName) {
      await updateProfile(userCredential.user, {
        displayName
      });
    }

    // Obtener token
    const token = await userCredential.user.getIdToken();

    // Registrar en backend
    try {
      const backendResponse = await fetch(`${API_URL}/auth/registro`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          email: data.email,
          password: data.password,
          nombres: data.nombres,
          apellidos: data.apellidos,
          documentoIdentidad: data.documentoIdentidad,
          empresaId: data.empresaId,
          centroId: data.centroId,
          rol: data.rol
        })
      });

      if (!backendResponse.ok) {
        // Si falla el backend, eliminar usuario de Firebase
        await userCredential.user.delete();
        const errorData = await backendResponse.json();
        throw new Error(errorData.message || 'Error al registrar en el backend');
      }

      const backendData = await backendResponse.json();
      console.log('✅ Usuario registrado en backend:', backendData);

      return {
        success: true,
        user: {
          uid: userCredential.user.uid,
          email: userCredential.user.email,
          displayName,
          rol: data.rol,
          empleadoId: backendData.empleadoId
        }
      };
    } catch (backendError: any) {
      // Rollback: eliminar usuario de Firebase si falla el backend
      await userCredential.user.delete();
      throw backendError;
    }
  } catch (error: any) {
    return {
      success: false,
      error: error.message,
      code: error.code
    };
  }
};

/**
 * Login de usuario - Autentica con Firebase y obtiene datos del backend
 */
export const loginUser = async (credentials: LoginCredentials): Promise<AuthResponse> => {
  try {
    console.log('🔐 Iniciando sesión con Firebase...');
    
    // 1. Autenticar con Firebase
    const userCredential = await signInWithEmailAndPassword(
      auth,
      credentials.email,
      credentials.password
    );

    console.log('✅ Autenticación Firebase exitosa');

    // 2. Obtener token
    const token = await userCredential.user.getIdToken();
    
    // 3. Verificar token en backend y obtener datos completos del usuario
    console.log('🔍 Verificando token en backend...');
    
    const response = await fetch(`${API_URL}/auth/verify`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Error al verificar token en backend');
    }

    const userData: UserData = await response.json();
    
    console.log('✅ Datos del usuario obtenidos del backend:', userData);
    console.log('👤 Rol del usuario:', userData.rol);

    // 4. Guardar datos en localStorage
    localStorage.setItem('token', userData.token);
    localStorage.setItem('user', JSON.stringify(userData));

    return {
      success: true,
      user: {
        uid: userData.uid,
        email: userData.email,
        displayName: `${userData.nombres} ${userData.apellidos}`,
        rol: userData.rol,
        empleadoId: userData.empleadoId,
        empresaId: userData.empresaId
      },
      userData
    };
  } catch (error: any) {
    console.error('❌ Error en login:', error);
    return {
      success: false,
      error: error.message,
      code: error.code
    };
  }
};

/**
 * Cerrar sesión
 */
export const logoutUser = async (): Promise<AuthResponse> => {
  try {
    await signOut(auth);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    console.log('✅ Sesión cerrada');
    return { success: true };
  } catch (error: any) {
    return {
      success: false,
      error: error.message
    };
  }
};

/**
 * Restablecer contraseña
 */
export const resetPassword = async (email: string): Promise<AuthResponse> => {
  try {
    await sendPasswordResetEmail(auth, email);
    return { success: true };
  } catch (error: any) {
    return {
      success: false,
      error: error.message
    };
  }
};

/**
 * Observar cambios en el estado de autenticación
 */
export const observeAuthState = (callback: (user: FirebaseUser | null) => void) => {
  return onAuthStateChanged(auth, callback);
};

/**
 * Obtener usuario actual de Firebase
 */
export const getCurrentUser = () => {
  return auth.currentUser;
};

/**
 * Obtener datos completos del usuario desde localStorage
 */
export const getCurrentUserData = (): UserData | null => {
  const userStr = localStorage.getItem('user');
  if (!userStr) return null;
  
  try {
    return JSON.parse(userStr);
  } catch {
    return null;
  }
};

/**
 * Obtener token de autenticación
 */
export const getAuthToken = async (): Promise<string | null> => {
  // Primero intentar desde localStorage
  const storedToken = localStorage.getItem('token');
  if (storedToken) return storedToken;

  // Si no hay en localStorage, obtener de Firebase
  const user = auth.currentUser;
  if (user) {
    const token = await user.getIdToken();
    localStorage.setItem('token', token);
    return token;
  }
  
  return null;
};

/**
 * Verificar si el usuario está autenticado
 */
export const isAuthenticated = (): boolean => {
  return !!localStorage.getItem('token');
};

/**
 * Obtener rol del usuario actual
 */
export const getUserRole = (): UserRole | null => {
  const userData = getCurrentUserData();
  return userData?.rol || null;
};

/**
 * Verificar si el usuario tiene un rol específico
 */
export const hasRole = (role: UserRole): boolean => {
  return getUserRole() === role;
};

/**
 * Verificar si el usuario tiene alguno de los roles especificados
 */
export const hasAnyRole = (roles: UserRole[]): boolean => {
  const userRole = getUserRole();
  return userRole ? roles.includes(userRole) : false;
};

/**
 * Obtener mensaje de error personalizado
 */
export const getErrorMessage = (code?: string): string => {
  switch (code) {
    case 'auth/email-already-in-use':
      return 'El email ya está registrado';
    case 'auth/invalid-email':
      return 'Email inválido';
    case 'auth/user-not-found':
      return 'Usuario no encontrado';
    case 'auth/wrong-password':
      return 'Contraseña incorrecta';
    case 'auth/weak-password':
      return 'La contraseña debe tener al menos 6 caracteres';
    case 'auth/invalid-credential':
      return 'Credenciales inválidas';
    case 'auth/invalid-company-email':
      return 'Solo se permiten correos de Gmail para pruebas';
    case 'auth/passwords-dont-match':
      return 'Las contraseñas no coinciden';
    default:
      return 'Error de autenticación';
  }
};

/**
 * Realizar petición con autenticación
 */
export const fetchWithAuth = async (url: string, options: RequestInit = {}) => {
  const token = await getAuthToken();
  if (!token) throw new Error("Usuario no autenticado");

  const response = await fetch(url, {
    ...options,
    headers: {
      ...options.headers,
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });

  if (response.status === 401) {
    // Token expirado, cerrar sesión
    await logoutUser();
    window.location.href = '/login';
    throw new Error("Sesión expirada");
  }

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`Error HTTP ${response.status}: ${errorText}`);
  }

  return response.json();
};

/**
 * Refrescar token de autenticación
 */
export const refreshToken = async (): Promise<string | null> => {
  try {
    const user = auth.currentUser;
    if (user) {
      const token = await user.getIdToken(true); // true = force refresh
      localStorage.setItem('token', token);
      return token;
    }
    return null;
  } catch (error) {
    console.error('Error al refrescar token:', error);
    return null;
  }
};