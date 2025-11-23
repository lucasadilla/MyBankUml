-- MyBankUML Database Schema

-- Create database
CREATE DATABASE IF NOT EXISTS mybankuml;
USE mybankuml;

-- Users table (base for all user types)
CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(50) PRIMARY KEY,
    user_password VARCHAR(255) NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    user_email VARCHAR(100) NOT NULL UNIQUE,
    user_phone VARCHAR(20),
    user_role ENUM('customer', 'banker', 'bank_manager', 'admin') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_email (user_email),
    INDEX idx_role (user_role)
);

-- Banks table
CREATE TABLE IF NOT EXISTS banks (
    bank_id VARCHAR(50) PRIMARY KEY,
    bank_name VARCHAR(100) NOT NULL,
    address TEXT,
    contact_info VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Branches table
CREATE TABLE IF NOT EXISTS branches (
    branch_id VARCHAR(50) PRIMARY KEY,
    branch_name VARCHAR(100) NOT NULL,
    address TEXT,
    contact_info VARCHAR(255),
    bank_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bank_id) REFERENCES banks(bank_id) ON DELETE CASCADE,
    INDEX idx_bank (bank_id)
);

-- Branch staff assignments
CREATE TABLE IF NOT EXISTS branch_staff (
    branch_id VARCHAR(50) NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (branch_id, user_id),
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Accounts table
CREATE TABLE IF NOT EXISTS accounts (
    account_id VARCHAR(50) PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    account_type ENUM('Checking', 'Saving', 'Card', 'Check') NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    interest_rate DECIMAL(5, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_customer (customer_id),
    INDEX idx_type (account_type)
);

-- Recipients table (for e-transfers)
CREATE TABLE IF NOT EXISTS recipients (
    recipient_id VARCHAR(50) PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_customer (customer_id)
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id VARCHAR(50) PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    transaction_type ENUM('TransferFunds', 'ETransfer', 'Payment') NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    source_account_id VARCHAR(50),
    destination_account_id VARCHAR(50),
    recipient_id VARCHAR(50),
    status ENUM('Pending', 'Completed', 'Failed') DEFAULT 'Pending',
    notification_method VARCHAR(20),
    initiated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (source_account_id) REFERENCES accounts(account_id) ON DELETE SET NULL,
    FOREIGN KEY (destination_account_id) REFERENCES accounts(account_id) ON DELETE SET NULL,
    FOREIGN KEY (recipient_id) REFERENCES recipients(recipient_id) ON DELETE SET NULL,
    INDEX idx_customer (customer_id),
    INDEX idx_status (status),
    INDEX idx_date (initiated_at)
);

-- Receipts table
CREATE TABLE IF NOT EXISTS receipts (
    receipt_id INT AUTO_INCREMENT PRIMARY KEY,
    reference_number VARCHAR(50) UNIQUE NOT NULL,
    transaction_id VARCHAR(50),
    amount DECIMAL(15, 2) NOT NULL,
    initiator_id VARCHAR(50),
    recipient_id VARCHAR(50),
    source_account_id VARCHAR(50),
    destination_account_id VARCHAR(50),
    notification_method VARCHAR(20),
    bank_id VARCHAR(50),
    date_time_issued TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id) ON DELETE SET NULL,
    FOREIGN KEY (initiator_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (source_account_id) REFERENCES accounts(account_id) ON DELETE SET NULL,
    FOREIGN KEY (destination_account_id) REFERENCES accounts(account_id) ON DELETE SET NULL,
    FOREIGN KEY (bank_id) REFERENCES banks(bank_id) ON DELETE SET NULL,
    INDEX idx_reference (reference_number),
    INDEX idx_transaction (transaction_id)
);

-- Loan requests table
CREATE TABLE IF NOT EXISTS loan_requests (
    loan_id VARCHAR(50) PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    purpose TEXT,
    proof_of_income TEXT,
    status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
    reviewed_by_id VARCHAR(50),
    date_submitted TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_customer (customer_id),
    INDEX idx_status (status),
    INDEX idx_reviewed_by (reviewed_by_id)
);

-- Statements table
CREATE TABLE IF NOT EXISTS statements (
    statement_id VARCHAR(50) PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    month INT NOT NULL,
    start_balance DECIMAL(15, 2) DEFAULT 0.00,
    end_balance DECIMAL(15, 2) DEFAULT 0.00,
    bank_id VARCHAR(50),
    branch_id VARCHAR(50),
    date_issued TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (bank_id) REFERENCES banks(bank_id) ON DELETE SET NULL,
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id) ON DELETE SET NULL,
    INDEX idx_customer (customer_id),
    INDEX idx_period (year, month)
);

-- Statement accounts (many-to-many relationship)
CREATE TABLE IF NOT EXISTS statement_accounts (
    statement_id VARCHAR(50) NOT NULL,
    account_id VARCHAR(50) NOT NULL,
    PRIMARY KEY (statement_id, account_id),
    FOREIGN KEY (statement_id) REFERENCES statements(statement_id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- Audit log table (for compliance and security)
CREATE TABLE IF NOT EXISTS audit_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50),
    action_type VARCHAR(50) NOT NULL,
    action_description TEXT,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user (user_id),
    INDEX idx_timestamp (timestamp),
    INDEX idx_action (action_type)
);

-- Create indexes for performance
CREATE INDEX idx_transactions_date ON transactions(initiated_at);
CREATE INDEX idx_accounts_balance ON accounts(balance);
CREATE INDEX idx_users_active ON users(is_active);

