"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import { Loader2 } from "lucide-react";
import Link from "next/link";

export default function AccountsPage() {
  const router = useRouter();
  const { user, accounts, loading, isAuthenticated, isCustomer, loadAccounts } = useAuth();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login");
      return;
    }

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

  return (
    <div className="container mx-auto py-8 px-4">
      <div className="mb-6">
        <h1 className="text-3xl font-bold mb-2">My Accounts</h1>
        <p className="text-muted-foreground">
          {isCustomer
            ? "View all your bank accounts and balances"
            : "Only customers have personal bank accounts"}
        </p>
      </div>

      {isCustomer && accounts.length === 0 ? (
        <Card>
          <CardContent className="py-12 text-center">
            <p className="text-muted-foreground mb-4">No accounts found.</p>
            <Button asChild>
              <Link href="/accounts/create">Create Your First Account</Link>
            </Button>
          </CardContent>
        </Card>
      ) : isCustomer ? (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {accounts.map((account) => (
            <Card key={account.accountID}>
              <CardHeader>
                <CardTitle className="flex items-center justify-between">
                  <span>{account.accountType}</span>
                  <span className="text-sm font-normal text-muted-foreground">
                    {account.accountID}
                  </span>
                </CardTitle>
                <CardDescription>Account Balance</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="text-3xl font-bold mb-4">
                  ${account.balance.toFixed(2)}
                </div>
                <Button
                  variant="outline"
                  onClick={() => router.push(`/transfer?account=${account.accountID}`)}
                  className="w-full"
                >
                  Transfer Funds
                </Button>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : null}

      <div className="mt-6 flex gap-4">
        <Button asChild>
          <Link href="/accounts/create">Create New Account</Link>
        </Button>
        <Button variant="outline" asChild>
          <Link href="/dashboard">Back to Dashboard</Link>
        </Button>
      </div>
    </div>
  );
}



