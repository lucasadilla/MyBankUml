"use client";

import { createContext, useContext, useEffect, useState, ReactNode } from "react";
import { useRouter } from "next/navigation";
import { api, User, Account } from "@/lib/api";
import { toast } from "sonner";

interface AuthContextType {
  user: User | null;
  accounts: Account[];
  loading: boolean;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => void;
  loadAccounts: () => Promise<void>;
  isAuthenticated: boolean;
  isAdmin: boolean;
  isBanker: boolean;
  isBankManager: boolean;
  isCustomer: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    // Load user from localStorage on mount
    const storedUser = localStorage.getItem("user");
    if (storedUser) {
      try {
        const userData = JSON.parse(storedUser);
        setUser(userData);
        if (userData.userRole === "customer") {
          loadAccounts(userData.userID);
        } else {
          setLoading(false);
        }
      } catch (error) {
        console.error("Error loading user:", error);
        localStorage.removeItem("user");
        setLoading(false);
      }
    } else {
      setLoading(false);
    }
  }, []);

  const loadAccounts = async (customerID?: string) => {
    const userId = customerID || user?.userID;
    if (!userId || user?.userRole !== "customer") {
      setLoading(false);
      return;
    }

    try {
      const response = await api.getAccounts(userId);
      if (response.success && response.accounts) {
        setAccounts(response.accounts);
      } else {
        setAccounts([]);
      }
    } catch (error) {
      console.error("Error loading accounts:", error);
      setAccounts([]);
    } finally {
      setLoading(false);
    }
  };

  const login = async (username: string, password: string): Promise<boolean> => {
    try {
      const response = await api.login(username, password);
      if (response.success && response.user) {
        setUser(response.user);
        localStorage.setItem("user", JSON.stringify(response.user));
        
        if (response.user.userRole === "customer") {
          await loadAccounts(response.user.userID);
        } else {
          setLoading(false);
        }
        
        return true;
      } else {
        toast.error(response.message || "Login failed");
        return false;
      }
    } catch (error: any) {
      toast.error(error.message || "Login failed");
      return false;
    }
  };

  const logout = () => {
    setUser(null);
    setAccounts([]);
    localStorage.removeItem("user");
    localStorage.removeItem("lastReceipt");
    router.push("/login");
  };

  const value: AuthContextType = {
    user,
    accounts,
    loading,
    login,
    logout,
    loadAccounts: () => loadAccounts(),
    isAuthenticated: !!user,
    isAdmin: user?.userRole === "admin",
    isBanker: user?.userRole === "banker",
    isBankManager: user?.userRole === "bank_manager",
    isCustomer: user?.userRole === "customer",
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}

