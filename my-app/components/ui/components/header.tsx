'use client'

import { Logo } from './ui/logo'
import React from 'react'
import Link from 'next/link'
import { Button } from './ui/button'

// Temporary solution without authentication
const useAuth = () => ({ user: null, loading: false });



export default function Header() {
    const { user, loading } = useAuth()

    const handleLogout = async () => {
        try {
            // Add your logout logic here
            console.log('Logout clicked')
        } catch (error) {
            console.error('Logout failed:', error)
        }
    }

    return (
        <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
            <div className="container flex h-14 items-center">
                <div className="mr-4 flex">
                    <Link href="/" className="mr-6 flex items-center space-x-2">
                        <Logo />
                    </Link>
                </div>
                <div className="flex flex-1 items-center justify-between space-x-2 md:justify-end">
                    <nav className="flex items-center space-x-6">
                        
                    </nav>
                    
                </div>
            </div>
        </header>
    )
}