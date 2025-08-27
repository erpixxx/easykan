import { createBrowserRouter, type RouteObject } from "react-router-dom";
import { routes } from "./route.ts";
import { AppLayout } from "../components/layouts/AppLayout.tsx";
import { PublicRoute } from "../components/routing/PublicRoute.tsx";
import { ProtectedRoute } from "../components/routing/ProtectedRoute.tsx";

const groupedRoutes = routes.reduce<{ public: RouteObject[], protected: RouteObject[] }>(
  (acc, route) => {
    const routeElement = {
      path: route.path,
      element: <route.Element />,
    };

    if (route.protected) {
      acc.protected.push(routeElement);
    } else {
      acc.public.push(routeElement);
    }

    return acc;
  },
  { public: [], protected: [] }
);

export const router = createBrowserRouter([
  {
    element: <AppLayout />,
    children: [
      {
        element: <PublicRoute />,
        children: groupedRoutes.public,
      },
      {
        element: <ProtectedRoute />,
        children: groupedRoutes.protected,
      }
    ],
  },
]);