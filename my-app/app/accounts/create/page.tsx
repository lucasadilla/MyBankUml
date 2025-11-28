"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useAuth } from "@/contexts/AuthContext";
import { api } from "@/lib/api";
import { toast } from "sonner";
import { Loader2, ArrowLeft } from "lucide-react";
import Link from "next/link";

export default function CreateAccountPage() {
  const router = useRouter();
  const { user, loadAccounts, isCustomer } = useAuth();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    accountID: "",
    accountType: "",
    initialBalance: "0",
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!user) {
      toast.error("Please login first");
      router.push("/login");
      return;
    }

    if (!isCustomer) {
      toast.error("Only customers can create accounts");
      router.push("/dashboard");
      return;
    }

    if (!formData.accountID || !formData.accountType) {
      toast.error("Please fill in all required fields");
      return;
    }

    try {
      setLoading(true);
      const response = await api.createAccount({
        customerID: user.userID,
        accountID: formData.accountID,
        accountType: formData.accountType,
        initialBalance: parseFloat(formData.initialBalance) || 0,
      });

      if (response.success) {
        toast.success("Account created successfully!");
        await loadAccounts(); // Refresh accounts list
        router.push("/accounts");
      } else {
        toast.error(response.message || "Failed to create account");
      }
    } catch (error: any) {
      toast.error(error.message || "Failed to create account");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto py-8 px-4 max-w-2xl">
      <div className="mb-6">
        <Button variant="ghost" asChild className="mb-4">
          <Link href="/accounts">
            <ArrowLeft className="mr-2 h-4 w-4" />
            Back to Accounts
          </Link>
        </Button>
        <h1 className="text-3xl font-bold mb-2">Create New Account</h1>
        <p className="text-muted-foreground">Open a new checking or savings account</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Account Information</CardTitle>
          <CardDescription>Enter the details for your new account</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="accountID">Account ID *</Label>
              <Input
                id="accountID"
                required
                value={formData.accountID}
                onChange={(e) => setFormData({ ...formData, accountID: e.target.value })}
                placeholder="e.g., CHK001, SAV001"
                disabled={loading}
              />
              <p className="text-xs text-muted-foreground">
                Choose a unique identifier for your account
              </p>
            </div>

            <div className="space-y-2">
              <Label htmlFor="accountType">Account Type *</Label>
              <Select
                value={formData.accountType}
                onValueChange={(value) => setFormData({ ...formData, accountType: value })}
                disabled={loading}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select account type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Checking">Checking Account</SelectItem>
                  <SelectItem value="Saving">Savings Account</SelectItem>
                </SelectContent>
              </Select>
              <p className="text-xs text-muted-foreground">
                {formData.accountType === "Checking" && "For everyday transactions"}
                {formData.accountType === "Saving" && "Earn interest on your savings"}
              </p>
            </div>

            <div className="space-y-2">
              <Label htmlFor="initialBalance">Initial Balance</Label>
              <Input
                id="initialBalance"
                type="number"
                step="0.01"
                min="0"
                value={formData.initialBalance}
                onChange={(e) => setFormData({ ...formData, initialBalance: e.target.value })}
                placeholder="0.00"
                disabled={loading}
              />
              <p className="text-xs text-muted-foreground">
                Starting balance for your account (optional)
              </p>
            </div>

            <div className="flex gap-4 pt-4">
              <Button
                type="button"
                variant="outline"
                onClick={() => router.push("/accounts")}
                disabled={loading}
                className="flex-1"
              >
                Cancel
              </Button>
              <Button type="submit" disabled={loading} className="flex-1">
                {loading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Creating...
                  </>
                ) : (
                  "Create Account"
                )}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}

