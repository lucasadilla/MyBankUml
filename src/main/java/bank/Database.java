package bank;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private Connection connection;
    private String url;
    private String username;
    private String password;
    private String databaseName;

    public Database(String url, String username, String password, String databaseName) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;
    }

    public boolean connect() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to database
            String connectionUrl = url + "/" + databaseName + "?useSSL=false&serverTimezone=UTC";
            connection = DriverManager.getConnection(connectionUrl, username, password);
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
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
    public void saveUser(User user) {
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
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
    }

    public User getUser(String userID) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Create appropriate user type based on role
                // This is simplified - in real system, you'd have separate tables or more complex logic
                return createUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
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

        // Simplified - in real system, you'd query related tables for branch assignments, etc.
        switch (userRole.toLowerCase()) {
            case "customer":
                return new Customer(userID, password, userName, email, phone);
            case "admin":
                return new Admin(userID, password, userName, email, phone);
            default:
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
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving account: " + e.getMessage());
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

    // Transaction operations
    public void saveTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (transaction_id, customer_id, transaction_type, amount, " +
                     "source_account_id, destination_account_id, status, initiated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, transaction.getTransactionID());
            stmt.setString(2, transaction.getInitiatedBy().getCustomerID());
            stmt.setString(3, transaction.getClass().getSimpleName());
            stmt.setDouble(4, transaction.getTransactionAmount());
            stmt.setString(5, transaction.getSourceAccount() != null ? transaction.getSourceAccount().getAccountID() : null);
            stmt.setString(6, transaction.getDestinationAccount() != null ? transaction.getDestinationAccount().getAccountID() : null);
            stmt.setString(7, transaction.getTransactionStatus());
            stmt.setTimestamp(8, Timestamp.valueOf(transaction.getInitiatedAt()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
        }
    }

    public Transaction getTransaction(String transactionID) {
        // Implementation would reconstruct transaction from database
        // Simplified for now
        return null;
    }

    // Receipt operations
    public void saveReceipt(Receipt receipt) {
        String sql = "INSERT INTO receipts (reference_number, transaction_id, amount, initiator_id, " +
                     "source_account_id, destination_account_id, date_time_issued) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, receipt.getReferenceNumber());
            // Would need transaction ID reference
            stmt.setDouble(2, receipt.getAmount());
            stmt.setString(3, receipt.getInitiator() != null ? receipt.getInitiator().getUserID() : null);
            stmt.setString(4, receipt.getSourceAccountNumber());
            stmt.setString(5, receipt.getDestinationAccountNumber());
            stmt.setTimestamp(6, Timestamp.valueOf(receipt.getDateTimeIssued()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving receipt: " + e.getMessage());
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
        // Implementation would reconstruct loan request from database
        return null;
    }

    public List<LoanRequest> getPendingLoanRequests() {
        List<LoanRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM loan_requests WHERE status = 'Pending'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                // Would reconstruct LoanRequest objects from results
                // For now, return empty list
            }
        } catch (SQLException e) {
            System.err.println("Error getting pending loan requests: " + e.getMessage());
        }
        return requests;
    }
}

