import { createContext, useContext } from "react";

export interface BackgroundContextType {
  imageUrl: string | null;
  setImageUrl: (url: string) => void;
}

export const BackgroundContext = createContext<BackgroundContextType>({
  imageUrl: null,
  setImageUrl: () => {},
});

export const useBackground = () => {
  const ctx = useContext(BackgroundContext);
  if (!ctx) {
    throw new Error("useBackground must be used within a BackgroundProvider");
  }
  return ctx;
};
