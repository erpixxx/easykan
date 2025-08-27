import { useAuth } from '../../context/AuthContext.tsx';
import { Navigate, Outlet } from 'react-router-dom';
import { loginPath } from "../../routes/route.ts";

export function ProtectedRoute() {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to={loginPath} replace />;
  }

  return <Outlet />;
}