import style from "../../../scss/components/ProjectCard.module.scss";
import type { ProjectListItem } from "../../../types/dto/project/ProjectListItem.ts";
import { Avatar, Box, Flex, Heading, Tooltip } from "@radix-ui/themes";
import { useNavigate } from "react-router-dom";

export function ProjectCard({ id, name, members }: ProjectListItem) {
  const navigate = useNavigate();
  const maxMemberShow = 3;

  const handleClick = () => {
    navigate(`/projects/${id}`);
  };

  return (
    <Box height="240px" asChild onClick={handleClick}>
      <Box className={style.projectCard}>
        <Box className={style.projectCardBackground}>
          <Box className={style.projectCardBackgroundGradient}>
            <Flex
              className={style.projectCardContent}
              height="100%"
              direction="column"
              justify="between"
            >
              <Box className="projects-card-header">
                <Flex justify="between">
                  <Heading className={style.projectCardTitle} size="5">
                    {name}
                  </Heading>
                </Flex>
              </Box>
              <Flex
                className="projects-card-members"
                align="center"
                gap="2"
                justify="end"
              >
                {members.slice(0, maxMemberShow).map((member, index) => (
                  <Avatar
                    key={`${id}-member-${index}`}
                    className="projects-card-member"
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
                        className="projects-card-member projects-card-member-more"
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
          </Box>
        </Box>
      </Box>
    </Box>
  );
}
