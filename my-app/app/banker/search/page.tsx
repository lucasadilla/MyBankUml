"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { api, User } from "@/lib/api";
import { toast } from "sonner";
import { Loader2, Search, ArrowLeft } from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";

export default function SearchCustomersPage() {
  const router = useRouter();
  const { user, isBanker, isBankManager, isAuthenticated } = useAuth();
  const [loading, setLoading] = useState(false);
  const [users, setUsers] = useState<User[]>([]);
  const [searchParams, setSearchParams] = useState({
    name: "",
    accountNumber: "",
    phoneNumber: "",
  });

  useEffect(() => {
    if (!isAuthenticated || (!isBanker && !isBankManager)) {
      toast.error("Banker or Bank Manager access required");
      router.push("/login");
    }
  }, [isAuthenticated, isBanker, isBankManager, router]);

  const handleSearch = async () => {
    try {
      setLoading(true);
      console.log("🔍 Frontend: Starting customer search with params:", searchParams);
      const response = await api.searchCustomers(searchParams);
      console.log("🔍 Frontend: Search response:", response);
      if (response.success && response.users) {
        setUsers(response.users);
        if (response.users.length === 0) {
          toast.info("No customers found matching your criteria");
        } else {
          toast.success(`Found ${response.users.length} customer(s)`);
        }
      } else {
        console.error("🔍 Frontend: Search failed:", response.message);
        toast.error(response.message || "Search failed");
      }
    } catch (error: any) {
      console.error("🔍 Frontend: Search error:", error);
      toast.error(error.message || "Search failed");
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setSearchParams({
      name: "",
      accountNumber: "",
      phoneNumber: "",
    });
    setUsers([]);
  };

  const getBackUrl = () => {
    if (isBankManager) {
      return "/dash";
    }
    return "/banker";
  };

  if (!isAuthenticated || (!isBanker && !isBankManager)) {
    return null;
  }

  return (
    <div className="container mx-auto py-8 px-4 max-w-4xl">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold mb-2">Search Customers</h1>
          <p className="text-muted-foreground">
            Search for customer accounts by name, account number, or phone number
          </p>
        </div>
        <Button variant="outline" onClick={() => router.push(getBackUrl())}>
          <ArrowLeft className="mr-2 h-4 w-4" />
          Back to Dashboard
        </Button>
      </div>

      <Card className="mb-6">
        <CardHeader>
          <CardTitle>Search Criteria</CardTitle>
          <CardDescription>Enter one or more search criteria to find customers</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 md:grid-cols-3">
            <div className="space-y-2">
              <Label htmlFor="name">Name or Email</Label>
              <Input
                id="name"
                value={searchParams.name}
                onChange={(e) => setSearchParams({ ...searchParams, name: e.target.value })}
                placeholder="Enter customer name or email"
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    handleSearch();
                  }
                }}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="accountNumber">Account Number</Label>
              <Input
                id="accountNumber"
                value={searchParams.accountNumber}
                onChange={(e) =>
                  setSearchParams({ ...searchParams, accountNumber: e.target.value })
                }
                placeholder="Enter account number"
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    handleSearch();
                  }
                }}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="phoneNumber">Phone Number</Label>
              <Input
                id="phoneNumber"
                value={searchParams.phoneNumber}
                onChange={(e) =>
                  setSearchParams({ ...searchParams, phoneNumber: e.target.value })
                }
                placeholder="Enter phone number"
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    handleSearch();
                  }
                }}
              />
            </div>
          </div>

          <div className="flex gap-4 mt-6">
            <Button onClick={handleSearch} disabled={loading} className="flex-1">
              {loading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Searching...
                </>
              ) : (
                <>
                  <Search className="mr-2 h-4 w-4" />
                  Search Customers
                </>
              )}
            </Button>
            <Button variant="outline" onClick={handleReset} className="flex-1">
              Reset
            </Button>
          </div>
        </CardContent>
      </Card>

      {users.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle>Search Results</CardTitle>
            <CardDescription>{users.length} customer(s) found</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {users.map((user) => (
                <div
                  key={user.userID}
                  onClick={() => router.push(`/banker/customers/${user.userID}`)}
                  className="flex items-center justify-between p-4 border rounded-lg hover:bg-accent transition-colors cursor-pointer"
                >
                  <div className="flex-1">
                    <div className="font-semibold">{user.userName}</div>
                    <div className="text-sm text-muted-foreground">
                      {user.userEmail}
                    </div>
                    {user.userPhone && (
                      <div className="text-xs text-muted-foreground mt-1">
                        Phone: {user.userPhone}
                      </div>
                    )}
                    <div className="text-xs text-muted-foreground mt-1">ID: {user.userID}</div>
                  </div>
                  <div className="text-sm text-muted-foreground mr-4">
                    Customer
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}

