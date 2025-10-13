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
import type { AuthResponse, LoginCredentials, RegisterData } from '../types/auth.types';

const ALLOWED_DOMAINS = ['gmail.com'];

export const isCompanyEmail = (email: string): boolean => {
  const domain = email.split('@')[1]?.toLowerCase();
  return ALLOWED_DOMAINS.includes(domain);
};

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

    const userCredential = await createUserWithEmailAndPassword(
      auth,
      data.email,
      data.password
    );

    if (data.name) {
      await updateProfile(userCredential.user, {
        displayName: data.name
      });
    }

    return {
      success: true,
      user: {
        uid: userCredential.user.uid,
        email: userCredential.user.email,
        displayName: data.name
      }
    };
  } catch (error: any) {
    return {
      success: false,
      error: error.message,
      code: error.code
    };
  }
};

export const loginUser = async (credentials: LoginCredentials): Promise<AuthResponse> => {
  try {
    const userCredential = await signInWithEmailAndPassword(
      auth,
      credentials.email,
      credentials.password
    );

    return {
      success: true,
      user: {
        uid: userCredential.user.uid,
        email: userCredential.user.email,
        displayName: userCredential.user.displayName
      }
    };
  } catch (error: any) {
    return {
      success: false,
      error: error.message,
      code: error.code
    };
  }
};

export const logoutUser = async (): Promise<AuthResponse> => {
  try {
    await signOut(auth);
    return { success: true };
  } catch (error: any) {
    return {
      success: false,
      error: error.message
    };
  }
};

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

export const observeAuthState = (callback: (user: FirebaseUser | null) => void) => {
  return onAuthStateChanged(auth, callback);
};

export const getCurrentUser = () => {
  return auth.currentUser;
};

export const getAuthToken = async (): Promise<string | null> => {
  const user = auth.currentUser;
  if (user) {
    return await user.getIdToken();
  }
  return null;
};

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