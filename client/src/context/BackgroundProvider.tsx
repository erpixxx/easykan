import { type ReactNode, useState } from "react";
import { BackgroundContext } from "./BackgroundContext.tsx";

export function BackgroundProvider({ children }: { children: ReactNode }) {
  const [imageUrl, setImageUrl] = useState<string | null>(null);

  const value = { imageUrl, setImageUrl };

  return (
    <BackgroundContext.Provider value={value}>
      {children}
    </BackgroundContext.Provider>
  );
}
