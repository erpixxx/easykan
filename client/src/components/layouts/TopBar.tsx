import "../../scss/components/user_nav.scss";
import logo from "../../assets/logo-256.png";
import {
  Avatar,
  Badge,
  Box,
  Card,
  DropdownMenu,
  Flex,
  Heading,
  IconButton,
  Popover,
  ScrollArea,
  Text,
} from "@radix-ui/themes";
import { BellIcon, ExitIcon, GearIcon } from "@radix-ui/react-icons";
import type { ReactNode } from "react";
import { logout } from "../../api/auth.api.ts";
import { useNavigate } from "react-router-dom";
import { LOGIN_PAGE_PATH } from "../../routes/route.ts";

export function TopBar() {
  const navigate = useNavigate();

  async function handleLogout() {
    try {
      await logout();
    } catch (error) {
      console.error("Logout failed:", error);
    } finally {
      navigate(LOGIN_PAGE_PATH);
    }
  }

  return (
    <Box width="100%" className="user-nav-container">
      <Box position="sticky" p="3" className="user-nav">
        <Flex align="center" justify="between">
          <Box height="32px" className="user-nav-item" asChild>
            <img
              src={logo}
              alt="logo"
              style={{
                maxHeight: "100%",
                width: "auto",
              }}
            />
          </Box>
          <Flex gap="5" align="center">
            <NotificationMenu>
              <Notification
                content={"@erpix added you to the Exvoid Network project"}
                timestamp={"2 hours ago"}
              />
              <Notification
                content={"@erpix added you to the Exvoid Network project"}
                timestamp={"2 hours ago"}
              />
            </NotificationMenu>
            <DropdownMenu.Root>
              <DropdownMenu.Trigger>
                <a>
                  <Avatar fallback="E" radius="full" size="2" />
                </a>
              </DropdownMenu.Trigger>
              <DropdownMenu.Content>
                <Flex width="100%" pb="2" align="center" gap="3">
                  <Avatar fallback="E" radius="full" size="2" />
                  <Text>erpix :3</Text>
                </Flex>
                <DropdownMenu.Item>
                  <GearIcon />
                  <Text>Settings</Text>
                </DropdownMenu.Item>
                <DropdownMenu.Separator />
                <DropdownMenu.Item color="red" onClick={handleLogout}>
                  <ExitIcon />
                  <Text>Logout</Text>
                </DropdownMenu.Item>
              </DropdownMenu.Content>
            </DropdownMenu.Root>
          </Flex>
        </Flex>
      </Box>
    </Box>
  );
}

export function NotificationMenu({ children }: { children: ReactNode }) {
  return (
    <Popover.Root>
      <Popover.Trigger>
        <a>
          <IconButton radius="full" variant="ghost">
            <Box position="relative">
              <BellIcon />
              <Badge
                variant="solid"
                style={{
                  position: "absolute",
                  top: 0,
                  right: 0,
                  transform: "translate(50%, -50%)",
                  padding: "0rem 0.3rem",
                  fontSize: "0.6rem",
                }}
              >
                2
              </Badge>
            </Box>
          </IconButton>
        </a>
      </Popover.Trigger>
      <Popover.Content>
        <ScrollArea style={{ maxHeight: "50vh" }}>
          <Flex direction="column" gap="3">
            {children}
          </Flex>
        </ScrollArea>
      </Popover.Content>
    </Popover.Root>
  );
}

export interface NotificationProps {
  title?: string;
  content: string;
  timestamp: string;
}

export function Notification({ title, content, timestamp }: NotificationProps) {
  return (
    <Card>
      <Flex direction="column" gap="2">
        {title && <Heading size="1">{title}</Heading>}
        <Text size="1">{content}</Text>
        <Flex>
          <Text size="1" color="gray">
            {timestamp}
          </Text>
        </Flex>
      </Flex>
    </Card>
  );
}
