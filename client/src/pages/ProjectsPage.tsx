import "../scss/projects.scss"
import {UserNav} from "../components/features/projects/UserNav.tsx";
import {ProjectsView} from "../components/features/projects/ProjectsView.tsx";
import {PageTransition} from "../components/PageTransition.tsx";

export function ProjectsPage() {
  return (
    <PageTransition>
      <div className="projects-page">
        <UserNav />
        <ProjectsView />
      </div>
    </PageTransition>
  );
}