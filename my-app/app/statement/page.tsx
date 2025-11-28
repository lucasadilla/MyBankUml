"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { api } from "@/lib/api";
import { toast } from "sonner";
import { Loader2 } from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";

export default function StatementPage() {
  const router = useRouter();
  const { user, accounts, isAuthenticated, isCustomer, loadAccounts } = useAuth();
  const [selectedAccounts, setSelectedAccounts] = useState<string[]>([]);
  const [year, setYear] = useState(new Date().getFullYear());
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [loading, setLoading] = useState(false);
  const [loadingAccounts, setLoadingAccounts] = useState(true);

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login");
      return;
    }

    if (user && user.userRole === "customer") {
      loadAccounts().then(() => setLoadingAccounts(false));
    } else {
      setLoadingAccounts(false);
    }
  }, [isAuthenticated, user, router, loadAccounts]);

  const handleGenerate = async () => {
    if (selectedAccounts.length === 0) {
      toast.error("Please select at least one account");
      return;
    }

    try {
      setLoading(true);
      if (!user || !isCustomer) {
        toast.error("Only customers can generate statements");
        router.push("/dashboard");
        return;
      }

      const response = await api.generateStatement({
        customerID: user.userID,
        accountIDs: selectedAccounts,
        year,
        month,
      });

      if (response.success && response.statement) {
        toast.success("Statement generated successfully!");
        // In a real app, you'd download or display the statement
        console.log("Statement:", response.statement);
      } else {
        toast.error(response.message || "Failed to generate statement");
      }
    } catch (error: any) {
      toast.error(error.message || "Failed to generate statement");
    } finally {
      setLoading(false);
    }
  };

  const toggleAccount = (accountID: string) => {
    setSelectedAccounts((prev) =>
      prev.includes(accountID)
        ? prev.filter((id) => id !== accountID)
        : [...prev, accountID]
    );
  };

  if (loadingAccounts) {
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
          <h1 className="text-3xl font-bold mb-2">Generate Statement</h1>
          <p className="text-muted-foreground">Generate monthly statement for your accounts</p>
        </div>
        <Button variant="outline" onClick={() => router.push("/dashboard")}>
          Back to Dashboard
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Statement Options</CardTitle>
          <CardDescription>Select accounts and period for your statement</CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="space-y-2">
            <Label>Select Accounts</Label>
            <div className="space-y-2">
              {accounts.map((account) => (
                <div
                  key={account.accountID}
                  className="flex items-center space-x-2 p-3 border rounded-lg cursor-pointer hover:bg-accent"
                  onClick={() => toggleAccount(account.accountID)}
                >
                  <input
                    type="checkbox"
                    checked={selectedAccounts.includes(account.accountID)}
                    onChange={() => toggleAccount(account.accountID)}
                    className="mr-2"
                  />
                  <div className="flex-1">
                    <div className="font-medium">{account.accountType}</div>
                    <div className="text-sm text-muted-foreground">
                      {account.accountID} - ${account.balance.toFixed(2)}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="year">Year</Label>
              <Select
                value={year.toString()}
                onValueChange={(value) => setYear(parseInt(value))}
              >
                <SelectTrigger id="year">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {Array.from({ length: 5 }, (_, i) => new Date().getFullYear() - i).map((y) => (
                    <SelectItem key={y} value={y.toString()}>
                      {y}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label htmlFor="month">Month</Label>
              <Select
                value={month.toString()}
                onValueChange={(value) => setMonth(parseInt(value))}
              >
                <SelectTrigger id="month">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {Array.from({ length: 12 }, (_, i) => i + 1).map((m) => (
                    <SelectItem key={m} value={m.toString()}>
                      {new Date(2000, m - 1).toLocaleString("default", { month: "long" })}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
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
              onClick={handleGenerate}
              disabled={loading || selectedAccounts.length === 0}
              className="flex-1"
            >
              {loading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Generating...
                </>
              ) : (
                "Generate Statement"
              )}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}



