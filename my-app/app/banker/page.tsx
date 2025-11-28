"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Loader2, ArrowRight, Activity, History } from "lucide-react";
import Link from "next/link";

export default function BankerDashboardPage() {
  const router = useRouter();
  const { user, isAuthenticated, isBanker, isBankManager, isAdmin, logout } = useAuth();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login");
      return;
    }

    // Admins and managers should use the admin dashboard instead
    if (isAdmin || isBankManager) {
      router.push("/dash");
      return;
    }

    // Only bankers can use this dashboard
    if (!isBanker) {
      router.push("/dashboard");
    }
  }, [isAuthenticated, isBanker, isAdmin, isBankManager, router]);

  if (!user || (!isBanker && !isAdmin && !isBankManager)) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8 px-4">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold mb-2">Welcome, {user.userName}!</h1>
          <p className="text-muted-foreground">
            Banker Dashboard – review and reverse customer transactions
          </p>
        </div>
        <Button variant="outline" onClick={logout}>
          Logout
        </Button>
      </div>

      {/* Quick Actions */}
      <div className="mb-8">
        <h2 className="text-2xl font-semibold mb-4">Quick Actions</h2>
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          <Link href="/banker/transactions">
            <Card className="cursor-pointer hover:bg-accent transition-colors">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <History className="h-5 w-5" />
                  Review Transactions
                </CardTitle>
                <CardDescription>
                  View recent customer transactions for your branch
                </CardDescription>
              </CardHeader>
            </Card>
          </Link>

          <Link href="/banker/reverse">
            <Card className="cursor-pointer hover:bg-accent transition-colors">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Activity className="h-5 w-5" />
                  Reverse Transaction
                </CardTitle>
                <CardDescription>
                  Locate and reverse an incorrect transaction
                </CardDescription>
              </CardHeader>
            </Card>
          </Link>
        </div>
      </div>

      {/* Info Section */}
      <Card>
        <CardHeader>
          <CardTitle>What you can do as a Banker</CardTitle>
          <CardDescription>
            Capabilities are aligned with the assignment role matrix
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-2 text-sm text-muted-foreground">
          <p>• Review customer transactions and investigate issues.</p>
          <p>• Initiate transaction reversals when appropriate.</p>
          <p className="flex items-center gap-1 text-xs">
            <ArrowRight className="h-3 w-3" />
            Admin-only actions like searching users or assigning roles remain in the Admin
            Dashboard.
          </p>
        </CardContent>
      </Card>
    </div>
  );
}


