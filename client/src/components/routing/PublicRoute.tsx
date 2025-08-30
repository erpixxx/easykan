import { useAuth } from '../../context/AuthContext.tsx';
import { Navigate, Outlet } from 'react-router-dom';
import { DEFAULT_PAGE_PATH } from "../../routes/route.ts";

export function PublicRoute() {
  const { user } = useAuth();

  if (user) {
    return <Navigate to={DEFAULT_PAGE_PATH} replace />;
  }

  return <Outlet />;
}