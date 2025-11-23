# MyBankUML Database Setup

## Prerequisites
- MySQL 8.0 or higher
- MySQL user with CREATE DATABASE privileges

## Setup Instructions

1. **Create the database and tables:**
   ```bash
   mysql -u your_username -p < schema.sql
   ```

2. **Database Configuration:**
   Update the database connection details in your application:
   - URL: `jdbc:mysql://localhost:3306`
   - Database Name: `mybankuml`
   - Username: Your MySQL username
   - Password: Your MySQL password

3. **Connection Example:**
   ```java
   Database db = new Database(
       "jdbc:mysql://localhost:3306",
       "your_username",
       "your_password",
       "mybankuml"
   );
   db.connect();
   ```

## Database Schema Overview

### Core Tables
- **users**: Base table for all user types (Customer, Banker, BankManager, Admin)
- **banks**: Bank information
- **branches**: Branch information linked to banks
- **accounts**: All account types (Checking, Savings, Card, Check)
- **transactions**: All transaction records
- **receipts**: Transaction receipts
- **loan_requests**: Loan applications and approvals
- **statements**: Monthly account statements
- **recipients**: Saved e-transfer recipients
- **audit_log**: Security and compliance logging

### Relationships
- Users can have multiple accounts
- Accounts belong to customers
- Transactions link accounts and users
- Receipts reference transactions
- Loan requests link customers and bank managers
- Statements aggregate multiple accounts

## Security Notes
- Passwords should be hashed (not stored in plain text)
- Use parameterized queries (implemented in Database class)
- Enable SSL for production connections
- Regular backups recommended

