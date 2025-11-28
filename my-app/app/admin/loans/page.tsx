"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { api, LoanRequest } from "@/lib/api";
import { toast } from "sonner";
import { Loader2, CheckCircle, XCircle, Clock } from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";

export default function LoanManagementPage() {
  const router = useRouter();
  const { user, isBankManager } = useAuth();
  const [loans, setLoans] = useState<LoanRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState<string | null>(null);

  useEffect(() => {
    if (!user) {
      router.push("/login");
      return;
    }

    if (!isBankManager) {
      toast.error("Access denied. Bank Manager access required.");
      router.push("/dashboard");
      return;
    }

    loadLoans();
  }, [user, isBankManager, router]);

  const loadLoans = async () => {
    try {
      setLoading(true);
      const response = await api.getPendingLoans();
      if (response.success && response.loans) {
        setLoans(response.loans);
      } else {
        toast.error(response.message || "Failed to load loan requests");
      }
    } catch (error: any) {
      toast.error(error.message || "Failed to load loan requests");
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (loanID: string) => {
    if (!user) return;

    try {
      setProcessing(loanID);
      const response = await api.approveLoan(loanID, user.userID);
      if (response.success) {
        toast.success("Loan approved successfully!");
        loadLoans();
      } else {
        toast.error(response.message || "Failed to approve loan");
      }
    } catch (error: any) {
      toast.error(error.message || "Failed to approve loan");
    } finally {
      setProcessing(null);
    }
  };

  const handleReject = async (loanID: string) => {
    if (!user) return;

    try {
      setProcessing(loanID);
      const response = await api.rejectLoan(loanID, user.userID);
      if (response.success) {
        toast.success("Loan rejected");
        loadLoans();
      } else {
        toast.error(response.message || "Failed to reject loan");
      }
    } catch (error: any) {
      toast.error(error.message || "Failed to reject loan");
    } finally {
      setProcessing(null);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8 px-4">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold mb-2">Loan Requests</h1>
          <p className="text-muted-foreground">Review and manage pending loan applications</p>
        </div>
        <Button variant="outline" onClick={() => router.push("/dash")}>
          Back to Dashboard
        </Button>
      </div>

      {loans.length === 0 ? (
        <Card>
          <CardContent className="py-12 text-center">
            <Clock className="h-12 w-12 mx-auto mb-4 text-muted-foreground" />
            <p className="text-muted-foreground">No pending loan requests</p>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-4">
          {loans.map((loan) => (
            <Card key={loan.loanID}>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <div>
                    <CardTitle className="flex items-center gap-2">
                      <Clock className="h-5 w-5 text-yellow-500" />
                      Loan Request #{loan.loanID.slice(0, 8)}
                    </CardTitle>
                    <CardDescription>
                      Submitted: {new Date(loan.dateSubmitted).toLocaleDateString()}
                    </CardDescription>
                  </div>
                  <div className="flex items-center gap-2">
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-medium ${
                        loan.status === "Pending"
                          ? "bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200"
                          : loan.status === "Approved"
                          ? "bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200"
                          : "bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200"
                      }`}
                    >
                      {loan.status}
                    </span>
                  </div>
                </div>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <p className="text-sm text-muted-foreground">Amount</p>
                      <p className="text-2xl font-bold">${loan.amount.toFixed(2)}</p>
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">Purpose</p>
                      <p className="font-medium">{loan.purpose}</p>
                    </div>
                  </div>

                  {loan.status === "Pending" && isBankManager && (
                    <div className="flex gap-4 pt-4 border-t">
                      <Button
                        onClick={() => handleApprove(loan.loanID)}
                        disabled={processing === loan.loanID}
                        className="flex-1"
                      >
                        {processing === loan.loanID ? (
                          <>
                            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                            Processing...
                          </>
                        ) : (
                          <>
                            <CheckCircle className="mr-2 h-4 w-4" />
                            Approve
                          </>
                        )}
                      </Button>
                      <Button
                        variant="destructive"
                        onClick={() => handleReject(loan.loanID)}
                        disabled={processing === loan.loanID}
                        className="flex-1"
                      >
                        {processing === loan.loanID ? (
                          <>
                            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                            Processing...
                          </>
                        ) : (
                          <>
                            <XCircle className="mr-2 h-4 w-4" />
                            Reject
                          </>
                        )}
                      </Button>
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}

