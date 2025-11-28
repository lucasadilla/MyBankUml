"use client";

import TransferForm from "@/components/ui/transfer-form";
import { useAuth } from "@/contexts/AuthContext";
import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";

export default function Page() {
  const { isAuthenticated, isCustomer } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login");
      return;
    }

    if (!isCustomer) {
      toast.error("Only customers can transfer funds");
      router.push("/dashboard");
    }
  }, [isAuthenticated, isCustomer, router]);

  if (!isCustomer) {
    return null;
  }

  return (
    <div className="container mx-auto py-8 px-4 max-w-2xl">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Transfer Funds</h1>
        <Button variant="outline" onClick={() => router.push("/dashboard")}>
          Back to Dashboard
        </Button>
      </div>
      <TransferForm />
    </div>
  );
}