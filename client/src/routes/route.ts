import { HomePage } from "../pages/HomePage.tsx";
import { LoginPage } from "../pages/LoginPage.tsx";
import { ProjectsPage } from "../pages/ProjectsPage.tsx";
import { ProjectBoardPage } from "../pages/ProjectBoardPage.tsx";

export const routes = [
  {
    path: "/",
    Element: HomePage,
    protected: true,
  },
  {
    path: "/login",
    Element: LoginPage,
    protected: false,
  },
  {
    path: "/projects",
    Element: ProjectsPage,
    protected: true,
  },
  {
    path: "/projects/:projectId",
    Element: ProjectBoardPage,
    protected: true,
  },
];

export const LOGIN_PAGE_PATH = "/login";
export const DEFAULT_PAGE_PATH = "/projects";
