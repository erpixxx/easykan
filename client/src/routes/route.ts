import { HomePage } from "../pages/HomePage.tsx";
import { LoginPage } from "../pages/LoginPage.tsx";
import {ProjectsPage} from "../pages/ProjectsPage.tsx";

export const routes = [
  {
    path: '/',
    Element: HomePage,
    protected: true
  },
  {
    path: '/login',
    Element: LoginPage,
    protected: false
  },
  {
    path: '/projects',
    Element: ProjectsPage,
    protected: true
  }
];

export const loginPath = '/login';
export const defaultPath = '/projects';