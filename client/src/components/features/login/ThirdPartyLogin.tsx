import {Box, Button, Flex, Separator, Text} from "@radix-ui/themes";
import type {ReactNode} from "react";
import {BsDiscord, BsGithub, BsGitlab} from "react-icons/bs";
import type {LoginCardProps} from "./LoginCard.tsx";

export function ThirdPartyLogin(form: LoginCardProps) {
  const buttons: ThirdPartyLoginButtonProps[] = [];

  if (form.enableDiscord) buttons.push(DiscordButtonData);
  if (form.enableGithub) buttons.push(GitHubButtonData);
  if (form.enableGitlab) buttons.push(GitLabButtonData);

  if (buttons.length === 0) return null;

  return (
    <Flex
      className="third-party-login-buttons"
      pt={form.enableLogin ? "5" : "0"}
      width="100%"
      direction="column"
      gap="3"
    >
      {form.enableLogin && (
        <Flex align="center" gap="2">
          <Separator size="4" />
          <Text wrap="nowrap" color="gray" size="2">
            Or login with
          </Text>
          <Separator size="4" />
        </Flex>
      )}
      <Flex
        className="third-party-login-buttons__container"
        direction={form.enableLogin ? "row" : "column"}
        justify="center"
        align="center"
        gap="3"
        px="2"
      >
        {buttons.map((btn, idx) => (
          <ThirdPartyLoginButton key={idx} size={buttons.length > 2 ? "2" : "3"} {...btn} />
        ))}
      </Flex>
    </Flex>
  );
}

interface ThirdPartyLoginButtonProps {
  icon: ReactNode;
  label: string;
  color: "gray" | "gold" | "bronze" | "brown" | "yellow" | "amber" | "orange" | "tomato" | "red" | "ruby" | "crimson" | "pink" | "plum" | "purple" | "violet" | "iris" | "indigo" | "blue" | "cyan" | "teal" | "jade" | "green" | "grass" | "lime" | "mint" | "sky";
  size?: "2" | "3";
  onClick?: () => void;
}

function ThirdPartyLoginButton({ icon, label, color, size, onClick }: ThirdPartyLoginButtonProps) {
  return (
    <Box width="100%">
      <Button
        className="third-party-login-button"
        variant="surface"
        size={size || "3"}
        color={color}
        onClick={onClick}
        style={{width: "100%"}}
      >
        {icon}
        <Text>{label}</Text>
      </Button>
    </Box>
  );
}

const DiscordButtonData: ThirdPartyLoginButtonProps = {
  icon: <BsDiscord />,
  label: "Discord",
  color: "indigo",
};

const GitHubButtonData: ThirdPartyLoginButtonProps = {
  icon: <BsGithub />,
  label: "GitHub",
  color: "green",
};

const GitLabButtonData: ThirdPartyLoginButtonProps = {
  icon: <BsGitlab />,
  label: "GitLab",
  color: "orange",
};