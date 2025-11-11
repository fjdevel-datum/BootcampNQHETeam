import type { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { isAuthenticated, hasAnyRole } from '../services/authService';
import type { UserRole } from '../types/user.types';

interface ProtectedRouteProps {
  children: ReactNode;
  allowedRoles?: UserRole[];
}

const ProtectedRoute = ({ children, allowedRoles }: ProtectedRouteProps) => {
  const authenticated = isAuthenticated();
  
  console.log('🔒 ProtectedRoute - Autenticado:', authenticated);

  if (!authenticated) {
    console.log('❌ No autenticado, redirigiendo a /login');
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !hasAnyRole(allowedRoles)) {
    console.log('❌ Rol no permitido, redirigiendo a /unauthorized');
    return <Navigate to="/unauthorized" replace />;
  }

  console.log('✅ Acceso permitido');
  return <>{children}</>;
};

export default ProtectedRoute;