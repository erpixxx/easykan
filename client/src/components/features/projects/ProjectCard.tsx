import type { ProjectListItem } from "../../../types/dto/project/ProjectListItem.ts";
import {
  Avatar,
  Box,
  Card,
  Flex,
  Heading,
  IconButton,
  Tooltip,
} from "@radix-ui/themes";
import { DotsHorizontalIcon } from "@radix-ui/react-icons";

export function ProjectCard({ id, name, members }: ProjectListItem) {
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
                  <DotsHorizontalIcon color="white" />
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
            {members.slice(0, maxMemberShow).map((member, index) => (
              <Avatar
                key={`${id}-member-${index}`}
                className="projects-view-card-member"
                variant="solid"
                fallback={member.displayName.charAt(0).toUpperCase()}
                size="2"
                radius="full"
                style={{ boxShadow: "0 0 4px rgba(0, 0, 0, 0.8)" }}
              />
            ))}
            {members.length > maxMemberShow && (
              <Tooltip content={members.slice(maxMemberShow).join(", ")}>
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
