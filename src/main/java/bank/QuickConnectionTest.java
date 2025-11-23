package bank;

/**
 * Quick test to verify database connection
 * 
 * Usage:
 * 1. Update the connection details below with your MySQL credentials
 * 2. Make sure MySQL is running and database 'mybankuml' exists
 * 3. Run: java -cp "target/classes:target/dependency/*" bank.QuickConnectionTest
 */
public class QuickConnectionTest {
    
    public static void main(String[] args) {
        System.out.println("=== MyBankUML Database Connection Test ===\n");
        
        // Database credentials configured
        String url = "jdbc:mysql://localhost:3306";
        String username = "root";
        String password = "Password1";
        String databaseName = "school_project";
        
        System.out.println("Connecting to: " + url + "/" + databaseName);
        System.out.println("Username: " + username);
        System.out.println();
        
        Database db = new Database(url, username, password, databaseName);
        
        if (db.connect()) {
            System.out.println("✓ SUCCESS: Connected to database!");
            System.out.println("✓ Database is ready to use.");
            
            // Test a simple query
            try {
                // Try to get a user (will return null if no users exist, but connection works)
                db.getUser("TEST_USER");
                System.out.println("✓ Database queries are working.");
            } catch (Exception e) {
                System.out.println("⚠ Warning: " + e.getMessage());
            }
            
            db.disconnect();
            System.out.println("✓ Disconnected successfully.");
        } else {
            System.err.println("✗ FAILED: Could not connect to database!");
            System.err.println("\nTroubleshooting:");
            System.err.println("1. Is MySQL server running?");
            System.err.println("2. Does the database 'school_project' exist?");
            System.err.println("   Run: mysql -u " + username + " -p < database/schema.sql");
            System.err.println("3. Are the username and password correct?");
            System.err.println("4. Does the MySQL user have proper permissions?");
            System.exit(1);
        }
    }
}

