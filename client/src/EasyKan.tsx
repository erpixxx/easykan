import { RouterProvider } from "react-router-dom";
import { router } from "./routes/Router.tsx";
import {Box, Theme} from "@radix-ui/themes";
import { ThemeProvider } from "next-themes";
import "@radix-ui/themes/styles.css";
import {Toast} from "radix-ui";

function EasyKan() {
  return (
    <div className="App">
      <ThemeProvider attribute="class">
        <Theme
          accentColor="violet"
          panelBackground="translucent"
          radius="medium"
          scaling="100%"
        >
          <Box
            className="background"
            height="100%"
            width="100%"
          >
            <Toast.Provider swipeDirection="right">
              <RouterProvider router={router} />
            </Toast.Provider>
          </Box>
        </Theme>
      </ThemeProvider>
    </div>
  );
}

export default EasyKan;
