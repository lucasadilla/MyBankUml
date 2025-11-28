"use client";

import { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Receipt } from "@/lib/api";
import { Loader2, Download, CheckCircle } from "lucide-react";

export default function ReceiptPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [receipt, setReceipt] = useState<Receipt | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // In a real app, you'd fetch the receipt by ID from the URL
    // For now, we'll get it from localStorage if it was just created
    const storedReceipt = localStorage.getItem("lastReceipt");
    if (storedReceipt) {
      try {
        setReceipt(JSON.parse(storedReceipt));
      } catch (error) {
        console.error("Error parsing receipt:", error);
      }
    }
    setLoading(false);
  }, []);

  const handleDownload = () => {
    if (!receipt) return;
    
    // Create a simple text receipt
    const receiptText = `
MyBankUML - Transaction Receipt
================================

Reference Number: ${receipt.referenceNumber}
Date/Time: ${new Date(receipt.dateTimeIssued).toLocaleString()}
Amount: $${receipt.amount.toFixed(2)}

Transaction completed successfully.

Thank you for banking with us!
    `.trim();

    const blob = new Blob([receiptText], { type: "text/plain" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `receipt-${receipt.referenceNumber}.txt`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  if (!receipt) {
    return (
      <div className="container mx-auto py-8 px-4 max-w-2xl">
        <Card>
          <CardContent className="py-12 text-center">
            <p className="text-muted-foreground mb-4">No receipt found</p>
            <Button onClick={() => router.push("/dash")}>Back to Dashboard</Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8 px-4 max-w-2xl">
      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <CheckCircle className="h-6 w-6 text-green-500" />
            <CardTitle>Transaction Receipt</CardTitle>
          </div>
          <CardDescription>Your transaction has been completed successfully</CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="space-y-4">
            <div className="flex justify-between items-center py-2 border-b">
              <span className="text-muted-foreground">Reference Number</span>
              <span className="font-mono font-semibold">{receipt.referenceNumber}</span>
            </div>
            <div className="flex justify-between items-center py-2 border-b">
              <span className="text-muted-foreground">Date/Time</span>
              <span>{new Date(receipt.dateTimeIssued).toLocaleString()}</span>
            </div>
            <div className="flex justify-between items-center py-2 border-b">
              <span className="text-muted-foreground">Amount</span>
              <span className="text-2xl font-bold text-primary">
                ${receipt.amount.toFixed(2)}
              </span>
            </div>
          </div>

          <div className="bg-green-50 dark:bg-green-950 border border-green-200 dark:border-green-800 rounded-lg p-4">
            <p className="text-sm text-green-900 dark:text-green-100">
              ✓ Your transaction has been processed successfully. Please keep this receipt for your records.
            </p>
          </div>

          <div className="flex gap-4">
            <Button variant="outline" onClick={() => router.push("/dash")} className="flex-1">
              Back to Dashboard
            </Button>
            <Button onClick={handleDownload} className="flex-1">
              <Download className="mr-2 h-4 w-4" />
              Download Receipt
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}




