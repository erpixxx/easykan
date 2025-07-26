import { HomePage } from "../pages/HomePage.tsx";
import { LoginPage } from "../pages/LoginPage.tsx";
import {ProjectsPage} from "../pages/ProjectsPage.tsx";

export const ROUTES = [
  {
    path: '/',
    element: HomePage
  },
  {
    path: '/login',
    element: LoginPage
  },
  {
    path: '/projects',
    element: ProjectsPage
  }
];