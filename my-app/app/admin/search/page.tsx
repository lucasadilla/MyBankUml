"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { api, User } from "@/lib/api";
import { toast } from "sonner";
import { Loader2, Search } from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";

export default function SearchUsersPage() {
  const router = useRouter();
  const { user, isAdmin, isAuthenticated } = useAuth();
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!isAuthenticated || !isAdmin) {
      toast.error("Admin access required");
      router.push("/login");
    }
  }, [isAuthenticated, isAdmin, router]);
  const [users, setUsers] = useState<User[]>([]);
  const [searchParams, setSearchParams] = useState({
    name: "",
    accountNumber: "",
    phoneNumber: "",
    userType: "",
  });

  const handleSearch = async () => {
    try {
      setLoading(true);
      console.log("🔍 Frontend: Starting search with params:", searchParams);
      const response = await api.searchUsers(searchParams);
      console.log("🔍 Frontend: Search response:", response);
      if (response.success && response.users) {
        setUsers(response.users);
        if (response.users.length === 0) {
          toast.info("No users found matching your criteria");
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
      userType: "",
    });
    setUsers([]);
  };

  return (
    <div className="container mx-auto py-8 px-4 max-w-4xl">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold mb-2">Search Users</h1>
          <p className="text-muted-foreground">Search for users by various criteria</p>
        </div>
        <Button variant="outline" onClick={() => router.push("/dash")}>
          Back to Admin Dashboard
        </Button>
      </div>

      <Card className="mb-6">
        <CardHeader>
          <CardTitle>Search Criteria</CardTitle>
          <CardDescription>Enter one or more search criteria</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-2">
              <Label htmlFor="name">Name</Label>
              <Input
                id="name"
                value={searchParams.name}
                onChange={(e) => setSearchParams({ ...searchParams, name: e.target.value })}
                placeholder="Enter user name"
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
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="userType">User Type</Label>
              <Select
                value={searchParams.userType || "all"}
                onValueChange={(value) => setSearchParams({ ...searchParams, userType: value === "all" ? "" : value })}
              >
                <SelectTrigger id="userType">
                  <SelectValue placeholder="Select user type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Types</SelectItem>
                  <SelectItem value="customer">Customer</SelectItem>
                  <SelectItem value="banker">Banker</SelectItem>
                  <SelectItem value="bank_manager">Bank Manager</SelectItem>
                  <SelectItem value="admin">Admin</SelectItem>
                </SelectContent>
              </Select>
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
                  Search
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
            <CardDescription>{users.length} user(s) found</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {users.map((user) => (
                <div
                  key={user.userID}
                  className="flex items-center justify-between p-4 border rounded-lg"
                >
                  <div>
                    <div className="font-semibold">{user.userName}</div>
                    <div className="text-sm text-muted-foreground">
                      {user.userEmail} • {user.userRole}
                    </div>
                    <div className="text-xs text-muted-foreground mt-1">ID: {user.userID}</div>
                  </div>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => router.push(`/admin/roles?userID=${user.userID}`)}
                  >
                    View/Edit
                  </Button>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}



