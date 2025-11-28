"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { api } from "@/lib/api";
import { toast } from "sonner";
import { Loader2, ArrowLeft, User, Mail, Phone, CreditCard, History, DollarSign } from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";

interface CustomerDetails {
  userID: string;
  userName: string;
  userEmail: string;
  userPhone: string;
  userRole: string;
  accounts?: {
    accountID: string;
    accountType: string;
    balance: number;
  }[];
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

export default function CustomerDetailsPage() {
  const router = useRouter();
  const params = useParams();
  const customerID = params.customerID as string;
  const { isBanker, isBankManager, isAuthenticated } = useAuth();
  const [loading, setLoading] = useState(true);
  const [customer, setCustomer] = useState<CustomerDetails | null>(null);

  useEffect(() => {
    if (!isAuthenticated || (!isBanker && !isBankManager)) {
      toast.error("Banker or Bank Manager access required");
      router.push("/login");
      return;
    }

    const loadCustomer = async () => {
      try {
        setLoading(true);
        const response = await api.getCustomerDetails(customerID);
        if (response.success && response.customer) {
          setCustomer(response.customer);
        } else {
          toast.error(response.message || "Failed to load customer details");
          router.push("/banker/search");
        }
      } catch (error: any) {
        toast.error(error.message || "Failed to load customer details");
        router.push("/banker/search");
      } finally {
        setLoading(false);
      }
    };

    if (customerID) {
      loadCustomer();
    }
  }, [customerID, isAuthenticated, isBanker, isBankManager, router]);

  const getBackUrl = () => {
    if (isBankManager) {
      return "/dash";
    }
    return "/banker/search";
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  if (!customer) {
    return null;
  }

  return (
    <div className="container mx-auto py-8 px-4 max-w-6xl">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold mb-2">Customer Details</h1>
          <p className="text-muted-foreground">View customer information, accounts, and transaction history</p>
        </div>
        <Button variant="outline" onClick={() => router.push(getBackUrl())}>
          <ArrowLeft className="mr-2 h-4 w-4" />
          Back
        </Button>
      </div>

      {/* Customer Information */}
      <Card className="mb-6">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <User className="h-5 w-5" />
            Customer Information
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-2">
              <div className="text-sm font-medium text-muted-foreground">Name</div>
              <div className="text-lg font-semibold">{customer.userName}</div>
            </div>
            <div className="space-y-2">
              <div className="text-sm font-medium text-muted-foreground flex items-center gap-2">
                <Mail className="h-4 w-4" />
                Email
              </div>
              <div className="text-lg">{customer.userEmail}</div>
            </div>
            <div className="space-y-2">
              <div className="text-sm font-medium text-muted-foreground flex items-center gap-2">
                <Phone className="h-4 w-4" />
                Phone
              </div>
              <div className="text-lg">{customer.userPhone || "N/A"}</div>
            </div>
            <div className="space-y-2">
              <div className="text-sm font-medium text-muted-foreground">Customer ID</div>
              <div className="text-lg font-mono text-sm">{customer.userID}</div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Accounts */}
      <Card className="mb-6">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <CreditCard className="h-5 w-5" />
            Accounts
          </CardTitle>
          <CardDescription>
            {customer.accounts?.length || 0} account(s) found
          </CardDescription>
        </CardHeader>
        <CardContent>
          {customer.accounts && customer.accounts.length > 0 ? (
            <div className="space-y-4">
              {customer.accounts.map((account) => (
                <div
                  key={account.accountID}
                  className="flex items-center justify-between p-4 border rounded-lg"
                >
                  <div>
                    <div className="font-semibold">{account.accountType}</div>
                    <div className="text-sm text-muted-foreground font-mono">{account.accountID}</div>
                  </div>
                  <div className="text-2xl font-bold text-green-600 dark:text-green-400">
                    ${account.balance.toFixed(2)}
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="py-8 text-center text-muted-foreground">
              No accounts found for this customer
            </div>
          )}
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
            {customer.transactions?.length || 0} transaction(s) found
          </CardDescription>
        </CardHeader>
        <CardContent>
          {customer.transactions && customer.transactions.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="min-w-full text-sm">
                <thead>
                  <tr className="border-b text-left text-muted-foreground">
                    <th className="py-2 pr-4">Transaction ID</th>
                    <th className="py-2 pr-4">Type</th>
                    <th className="py-2 pr-4">Amount</th>
                    <th className="py-2 pr-4">Status</th>
                    <th className="py-2 pr-4">Source Account</th>
                    <th className="py-2 pr-4">Destination Account</th>
                    <th className="py-2">Date</th>
                  </tr>
                </thead>
                <tbody>
                  {customer.transactions.map((tx) => (
                    <tr key={tx.transactionID} className="border-b last:border-0">
                      <td className="py-2 pr-4 font-mono text-xs">{tx.transactionID}</td>
                      <td className="py-2 pr-4">{tx.transactionType}</td>
                      <td className="py-2 pr-4 font-semibold">
                        <span className="flex items-center gap-1">
                          <DollarSign className="h-3 w-3" />
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
                        {tx.sourceAccountID || "N/A"}
                      </td>
                      <td className="py-2 pr-4 font-mono text-xs">
                        {tx.destinationAccountID || "N/A"}
                      </td>
                      <td className="py-2 text-xs">
                        {new Date(tx.initiatedAt).toLocaleString()}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="py-8 text-center text-muted-foreground">
              No transactions found for this customer
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}

