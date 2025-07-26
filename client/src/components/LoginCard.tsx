import {Box, Button, Callout, Card, Checkbox, Flex, Heading, Separator, Text, TextField} from "@radix-ui/themes";
import {Form} from "radix-ui";
import {EnterIcon, ExclamationTriangleIcon, EyeClosedIcon, EyeOpenIcon} from "@radix-ui/react-icons";
import {type FormEvent, type ReactNode, useState} from "react";
import {BsDiscord, BsGithub, BsGitlab} from "react-icons/bs";
import { env } from "../config/env.ts"
import {useNavigate} from "react-router-dom";

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

interface LoginFormProps {
  enableLogin?: boolean;
  setError: (err: string | null) => void;
}

export function LoginForm({ enableLogin, setError }: LoginFormProps) {
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();
  if (!enableLogin) return null;

  async function login(event: FormEvent) {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`${env.API_URL}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
        credentials: "include"
      });

      if (response.status === 200) {
        navigate("/projects");
      } else {
        setError("Invalid username or password");
      }

    } catch (err) {
      setError("Unexpected error. Try again later.");
      console.error("Login error:", err);
    } finally {
      setLoading(false);
    }
  }

  return (
    <Form.Root
      className="login-form-root"
      onSubmit={login}
    >
      <Box pb="5">
        <Form.Field className="login-form-field" name="username">
          <div
            style={{
              display: "flex",
              alignItems: "baseline",
              justifyContent: "space-between",
            }}
          >
            <Flex width="100%" justify="between" align="end" pb="1">
              <Form.Label className="login-form-field__label">Username</Form.Label>
              <Form.Message className="login-form-field__message" match="valueMissing">
                Please enter your email
              </Form.Message>
            </Flex>
          </div>
          <Form.Control className="login-form-field__control" asChild>
            <TextField.Root
              size="3"
              variant="surface"
              placeholder="Enter your username"
              onChange={e => setEmail(e.target.value)}
              autoComplete="username"
              required
            />
          </Form.Control>
        </Form.Field>
      </Box>
      <Box pb="5">
        <Form.Field className="login-form-field" name="password">
          <div
            style={{
              display: "flex",
              alignItems: "baseline",
              justifyContent: "space-between",
            }}
          >
            <Flex width="100%" justify="between" align="end" pb="1">
              <Form.Label className="login-form-field__label">Password</Form.Label>
              <Form.Message className="login-form-field__message" match="valueMissing">
                Please enter a password
              </Form.Message>
            </Flex>
          </div>
          <Form.Control className="login-form-field__control" asChild>
            <TextField.Root
              size="3"
              variant="surface"
              placeholder="Enter your password"
              type={showPassword ? "text" : "password"}
              onChange={e => setPassword(e.target.value)}
              autoComplete="current-password"
              required
            >
              <TextField.Slot
                side="right"
                onClick={() => setShowPassword(!showPassword)}
                style={{cursor: "pointer"}}
              >
                {showPassword ? <EyeOpenIcon /> : <EyeClosedIcon />}
              </TextField.Slot>
            </TextField.Root>
          </Form.Control>
        </Form.Field>
      </Box>
      <Flex justify="between" align="center">
        <Text as="label" size="2">
          <Flex gap="1">
            <Checkbox size="3" />
            Remember me
          </Flex>
        </Text>
        <Form.Submit className="login-form-submit" asChild>
          <Button loading={loading} size="3" variant="surface">
            <EnterIcon />
            <Text>Login</Text>
          </Button>
        </Form.Submit>
      </Flex>
    </Form.Root>
  );
}

function ThirdPartyLogin(form: LoginCardProps) {
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