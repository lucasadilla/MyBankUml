"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { api } from "@/lib/api";
import { toast } from "sonner";
import { Loader2 } from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";

export default function LoanRequestPage() {
  const router = useRouter();
  const { isAuthenticated, isCustomer } = useAuth();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    amount: "",
    purpose: "",
    proofOfIncome: "",
  });

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login");
      return;
    }

    if (!isCustomer) {
      toast.error("Only customers can submit loan requests");
      router.push("/dashboard");
    }
  }, [isAuthenticated, isCustomer, router]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const storedUser = localStorage.getItem("user");
    if (!storedUser) {
      toast.error("Please login first");
      router.push("/login");
      return;
    }

    try {
      const user = JSON.parse(storedUser);
      setLoading(true);

      const response = await api.requestLoan({
        customerID: user.userID,
        amount: parseFloat(formData.amount),
        purpose: formData.purpose,
        proofOfIncome: formData.proofOfIncome,
      });

      if (response.success) {
        toast.success("Loan request submitted successfully!");
        router.push("/dashboard");
      } else {
        toast.error(response.message || "Failed to submit loan request");
      }
    } catch (error: any) {
      toast.error(error.message || "Failed to submit loan request");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto py-8 px-4 max-w-2xl">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold mb-2">Request a Loan</h1>
          <p className="text-muted-foreground">Submit your loan application for review</p>
        </div>
        <Button variant="outline" onClick={() => router.push("/dashboard")}>
          Back to Dashboard
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Loan Application</CardTitle>
          <CardDescription>Please fill in all required information</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="amount">Loan Amount ($)</Label>
              <Input
                id="amount"
                type="number"
                step="0.01"
                min="0"
                required
                value={formData.amount}
                onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                placeholder="Enter loan amount"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="purpose">Purpose of Loan</Label>
              <Textarea
                id="purpose"
                required
                value={formData.purpose}
                onChange={(e) => setFormData({ ...formData, purpose: e.target.value })}
                placeholder="Describe the purpose of your loan"
                rows={4}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="proofOfIncome">Proof of Income</Label>
              <Input
                id="proofOfIncome"
                type="text"
                required
                value={formData.proofOfIncome}
                onChange={(e) => setFormData({ ...formData, proofOfIncome: e.target.value })}
                placeholder="Upload or describe proof of income"
              />
              <p className="text-sm text-muted-foreground">
                Please provide documentation or description of your income source
              </p>
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
              <Button type="submit" disabled={loading} className="flex-1">
                {loading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Submitting...
                  </>
                ) : (
                  "Submit Request"
                )}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}



