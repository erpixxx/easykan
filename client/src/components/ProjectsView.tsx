import "../scss/components/projects_view.scss";
import {
  Avatar,
  Box,
  Button,
  Card,
  Dialog,
  Flex,
  Grid,
  Heading,
  IconButton,
  Text,
  TextField,
  Tooltip
} from "@radix-ui/themes";
import {Cross1Icon, DotsHorizontalIcon, PlusIcon, RocketIcon} from "@radix-ui/react-icons";

export function ProjectsView() {
  return (
    <Box m="9" className="projects-view">
      <Grid
        className="projects-view-grid"
        columns={{ initial: '1', sm: '2', md: '3', lg: '4', xl: '5' }}
        gap="6"
      >
        <Project id="prj0" name="Exvoid" members={["erpix", "Matou0014", "erpix", "Matou0014", "erpix", "Matou0014", "erpix", "Matou0014"]} />
        <NewProjectButton />
      </Grid>
    </Box>
  );
}

export interface ProjectProps {
  id: string;
  name: string;
  members: string[]
}

export function Project({ id, name, members }: ProjectProps) {
  const maxMemberShow = 3;

  return (
    <Box
      className="single-project-container"
      height="240px"
      width="320px"
      asChild
    >
      <Card className="projects-view-card">
        <Flex
          className="projects-view-card-content"
          height="100%"
          direction="column"
          justify="between"
        >
          <Box className="projects-view-card-header">
            <Flex justify="between">
              <Heading className="projects-view-card-title" size="5">
                {name}
              </Heading>
              <Tooltip content="Edit project">
                <IconButton
                  className="projects-view-card-edit-button"
                  variant="ghost"
                  radius="full"
                >
                  <DotsHorizontalIcon color="white"  />
                </IconButton>
              </Tooltip>
            </Flex>
          </Box>
          <Flex
            className="projects-view-card-members"
            align="center"
            gap="2"
            justify="end"
          >
            {
              members.slice(0, maxMemberShow).map( (member, index) => (
                <Avatar
                  key={`${id}-member-${index}`}
                  className="projects-view-card-member"
                  variant="solid"
                  fallback={member.charAt(0).toUpperCase()}
                  size="2"
                  radius="full"
                  style={{ boxShadow: "0 0 4px rgba(0, 0, 0, 0.8)" }}
                />
              ))
            }
            { members.length > maxMemberShow && (
              <Tooltip content={members.slice(3).map(member => `${member}`).join(', ')}>
                <a>
                  <Avatar
                    className="projects-view-card-member projects-view-card-member-more"
                    color="gray"
                    variant="solid"
                    fallback={`+${members.length - maxMemberShow}`}
                    size="2"
                    radius="full"
                    style={{ boxShadow: "0 0 4px rgba(0, 0, 0, 0.8)" }}
                  />
                </a>
              </Tooltip>
            )}
          </Flex>
        </Flex>
      </Card>
    </Box>
  );
}

export function NewProjectButton() {
  return (
    <Card variant="ghost" className="projects-view-new-project">
      <Flex
        className="projects-view-new-project-content"
        align="center"
        justify="center"
        height="240px"
        p="3"
      >
        <Dialog.Root>
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
                Enter the name of your new project below. You can add more details later.
              </Dialog.Description>
              <Box>
                <Box pb="1">
                  <Text color="gray" size="2">Project name</Text>
                </Box>
                <TextField.Root />
              </Box>
              <Button>
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