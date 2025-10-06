import { type ReactNode, useEffect, useState } from "react";
import type { User } from "../types/User.ts";
import { AuthContext } from "./AuthContext.tsx";
import { getCurrentUser, logout as apiLogout } from "../api/auth.api.ts";
import { useNavigate } from "react-router-dom";
import { LOGIN_PAGE_PATH } from "../routes/route.ts";

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    (async () => {
      const user = await getCurrentUser();
      setUser(user);
      setIsLoading(false);
    })();
  }, []);

  const logout = async (): Promise<void> => {
    try {
      await apiLogout();
    } catch (error) {
      console.warn(
        "Logout request failed, but clearing client state anyway.",
        error,
      );
    } finally {
      setUser(null);
      navigate(LOGIN_PAGE_PATH);
    }
  };

  return (
    <AuthContext.Provider value={{ user, setUser, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
};
