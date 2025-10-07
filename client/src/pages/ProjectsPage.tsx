import "../scss/projects.scss";
import { TopBar } from "../components/layouts/TopBar.tsx";
import { ProjectsView } from "../components/features/projects/ProjectsView.tsx";
import { PageTransition } from "../components/PageTransition.tsx";

export function ProjectsPage() {
  return (
    <PageTransition>
      <div className="projects-page">
        <TopBar />
        <ProjectsView />
      </div>
    </PageTransition>
  );
}
