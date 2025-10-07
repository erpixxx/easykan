import { PageTransition } from "../components/PageTransition.tsx";
import { useParams } from "react-router-dom";

export function ProjectBoardPage() {
  const { projectId } = useParams();

  return (
    <PageTransition>
      <div className="project-board-page">
        <h1>Project Board Page {projectId}</h1>
      </div>
    </PageTransition>
  );
}
