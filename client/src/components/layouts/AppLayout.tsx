import { AuthProvider } from "../../context/AuthProvider.tsx";
import { Theme, Box } from "@radix-ui/themes";
import { Toast } from "radix-ui";
import { Outlet } from "react-router-dom";

export function AppLayout() {
  return (
    <Theme
      accentColor="violet"
      panelBackground="translucent"
      radius="medium"
      scaling="100%"
    >
      <AuthProvider>
        <Box
          className="background"
          height="100%"
          width="100%"
        >
          <Toast.Provider swipeDirection="right">
            <Outlet />
          </Toast.Provider>
        </Box>
      </AuthProvider>
    </Theme>
  );
}