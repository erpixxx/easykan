import "../../../scss/components/projects_view.scss";
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
import { ProjectCard } from "./ProjectCard.tsx";

export function ProjectsView() {
  const [projects, setProjects] = useState<ProjectListItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadProjects = async () => {
      try {
        setLoading(true);
        // await new Promise((resolve) => setTimeout(resolve, 2000));
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
    setProjects((prevProjects) => [newProject, ...prevProjects]);
  };

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
      <Grid
        className="projects-view-grid"
        columns={{ initial: "1", sm: "2", md: "3", lg: "4", xl: "5" }}
        gap="6"
      >
        {projects.map((project) => (
          <ProjectCard
            key={project.id}
            id={project.id}
            name={project.name}
            members={project.members}
          />
        ))}
        <NewProjectButton onProjectCreated={handleProjectCreated} />
      </Grid>
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
