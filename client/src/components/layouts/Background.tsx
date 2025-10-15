import style from "../../scss/components/layout/Background.module.scss";
import type { ReactNode } from "react";
import { Box } from "@radix-ui/themes";
import { useBackground } from "../../context/BackgroundContext.tsx";

export function Background({ children }: { children: ReactNode }) {
  const imageUrl = useBackground();

  return (
    <Box
      className={style.background}
      height="100%"
      width="100%"
      style={{
        backgroundImage: imageUrl ? `url(${imageUrl})` : "none",
      }}
    >
      {children}
    </Box>
  );
}
