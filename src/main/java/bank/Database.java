package bank;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private Connection connection;
    private String url;
    private String username;
    private String password;
    private String databaseName;

    public Connection getConnection() {
        return connection;
    }

    public Database(String url, String username, String password, String databaseName) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;
    }

    public boolean connect() {
        try {
            // Check if connection is already open and valid
            if (connection != null && !connection.isClosed()) {
                try {
                    if (connection.isValid(2)) {
                        return true;
                    }
                } catch (SQLException e) {
                    // Connection is invalid, will reconnect
                }
            }
            
            // Close old connection if exists
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
            
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to database
            String connectionUrl = url + "/" + databaseName + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            System.out.println("Attempting to connect to: " + connectionUrl);
            System.out.println("Username: " + username);
            
            connection = DriverManager.getConnection(connectionUrl, username, password);
            System.out.println("✓ Database connection successful!");
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            System.err.println("✗ Connection failed: " + e.getMessage());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  SQL State: " + e.getSQLState());
            e.printStackTrace();
            return false;
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    // User operations
    public void saveUser(User user) throws SQLException {
        String sql = "INSERT INTO users (user_id, user_password, user_name, user_email, user_phone, user_role, created_at, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE user_password=?, user_name=?, user_email=?, user_phone=?, user_role=?, is_active=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUserID());
            stmt.setString(2, user.getUserPassword());
            stmt.setString(3, user.getUserName());
            stmt.setString(4, user.getUserEmail());
            stmt.setString(5, user.getUserPhone());
            stmt.setString(6, user.getUserRole());
            stmt.setTimestamp(7, Timestamp.valueOf(user.getCreatedAt()));
            stmt.setBoolean(8, user.isActive());
            stmt.setString(9, user.getUserPassword());
            stmt.setString(10, user.getUserName());
            stmt.setString(11, user.getUserEmail());
            stmt.setString(12, user.getUserPhone());
            stmt.setString(13, user.getUserRole());
            stmt.setBoolean(14, user.isActive());
            int rowsAffected = stmt.executeUpdate();
            System.out.println("✓ User saved: " + user.getUserID() + " (rows affected: " + rowsAffected + ")");
        } catch (SQLException e) {
            System.err.println("✗ Error saving user: " + e.getMessage());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  SQL State: " + e.getSQLState());
            e.printStackTrace();
            throw e; // Re-throw so controller can handle it
        }
    }

    public User getUser(String userID) {
        // Try exact match first, then case-insensitive email match
        String sql = "SELECT * FROM users WHERE user_id = ? OR LOWER(user_email) = LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userID);
            stmt.setString(2, userID); // Also search by email (case-insensitive)
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Create appropriate user type based on role
                User foundUser = createUserFromResultSet(rs);
                System.out.println("✓ Found user: " + foundUser.getUserName() + " (ID: " + foundUser.getUserID() + ", Email: " + foundUser.getUserEmail() + ")");
                return foundUser;
            } else {
                System.out.println("✗ User not found: " + userID);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        String userID = rs.getString("user_id");
        String password = rs.getString("user_password");
        String userName = rs.getString("user_name");
        String email = rs.getString("user_email");
        String phone = rs.getString("user_phone");
        String userRole = rs.getString("user_role");

        // Note: Branch information is not yet wired from the DB, so we pass null for branch.
        // This still correctly identifies the concrete user type and enforces role capabilities.
        if (userRole == null) {
            return new Customer(userID, password, userName, email, phone);
        }

        switch (userRole.toLowerCase()) {
            case "customer":
                return new Customer(userID, password, userName, email, phone);
            case "admin":
                return new Admin(userID, password, userName, email, phone);
            case "banker":
                return new Banker(userID, password, userName, email, phone, null);
            case "bank_manager":
                return new BankManager(userID, password, userName, email, phone, null);
            default:
                // Fallback to customer for any unknown role
                return new Customer(userID, password, userName, email, phone);
        }
    }

    // Account operations
    public void saveAccount(Account account) {
        String sql = "INSERT INTO accounts (account_id, customer_id, account_type, balance, created_at) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE balance=?, account_type=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, account.getAccountID());
            stmt.setString(2, account.getCustomerID());
            stmt.setString(3, account.getClass().getSimpleName());
            stmt.setDouble(4, account.getBalance());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setDouble(6, account.getBalance());
            stmt.setString(7, account.getClass().getSimpleName());
            int rowsAffected = stmt.executeUpdate();
            System.out.println("💾 Saved account " + account.getAccountID() + " (balance: $" + account.getBalance() + ", rows affected: " + rowsAffected + ")");
        } catch (SQLException e) {
            System.err.println("✗ Error saving account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Account getAccount(String accountID) {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Would need to reconstruct account with customer reference
                // Simplified for now
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error getting account: " + e.getMessage());
        }
        return null;
    }

    public List<Account> getAccountsForCustomer(String customerID, Customer customer) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customerID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String accountID = rs.getString("account_id");
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                
                Account account;
                if ("Checking".equalsIgnoreCase(accountType) || "Check".equalsIgnoreCase(accountType)) {
                    account = new Checking(accountID, customer, balance);
                } else if ("Saving".equalsIgnoreCase(accountType) || "Savings".equalsIgnoreCase(accountType)) {
                    // Default interest rate - could be stored in DB if needed
                    account = new Saving(accountID, customer, balance, 0.02);
                } else {
                    // Default to Checking if type unknown
                    account = new Checking(accountID, customer, balance);
                }
                accounts.add(account);
            }
        } catch (SQLException e) {
            System.err.println("Error getting accounts for customer: " + e.getMessage());
            e.printStackTrace();
        }
        return accounts;
    }

    // Transaction operations
    public void saveTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (transaction_id, customer_id, transaction_type, amount, " +
                     "source_account_id, destination_account_id, status, initiated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println("========================================");
        System.out.println("💾 Saving transaction to database");
        System.out.println("  Transaction ID: " + transaction.getTransactionID());
        System.out.println("  Customer ID: " + (transaction.getInitiatedBy() != null ? transaction.getInitiatedBy().getCustomerID() : "null"));
        System.out.println("  Type: " + transaction.getClass().getSimpleName());
        System.out.println("  Amount: $" + transaction.getTransactionAmount());
        System.out.println("  Source Account: " + (transaction.getSourceAccount() != null ? transaction.getSourceAccount().getAccountID() : "null"));
        System.out.println("  Destination Account: " + (transaction.getDestinationAccount() != null ? transaction.getDestinationAccount().getAccountID() : "null"));
        System.out.println("  Status: " + transaction.getTransactionStatus());
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, transaction.getTransactionID());
            String customerID = transaction.getInitiatedBy() != null ? transaction.getInitiatedBy().getCustomerID() : null;
            stmt.setString(2, customerID);
            stmt.setString(3, transaction.getClass().getSimpleName());
            stmt.setDouble(4, transaction.getTransactionAmount());
            stmt.setString(5, transaction.getSourceAccount() != null ? transaction.getSourceAccount().getAccountID() : null);
            stmt.setString(6, transaction.getDestinationAccount() != null ? transaction.getDestinationAccount().getAccountID() : null);
            stmt.setString(7, transaction.getTransactionStatus());
            stmt.setTimestamp(8, Timestamp.valueOf(transaction.getInitiatedAt()));
            int rowsAffected = stmt.executeUpdate();
            System.out.println("✓ Transaction saved successfully (rows affected: " + rowsAffected + ")");
            System.out.println("========================================");
        } catch (SQLException e) {
            System.err.println("✗ Error saving transaction: " + e.getMessage());
            System.err.println("  SQL State: " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
    }

    public Transaction getTransaction(String transactionID) {
        // Implementation would reconstruct transaction from database
        // Simplified for now
        return null;
    }

    public List<Map<String, Object>> getTransactionsForCustomer(String customerID) {
        List<Map<String, Object>> transactions = new ArrayList<>();
        
        if (connection == null) {
            System.err.println("✗ Database connection is null in getTransactionsForCustomer");
            return transactions;
        }
        
        String sql = "SELECT * FROM transactions WHERE customer_id = ? ORDER BY initiated_at DESC LIMIT 100";
        System.out.println("========================================");
        System.out.println("🔍 Querying transactions for customer: " + customerID);
        System.out.println("📋 SQL: " + sql);
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customerID);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                Map<String, Object> tx = new HashMap<>();
                tx.put("transactionID", rs.getString("transaction_id"));
                tx.put("customerID", rs.getString("customer_id"));
                tx.put("transactionType", rs.getString("transaction_type"));
                tx.put("amount", rs.getDouble("amount"));
                tx.put("sourceAccountID", rs.getString("source_account_id"));
                tx.put("destinationAccountID", rs.getString("destination_account_id"));
                tx.put("status", rs.getString("status"));
                Timestamp initiatedAt = rs.getTimestamp("initiated_at");
                if (initiatedAt != null) {
                    tx.put("initiatedAt", initiatedAt.toLocalDateTime().toString());
                }
                transactions.add(tx);
                count++;
                
                // Debug: print first transaction details
                if (count == 1) {
                    System.out.println("  First transaction found:");
                    System.out.println("    ID: " + tx.get("transactionID"));
                    System.out.println("    Type: " + tx.get("transactionType"));
                    System.out.println("    Amount: $" + tx.get("amount"));
                }
            }
            System.out.println("✓ Retrieved " + count + " transaction(s) for customer " + customerID);
            System.out.println("========================================");
            
            if (count == 0) {
                // Debug: Check if there are any transactions at all for this customer
                String checkSQL = "SELECT COUNT(*) as cnt FROM transactions WHERE customer_id = ?";
                try (PreparedStatement checkStmt = connection.prepareStatement(checkSQL)) {
                    checkStmt.setString(1, customerID);
                    ResultSet checkRs = checkStmt.executeQuery();
                    if (checkRs.next()) {
                        int totalCount = checkRs.getInt("cnt");
                        System.out.println("⚠️  Total transactions for customer " + customerID + ": " + totalCount);
                    }
                }
                
                // Debug: Show sample customer IDs from transactions table
                String sampleSQL = "SELECT DISTINCT customer_id FROM transactions LIMIT 10";
                try (PreparedStatement sampleStmt = connection.prepareStatement(sampleSQL)) {
                    ResultSet sampleRs = sampleStmt.executeQuery();
                    System.out.println("📋 Sample customer IDs in transactions table:");
                    while (sampleRs.next()) {
                        System.out.println("  Customer ID: " + sampleRs.getString("customer_id"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error getting transactions for customer: " + e.getMessage());
            System.err.println("  SQL State: " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
        return transactions;
    }

    public List<Map<String, Object>> getAllTransactions(String customerIDFilter) {
        List<Map<String, Object>> transactions = new ArrayList<>();
        String sql;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            if (customerIDFilter != null && !customerIDFilter.trim().isEmpty()) {
                sql = "SELECT * FROM transactions WHERE customer_id = ? ORDER BY initiated_at DESC LIMIT 500";
                stmt = connection.prepareStatement(sql);
                stmt.setString(1, customerIDFilter.trim());
                System.out.println("🔍 Querying transactions for customer: " + customerIDFilter.trim());
            } else {
                sql = "SELECT * FROM transactions ORDER BY initiated_at DESC LIMIT 500";
                stmt = connection.prepareStatement(sql);
                System.out.println("🔍 Querying all transactions");
            }
            
            System.out.println("📋 Executing SQL: " + sql);
            rs = stmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                Map<String, Object> tx = new HashMap<>();
                tx.put("transactionID", rs.getString("transaction_id"));
                tx.put("customerID", rs.getString("customer_id"));
                tx.put("transactionType", rs.getString("transaction_type"));
                tx.put("amount", rs.getDouble("amount"));
                tx.put("sourceAccountID", rs.getString("source_account_id"));
                tx.put("destinationAccountID", rs.getString("destination_account_id"));
                tx.put("status", rs.getString("status"));
                Timestamp initiatedAt = rs.getTimestamp("initiated_at");
                if (initiatedAt != null) {
                    tx.put("initiatedAt", initiatedAt.toLocalDateTime().toString());
                }
                transactions.add(tx);
                count++;
            }
            System.out.println("✓ Retrieved " + count + " transaction(s) from database");
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting all transactions: " + e.getMessage());
            System.err.println("  SQL State: " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return transactions;
    }

    public List<Map<String, Object>> getTransactionsForAccount(String accountID) {
        List<Map<String, Object>> transactions = new ArrayList<>();
        
        if (connection == null) {
            System.err.println("✗ Database connection is null in getTransactionsForAccount");
            return transactions;
        }
        
        String sql = "SELECT * FROM transactions WHERE source_account_id = ? OR destination_account_id = ? ORDER BY initiated_at DESC LIMIT 100";
        System.out.println("========================================");
        System.out.println("🔍 Querying transactions for account: " + accountID);
        System.out.println("📋 SQL: " + sql);
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountID);
            stmt.setString(2, accountID);
            
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                Map<String, Object> tx = new HashMap<>();
                tx.put("transactionID", rs.getString("transaction_id"));
                tx.put("customerID", rs.getString("customer_id"));
                tx.put("transactionType", rs.getString("transaction_type"));
                tx.put("amount", rs.getDouble("amount"));
                tx.put("sourceAccountID", rs.getString("source_account_id"));
                tx.put("destinationAccountID", rs.getString("destination_account_id"));
                tx.put("status", rs.getString("status"));
                Timestamp initiatedAt = rs.getTimestamp("initiated_at");
                if (initiatedAt != null) {
                    tx.put("initiatedAt", initiatedAt.toLocalDateTime().toString());
                }
                transactions.add(tx);
                count++;
                
                // Debug: print first transaction details
                if (count == 1) {
                    System.out.println("  First transaction found:");
                    System.out.println("    ID: " + tx.get("transactionID"));
                    System.out.println("    Source: " + tx.get("sourceAccountID"));
                    System.out.println("    Destination: " + tx.get("destinationAccountID"));
                }
            }
            System.out.println("✓ Retrieved " + count + " transaction(s) for account " + accountID);
            System.out.println("========================================");
            
            if (count == 0) {
                // Debug: Check if there are any transactions at all
                String checkSQL = "SELECT COUNT(*) as cnt FROM transactions WHERE source_account_id = ? OR destination_account_id = ?";
                try (PreparedStatement checkStmt = connection.prepareStatement(checkSQL)) {
                    checkStmt.setString(1, accountID);
                    checkStmt.setString(2, accountID);
                    ResultSet checkRs = checkStmt.executeQuery();
                    if (checkRs.next()) {
                        int totalCount = checkRs.getInt("cnt");
                        System.out.println("⚠️  Total transactions matching account " + accountID + ": " + totalCount);
                    }
                }
                
                // Debug: Show sample account IDs from transactions table
                String sampleSQL = "SELECT DISTINCT source_account_id, destination_account_id FROM transactions LIMIT 10";
                try (PreparedStatement sampleStmt = connection.prepareStatement(sampleSQL)) {
                    ResultSet sampleRs = sampleStmt.executeQuery();
                    System.out.println("📋 Sample account IDs in transactions table:");
                    while (sampleRs.next()) {
                        System.out.println("  Source: " + sampleRs.getString("source_account_id") + 
                                         ", Destination: " + sampleRs.getString("destination_account_id"));
                    }
                }
                
                // Debug: Show all account IDs
                String allAccountsSQL = "SELECT account_id FROM accounts";
                try (PreparedStatement allStmt = connection.prepareStatement(allAccountsSQL)) {
                    ResultSet allRs = allStmt.executeQuery();
                    System.out.println("📋 All account IDs in accounts table:");
                    while (allRs.next()) {
                        System.out.println("  Account ID: " + allRs.getString("account_id"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error getting transactions for account: " + e.getMessage());
            System.err.println("  SQL State: " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
        return transactions;
    }

    // Receipt operations
    public void saveReceipt(Receipt receipt) {
        String sql = "INSERT INTO receipts (reference_number, transaction_id, amount, initiator_id, " +
                     "source_account_id, destination_account_id, date_time_issued) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, receipt.getReferenceNumber());
            stmt.setString(2, null); // transaction_id - can be null, will link later if needed
            stmt.setDouble(3, receipt.getAmount());
            stmt.setString(4, receipt.getInitiator() != null ? receipt.getInitiator().getUserID() : null);
            stmt.setString(5, receipt.getSourceAccountNumber());
            stmt.setString(6, receipt.getDestinationAccountNumber());
            stmt.setTimestamp(7, Timestamp.valueOf(receipt.getDateTimeIssued()));
            int rowsAffected = stmt.executeUpdate();
            System.out.println("💾 Saved receipt " + receipt.getReferenceNumber() + " (rows affected: " + rowsAffected + ")");
        } catch (SQLException e) {
            System.err.println("Error saving receipt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Receipt getReceipt(String receiptID) {
        // Implementation would reconstruct receipt from database
        return null;
    }

    // Statement operations
    public void saveStatement(Statement statement) {
        String sql = "INSERT INTO statements (statement_id, customer_id, year, month, " +
                     "start_balance, end_balance, date_issued) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, statement.getStatementID());
            stmt.setString(2, statement.getCustomer().getCustomerID());
            stmt.setInt(3, statement.getYear());
            stmt.setInt(4, statement.getMonth());
            stmt.setDouble(5, statement.getStartBalance());
            stmt.setDouble(6, statement.getEndBalance());
            stmt.setTimestamp(7, Timestamp.valueOf(statement.getDateIssued()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving statement: " + e.getMessage());
        }
    }

    public Statement getStatement(String statementID) {
        // Implementation would reconstruct statement from database
        return null;
    }

    // Loan Request operations
    public void saveLoanRequest(LoanRequest loanRequest) {
        String sql = "INSERT INTO loan_requests (loan_id, customer_id, amount, purpose, proof_of_income, " +
                     "status, reviewed_by_id, date_submitted, last_updated) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE status=?, reviewed_by_id=?, last_updated=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, loanRequest.getLoanID());
            stmt.setString(2, loanRequest.getCustomer().getCustomerID());
            stmt.setDouble(3, loanRequest.getAmount());
            stmt.setString(4, loanRequest.getPurpose());
            stmt.setString(5, loanRequest.getProofOfIncome());
            stmt.setString(6, loanRequest.getStatus());
            stmt.setString(7, loanRequest.getReviewedBy() != null ? loanRequest.getReviewedBy().getManagerID() : null);
            stmt.setTimestamp(8, Timestamp.valueOf(loanRequest.getDateSubmitted()));
            stmt.setTimestamp(9, Timestamp.valueOf(loanRequest.getLastUpdated()));
            stmt.setString(10, loanRequest.getStatus());
            stmt.setString(11, loanRequest.getReviewedBy() != null ? loanRequest.getReviewedBy().getManagerID() : null);
            stmt.setTimestamp(12, Timestamp.valueOf(loanRequest.getLastUpdated()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving loan request: " + e.getMessage());
        }
    }

    public LoanRequest getLoanRequest(String loanID) {
        String sql = "SELECT * FROM loan_requests WHERE loan_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, loanID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String customerID = rs.getString("customer_id");
                double amount = rs.getDouble("amount");
                String purpose = rs.getString("purpose");
                String proofOfIncome = rs.getString("proof_of_income");
                String status = rs.getString("status");
                String reviewedByID = rs.getString("reviewed_by_id");
                Timestamp submittedTs = rs.getTimestamp("date_submitted");
                Timestamp updatedTs = rs.getTimestamp("last_updated");

                // Load customer
                User user = getUser(customerID);
                if (!(user instanceof Customer)) {
                    System.err.println("LoanRequest customer is not a Customer: " + customerID);
                    return null;
                }
                Customer customer = (Customer) user;

                LoanRequest request = new LoanRequest(customer, amount, purpose, proofOfIncome);
                request.setLoanID(loanID);
                request.setStatus(status);

                if (submittedTs != null) {
                    request.setDateSubmitted(submittedTs.toLocalDateTime());
                }
                if (updatedTs != null) {
                    request.setLastUpdated(updatedTs.toLocalDateTime());
                }

                // Optionally load manager who reviewed the loan
                if (reviewedByID != null) {
                    User managerUser = getUser(reviewedByID);
                    if (managerUser instanceof BankManager) {
                        request.setReviewedBy((BankManager) managerUser);
                    }
                }

                return request;
            }
        } catch (SQLException e) {
            System.err.println("Error getting loan request: " + e.getMessage());
        }
        return null;
    }

    public List<LoanRequest> getPendingLoanRequests() {
        List<LoanRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM loan_requests WHERE status = 'Pending'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String loanID = rs.getString("loan_id");
                String customerID = rs.getString("customer_id");
                double amount = rs.getDouble("amount");
                String purpose = rs.getString("purpose");
                String proofOfIncome = rs.getString("proof_of_income");
                String status = rs.getString("status");
                Timestamp submittedTs = rs.getTimestamp("date_submitted");
                Timestamp updatedTs = rs.getTimestamp("last_updated");

                // Load the customer for this loan
                User user = getUser(customerID);
                if (!(user instanceof Customer)) {
                    continue;
                }
                Customer customer = (Customer) user;

                LoanRequest request = new LoanRequest(customer, amount, purpose, proofOfIncome);
                request.setLoanID(loanID);
                request.setStatus(status);
                if (submittedTs != null) {
                    request.setDateSubmitted(submittedTs.toLocalDateTime());
                }
                if (updatedTs != null) {
                    request.setLastUpdated(updatedTs.toLocalDateTime());
                }

                requests.add(request);
            }
        } catch (SQLException e) {
            System.err.println("Error getting pending loan requests: " + e.getMessage());
        }
        return requests;
    }

    public int getLoanRequestCountForCustomer(String customerID) {
        String sql = "SELECT COUNT(*) AS cnt FROM loan_requests WHERE customer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customerID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        } catch (SQLException e) {
            System.err.println("Error getting loan request count: " + e.getMessage());
        }
        return 0;
    }

    public List<User> searchUsers(String name, String accountNumber, String phoneNumber, String userType) {
        List<User> users = new ArrayList<>();
        
        // Build dynamic SQL query based on provided search criteria
        StringBuilder sqlBuilder = new StringBuilder("SELECT DISTINCT u.* FROM users u ");
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        
        // Join with accounts if searching by account number
        boolean needsAccountJoin = accountNumber != null && !accountNumber.trim().isEmpty();
        if (needsAccountJoin) {
            sqlBuilder.append("LEFT JOIN accounts a ON u.user_id = a.customer_id ");
        }
        
        // If no criteria provided, return all users
        boolean hasAnyCriteria = (name != null && !name.trim().isEmpty()) ||
                                 (accountNumber != null && !accountNumber.trim().isEmpty()) ||
                                 (phoneNumber != null && !phoneNumber.trim().isEmpty()) ||
                                 (userType != null && !userType.trim().isEmpty() && !userType.equalsIgnoreCase("all"));
        
        if (hasAnyCriteria) {
            sqlBuilder.append("WHERE 1=1 ");
            
            // Search by name (matches name OR email)
            if (name != null && !name.trim().isEmpty()) {
                conditions.add("(LOWER(u.user_name) LIKE LOWER(?) OR LOWER(u.user_email) LIKE LOWER(?))");
                String namePattern = "%" + name.trim() + "%";
                params.add(namePattern);
                params.add(namePattern);
            }
            
            // Search by account number
            if (accountNumber != null && !accountNumber.trim().isEmpty()) {
                conditions.add("a.account_id LIKE ?");
                params.add("%" + accountNumber.trim() + "%");
            }
            
            // Search by phone number
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                conditions.add("u.user_phone LIKE ?");
                params.add("%" + phoneNumber.trim() + "%");
            }
            
            // Filter by user type
            if (userType != null && !userType.trim().isEmpty() && !userType.equalsIgnoreCase("all")) {
                conditions.add("LOWER(u.user_role) = LOWER(?)");
                params.add(userType.trim());
            }
            
            // Add all conditions with AND (all criteria must match)
            if (!conditions.isEmpty()) {
                sqlBuilder.append("AND (").append(String.join(" AND ", conditions)).append(") ");
            }
        }
        
        sqlBuilder.append("ORDER BY u.user_name");
        
        String sql = sqlBuilder.toString();
        System.out.println("🔍 Search query: " + sql);
        System.out.println("🔍 Search params: " + params);
        System.out.println("🔍 Has criteria: " + hasAnyCriteria);
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = createUserFromResultSet(rs);
                users.add(user);
            }
            
            System.out.println("✓ Search returned " + users.size() + " user(s)");
        } catch (SQLException e) {
            System.err.println("✗ Error searching users: " + e.getMessage());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  SQL State: " + e.getSQLState());
            e.printStackTrace();
        }
        
        return users;
    }

    // Admin metrics
    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) AS cnt FROM users";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total user count: " + e.getMessage());
        }
        return 0;
    }
}

