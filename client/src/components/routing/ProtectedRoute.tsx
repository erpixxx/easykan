import { useAuth } from '../../context/AuthContext.tsx';
import { Navigate, Outlet } from 'react-router-dom';
import { LOGIN_PAGE_PATH } from "../../routes/route.ts";

export function ProtectedRoute() {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to={LOGIN_PAGE_PATH} replace />;
  }

  return <Outlet />;
}