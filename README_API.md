# MyBankUML - Frontend-Backend Integration

This document describes how the frontend and backend are connected.

## Backend API (Spring Boot)

The backend runs on `http://localhost:8080` and provides REST API endpoints:

### Endpoints

- **POST /api/auth/login** - User login
- **POST /api/auth/register** - User registration
- **GET /api/accounts/{customerID}** - Get user accounts
- **POST /api/transactions/transfer** - Transfer funds between accounts
- **POST /api/transactions/etransfer** - Send e-transfer
- **POST /api/loans/request** - Request a loan
- **GET /api/loans/pending** - Get pending loans
- **POST /api/loans/{loanID}/approve** - Approve loan
- **POST /api/loans/{loanID}/reject** - Reject loan
- **POST /api/statements/generate** - Generate statement
- **GET /api/admin/users/search** - Search users
- **POST /api/admin/users/{userID}/role** - Assign role

## Frontend (Next.js)

The frontend runs on `http://localhost:3000` and uses the API service layer in `my-app/lib/api.ts`.

### Pages Created

1. **Login** (`/login`) - User authentication
2. **Signup** (`/signup`) - User registration
3. **Dashboard** (`/dashboard`) - User dashboard with account overview
4. **Admin Dashboard** (`/dash`) - Admin dashboard (existing)
5. **Accounts** (`/accounts`) - View all accounts
6. **Transfer Funds** (`/transfer`) - Transfer between accounts
7. **E-Transfer** (`/etransfer`) - Send e-transfer
8. **Loan Request** (`/loan`) - Request a loan
9. **Statement** (`/statement`) - Generate monthly statement
10. **Receipt** (`/receipt`) - View transaction receipt
11. **Search Users** (`/admin/search`) - Admin user search
12. **Role Assignment** (`/admin/roles`) - Admin role management

## Running the Application

### Backend

```bash
# From project root
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend

```bash
# From my-app directory
cd my-app
npm install
npm run dev
```

The frontend will start on `http://localhost:3000`

## Configuration

Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mybankuml
spring.datasource.username=your_username
spring.datasource.password=your_password
```

Update `my-app/lib/api.ts` if your backend runs on a different port:

```typescript
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';
```

## Features Implemented

✅ Login/Registration
✅ Account Management
✅ Transfer Funds
✅ E-Transfer
✅ Loan Requests
✅ Statement Generation
✅ Receipt Viewing
✅ Admin Dashboard
✅ User Search
✅ Role Assignment



