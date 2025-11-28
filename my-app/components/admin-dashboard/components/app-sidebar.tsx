"use client";

import * as React from "react";
import Link from "next/link";
import {
  IconDashboard,
  IconInnerShadowTop,
  IconListDetails,
  IconSearch,
  IconUsers
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
    name: "shadcn",
    email: "m@example.com",
    avatar: "/avatars/shadcn.jpg"
  },
  navMain: [
    {
      title: "Dashboard",
      url: "/dash",
      icon: IconDashboard
    },
    {
      title: "Loan Requests",
      url: "/admin/loans",
      icon: IconListDetails
    },
    {
      title: "Search Users",
      url: "/admin/search",
      icon: IconSearch
    },
    {
      title: "Role Management",
      url: "/admin/roles",
      icon: IconUsers
    }
  ],
  // We don't need extra cloud/doc/secondary sections for this dashboard,
  // so navClouds, navSecondary, and documents have been removed.
};

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
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
        <NavMain items={data.navMain} />
      </SidebarContent>
      <SidebarFooter>
        <NavUser />
      </SidebarFooter>
    </Sidebar>
  );
}
