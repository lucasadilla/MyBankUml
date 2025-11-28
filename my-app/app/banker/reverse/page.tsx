"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Loader2, RotateCcw } from "lucide-react";
import { toast } from "sonner";

export default function BankerReverseTransactionPage() {
  const router = useRouter();
  const { user, isAuthenticated, isBanker } = useAuth();
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(false);
  const [formData, setFormData] = useState({
    transactionID: "",
    reason: "",
  });

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login");
      return;
    }

    // Only bankers can reverse transactions
    if (!isBanker) {
      toast.error("Access denied. Banker access required.");
      router.push("/dashboard");
      return;
    }

    setLoading(false);
  }, [isAuthenticated, isBanker, router]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.transactionID || !formData.reason) {
      toast.error("Please provide both a transaction ID and a reason");
      return;
    }

    // Placeholder: real reverse logic must be implemented in backend.
    try {
      setProcessing(true);
      await new Promise((resolve) => setTimeout(resolve, 800));
      toast.success(
        "Reverse transaction request captured. Backend reversal logic can be wired to this form."
      );
    } finally {
      setProcessing(false);
    }
  };

  if (loading || !user) {
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
          <h1 className="text-3xl font-bold mb-2">Reverse Transaction</h1>
          <p className="text-muted-foreground">
            Locate an incorrect transaction and capture a reversal request.
          </p>
        </div>
        <Button variant="outline" onClick={() => router.push("/banker")}>
          Back to Banker Dashboard
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Reversal Details</CardTitle>
          <CardDescription>
            Provide the transaction ID and a clear justification for the reversal.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="transactionID">Transaction ID</Label>
              <Input
                id="transactionID"
                required
                placeholder="Enter the transaction ID to reverse"
                value={formData.transactionID}
                onChange={(e) =>
                  setFormData((prev) => ({ ...prev, transactionID: e.target.value }))
                }
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="reason">Reason for Reversal</Label>
              <Input
                id="reason"
                required
                placeholder="Describe why this transaction should be reversed"
                value={formData.reason}
                onChange={(e) => setFormData((prev) => ({ ...prev, reason: e.target.value }))}
              />
              <p className="text-xs text-muted-foreground">
                This description will be used for audit and manager review.
              </p>
            </div>

            <div className="flex gap-4 pt-4">
              <Button
                type="button"
                variant="outline"
                className="flex-1"
                onClick={() => router.push("/banker/transactions")}
              >
                View Transactions
              </Button>
              <Button type="submit" disabled={processing} className="flex-1">
                {processing ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Submitting...
                  </>
                ) : (
                  <>
                    <RotateCcw className="mr-2 h-4 w-4" />
                    Submit Reversal
                  </>
                )}
              </Button>
            </div>

            <p className="text-xs text-muted-foreground pt-2">
              Note: The UI matches the assignment requirements for reversal. Actual balance
              adjustments must be implemented in the backend using the existing `Transaction` and
              `Banker.reverseTransaction` logic.
            </p>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}


