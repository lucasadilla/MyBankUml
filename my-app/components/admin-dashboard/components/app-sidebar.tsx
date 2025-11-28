"use client";

import * as React from "react";
import Link from "next/link";

import {
  IconArcheryArrow,
  IconArrowBadgeRight,
  IconCamera,
  IconChartBar,
  IconDashboard,
  IconDatabase,
  IconFileAi,
  IconFileDescription,
  IconFileWord,
  IconFolder,
  IconHelp,
  IconInnerShadowTop,
  IconListDetails,
  IconReport,
  IconSearch,
  IconSettings,
  IconUsers,
  IconHistory
} from "@tabler/icons-react";

import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem
} from "@/components/ui/sidebar";
import { NavDocuments } from "./nav-documents";
import { NavMain } from "./nav-main";
import { NavSecondary } from "./nav-secondary";
import { NavUser } from "./nav-user";

const data = {
  user: {
    name: "Team 18",
    email: "m@example.com",
    avatar: "/avatars/shadcn.jpg",
  },
  navMain: [
    {
      name: "Dashboard",
      url: "/dashboard",
      icon: IconDashboard,
    },
    {
      name: "Transactions",
      url: "/transactions",
      icon: IconHistory,
    },
    {
      name: "Transfer",
      url: "/transfer",
      icon: IconArcheryArrow
    },
    {
      name: "Loan Requests",
      url: "#",
      icon: IconUsers 
    }
  ],
  navSecondary: [
    {
      name: "Settings",
      url: "#",
      icon: IconSettings
    },
    {
      name: "Get Help",
      url: "#",
      icon: IconHelp
    },
    {
      name: "Search",
      url: "#",
      icon: IconSearch
    }
  ],
  documents: [
    {
      name: "Generate Statement",
      url: "/statement",
      icon: IconDatabase
    },
  ]
};

/*  
  👇 Add activeItem as a prop so Page can set the selected item
*/
export function AppSidebar({
  activeItem,
  ...props
}: React.ComponentProps<typeof Sidebar> & { activeItem?: string }) {
  return (
    <Sidebar collapsible="none" className="h-auto border-r" {...props}>
      <SidebarHeader className="border-b">
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton asChild className="data-[slot=sidebar-menu-button]:!p-1.5">
              <Link href="/">
                <IconInnerShadowTop className="!size-5" />
                <span className="text-base font-semibold">UML Bank</span>
              </Link>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>

      <SidebarContent>
        {/* 👇 Pass activeItem down */}
        <NavMain items={data.navMain} activeItem={activeItem} />
        <NavDocuments items={data.documents} activeItem={activeItem} />
        <NavSecondary items={data.navSecondary} activeItem={activeItem} className="mt-auto" />
      </SidebarContent>

      <SidebarFooter>
        <NavUser user={data.user} />
      </SidebarFooter>
    </Sidebar>
  );
}
