import { AuthProvider } from "../../context/AuthProvider.tsx";
import { Theme } from "@radix-ui/themes";
import { Toast } from "radix-ui";
import { Outlet } from "react-router-dom";
import { Loader } from "./Loader.tsx";
import { BackgroundProvider } from "../../context/BackgroundProvider.tsx";
import { Background } from "./Background.tsx";

export function AppLayout() {
  return (
    <Theme
      accentColor="violet"
      panelBackground="translucent"
      radius="medium"
      scaling="100%"
    >
      <AuthProvider>
        <BackgroundProvider>
          <Background>
            <Loader>
              <Toast.Provider swipeDirection="right">
                <Outlet />
              </Toast.Provider>
            </Loader>
          </Background>
        </BackgroundProvider>
      </AuthProvider>
    </Theme>
  );
}
