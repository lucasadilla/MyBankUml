# MyBankUML - Banking System Backend

Welcome to **MyBankUML**, a comprehensive Java-based banking application that implements core banking operations including account management, transactions, loan processing, and statement generation. This project follows Object-Oriented Programming principles and UML specifications.

## Features

### Core Functionality
- **User Management**: Support for Customers, Bankers, Bank Managers, and System Administrators
- **Account Management**: Multiple account types (Checking, Savings, Card, Check)
- **Transaction Processing**: 
  - Internal fund transfers between customer accounts
  - E-transfers to external recipients
  - Transaction validation and limits
- **Loan Management**: Loan request submission and approval workflow
- **Statement Generation**: Monthly multi-account statements
- **Receipt Generation**: Transaction receipts with full details
- **Database Integration**: MySQL database for persistent storage

### Security Features
- Role-based access control (RBAC)
- Transaction limits and validation
- Account ownership verification
- Input validation

## Project Structure

```
MyBankUml/
├── src/main/java/bank/
│   ├── User.java              # Base user class
│   ├── Customer.java          # Customer implementation
│   ├── Banker.java            # Banker implementation
│   ├── BankManager.java       # Bank manager implementation
│   ├── Admin.java             # System administrator
│   ├── Account.java           # Base account class
│   ├── Checking.java          # Checking account
│   ├── Saving.java            # Savings account
│   ├── Card.java              # Card account
│   ├── Check.java             # Check account
│   ├── Transaction.java       # Base transaction class
│   ├── TransferFunds.java     # Internal transfer
│   ├── ETransfer.java         # E-transfer to recipients
│   ├── Recipient.java         # E-transfer recipient
│   ├── Receipt.java           # Transaction receipt
│   ├── LoanRequest.java       # Loan application
│   ├── Statement.java         # Monthly statement
│   ├── Bank.java              # Bank entity
│   ├── Branch.java            # Branch entity
│   ├── Database.java          # MySQL database operations
│   └── Main.java              # Main entry point
├── database/
│   ├── schema.sql             # Database schema
│   └── README.md              # Database setup instructions
└── pom.xml                     # Maven dependencies

```

## Prerequisites

- **Java 17** or higher
- **Maven** 3.6+
- **MySQL 8.0** or higher
- **Lombok** (handled via Maven)

## Setup Instructions

### 1. Database Setup

First, set up the MySQL database:

```bash
# Create database and tables
mysql -u your_username -p < database/schema.sql
```

Or manually:
```sql
mysql -u your_username -p
CREATE DATABASE mybankuml;
USE mybankuml;
SOURCE database/schema.sql;
```

### 2. Database Configuration

Update your database connection details. You can either:

**Option A: Direct configuration in code**
```java
Database db = new Database(
    "jdbc:mysql://localhost:3306",
    "your_username",
    "your_password",
    "mybankuml"
);
db.connect();
```

**Option B: Use properties file**
Copy `src/main/resources/db.properties.example` to `src/main/resources/db.properties` and update with your credentials.

### 3. Build the Project

```bash
# Compile with Maven
mvn clean compile

# Or download dependencies
mvn dependency:copy-dependencies
```

### 4. Run the Application

```bash
# Using Maven
mvn exec:java -Dexec.mainClass="bank.Main"

# Or directly with Java
java -cp "target/classes:target/dependency/*" bank.Main
```

## Database Schema

The database includes the following main tables:

- **users**: All user types (customers, bankers, managers, admins)
- **banks**: Bank information
- **branches**: Branch information
- **accounts**: All account types
- **transactions**: Transaction records
- **receipts**: Transaction receipts
- **loan_requests**: Loan applications
- **statements**: Monthly statements
- **recipients**: E-transfer recipients
- **audit_log**: Security and compliance logging

See `database/schema.sql` for complete schema details.

## Usage Examples

### Creating a Customer
```java
Customer customer = new Customer(
    "CUST001",
    "password123",
    "John Doe",
    "john@example.com",
    "555-1234"
);
```

### Creating Accounts
```java
Checking checking = new Checking("CHK001", customer, 1000.0);
Saving saving = new Saving("SAV001", customer, 5000.0, 0.02);
```

### Transferring Funds
```java
Receipt receipt = customer.transferFunds(checking, saving, 200.0);
```

### E-Transfer
```java
Recipient recipient = new Recipient("REC001", "Jane Smith", 
    "jane@example.com", "555-5678", customer);
Receipt receipt = customer.sendEtransfer(checking, recipient, 100.0, "Email");
```

### Generating Statement
```java
List<Account> accounts = Arrays.asList(checking, saving);
Statement statement = customer.generateStatement(accounts, 2025, 11);
```

### Loan Request
```java
LoanRequest loan = customer.loanRequest(10000.0, "Home improvement", "proof.pdf");
```

## Architecture

The system follows a layered architecture:

1. **Domain Layer**: Core business entities (User, Account, Transaction, etc.)
2. **Service Layer**: Business logic (Customer operations, Banker operations, etc.)
3. **Data Access Layer**: Database operations (Database class)
4. **Presentation Layer**: Main class and future UI components

## Key Design Patterns

- **Inheritance**: User hierarchy, Account hierarchy, Transaction hierarchy
- **Polymorphism**: Different account types, transaction types
- **Encapsulation**: Private fields with getters/setters
- **Abstraction**: Abstract base classes (User, Account, Transaction)

## Testing

Unit tests and integration tests are specified in the requirements document. To implement:

```bash
mvn test
```

## Security Considerations

- Passwords should be hashed (currently stored as plain text - needs implementation)
- Use parameterized queries (implemented in Database class)
- Enable SSL for production database connections
- Implement transaction limits (partially implemented)
- Add audit logging (schema includes audit_log table)



---

Originally developed by [@shayanaminaei](https://github.com/shayanaminaei)  
