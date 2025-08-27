import { RouterProvider } from "react-router-dom";
import { router } from "./routes/Router.tsx";
import { ThemeProvider } from "next-themes";
import "@radix-ui/themes/styles.css";

function EasyKan() {
  return (
    <div className="App">
      <ThemeProvider attribute="class">
        <RouterProvider router={router} />
      </ThemeProvider>
    </div>
  );
}

export default EasyKan;
