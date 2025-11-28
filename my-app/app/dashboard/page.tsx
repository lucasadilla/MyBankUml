"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import { Loader2, ArrowRight, Wallet, Send, FileText, DollarSign } from "lucide-react";
import Link from "next/link";

export default function UserDashboardPage() {
  const router = useRouter();
  const { user, accounts, loading, logout, isAuthenticated, loadAccounts, isCustomer } = useAuth();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login");
      return;
    }

    // Redirect managers and admins to the admin/manager dashboard
    if (user && (user.userRole === "admin" || user.userRole === "bank_manager")) {
      router.push("/dash");
      return;
    }

    // Only customers have personal accounts loaded here
    if (user && user.userRole === "customer") {
      loadAccounts();
    }
  }, [isAuthenticated, user, router, loadAccounts]);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  const totalBalance = accounts.reduce((sum, acc) => sum + acc.balance, 0);

  return (
    <div className="container mx-auto py-8 px-4">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold mb-2">Welcome, {user?.userName || "User"}!</h1>
          <p className="text-muted-foreground">
            {isCustomer
              ? "Manage your accounts and transactions"
              : "This dashboard is only for customer accounts"}
          </p>
        </div>
        <Button variant="outline" onClick={logout}>
          Logout
        </Button>
      </div>

      {isCustomer && (
        <>
          {/* Quick Stats */}
          <div className="grid gap-4 md:grid-cols-4 mb-8">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Total Balance</CardTitle>
                <Wallet className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">${totalBalance.toFixed(2)}</div>
                <p className="text-xs text-muted-foreground">{accounts.length} account(s)</p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Accounts</CardTitle>
                <FileText className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{accounts.length}</div>
                <p className="text-xs text-muted-foreground">Active accounts</p>
              </CardContent>
            </Card>
          </div>

          {/* Quick Actions */}
          <div className="mb-8">
            <h2 className="text-2xl font-semibold mb-4">Quick Actions</h2>
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
              <Link href="/transfer">
                <Card className="cursor-pointer hover:bg-accent transition-colors">
                  <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                      <Send className="h-5 w-5" />
                      Transfer Funds
                    </CardTitle>
                    <CardDescription>Transfer between your accounts</CardDescription>
                  </CardHeader>
                </Card>
              </Link>

              <Link href="/etransfer">
                <Card className="cursor-pointer hover:bg-accent transition-colors">
                  <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                      <Send className="h-5 w-5" />
                      E-Transfer
                    </CardTitle>
                    <CardDescription>Send money to others</CardDescription>
                  </CardHeader>
                </Card>
              </Link>

              <Link href="/loan">
                <Card className="cursor-pointer hover:bg-accent transition-colors">
                  <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                      <DollarSign className="h-5 w-5" />
                      Request Loan
                    </CardTitle>
                    <CardDescription>Apply for a loan</CardDescription>
                  </CardHeader>
                </Card>
              </Link>

              <Link href="/statement">
                <Card className="cursor-pointer hover:bg-accent transition-colors">
                  <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                      <FileText className="h-5 w-5" />
                      Generate Statement
                    </CardTitle>
                    <CardDescription>Monthly account statement</CardDescription>
                  </CardHeader>
                </Card>
              </Link>
            </div>
          </div>
        </>
      )}

      {/* Accounts Overview */}
      <div>
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-2xl font-semibold">My Accounts</h2>
          <Button variant="outline" asChild>
            <Link href="/accounts">
              View All <ArrowRight className="ml-2 h-4 w-4" />
            </Link>
          </Button>
        </div>

        {accounts.length === 0 ? (
          <Card>
            <CardContent className="py-12 text-center">
              <p className="text-muted-foreground mb-4">No accounts found</p>
              <Button asChild>
                <Link href="/accounts/create">Create Your First Account</Link>
              </Button>
            </CardContent>
          </Card>
        ) : (
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {accounts.slice(0, 6).map((account) => (
              <Card key={account.accountID}>
                <CardHeader>
                  <CardTitle className="text-lg">{account.accountType}</CardTitle>
                  <CardDescription>{account.accountID}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold mb-4">
                    ${account.balance.toFixed(2)}
                  </div>
                  <Button variant="outline" size="sm" className="w-full" asChild>
                    <Link href={`/transfer?account=${account.accountID}`}>View Details</Link>
                  </Button>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}



