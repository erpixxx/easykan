import "../scss/projects.scss"
import {UserNav} from "../components/UserNav.tsx";
import {ProjectsView} from "../components/ProjectsView.tsx";
import {PageTransition} from "../components/PageTransition.tsx";

export function ProjectsPage() {
  return (
    <PageTransition >
      <div className="projects-page">
        <UserNav />
        <ProjectsView />
      </div>
    </PageTransition>
  );
}