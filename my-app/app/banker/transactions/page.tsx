"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Loader2, Search, History } from "lucide-react";
import { toast } from "sonner";
import { api } from "@/lib/api";

interface SimpleTransaction {
  transactionID: string;
  customerID: string;
  amount: number;
  type: string;
  status: string;
  sourceAccountID: string | null;
  destinationAccountID: string | null;
  initiatedAt: string;
}

export default function BankerTransactionsPage() {
  const router = useRouter();
  const { user, isAuthenticated, isBanker } = useAuth();
  const [loading, setLoading] = useState(true);
  const [searchCustomer, setSearchCustomer] = useState("");
  const [transactions, setTransactions] = useState<SimpleTransaction[]>([]);

  const loadTransactions = async (customerIDFilter?: string) => {
    try {
      setLoading(true);
      console.log("📋 Loading transactions, filter:", customerIDFilter || "all");
      const response = await api.getTransactions(customerIDFilter);
      console.log("📋 Response:", response);
      if (response.success && response.transactions) {
        // Map transactionType to type for compatibility
        const mappedTransactions = response.transactions.map(tx => ({
          ...tx,
          type: tx.transactionType,
        }));
        setTransactions(mappedTransactions);
        console.log("📋 Mapped transactions:", mappedTransactions.length);
        if (mappedTransactions.length === 0) {
          toast.info("No transactions found in the database");
        } else {
          toast.success(`Loaded ${mappedTransactions.length} transaction(s)`);
        }
      } else {
        console.error("📋 Failed response:", response);
        toast.error(response.message || "Failed to load transactions");
      }
    } catch (error: any) {
      console.error("📋 Error loading transactions:", error);
      toast.error(error.message || "Failed to load transactions");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login");
      return;
    }

    // Only bankers can review transactions
    if (!isBanker) {
      toast.error("Access denied. Banker access required.");
      router.push("/dashboard");
      return;
    }

    // Load all transactions on page load
    loadTransactions();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated, isBanker, router]);

  const handleSearch = async () => {
    await loadTransactions(searchCustomer.trim() || undefined);
  };

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
          <h1 className="text-3xl font-bold mb-2">Review Transactions</h1>
          <p className="text-muted-foreground">
            Search and review recent transactions for investigation or follow-up.
          </p>
        </div>
        <Button variant="outline" onClick={() => router.push("/banker")}>
          Back to Banker Dashboard
        </Button>
      </div>

      <Card className="mb-6">
        <CardHeader>
          <CardTitle>Search Criteria</CardTitle>
          <CardDescription>Search by customer ID to focus on a specific customer.</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-4 md:grid-cols-[2fr,1fr] items-end">
            <div className="space-y-2">
              <Label htmlFor="customerID">Customer ID (optional)</Label>
              <Input
                id="customerID"
                placeholder="Enter customer ID to filter (leave blank for all)"
                value={searchCustomer}
                onChange={(e) => setSearchCustomer(e.target.value)}
              />
            </div>
            <Button className="flex-1" onClick={handleSearch}>
              <Search className="mr-2 h-4 w-4" />
              Search Transactions
            </Button>
          </div>
          <p className="text-xs text-muted-foreground">
            Leave customer ID blank to view all transactions, or enter a specific customer ID to filter.
          </p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <History className="h-5 w-5" />
            Recent Transactions
          </CardTitle>
          <CardDescription>
            Showing {transactions.length} transaction(s) matching your search criteria.
          </CardDescription>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="py-10 text-center">
              <Loader2 className="h-6 w-6 animate-spin mx-auto mb-2" />
              <p className="text-muted-foreground text-sm">Loading transactions...</p>
            </div>
          ) : transactions.length === 0 ? (
            <div className="py-10 text-center text-muted-foreground text-sm">
              No transactions found. Try adjusting your search criteria.
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full text-sm">
                <thead>
                  <tr className="border-b text-left text-muted-foreground">
                    <th className="py-2 pr-4">ID</th>
                    <th className="py-2 pr-4">Customer</th>
                    <th className="py-2 pr-4">Type</th>
                    <th className="py-2 pr-4">Amount</th>
                    <th className="py-2 pr-4">Status</th>
                    <th className="py-2 pr-4">Source → Destination</th>
                    <th className="py-2">Date</th>
                  </tr>
                </thead>
                <tbody>
                  {transactions.map((tx) => (
                    <tr key={tx.transactionID} className="border-b last:border-0">
                      <td className="py-2 pr-4 font-mono text-xs">{tx.transactionID}</td>
                      <td className="py-2 pr-4">{tx.customerID}</td>
                      <td className="py-2 pr-4">{tx.type}</td>
                      <td className="py-2 pr-4">${tx.amount.toFixed(2)}</td>
                      <td className="py-2 pr-4">{tx.status}</td>
                      <td className="py-2 pr-4 text-xs">
                        {tx.sourceAccountID || "N/A"} → {tx.destinationAccountID || "N/A"}
                      </td>
                      <td className="py-2 text-xs">
                        {new Date(tx.initiatedAt).toLocaleString()}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}


