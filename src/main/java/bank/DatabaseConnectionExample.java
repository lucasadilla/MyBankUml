package bank;

/**
 * Example class demonstrating how to connect to the MySQL database
 * and perform basic operations.
 */
public class DatabaseConnectionExample {
    
    public static void main(String[] args) {
        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306";
        String username = "root";
        String password = "Password1";
        String databaseName = "school_project";
        
        // Create database instance
        Database db = new Database(url, username, password, databaseName);
        
        // Connect to database
        System.out.println("Attempting to connect to database...");
        if (db.connect()) {
            System.out.println("✓ Successfully connected to database!");
            
            // Example: Create and save a customer
            try {
                Customer customer = new Customer(
                    "CUST001",
                    "password123",
                    "John Doe",
                    "john@example.com",
                    "555-1234"
                );
                
                // Save customer to database
                db.saveUser(customer);
                System.out.println("✓ Customer saved to database");
                
                // Create accounts
                Checking checking = new Checking("CHK001", customer, 1000.0);
                Saving saving = new Saving("SAV001", customer, 5000.0, 0.02);
                
                // Save accounts to database
                db.saveAccount(checking);
                db.saveAccount(saving);
                System.out.println("✓ Accounts saved to database");
                
                // Retrieve user from database
                User retrievedUser = db.getUser("CUST001");
                if (retrievedUser != null) {
                    System.out.println("✓ Retrieved user: " + retrievedUser.getUserName());
                }
                
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Always disconnect when done
                db.disconnect();
                System.out.println("✓ Disconnected from database");
            }
        } else {
            System.err.println("✗ Failed to connect to database!");
            System.err.println("Please check:");
            System.err.println("1. MySQL server is running");
            System.err.println("2. Database 'school_project' exists (run schema_school_project.sql)");
            System.err.println("3. Username and password are correct");
            System.err.println("4. MySQL user has proper permissions");
        }
    }
}

