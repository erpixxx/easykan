import {Box, Callout, Card, Flex, Heading} from "@radix-ui/themes";
import {ExclamationTriangleIcon} from "@radix-ui/react-icons";
import {useState} from "react";
import {LoginForm} from "./LoginForm.tsx";
import {ThirdPartyLogin} from "./ThirdPartyLogin.tsx";

export interface LoginCardProps {
  enableLogin?: boolean;
  enableGithub?: boolean;
  enableGitlab?: boolean;
  enableDiscord?: boolean;
}

export function LoginCard({enableLogin = true, enableGithub = true, enableGitlab = true, enableDiscord = true}: LoginCardProps) {
  const [error, setError] = useState<string | null>(null);
  return (
    <Box
      className="login-card-container"
      width="400px"
    >
      <Card className="login-card">
        <Flex pb="5" align="center" justify="between">
          <Heading size="5">Log in to EasyKan</Heading>
        </Flex>
        <LoginForm enableLogin={enableLogin} setError={setError} />
        <ThirdPartyLogin
          enableLogin={enableLogin}
          enableGithub={enableGithub}
          enableGitlab={enableGitlab}
          enableDiscord={enableDiscord}
        />
      </Card>
      <Box
        className="login-error-message"
        display={error ? "block" : "none"}
        pt="3"
      >
        <Callout.Root color="red" variant="surface">
          <Callout.Icon>
            <ExclamationTriangleIcon />
          </Callout.Icon>
          <Callout.Text>
            {error}
          </Callout.Text>
        </Callout.Root>
      </Box>
    </Box>
  );
}