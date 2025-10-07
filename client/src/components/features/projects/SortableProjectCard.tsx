import type { ProjectListItem } from "../../../types/dto/project/ProjectListItem.ts";
import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { ProjectCard } from "./ProjectCard.tsx";

export interface SortableProjectCardProps {
  project: ProjectListItem;
  isDragging: boolean;
}

export function SortableProjectCard({
  project,
  isDragging,
}: SortableProjectCardProps) {
  const { attributes, listeners, setNodeRef, transform, transition } =
    useSortable({ id: project.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.1 : 1,
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes} {...listeners}>
      <ProjectCard
        id={project.id}
        name={project.name}
        members={project.members}
      />
    </div>
  );
}
