"use client";
import * as React from "react";
import MultiStepForm from "@/components/multi-step-form";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";


export default function GenerateStatementPage() {


  const [currentStep, setCurrentStep] = React.useState(1);
  const totalSteps = 2;

  const [fromDate, setFromDate] = React.useState("");
  const [toDate, setToDate] = React.useState("");
  const [error, setError] = React.useState<string | null>(null);
  const [statementPreview, setStatementPreview] = React.useState<string | null>(
    null
  );

  const handleBack = () => {
    if (currentStep > 1) {
      setCurrentStep((prev) => prev - 1);
      setError(null);
    }
  };

  const validateDates = () => {
    if (!fromDate || !toDate) {
      return "Please select both start and end dates.";
    }

    const from = new Date(fromDate);
    const to = new Date(toDate);

    if (Number.isNaN(from.getTime()) || Number.isNaN(to.getTime())) {
      return "One or both dates are invalid.";
    }

    if (from > to) {
      return "Start date cannot be after end date.";
    }

    return null;
  };

  const generateStatement = () => {
    // Placeholder – here you’d call your API / fetch data.
    setStatementPreview(
      `Statement for transactions from ${fromDate} to ${toDate}. (Replace this with real data)`
    );
  };

  const handleNext = () => {
    setError(null);

    if (currentStep === 1) {
      const validationError = validateDates();
      if (validationError) {
        setError(validationError);
        return;
      }

      // Dates are valid → move to confirmation/generation step
      setCurrentStep(2);
      generateStatement();
      return;
    }

    if (currentStep === 2) {
      // On the last step, you might:
      // - trigger a file download
      // - navigate somewhere
      // - call an API to actually generate a PDF, etc.
      console.log("Generate statement confirmed");
    }
  };

  const nextButtonText =
    currentStep === totalSteps ? "Generate Statement" : "Next";

  const footerContent = error ? (
    <p className="text-sm text-red-500">{error}</p>
  ) : null;

  return (
    <div className="flex min-h-screen items-center justify-center p-4">
      <MultiStepForm
        currentStep={currentStep}
        totalSteps={totalSteps}
        title="Generate Account Statement"
        description="Choose a date range to generate your statement."
        onBack={handleBack}
        onNext={handleNext}
        nextButtonText={nextButtonText}
        footerContent={footerContent}
      >
        {currentStep === 1 && (
          <div className="space-y-6">
            <div className="grid gap-4 md:grid-cols-2">
              <div className="flex flex-col gap-2">
                <Label htmlFor="fromDate">From date</Label>
                <Input
                  id="fromDate"
                  type="date"
                  value={fromDate}
                  onChange={(e) => setFromDate(e.target.value)}
                />
              </div>
              <div className="flex flex-col gap-2">
                <Label htmlFor="toDate">To date</Label>
                <Input
                  id="toDate"
                  type="date"
                  value={toDate}
                  onChange={(e) => setToDate(e.target.value)}
                />
              </div>
            </div>

            <p className="text-sm text-muted-foreground">
              Statements will include all transactions between the selected
              dates (inclusive).
            </p>
          </div>
        )}

        {currentStep === 2 && (
          <div className="space-y-4">
            <div>
              <h3 className="text-lg font-semibold">Confirm details</h3>
              <p className="text-sm text-muted-foreground">
                You&apos;re about to generate a statement for:
              </p>
              <div className="mt-3 rounded-md border p-3 text-sm">
                <p>
                  <span className="font-medium">From:</span> {fromDate}
                </p>
                <p>
                  <span className="font-medium">To:</span> {toDate}
                </p>
              </div>
            </div>

            <div>
              <h4 className="text-sm font-semibold">Preview</h4>
              <p className="mt-2 text-sm text-muted-foreground">
                {statementPreview ??
                  "Your statement preview will appear here once generated."}
              </p>
            </div>

            <p className="text-xs text-muted-foreground">
              On the next action, the final statement will be generated. You can
              go back to adjust the dates if needed.
            </p>
          </div>
        )}
      </MultiStepForm>
    </div>
  );
}
