"use client";

import { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { api, AdminUserDetails } from "@/lib/api";
import { toast } from "sonner";
import { Loader2 } from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";

export default function RoleAssignmentPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { isAdmin, isAuthenticated } = useAuth();
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!isAuthenticated || !isAdmin) {
      toast.error("Admin access required");
      router.push("/login");
    }
  }, [isAuthenticated, isAdmin, router]);
  const [user, setUser] = useState<AdminUserDetails | null>(null);
  const [selectedRole, setSelectedRole] = useState("");
  const userID = searchParams.get("userID");

  useEffect(() => {
    if (!userID) {
      toast.error("No user ID provided");
      router.push("/admin/search");
      return;
    }

    const loadUser = async () => {
      try {
        setLoading(true);
        const response = await api.getUserDetails(userID);
        if (response.success && response.user) {
          setUser(response.user);
          setSelectedRole(response.user.userRole);
        } else {
          toast.error(response.message || "Failed to load user details");
          router.push("/admin/search");
        }
      } catch (error: any) {
        toast.error(error.message || "Failed to load user details");
        router.push("/admin/search");
      } finally {
        setLoading(false);
      }
    };

    loadUser();
  }, [userID, router]);

  const handleAssignRole = async () => {
    if (!userID || !selectedRole) {
      toast.error("Please select a role");
      return;
    }

    try {
      setLoading(true);
      const response = await api.assignRole(userID, selectedRole);
      if (response.success) {
        toast.success("Role assigned successfully!");
        router.push("/admin/search");
      } else {
        toast.error(response.message || "Failed to assign role");
      }
    } catch (error: any) {
      toast.error(error.message || "Failed to assign role");
    } finally {
      setLoading(false);
    }
  };

  if (!user) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8 px-4 max-w-2xl">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold mb-2">Assign Role</h1>
          <p className="text-muted-foreground">Assign or change user role permissions</p>
        </div>
        <Button variant="outline" onClick={() => router.push("/dash")}>
          Back to Admin Dashboard
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>User Information</CardTitle>
          <CardDescription>Current user details</CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="space-y-2">
            <Label>User ID</Label>
            <div className="p-2 bg-muted rounded-md font-mono text-sm">{user.userID}</div>
          </div>

          <div className="space-y-2">
            <Label>Name</Label>
            <div className="p-2 bg-muted rounded-md">{user.userName}</div>
          </div>

          <div className="space-y-2">
            <Label>Email</Label>
            <div className="p-2 bg-muted rounded-md">{user.userEmail}</div>
          </div>

          <div className="space-y-2">
            <Label>Current Role</Label>
            <div className="p-2 bg-muted rounded-md capitalize">{user.userRole}</div>
          </div>

          {user.userPhone && (
            <div className="space-y-2">
              <Label>Phone</Label>
              <div className="p-2 bg-muted rounded-md">{user.userPhone}</div>
            </div>
          )}

          {user.password && (
            <div className="space-y-2">
              <Label>Password</Label>
              <div className="p-2 bg-muted rounded-md font-mono text-sm">{user.password}</div>
            </div>
          )}

          {user.accounts && user.accounts.length > 0 && (
            <div className="space-y-2">
              <Label>Accounts</Label>
              <div className="space-y-2">
                {user.accounts.map((account) => (
                  <div
                    key={account.accountID}
                    className="flex items-center justify-between p-2 border rounded-md text-sm"
                  >
                    <div>
                      <div className="font-medium">{account.accountType}</div>
                      <div className="text-xs text-muted-foreground">{account.accountID}</div>
                    </div>
                    <div className="font-mono">
                      ${account.balance.toFixed(2)}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {typeof user.loanRequestCount === "number" && (
            <div className="space-y-2">
              <Label>Loan Requests</Label>
              <div className="p-2 bg-muted rounded-md text-sm">
                {user.loanRequestCount} request
                {user.loanRequestCount === 1 ? "" : "s"}
              </div>
            </div>
          )}

          <div className="space-y-2">
            <Label htmlFor="role">New Role</Label>
            <Select value={selectedRole} onValueChange={setSelectedRole}>
              <SelectTrigger id="role">
                <SelectValue placeholder="Select a role" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="customer">Customer</SelectItem>
                <SelectItem value="banker">Banker</SelectItem>
                <SelectItem value="bank_manager">Bank Manager</SelectItem>
                <SelectItem value="admin">Admin</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div className="flex gap-4 pt-4">
            <Button
              type="button"
              variant="outline"
              onClick={() => router.back()}
              className="flex-1"
            >
              Cancel
            </Button>
            <Button
              type="button"
              onClick={handleAssignRole}
              disabled={loading || selectedRole === user.userRole}
              className="flex-1"
            >
              {loading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Assigning...
                </>
              ) : (
                "Assign Role"
              )}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}



