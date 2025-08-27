import { useAuth } from '../../context/AuthContext.tsx';
import { Navigate, Outlet } from 'react-router-dom';
import { defaultPath } from "../../routes/route.ts";

export function PublicRoute() {
  const { user } = useAuth();

  if (user) {
    return <Navigate to={defaultPath} replace />;
  }

  return <Outlet />;
}