# Database Connection Guide

This guide explains how to connect your MyBankUML application to a MySQL database.

## Step 1: Set Up MySQL Database

First, make sure MySQL is installed and running on your system.

### Create the Database Schema

Run the SQL script to create all necessary tables:

```bash
mysql -u your_username -p < database/schema.sql
```

Or connect to MySQL and run it manually:

```bash
mysql -u your_username -p
```

Then in MySQL:

```sql
CREATE DATABASE IF NOT EXISTS mybankuml;
USE mybankuml;
SOURCE database/schema.sql;
```

## Step 2: Configure Connection Details

You have three options to configure your database connection:

### Option 1: Direct Connection (Quick Start)

Create a Database instance directly in your code:

```java
import bank.Database;

// Replace with your MySQL credentials
String url = "jdbc:mysql://localhost:3306";
String username = "root";  // Your MySQL username
String password = "Password1";  // Your MySQL password
String databaseName = "school_project";

Database db = new Database(url, username, password, databaseName);

// Connect
if (db.connect()) {
    System.out.println("Connected successfully!");
    // Use database operations here
    db.disconnect();
} else {
    System.err.println("Connection failed!");
}
```

### Option 2: Using Properties File (Recommended)

1. Copy the example properties file:
   ```bash
   cp src/main/resources/db.properties.example src/main/resources/db.properties
   ```

2. Edit `src/main/resources/db.properties` with your credentials:
   ```properties
   db.url=jdbc:mysql://localhost:3306
   db.username=root
   db.password=your_password
   db.database=mybankuml
   ```

3. Use DatabaseConfig to load configuration:
   ```java
   import bank.Database;
   import bank.DatabaseConfig;
   
   try {
       DatabaseConfig config = new DatabaseConfig("src/main/resources/db.properties");
       Database db = config.createDatabase();
       
       if (db.connect()) {
           // Use database here
           db.disconnect();
       }
   } catch (IOException e) {
       System.err.println("Error loading config: " + e.getMessage());
   }
   ```

### Option 3: Environment Variables

You can also read from environment variables:

```java
String username = System.getenv("DB_USERNAME");
String password = System.getenv("DB_PASSWORD");
// ... etc
```

## Step 3: Test the Connection

Run the example connection class:

```java
// Edit DatabaseConnectionExample.java with your credentials first
java -cp "target/classes:target/dependency/*" bank.DatabaseConnectionExample
```

## Common Connection Issues

### Issue: "Access denied for user"
**Solution**: Check your MySQL username and password are correct.

### Issue: "Unknown database 'mybankuml'"
**Solution**: Run the schema.sql script to create the database:
```bash
mysql -u your_username -p < database/schema.sql
```

### Issue: "Communications link failure"
**Solution**: 
- Make sure MySQL server is running: `mysql.server start` (Mac) or `sudo systemctl start mysql` (Linux)
- Check if MySQL is listening on port 3306
- Verify firewall settings

### Issue: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
**Solution**: Make sure MySQL connector dependency is in your classpath. Run:
```bash
mvn dependency:copy-dependencies
```

## Example: Complete Connection Workflow

```java
package bank;

public class MyApp {
    public static void main(String[] args) {
        // 1. Create database instance
        Database db = new Database(
            "jdbc:mysql://localhost:3306",
            "root",
            "your_password",
            "mybankuml"
        );
        
        // 2. Connect
        if (!db.connect()) {
            System.err.println("Failed to connect to database!");
            return;
        }
        
        try {
            // 3. Create and save a customer
            Customer customer = new Customer(
                "CUST001",
                "password123",
                "John Doe",
                "john@example.com",
                "555-1234"
            );
            db.saveUser(customer);
            
            // 4. Create and save accounts
            Checking account = new Checking("CHK001", customer, 1000.0);
            db.saveAccount(account);
            
            // 5. Perform operations...
            // Your application logic here
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 6. Always disconnect when done
            db.disconnect();
        }
    }
}
```

## Connection String Format

The connection URL format is:
```
jdbc:mysql://[host]:[port]/[database]?[parameters]
```

Examples:
- Local MySQL: `jdbc:mysql://localhost:3306/mybankuml`
- Remote MySQL: `jdbc:mysql://192.168.1.100:3306/mybankuml`
- With SSL: `jdbc:mysql://localhost:3306/mybankuml?useSSL=true`
- With timezone: `jdbc:mysql://localhost:3306/mybankuml?serverTimezone=UTC`

## Security Best Practices

1. **Never commit passwords to version control**
   - Use properties files (add to .gitignore)
   - Use environment variables
   - Use secure credential stores

2. **Use parameterized queries** (already implemented in Database class)

3. **Enable SSL in production**:
   ```java
   String connectionUrl = url + "/" + databaseName + 
       "?useSSL=true&requireSSL=true&serverTimezone=UTC";
   ```

4. **Hash passwords** before storing (implement password hashing)

5. **Use connection pooling** for production applications

## Next Steps

Once connected, you can:
- Save users, accounts, and transactions
- Retrieve data from the database
- Generate reports and statements
- Process loan requests

See `DatabaseConnectionExample.java` for a complete working example.

