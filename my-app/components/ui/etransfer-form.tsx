"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { motion, AnimatePresence } from "framer-motion";
import { api, Account } from "@/lib/api";
import { ChevronLeft, ChevronRight, Check, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Checkbox } from "@/components/ui/checkbox";
import { toast } from "sonner";
import { cn } from "@/lib/utils";

const steps = [
  { id: "transfer", title: "Recipient Details" },
  { id: "confirmation", title: "Reciept" },

];

interface FormData {
  name: string;
  email: string;
  phone: string;
  amount: number;
  company: string;
  profession: string;
  experience: string;
  industry: string; // Source account ID
  primaryGoal: string;
  targetAudience: string;
  contentTypes: string[];
  colorPreference: string;
  stylePreference: string;
  inspirations: string;
  budget: string;
  timeline: string;
  features: string[];
  additionalInfo: string;
}

const fadeInUp = {
  hidden: { opacity: 0, y: 20 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.3 } },
};

const contentVariants = {
  hidden: { opacity: 0, x: 50 },
  visible: { opacity: 1, x: 0, transition: { duration: 0.3 } },
  exit: { opacity: 0, x: -50, transition: { duration: 0.2 } },
};

const ETransferForm = () => {
  const router = useRouter();
  const [currentStep, setCurrentStep] = useState(0);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [formData, setFormData] = useState<FormData>({
    name: "",
    email: "",
    phone: "",
    amount: 0,
    company: "",
    profession: "",
    experience: "",
    industry: "", // Source account ID
    primaryGoal: "",
    targetAudience: "",
    contentTypes: [],
    colorPreference: "",
    stylePreference: "",
    inspirations: "",
    budget: "",
    timeline: "",
    features: [],
    additionalInfo: "",
  });

  useEffect(() => {
    const loadAccounts = async () => {
      const storedUser = localStorage.getItem("user");
      if (storedUser) {
        try {
          const user = JSON.parse(storedUser);
          const response = await api.getAccounts(user.userID);
          if (response.success && response.accounts) {
            setAccounts(response.accounts);
          }
        } catch (error) {
          console.error("Error loading accounts:", error);
        }
      }
    };
    loadAccounts();
  }, []);

  const updateFormData = (field: keyof FormData, value: string | number) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const toggleFeature = (feature: string) => {
    setFormData((prev) => {
      const features = [...prev.features];
      if (features.includes(feature)) {
        return { ...prev, features: features.filter((f) => f !== feature) };
      } else {
        return { ...prev, features: [...features, feature] };
      }
    });
  };

  const toggleContentType = (type: string) => {
    setFormData((prev) => {
      const types = [...prev.contentTypes];
      if (types.includes(type)) {
        return { ...prev, contentTypes: types.filter((t) => t !== type) };
      } else {
        return { ...prev, contentTypes: [...types, type] };
      }
    });
  };

  const nextStep = () => {
    if (currentStep < steps.length - 1) {
      setCurrentStep((prev) => prev + 1);
    }
  };

  const prevStep = () => {
    if (currentStep > 0) {
      setCurrentStep((prev) => prev - 1);
    }
  };

  const handleSubmit = async () => {
    setIsSubmitting(true);

    try {
      const storedUser = localStorage.getItem("user");
      if (!storedUser) {
        toast.error("Please login first");
        router.push("/login");
        return;
      }

      // Validate required fields
      if (!formData.industry) {
        toast.error("Please select a source account");
        setIsSubmitting(false);
        return;
      }

      if (!formData.email || !formData.email.trim()) {
        toast.error("Please enter recipient email");
        setIsSubmitting(false);
        return;
      }

      if (!formData.name || !formData.name.trim()) {
        toast.error("Please enter recipient name");
        setIsSubmitting(false);
        return;
      }

      if (!formData.amount || formData.amount <= 0) {
        toast.error("Please enter a valid amount");
        setIsSubmitting(false);
        return;
      }

      const user = JSON.parse(storedUser);
      const response = await api.eTransfer({
        customerID: user.userID,
        sourceAccountID: formData.industry,
        recipientEmail: formData.email.trim(),
        recipientName: formData.name.trim(),
        recipientPhone: formData.phone || "",
        amount: formData.amount,
        notificationMethod: "Email",
      });

      if (response.success && response.receipt) {
        localStorage.setItem("lastReceipt", JSON.stringify(response.receipt));
        toast.success("E-Transfer completed successfully!");
        router.push("/receipt");
      } else {
        toast.error(response.message || "E-Transfer failed");
        setIsSubmitting(false);
      }
    } catch (error: any) {
      toast.error(error.message || "E-Transfer failed");
      setIsSubmitting(false);
    }
  };

  // Check if step is valid for next button
  const isStepValid = () => {
    switch (currentStep) {
      case 0:
        // First step: need source account, recipient email, name, and amount
        return formData.industry !== "" && 
               formData.email.trim() !== "" && 
               formData.name.trim() !== "" && 
               formData.amount > 0;
      case 1:
        // Confirmation step is always valid
        return true;
      default:
        return true;
    }
  };

  const preventDefault = (e: React.MouseEvent) => {
    e.preventDefault();
  };

  return (
    <div className="w-full max-w-lg mx-auto py-8">
      {/* Progress indicator */}
      <motion.div
        className="mb-8"
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <div className="flex justify-between mb-2">
          {steps.map((step, index) => (
            <motion.div
              key={index}
              className="flex flex-col items-center"
              whileHover={{ scale: 1.1 }}
            >
              <motion.div
                className={cn(
                  "w-4 h-4 rounded-full cursor-pointer transition-colors duration-300",
                  index < currentStep
                    ? "bg-primary"
                    : index === currentStep
                      ? "bg-primary ring-4 ring-primary/20"
                      : "bg-muted",
                )}
                onClick={() => {
                  // Only allow going back or to completed steps
                  if (index <= currentStep) {
                    setCurrentStep(index);
                  }
                }}
                whileTap={{ scale: 0.95 }}
              />
              <motion.span
                className={cn(
                  "text-xs mt-1.5 hidden sm:block",
                  index === currentStep
                    ? "text-primary font-medium"
                    : "text-muted-foreground",
                )}
              >
                {step.title}
              </motion.span>
            </motion.div>
          ))}
        </div>
        <div className="w-full bg-muted h-1.5 rounded-full overflow-hidden mt-2">
          <motion.div
            className="h-full bg-primary"
            initial={{ width: 0 }}
            animate={{ width: `${(currentStep / (steps.length - 1)) * 100}%` }}
            transition={{ duration: 0.3 }}
          />
        </div>
      </motion.div>

      {/* Form card */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.2 }}
      >
        <Card className="border shadow-md rounded-3xl overflow-hidden">
          <div>
            <AnimatePresence mode="wait">
              <motion.div
                key={currentStep}
                initial="hidden"
                animate="visible"
                exit="exit"
                variants={contentVariants}
              >
                {/* Step 1: Personal Info */}
                {currentStep === 0 && (
                  <>
                    <CardHeader>
                      <CardTitle>E-Transfer Details</CardTitle>
                      
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="industry">
                          What account would you like to use?
                        </Label>
                        <Select
                          value={formData.industry}
                          onValueChange={(value) =>
                            updateFormData("industry", value)
                          }
                        >
                          <SelectTrigger
                            id="industry"
                            className="transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                          >
                            <SelectValue placeholder="Select source account" />
                          </SelectTrigger>
                          <SelectContent>
                            {accounts.map((account) => (
                              <SelectItem key={account.accountID} value={account.accountID}>
                                {account.accountType} - ${account.balance.toFixed(2)}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="name">Recipient Name</Label>
                        <Input
                          id="name"
                          type="text"
                          placeholder="Recipient name"
                          value={formData.name}
                          onChange={(e) =>
                            updateFormData("name", e.target.value)
                          }
                          className="transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                        />
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="email">Recipient Email Address *</Label>
                        <Input
                          id="email"
                          type="email"
                          required
                          placeholder="example@example.com"
                          value={formData.email}
                          onChange={(e) =>
                            updateFormData("email", e.target.value)
                          }
                          className="transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                        />
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="phone">Recipient Phone Number (Optional)</Label>
                        <Input
                          id="phone"
                          type="tel"
                          placeholder="(555) 123-4567"
                          value={formData.phone}
                          onChange={(e) =>
                            updateFormData("phone", e.target.value)
                          }
                          className="transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                        />
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="amount">
                          Amount
                        </Label>
                        <Input
                          id="amount"
                          type="number"
                          placeholder="total amount to send"
                          value={formData.amount || ""}
                          onChange={(e) =>
                            updateFormData("amount", parseFloat(e.target.value) || 0)
                          }
                          className="transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                        />
                      </motion.div>
                    </CardContent>
                  </>
                )}

                {/* Step 2: Confirmation/Receipt */}
                {currentStep === 1 && (
                  <>
                    <CardHeader>
                      <CardTitle>Transfer Confirmation</CardTitle>
                      <CardDescription>
                        Please review your transfer details
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-6">
                      <div className="space-y-4 border rounded-lg p-4 bg-muted/50">
                        <motion.div variants={fadeInUp} className="flex justify-between items-center">
                          <span className="text-sm font-medium text-muted-foreground">From Account:</span>
                          <span className="font-semibold">
                            {accounts.find(acc => acc.accountID === formData.industry)?.accountType || formData.industry || "Not selected"}
                          </span>
                        </motion.div>
                        
                        <motion.div variants={fadeInUp} className="flex justify-between items-center">
                          <span className="text-sm font-medium text-muted-foreground">Recipient Name:</span>
                          <span className="font-semibold">{formData.name || "Not provided"}</span>
                        </motion.div>
                        
                        <motion.div variants={fadeInUp} className="flex justify-between items-center">
                          <span className="text-sm font-medium text-muted-foreground">Recipient Email:</span>
                          <span className="font-semibold">{formData.email || "Not provided"}</span>
                        </motion.div>
                        
                        {formData.phone && (
                          <motion.div variants={fadeInUp} className="flex justify-between items-center">
                            <span className="text-sm font-medium text-muted-foreground">Recipient Phone:</span>
                            <span className="font-semibold">{formData.phone}</span>
                          </motion.div>
                        )}
                        
                        <motion.div variants={fadeInUp} className="flex justify-between items-center border-t pt-4">
                          <span className="text-sm font-medium text-muted-foreground">Amount:</span>
                          <span className="text-xl font-bold text-primary">${formData.amount || 0}</span>
                        </motion.div>

                        <motion.div variants={fadeInUp} className="flex justify-between items-center border-t pt-4">
                          <span className="text-sm font-medium text-muted-foreground">Remaining Balance:</span>
                          <span className="text-lg font-bold text-green-600 dark:text-green-400">
                            ${((accounts.find(acc => acc.accountID === formData.industry)?.balance || 0) - (formData.amount || 0)).toFixed(2)}
                          </span>
                        </motion.div>
                      </div>

                      <motion.div variants={fadeInUp} className="bg-blue-50 dark:bg-blue-950 border border-blue-200 dark:border-blue-800 rounded-lg p-4">
                        <p className="text-sm text-blue-900 dark:text-blue-100">
                          ✓ Please review the details above carefully. Once you proceed, this transfer will be processed.
                        </p>
                      </motion.div>
                    </CardContent>
                  </>
                )}

                {/* Step 3: Website Goals */}
                {currentStep === 2 && (
                  <>
                    <CardHeader>
                      <CardTitle>Website Goals</CardTitle>
                      <CardDescription>
                        What are you trying to achieve with your website?
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label>
                          What&apos;s the primary goal of your website?
                        </Label>
                        <RadioGroup
                          value={formData.primaryGoal}
                          onValueChange={(value) =>
                            updateFormData("primaryGoal", value)
                          }
                          className="space-y-2"
                        >
                          {[
                            {
                              value: "showcase",
                              label: "Showcase portfolio/work",
                            },
                            { value: "sell", label: "Sell products/services" },
                            {
                              value: "generate-leads",
                              label: "Generate leads/inquiries",
                            },
                            {
                              value: "provide-info",
                              label: "Provide information",
                            },
                            { value: "blog", label: "Blog/content publishing" },
                          ].map((goal, index) => (
                            <motion.div
                              key={goal.value}
                              className="flex items-center space-x-2 rounded-md border p-3 cursor-pointer hover:bg-accent transition-colors"
                              whileHover={{ scale: 1.02 }}
                              whileTap={{ scale: 0.98 }}
                              transition={{ duration: 0.2 }}
                              initial={{ opacity: 0, x: -10 }}
                              animate={{
                                opacity: 1,
                                x: 0,
                                transition: {
                                  delay: 0.1 * index,
                                  duration: 0.3,
                                },
                              }}
                            >
                              <RadioGroupItem
                                value={goal.value}
                                id={`goal-${index + 1}`}
                              />
                              <Label
                                htmlFor={`goal-${index + 1}`}
                                className="cursor-pointer w-full"
                              >
                                {goal.label}
                              </Label>
                            </motion.div>
                          ))}
                        </RadioGroup>
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="targetAudience">
                          Who is your target audience?
                        </Label>
                        <Textarea
                          id="targetAudience"
                          placeholder="Describe your ideal visitors/customers"
                          value={formData.targetAudience}
                          onChange={(e) =>
                            updateFormData("targetAudience", e.target.value)
                          }
                          className="min-h-20 transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                        />
                      </motion.div>
                    </CardContent>
                  </>
                )}

                {/* Step 4: Design Preferences */}
                {currentStep === 3 && (
                  <>
                    <CardHeader>
                      <CardTitle>Design Preferences</CardTitle>
                      <CardDescription>
                        Tell us about your aesthetic preferences
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label>
                          What style do you prefer for your website?
                        </Label>
                        <RadioGroup
                          value={formData.stylePreference}
                          onValueChange={(value) =>
                            updateFormData("stylePreference", value)
                          }
                          className="space-y-2"
                        >
                          {[
                            { value: "modern", label: "Modern & Sleek" },
                            { value: "minimalist", label: "Minimalist" },
                            { value: "bold", label: "Bold & Creative" },
                            {
                              value: "corporate",
                              label: "Corporate & Professional",
                            },
                          ].map((style, index) => (
                            <motion.div
                              key={style.value}
                              className="flex items-center space-x-2 rounded-md border p-3 cursor-pointer hover:bg-accent transition-colors"
                              whileHover={{ scale: 1.02 }}
                              whileTap={{ scale: 0.98 }}
                              transition={{ duration: 0.2 }}
                              initial={{ opacity: 0, y: 10 }}
                              animate={{
                                opacity: 1,
                                y: 0,
                                transition: {
                                  delay: 0.1 * index,
                                  duration: 0.3,
                                },
                              }}
                            >
                              <RadioGroupItem
                                value={style.value}
                                id={`style-${index + 1}`}
                              />
                              <Label
                                htmlFor={`style-${index + 1}`}
                                className="cursor-pointer w-full"
                              >
                                {style.label}
                              </Label>
                            </motion.div>
                          ))}
                        </RadioGroup>
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="inspirations">
                          Any websites you like for inspiration?
                        </Label>
                        <Textarea
                          id="inspirations"
                          placeholder="List websites you admire or want to emulate"
                          value={formData.inspirations}
                          onChange={(e) =>
                            updateFormData("inspirations", e.target.value)
                          }
                          className="min-h-20 transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                        />
                      </motion.div>
                    </CardContent>
                  </>
                )}

                {/* Step 5: Budget & Timeline */}
                {currentStep === 4 && (
                  <>
                    <CardHeader>
                      <CardTitle>Budget & Timeline</CardTitle>
                      <CardDescription>
                        Let&apos;s talk about your investment and timeline
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="budget">
                          What&apos;s your budget range? (USD)
                        </Label>
                        <Select
                          value={formData.budget}
                          onValueChange={(value) =>
                            updateFormData("budget", value)
                          }
                        >
                          <SelectTrigger
                            id="budget"
                            className="transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                          >
                            <SelectValue placeholder="Select your budget" />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="under-1000">
                              Under $1,000
                            </SelectItem>
                            <SelectItem value="1000-3000">
                              $1,000 - $3,000
                            </SelectItem>
                            <SelectItem value="3000-5000">
                              $3,000 - $5,000
                            </SelectItem>
                            <SelectItem value="5000-10000">
                              $5,000 - $10,000
                            </SelectItem>
                            <SelectItem value="over-10000">
                              Over $10,000
                            </SelectItem>
                          </SelectContent>
                        </Select>
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label>What&apos;s your expected timeline?</Label>
                        <RadioGroup
                          value={formData.timeline}
                          onValueChange={(value) =>
                            updateFormData("timeline", value)
                          }
                          className="space-y-2"
                        >
                          {[
                            { value: "asap", label: "ASAP" },
                            { value: "1-month", label: "Within 1 month" },
                            { value: "3-months", label: "1-3 months" },
                            { value: "flexible", label: "Flexible" },
                          ].map((time, index) => (
                            <motion.div
                              key={time.value}
                              className="flex items-center space-x-2 rounded-md border p-3 cursor-pointer hover:bg-accent transition-colors"
                              whileHover={{ scale: 1.02 }}
                              whileTap={{ scale: 0.98 }}
                              transition={{ duration: 0.2 }}
                              initial={{ opacity: 0, x: -10 }}
                              animate={{
                                opacity: 1,
                                x: 0,
                                transition: {
                                  delay: 0.1 * index,
                                  duration: 0.3,
                                },
                              }}
                            >
                              <RadioGroupItem
                                value={time.value}
                                id={`time-${index + 1}`}
                              />
                              <Label
                                htmlFor={`time-${index + 1}`}
                                className="cursor-pointer w-full"
                              >
                                {time.label}
                              </Label>
                            </motion.div>
                          ))}
                        </RadioGroup>
                      </motion.div>
                    </CardContent>
                  </>
                )}

                {/* Step 6: Additional Requirements */}
                {currentStep === 5 && (
                  <>
                    <CardHeader>
                      <CardTitle>Additional Requirements</CardTitle>
                      <CardDescription>
                        Any other specific needs for your website?
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label>Which features do you need?</Label>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                          {[
                            "Contact Form",
                            "Blog/News",
                            "E-commerce",
                            "User Accounts",
                            "Search Functionality",
                            "Social Media Integration",
                            "Newsletter Signup",
                            "Analytics",
                          ].map((feature, index) => (
                            <motion.div
                              key={feature}
                              className="flex items-center space-x-2 rounded-md border p-3 cursor-pointer hover:bg-accent transition-colors"
                              whileHover={{ scale: 1.02 }}
                              whileTap={{ scale: 0.98 }}
                              transition={{ duration: 0.2 }}
                              initial={{ opacity: 0, y: 10 }}
                              animate={{
                                opacity: 1,
                                y: 0,
                                transition: {
                                  delay: 0.05 * index,
                                  duration: 0.3,
                                },
                              }}
                              onClick={() =>
                                toggleFeature(feature.toLowerCase())
                              }
                            >
                              <Checkbox
                                id={`feature-${feature}`}
                                checked={formData.features.includes(
                                  feature.toLowerCase(),
                                )}
                                onCheckedChange={() =>
                                  toggleFeature(feature.toLowerCase())
                                }
                              />
                              <Label
                                htmlFor={`feature-${feature}`}
                                className="cursor-pointer w-full"
                              >
                                {feature}
                              </Label>
                            </motion.div>
                          ))}
                        </div>
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="additionalInfo">
                          Anything else we should know?
                        </Label>
                        <Textarea
                          id="additionalInfo"
                          placeholder="Any additional requirements or information"
                          value={formData.additionalInfo}
                          onChange={(e) =>
                            updateFormData("additionalInfo", e.target.value)
                          }
                          className="min-h-20 transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                        />
                      </motion.div>
                    </CardContent>
                  </>
                )}
              </motion.div>
            </AnimatePresence>

            <CardFooter className="flex justify-between pt-6 pb-4">
              <motion.div
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                <Button
                  type="button"
                  variant="outline"
                  onClick={prevStep}
                  disabled={currentStep === 0}
                  className="flex items-center gap-1 transition-all duration-300 rounded-2xl"
                >
                  <ChevronLeft className="h-4 w-4" /> Back
                </Button>
              </motion.div>
              <motion.div
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                <Button
                  type="button"
                  onClick={
                    currentStep === steps.length - 1 ? handleSubmit : nextStep
                  }
                  disabled={!isStepValid() || isSubmitting}
                  className={cn(
                    "flex items-center gap-1 transition-all duration-300 rounded-2xl",
                    currentStep === steps.length - 1 ? "" : "",
                  )}
                >
                  {isSubmitting ? (
                    <>
                      <Loader2 className="h-4 w-4 animate-spin" /> Submitting...
                    </>
                  ) : (
                    <>
                      {currentStep === steps.length - 1 ? "Submit" : "Next"}
                      {currentStep === steps.length - 1 ? (
                        <Check className="h-4 w-4" />
                      ) : (
                        <ChevronRight className="h-4 w-4" />
                      )}
                    </>
                  )}
                </Button>
              </motion.div>
            </CardFooter>
          </div>
        </Card>
      </motion.div>

      {/* Step indicator */}
      <motion.div
        className="mt-4 text-center text-sm text-muted-foreground"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5, delay: 0.4 }}
      >
        Step {currentStep + 1} of {steps.length}: {steps[currentStep].title}
      </motion.div>
    </div>
  );
};

export default ETransferForm;
