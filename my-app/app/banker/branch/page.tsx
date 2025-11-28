"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Loader2, Building, Users, Wallet, Activity } from "lucide-react";
import { toast } from "sonner";

interface BranchMetric {
  label: string;
  value: string;
  helper?: string;
}

export default function BranchOverviewPage() {
  const router = useRouter();
  const { user, isAuthenticated, isBankManager } = useAuth();
  const [loading, setLoading] = useState(true);
  const [metrics] = useState<BranchMetric[]>([
    { label: "Total Customers", value: "—", helper: "Number of active customers in this branch" },
    { label: "Total Accounts", value: "—", helper: "Checking and savings accounts" },
    { label: "Total Deposits", value: "—", helper: "Sum of all account balances" },
    { label: "Today's Transactions", value: "—", helper: "Count of transactions today" },
  ]);

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login");
      return;
    }

    // Only managers can access this overview
    if (!isBankManager) {
      toast.error("Access denied. Bank Manager access required.");
      router.push("/dashboard");
      return;
    }

    // Placeholder: load real metrics from backend when available
    setLoading(false);
  }, [isAuthenticated, isBankManager, router]);

  if (loading || !user) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8 px-4 max-w-5xl">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold mb-2">Branch Operations Overview</h1>
          <p className="text-muted-foreground">
            High-level metrics for your branch. These cards can be wired to real data in the
            database.
          </p>
        </div>
        <Button variant="outline" onClick={() => router.push("/dash")}>
          Back to Dashboard
        </Button>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4 mb-8">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Branch</CardTitle>
            <Building className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-lg font-semibold">
              {user.userRole === "bank_manager" ? "Manager Branch" : "Assigned Branch"}
            </div>
            <p className="text-xs text-muted-foreground mt-1">
              Branch information can be loaded from the `branch` table when connected.
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Customers</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">—</div>
            <p className="text-xs text-muted-foreground">Active customers in this branch</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Deposits</CardTitle>
            <Wallet className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">—</div>
            <p className="text-xs text-muted-foreground">Aggregate balance for all accounts</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Today's Activity</CardTitle>
            <Activity className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">—</div>
            <p className="text-xs text-muted-foreground">Transactions processed today</p>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Branch Metrics</CardTitle>
          <CardDescription>
            These rows mirror the kind of KPIs a banker/manager would monitor. They can be wired to
            SQL queries later.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {metrics.map((metric) => (
              <div
                key={metric.label}
                className="flex items-center justify-between border-b last:border-0 py-2"
              >
                <div>
                  <div className="font-medium text-sm">{metric.label}</div>
                  {metric.helper && (
                    <div className="text-xs text-muted-foreground">{metric.helper}</div>
                  )}
                </div>
                <div className="text-base font-semibold">{metric.value}</div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}


