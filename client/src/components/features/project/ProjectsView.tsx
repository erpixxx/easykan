import {
  Box,
  Button,
  Card,
  Dialog,
  Flex,
  Grid,
  IconButton,
  Spinner,
  Text,
  TextField,
} from "@radix-ui/themes";
import { Cross1Icon, PlusIcon, RocketIcon } from "@radix-ui/react-icons";
import { useEffect, useState } from "react";
import { create, getProjectList } from "../../../api/projects.api.ts";
import type { ProjectListItem } from "../../../types/dto/project/ProjectListItem.ts";
import {
  DndContext,
  type DragEndEvent,
  DragOverlay,
  PointerSensor,
  useSensor,
  useSensors,
} from "@dnd-kit/core";
import {
  arrayMove,
  rectSortingStrategy,
  SortableContext,
} from "@dnd-kit/sortable";
import { SortableProjectCard } from "./SortableProjectCard.tsx";
import { ProjectCard } from "./ProjectCard.tsx";

export function ProjectsView() {
  const [projects, setProjects] = useState<ProjectListItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [activeProjectId, setActiveProjectId] = useState<string | null>(null);
  const [dragging, setDragging] = useState(false);
  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 8,
      },
    }),
  );

  useEffect(() => {
    const loadProjects = async () => {
      try {
        setLoading(true);
        const res = await getProjectList();
        setProjects(res);
      } catch (err) {
        setError("Failed to load projects.");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    loadProjects();
  }, []);

  const handleProjectCreated = (newProject: ProjectListItem) => {
    setProjects((prevProjects) => [...prevProjects, newProject]);
  };

  function handleDragEnd(event: DragEndEvent) {
    const { active, over } = event;

    if (over && active.id !== over.id) {
      setProjects((items) => {
        const oldIndex = items.findIndex((item) => item.id === active.id);
        const newIndex = items.findIndex((item) => item.id === over.id);

        return arrayMove(items, oldIndex, newIndex);
      });
    }

    setActiveProjectId(null);
    setDragging(false);
  }

  const activeProject = projects.find((p) => p.id === activeProjectId) || null;

  if (loading) {
    return (
      <Box m="9">
        <Spinner size="3" /> <Text>Loading projects...</Text>
      </Box>
    );
  }

  if (error) {
    return (
      <Box m="9">
        <Text color="red">{error}</Text>
      </Box>
    );
  }

  return (
    <Box m="9" className="projects-view">
      <div>
        <DndContext
          onDragStart={(e) => {
            setActiveProjectId(e.active.id as string);
            setDragging(true);
          }}
          onDragEnd={handleDragEnd}
          sensors={sensors}
        >
          <SortableContext
            items={projects.map((p) => p.id)}
            strategy={rectSortingStrategy}
          >
            <Grid
              className="projects-view-grid"
              columns={{ initial: "1", sm: "2", md: "3", lg: "4", xl: "5" }}
              gap="6"
            >
              {projects.map((project) => (
                <SortableProjectCard
                  key={project.id}
                  project={project}
                  isDragging={dragging && activeProjectId === project.id}
                />
              ))}
              <NewProjectButton onProjectCreated={handleProjectCreated} />
            </Grid>
          </SortableContext>
          <DragOverlay>
            {activeProject ? (
              <ProjectCard
                id={activeProject.id}
                name={activeProject.name}
                members={activeProject.members}
              />
            ) : null}
          </DragOverlay>
        </DndContext>
      </div>
    </Box>
  );
}

interface NewProjectButtonProps {
  onProjectCreated: (newProject: ProjectListItem) => void;
}

export function NewProjectButton({ onProjectCreated }: NewProjectButtonProps) {
  const [projectName, setProjectName] = useState("");
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);

  const createProject = async () => {
    if (!projectName.trim()) return;

    try {
      setLoading(true);
      const newProject = await create({ name: projectName });
      onProjectCreated(newProject);
      setProjectName("");
      setOpen(false);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card variant="ghost" className="projects-view-new-project">
      <Flex
        className="projects-view-new-project-content"
        align="center"
        justify="center"
        height="240px"
        p="3"
      >
        <Dialog.Root open={open} onOpenChange={setOpen}>
          <Dialog.Trigger>
            <Button variant="surface" size="3">
              <PlusIcon />
              <Text>Create new project</Text>
            </Button>
          </Dialog.Trigger>
          <Dialog.Content width="20vw">
            <Flex align="start" justify="between">
              <Dialog.Title>Create new project</Dialog.Title>
              <Dialog.Close>
                <IconButton
                  className="projects-view-new-project-close-button"
                  color="red"
                  variant="ghost"
                  radius="full"
                >
                  <Cross1Icon />
                </IconButton>
              </Dialog.Close>
            </Flex>
            <Flex direction="column" gap="4">
              <Dialog.Description>
                Enter the name of your new project below. You can add more
                details later.
              </Dialog.Description>
              <Box>
                <Box pb="1">
                  <Text color="gray" size="2">
                    Project name
                  </Text>
                </Box>
                <TextField.Root
                  value={projectName}
                  onChange={(e) => setProjectName(e.target.value)}
                  disabled={loading}
                />
              </Box>
              <Button onClick={createProject} loading={loading}>
                <RocketIcon />
                <Text>Create</Text>
              </Button>
            </Flex>
          </Dialog.Content>
        </Dialog.Root>
      </Flex>
    </Card>
  );
}
