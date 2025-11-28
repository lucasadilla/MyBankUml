"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { api } from "@/lib/api";
import { toast } from "sonner";
import { Loader2, ArrowLeft, CreditCard, History, DollarSign, TrendingUp, TrendingDown } from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";

interface AccountDetails {
  accountID: string;
  accountType: string;
  balance: number;
  customerID: string;
  transactions?: {
    transactionID: string;
    customerID: string;
    transactionType: string;
    amount: number;
    sourceAccountID: string | null;
    destinationAccountID: string | null;
    status: string;
    initiatedAt: string;
  }[];
}

export default function AccountDetailsPage() {
  const router = useRouter();
  const params = useParams();
  const accountID = params.accountID as string;
  const { user, isAuthenticated, isCustomer } = useAuth();
  const [loading, setLoading] = useState(true);
  const [account, setAccount] = useState<AccountDetails | null>(null);

  useEffect(() => {
    if (!isAuthenticated || !isCustomer) {
      toast.error("Customer access required");
      router.push("/login");
      return;
    }

    const loadAccount = async () => {
      try {
        setLoading(true);
        const response = await api.getAccountDetails(accountID);
        if (response.success && response.account) {
          // Verify this account belongs to the logged-in customer
          if (response.account.customerID !== user?.userID) {
            toast.error("You don't have access to this account");
            router.push("/accounts");
            return;
          }
          setAccount(response.account);
        } else {
          toast.error(response.message || "Failed to load account details");
          router.push("/accounts");
        }
      } catch (error: any) {
        toast.error(error.message || "Failed to load account details");
        router.push("/accounts");
      } finally {
        setLoading(false);
      }
    };

    if (accountID) {
      loadAccount();
    }
  }, [accountID, isAuthenticated, isCustomer, user, router]);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  if (!account) {
    return null;
  }

  // Determine if transaction is incoming or outgoing for this account
  const getTransactionDirection = (tx: AccountDetails["transactions"][0]) => {
    if (tx.sourceAccountID === accountID) {
      return "outgoing";
    } else if (tx.destinationAccountID === accountID) {
      return "incoming";
    }
    return "unknown";
  };

  return (
    <div className="container mx-auto py-8 px-4 max-w-6xl">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold mb-2">Account Details</h1>
          <p className="text-muted-foreground">View account information and transaction history</p>
        </div>
        <Button variant="outline" onClick={() => router.push("/accounts")}>
          <ArrowLeft className="mr-2 h-4 w-4" />
          Back to Accounts
        </Button>
      </div>

      {/* Account Information */}
      <Card className="mb-6">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <CreditCard className="h-5 w-5" />
            Account Information
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-2">
              <div className="text-sm font-medium text-muted-foreground">Account Type</div>
              <div className="text-lg font-semibold">{account.accountType}</div>
            </div>
            <div className="space-y-2">
              <div className="text-sm font-medium text-muted-foreground">Account Number</div>
              <div className="text-lg font-mono text-sm">{account.accountID}</div>
            </div>
            <div className="space-y-2 md:col-span-2">
              <div className="text-sm font-medium text-muted-foreground">Current Balance</div>
              <div className="text-4xl font-bold text-green-600 dark:text-green-400">
                ${account.balance.toFixed(2)}
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Transaction History */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <History className="h-5 w-5" />
            Transaction History
          </CardTitle>
          <CardDescription>
            {account.transactions?.length || 0} transaction(s) found
          </CardDescription>
        </CardHeader>
        <CardContent>
          {account.transactions && account.transactions.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="min-w-full text-sm">
                <thead>
                  <tr className="border-b text-left text-muted-foreground">
                    <th className="py-2 pr-4">Date</th>
                    <th className="py-2 pr-4">Type</th>
                    <th className="py-2 pr-4">Direction</th>
                    <th className="py-2 pr-4">Amount</th>
                    <th className="py-2 pr-4">Status</th>
                    <th className="py-2 pr-4">Other Account</th>
                    <th className="py-2">Transaction ID</th>
                  </tr>
                </thead>
                <tbody>
                  {account.transactions.map((tx) => {
                    const direction = getTransactionDirection(tx);
                    const isIncoming = direction === "incoming";
                    const otherAccount = isIncoming ? tx.sourceAccountID : tx.destinationAccountID;
                    return (
                      <tr key={tx.transactionID} className="border-b last:border-0 hover:bg-accent">
                        <td className="py-2 pr-4 text-xs">
                          {new Date(tx.initiatedAt).toLocaleString()}
                        </td>
                        <td className="py-2 pr-4">{tx.transactionType}</td>
                        <td className="py-2 pr-4">
                          <span className="flex items-center gap-1">
                            {isIncoming ? (
                              <>
                                <TrendingUp className="h-3 w-3 text-green-600" />
                                <span className="text-green-600">Incoming</span>
                              </>
                            ) : (
                              <>
                                <TrendingDown className="h-3 w-3 text-red-600" />
                                <span className="text-red-600">Outgoing</span>
                              </>
                            )}
                          </span>
                        </td>
                        <td className="py-2 pr-4 font-semibold">
                          <span className={isIncoming ? "text-green-600" : "text-red-600"}>
                            {isIncoming ? "+" : "-"}
                            <DollarSign className="h-3 w-3 inline" />
                            {tx.amount.toFixed(2)}
                          </span>
                        </td>
                        <td className="py-2 pr-4">
                          <span
                            className={`px-2 py-1 rounded text-xs ${
                              tx.status === "Completed"
                                ? "bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200"
                                : tx.status === "Failed"
                                ? "bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200"
                                : "bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200"
                            }`}
                          >
                            {tx.status}
                          </span>
                        </td>
                        <td className="py-2 pr-4 font-mono text-xs">
                          {otherAccount || "N/A"}
                        </td>
                        <td className="py-2 font-mono text-xs">{tx.transactionID}</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="py-8 text-center text-muted-foreground">
              No transactions found for this account
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}

