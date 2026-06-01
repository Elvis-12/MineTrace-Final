import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { Role } from '../constants/roles';
import { ROUTES } from '../constants/routes';

interface RoleRouteProps {
  allowedRoles: Role[];
}

export default function RoleRoute({ allowedRoles }: RoleRouteProps) {
  const { user, hasHydrated } = useAuthStore();

  if (!hasHydrated) {
    return null;
  }

  if (!user || !allowedRoles.some(r => r === user.role)) {
    return <Navigate to={ROUTES.UNAUTHORIZED} replace />;
  }

  return <Outlet />;
}
