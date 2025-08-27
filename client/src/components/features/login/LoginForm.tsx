import { type FormEvent, useState } from "react";
import { Form } from "radix-ui";
import { Box, Button, Checkbox, Flex, Text, TextField } from "@radix-ui/themes";
import { EnterIcon, EyeClosedIcon, EyeOpenIcon } from "@radix-ui/react-icons";
import { login } from "../../../api/auth.ts";
import { useAuth } from "../../../context/AuthContext.tsx";

interface LoginFormProps {
  enableLogin?: boolean;
  setError: (err: string | null) => void;
}

export function LoginForm({ enableLogin, setError }: LoginFormProps) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const { setUser } = useAuth();

  if (!enableLogin) return null;

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const user = await login({ login: username, password });
      setUser(user);
    } catch (err: unknown) {
      let errorMessage = "An unknown error occurred during login.";
      if (err instanceof Error) {
        errorMessage = err.message;
      }
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  }

  return (
    <Form.Root
      className="login-form-root"
      onSubmit={handleSubmit}
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
              onChange={e => setUsername(e.target.value)}
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