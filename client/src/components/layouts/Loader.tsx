import { type ReactNode, useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext.tsx";
import { ScaleLoader } from "react-spinners";
import "../../scss/loading-screen.scss";

export const Loader = ({ children }: { children: ReactNode }) => {
  const { isLoading } = useAuth();
  const [isLoaderMounted, setIsLoaderMounted] = useState(true);

  useEffect(() => {
    if (!isLoading) {
      const timeout = setTimeout(() => setIsLoaderMounted(false), 500);
      return () => clearTimeout(timeout);
    }
  }, [isLoading]);

  if (isLoaderMounted) {
    return (
      <div className={"loading-screen"}>
        <ScaleLoader
          className={`scale-loader ${!isLoading ? "fade-out" : ""}`}
          height={70}
          width={8}
          radius={100}
          margin={3}
          color={"white"}
          speedMultiplier={0.9}
        />
      </div>
    );
  }

  return <>{children}</>;
};
