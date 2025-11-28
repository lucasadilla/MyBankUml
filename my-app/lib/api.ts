const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export interface User {
  userID: string;
  userName: string;
  userEmail: string;
  userRole: string;
}

export interface AdminUserDetails extends User {
  userPhone?: string;
  password?: string;
  accounts?: {
    accountID: string;
    accountType: string;
    balance: number;
  }[];
  loanRequestCount?: number;
}

export interface Account {
  accountID: string;
  accountType: string;
  balance: number;
  customerID: string;
}

export interface Receipt {
  referenceNumber: string;
  amount: number;
  dateTimeIssued: string;
}

export interface LoanRequest {
  loanID: string;
  amount: number;
  purpose: string;
  status: string;
  dateSubmitted: string;
}

export interface Statement {
  statementID: string;
  year: number;
  month: number;
  startBalance: number;
  endBalance: number;
  dateIssued: string;
}

class ApiService {
  private async request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
    });

    if (!response.ok) {
      const error = await response.json().catch(() => ({ message: 'Request failed' }));
      throw new Error(error.message || 'Request failed');
    }

    return response.json();
  }

  // Auth
  async login(username: string, password: string) {
    return this.request<{ success: boolean; user?: User; message?: string }>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    });
  }

  async register(userData: {
    userID: string;
    password: string;
    userName: string;
    userEmail: string;
    userPhone: string;
    userRole?: string;
  }) {
    return this.request<{ success: boolean; user?: User; message?: string }>('/auth/register', {
      method: 'POST',
      body: JSON.stringify(userData),
    });
  }

  // Accounts
  async getAccounts(customerID: string) {
    return this.request<{ success: boolean; accounts?: Account[]; message?: string }>(
      `/accounts/${customerID}`
    );
  }

  async createAccount(data: {
    customerID: string;
    accountID: string;
    accountType: string;
    initialBalance?: number;
    interestRate?: number;
  }) {
    return this.request<{ success: boolean; account?: Account; message?: string }>(
      '/accounts/create',
      {
        method: 'POST',
        body: JSON.stringify(data),
      }
    );
  }

  // Transactions
  async transferFunds(data: {
    customerID: string;
    sourceAccountID: string;
    destinationAccountID: string;
    amount: number;
  }) {
    return this.request<{ success: boolean; receipt?: Receipt; message?: string }>(
      '/transactions/transfer',
      {
        method: 'POST',
        body: JSON.stringify(data),
      }
    );
  }

  async eTransfer(data: {
    customerID: string;
    sourceAccountID: string;
    recipientEmail: string;
    recipientName: string;
    recipientPhone: string;
    amount: number;
    notificationMethod: string;
  }) {
    return this.request<{ success: boolean; receipt?: Receipt; message?: string }>(
      '/transactions/etransfer',
      {
        method: 'POST',
        body: JSON.stringify(data),
      }
    );
  }

  // Loans
  async requestLoan(data: {
    customerID: string;
    amount: number;
    purpose: string;
    proofOfIncome: string;
  }) {
    return this.request<{ success: boolean; loanRequest?: LoanRequest; message?: string }>(
      '/loans/request',
      {
        method: 'POST',
        body: JSON.stringify(data),
      }
    );
  }

  async getPendingLoans() {
    return this.request<{ success: boolean; loans?: LoanRequest[]; message?: string }>(
      '/loans/pending'
    );
  }

  // Admin stats
  async getAdminStats() {
    return this.request<{ success: boolean; totalUsers?: number; message?: string }>(
      '/admin/stats'
    );
  }

  async approveLoan(loanID: string, managerID: string) {
    return this.request<{ success: boolean; message?: string }>(`/loans/${loanID}/approve`, {
      method: 'POST',
      body: JSON.stringify({ managerID }),
    });
  }

  async rejectLoan(loanID: string, managerID: string) {
    return this.request<{ success: boolean; message?: string }>(`/loans/${loanID}/reject`, {
      method: 'POST',
      body: JSON.stringify({ managerID }),
    });
  }

  // Statements
  async generateStatement(data: {
    customerID: string;
    accountIDs: string[];
    year: number;
    month: number;
  }) {
    return this.request<{ success: boolean; statement?: Statement; message?: string }>(
      '/statements/generate',
      {
        method: 'POST',
        body: JSON.stringify(data),
      }
    );
  }

  // Admin
  async searchUsers(params: {
    name?: string;
    accountNumber?: string;
    phoneNumber?: string;
    userType?: string;
  }) {
    const queryParams = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
      // Include empty strings but not null/undefined
      if (value !== null && value !== undefined) {
        queryParams.append(key, value);
      }
    });
    const url = `/admin/users/search?${queryParams.toString()}`;
    console.log("🔍 API: Making request to:", `${API_BASE_URL}${url}`);
    return this.request<{ success: boolean; users?: User[]; message?: string }>(url);
  }

  async assignRole(userID: string, role: string) {
    return this.request<{ success: boolean; message?: string }>(`/admin/users/${userID}/role`, {
      method: 'POST',
      body: JSON.stringify({ role }),
    });
  }

  async getUserDetails(userID: string) {
    return this.request<{ success: boolean; user?: AdminUserDetails; message?: string }>(
      `/admin/users/${userID}`
    );
  }
}

export const api = new ApiService();



