"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { SidebarInset, SidebarProvider } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/admin-dashboard/components/app-sidebar";
import { SiteHeader } from "@/components/admin-dashboard/components/site-header";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import { Loader2, Users, UserCog, FileText, DollarSign } from "lucide-react";
import Link from "next/link";
import { api, LoanRequest } from "@/lib/api";
import { toast } from "sonner";

export default function AdminDashboardPage() {
  const router = useRouter();
  const { user, isAdmin, isBankManager, logout } = useAuth();
  const [pendingLoans, setPendingLoans] = useState<LoanRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalUsers, setTotalUsers] = useState<number | null>(null);

  useEffect(() => {
    if (!user) {
      router.push("/login");
      return;
    }

    if (!isAdmin && !isBankManager) {
      router.push("/dashboard");
      return;
    }

    const loadData = async () => {
      try {
        const tasks: Promise<any>[] = [];

        if (isBankManager) {
          tasks.push(loadPendingLoans());
        }

        if (isAdmin) {
          tasks.push(loadAdminStats());
        }

        if (tasks.length === 0) {
          setLoading(false);
        } else {
          await Promise.all(tasks);
          setLoading(false);
        }
      } catch (e) {
        console.error("Error loading admin dashboard data:", e);
        setLoading(false);
      }
    };

    loadData();
  }, [user, isAdmin, isBankManager, router]);

  const loadPendingLoans = async () => {
    try {
      const response = await api.getPendingLoans();
      if (response.success && response.loans) {
        setPendingLoans(response.loans);
      }
    } catch (error) {
      console.error("Error loading loans:", error);
    }
  };

  const loadAdminStats = async () => {
    try {
      const response = await api.getAdminStats();
      if (response.success && typeof response.totalUsers === "number") {
        setTotalUsers(response.totalUsers);
      }
    } catch (error) {
      console.error("Error loading admin stats:", error);
    }
  };

  if (loading || !user) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  return (
    <SidebarProvider
      className="min-h-auto"
      style={
        {
          "--sidebar-width": "calc(var(--spacing) * 64)",
          "--header-height": "calc(var(--spacing) * 12 + 1px)"
        } as React.CSSProperties
      }>
      <AppSidebar variant="sidebar" />
      <SidebarInset>
        <SiteHeader />
        <div className="flex flex-1 flex-col">
          <div className="@container/main flex flex-1 flex-col gap-2">
            <div className="flex flex-col gap-4 py-4 md:gap-6 md:py-6 px-4 lg:px-6">
              {/* Welcome Section */}
              <div className="mb-6">
                <h1 className="text-3xl font-bold mb-2">
                  Welcome, {user.userName}!
                </h1>
                <p className="text-muted-foreground">
                  {isAdmin ? "Admin Dashboard" : "Bank Manager Dashboard"}
                </p>
              </div>

              {/* Quick Stats */}
              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4 mb-6">
                {isBankManager && (
                  <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                      <CardTitle className="text-sm font-medium">Pending Loans</CardTitle>
                      <DollarSign className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                      <div className="text-2xl font-bold">{pendingLoans.length}</div>
                      <p className="text-xs text-muted-foreground">Awaiting review</p>
                    </CardContent>
                  </Card>
                )}

                {isAdmin && (
                  <>
                    <Card>
                      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">User Management</CardTitle>
                        <Users className="h-4 w-4 text-muted-foreground" />
                      </CardHeader>
                      <CardContent>
                        <div className="text-2xl font-bold">
                          {totalUsers !== null ? totalUsers : "—"}
                        </div>
                        <p className="text-xs text-muted-foreground">Total users</p>
                      </CardContent>
                    </Card>

                    <Card>
                      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Role Management</CardTitle>
                        <UserCog className="h-4 w-4 text-muted-foreground" />
                      </CardHeader>
                      <CardContent>
                        <div className="text-2xl font-bold">-</div>
                        <p className="text-xs text-muted-foreground">Manage permissions</p>
                      </CardContent>
                    </Card>
                  </>
                )}
              </div>

              {/* Quick Actions */}
              <div className="mb-6">
                <h2 className="text-2xl font-semibold mb-4">Quick Actions</h2>
                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                  {isBankManager && (
                    <Link href="/admin/loans">
                      <Card className="cursor-pointer hover:bg-accent transition-colors">
                        <CardHeader>
                          <CardTitle className="flex items-center gap-2">
                            <DollarSign className="h-5 w-5" />
                            Manage Loans
                          </CardTitle>
                          <CardDescription>
                            Review and approve/reject loan requests
                          </CardDescription>
                        </CardHeader>
                      </Card>
                    </Link>
                  )}

                  {isAdmin && (
                    <>
                      <Link href="/admin/search">
                        <Card className="cursor-pointer hover:bg-accent transition-colors">
                          <CardHeader>
                            <CardTitle className="flex items-center gap-2">
                              <Users className="h-5 w-5" />
                              Search Users
                            </CardTitle>
                            <CardDescription>
                              Find and manage user accounts
                            </CardDescription>
                          </CardHeader>
                        </Card>
                      </Link>

                      <Link href="/admin/roles">
                        <Card className="cursor-pointer hover:bg-accent transition-colors">
                          <CardHeader>
                            <CardTitle className="flex items-center gap-2">
                              <UserCog className="h-5 w-5" />
                              Assign Roles
                            </CardTitle>
                            <CardDescription>
                              Manage user permissions and roles
                            </CardDescription>
                          </CardHeader>
                        </Card>
                      </Link>
                    </>
                  )}
                </div>
              </div>

              {/* Pending Loans Section (manager only) */}
              {isBankManager && pendingLoans.length > 0 && (
                <div>
                  <div className="flex justify-between items-center mb-4">
                    <h2 className="text-2xl font-semibold">Pending Loan Requests</h2>
                    <Button variant="outline" asChild>
                      <Link href="/admin/loans">
                        View All
                      </Link>
                    </Button>
                  </div>
                  <div className="grid gap-4 md:grid-cols-2">
                    {pendingLoans.slice(0, 4).map((loan) => (
                      <Card key={loan.loanID}>
                        <CardHeader>
                          <CardTitle className="text-lg">
                            Loan #{loan.loanID.slice(0, 8)}
                          </CardTitle>
                          <CardDescription>
                            ${loan.amount.toFixed(2)} • {loan.purpose}
                          </CardDescription>
                        </CardHeader>
                        <CardContent>
                          <Button variant="outline" className="w-full" asChild>
                            <Link href="/admin/loans">Review</Link>
                          </Button>
                        </CardContent>
                      </Card>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </SidebarInset>
    </SidebarProvider>
  );
}
