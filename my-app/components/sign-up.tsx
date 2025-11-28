"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { LogoIcon } from '@/components/logo'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { api } from '@/lib/api'
import { toast } from 'sonner'
import { Loader2 } from 'lucide-react'
import Link from 'next/link'

export default function SignupPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState({
        userID: "",
        userName: "",
        userEmail: "",
        userPhone: "",
        password: "",
    userRole: "customer",
    });

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        try {
            const response = await api.register(formData);
            if (response.success && response.user) {
                toast.success("Registration successful!");
                const role = response.user.userRole;

                // Redirect based on role so you immediately see the correct UI
                if (role === "admin" || role === "bank_manager") {
                    router.push("/dash");
                } else if (role === "banker") {
                    router.push("/banker");
                } else {
                    router.push("/dashboard");
                }
            } else {
                toast.error(response.message || "Registration failed");
            }
        } catch (error: any) {
            toast.error(error.message || "Registration failed");
        } finally {
            setLoading(false);
        }
    };
    return (
        <section className="flex min-h-screen bg-zinc-50 px-4 py-16 md:py-32 dark:bg-transparent">
            <div
                className="bg-card m-auto h-fit w-full max-w-sm rounded-[calc(var(--radius)+.125rem)] border p-0.5 shadow-md dark:[--color-muted:var(--color-zinc-900)]">
                <div className="p-8 pb-6">
                    <div>
                        <Link
                            href="/"
                            aria-label="go home">
                            <LogoIcon />
                        </Link>
                        <h1 className="mb-1 mt-4 text-xl font-semibold">Create a MyBankUML Account</h1>
                        <p className="text-sm">Welcome! Create an account to get started</p>
                    </div>

                    <hr className="my-4 border-dashed" />

                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div className="space-y-2">
                            <Label htmlFor="userID" className="block text-sm">
                                User ID
                            </Label>
                            <Input
                                type="text"
                                required
                                id="userID"
                                value={formData.userID}
                                onChange={(e) => setFormData({ ...formData, userID: e.target.value })}
                                disabled={loading}
                                placeholder="Enter unique user ID"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="userRole" className="block text-sm">
                                Account Type
                            </Label>
                            <Select
                                value={formData.userRole}
                                onValueChange={(value) => setFormData({ ...formData, userRole: value })}
                                disabled={loading}
                            >
                                <SelectTrigger id="userRole" className="w-full">
                                    <SelectValue placeholder="Select account type" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="customer">Customer</SelectItem>
                                    <SelectItem value="banker">Banker</SelectItem>
                                    <SelectItem value="bank_manager">Bank Manager</SelectItem>
                                    <SelectItem value="admin">Admin</SelectItem>
                                </SelectContent>
                            </Select>
                            <p className="text-xs text-muted-foreground">
                                Choose what kind of user account to create.
                            </p>
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="userName" className="block text-sm">
                                Full Name
                            </Label>
                            <Input
                                type="text"
                                required
                                id="userName"
                                value={formData.userName}
                                onChange={(e) => setFormData({ ...formData, userName: e.target.value })}
                                disabled={loading}
                                placeholder="Enter your full name"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="userEmail" className="block text-sm">
                                Email
                            </Label>
                            <Input
                                type="email"
                                required
                                id="userEmail"
                                value={formData.userEmail}
                                onChange={(e) => setFormData({ ...formData, userEmail: e.target.value })}
                                disabled={loading}
                                placeholder="Enter your email"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="userPhone" className="block text-sm">
                                Phone Number
                            </Label>
                            <Input
                                type="tel"
                                required
                                id="userPhone"
                                value={formData.userPhone}
                                onChange={(e) => setFormData({ ...formData, userPhone: e.target.value })}
                                disabled={loading}
                                placeholder="Enter your phone number"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="password" className="text-sm">
                                Password
                            </Label>
                            <Input
                                type="password"
                                required
                                id="password"
                                value={formData.password}
                                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                                disabled={loading}
                                className="input sz-md variant-mixed"
                                placeholder="Enter your password"
                            />
                        </div>

                        <Button className="w-full" type="submit" disabled={loading}>
                            {loading ? (
                                <>
                                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                    Creating account...
                                </>
                            ) : (
                                "Create Account"
                            )}
                        </Button>
                    </form>
                </div>

                <div className="bg-muted rounded-(--radius) border p-3">
                    <p className="text-accent-foreground text-center text-sm">
                        Have an account ?
                        <Button
                            asChild
                            variant="link"
                            className="px-2">
                            <Link href="/login">Sign In</Link>
                        </Button>
                    </p>
                </div>
            </div>
        </section>
    )
}
