import { type ReactNode, useEffect, useState } from "react";
import type { User } from "../types/User.ts";
import { AuthContext } from "./AuthContext.tsx";
import { getCurrentUser } from "../api/auth.ts";
import { ScaleLoader } from "react-spinners";
import "../scss/loading-screen.scss";

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [isLoaderMounted, setIsLoaderMounted] = useState(true);

  useEffect(() => {
    (async () => {
      const user = await getCurrentUser();
      setUser(user);
      setLoading(false);
    })();
  }, []);

  useEffect(() => {
    if (!loading) {
      const timeout = setTimeout(() => setIsLoaderMounted(false), 500);
      return () => clearTimeout(timeout);
    }
  }, [loading]);

  if (isLoaderMounted) {
    return (
      <div className={"loading-screen"}>
        <ScaleLoader
          className={`scale-loader ${!loading ? 'fade-out' : ''}`}
          height={70}
          width={8}
          radius={100}
          margin={3}
          color={"white"}
          speedMultiplier={0.9}
          barCount={6}
        />
      </div>
    );
  }

  return (
    <AuthContext.Provider value={{ user, setUser }}>
      {children}
    </AuthContext.Provider>
  );
}