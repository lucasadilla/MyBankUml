# Step-by-Step Database Setup Guide

Follow these steps in order to connect your MyBankUML application to MySQL.

## Prerequisites Check

Before starting, make sure you have:
- ✅ MySQL installed and running
- ✅ Java 17+ installed
- ✅ Maven installed (or Lombok JAR in libs folder)

---

## Step 1: Start MySQL Server

**On macOS:**
```bash
brew services start mysql
# OR
mysql.server start
```

**On Linux:**
```bash
sudo systemctl start mysql
# OR
sudo service mysql start
```

**On Windows:**
- Open Services (services.msc)
- Find "MySQL" and start it
- OR use MySQL Workbench to start the server

**Verify MySQL is running:**
```bash
mysql --version
```

---

## Step 2: Create MySQL User (if needed)

If you don't have a MySQL user yet, create one:

```bash
mysql -u root -p
```

Then in MySQL:
```sql
CREATE USER 'mybankuser'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON *.* TO 'mybankuser'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

**Note:** You can also use the default `root` user if you prefer.

---

## Step 3: Create the Database and Tables

Navigate to your project directory:
```bash
cd /Users/lucaspentland-hyde/Documents/GitHub/MyBankUml
```

Run the schema script:
```bash
mysql -u root -p < database/schema.sql
```

**OR** if using a different user:
```bash
mysql -u mybankuser -p < database/schema.sql
```

**What this does:**
- Creates database `mybankuml`
- Creates all necessary tables (users, accounts, transactions, etc.)
- Sets up relationships and indexes

**Verify it worked:**
```bash
mysql -u root -p -e "USE mybankuml; SHOW TABLES;"
```

You should see tables like: `users`, `accounts`, `transactions`, `banks`, etc.

---

## Step 4: Get Your Connection Details

You'll need:
1. **Host:** Usually `localhost` (or `127.0.0.1`)
2. **Port:** Usually `3306` (MySQL default)
3. **Username:** Your MySQL username (e.g., `root` or `mybankuser`)
4. **Password:** Your MySQL password
5. **Database:** `mybankuml`

**Test your credentials:**
```bash
mysql -u your_username -p mybankuml
```

If you can connect, your credentials are correct!

---

## Step 5: Configure Your Application

You have two options:

### Option A: Quick Test (Direct Connection)

1. Open `src/main/java/bank/QuickConnectionTest.java`

2. Update these lines (around line 18-21):
   ```java
   String url = "jdbc:mysql://localhost:3306";
   String username = "root";           // ← Change this
   String password = "your_password";  // ← Change this
   String databaseName = "mybankuml";
   ```

3. Save the file

### Option B: Properties File (Recommended for Production)

1. Copy the example file:
   ```bash
   cp src/main/resources/db.properties.example src/main/resources/db.properties
   ```

2. Edit `src/main/resources/db.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306
   db.username=root              # ← Your MySQL username
   db.password=your_password    # ← Your MySQL password
   db.database=mybankuml
   ```

3. **Important:** Add `db.properties` to `.gitignore` to avoid committing passwords:
   ```bash
   echo "src/main/resources/db.properties" >> .gitignore
   ```

---

## Step 6: Compile Your Code

Make sure all dependencies are available:

**If using Maven:**
```bash
mvn clean compile
mvn dependency:copy-dependencies
```

**If not using Maven:**
Make sure `libs/lombok-1.18.30.jar` exists, then:
```bash
javac -cp "libs/*" src/main/java/bank/*.java -d target/classes
```

---

## Step 7: Test the Connection

Run the quick connection test:

```bash
java -cp "target/classes:target/dependency/*:libs/*" bank.QuickConnectionTest
```

**Expected output if successful:**
```
=== MyBankUML Database Connection Test ===

Connecting to: jdbc:mysql://localhost:3306/mybankuml
Username: root

✓ SUCCESS: Connected to database!
✓ Database is ready to use.
✓ Database queries are working.
✓ Disconnected successfully.
```

**If you see errors:**

| Error | Solution |
|-------|----------|
| `Access denied` | Check username/password |
| `Unknown database` | Run Step 3 again (create database) |
| `Communications link failure` | Check MySQL is running (Step 1) |
| `ClassNotFoundException` | Run `mvn dependency:copy-dependencies` |

---

## Step 8: Use Database in Your Code

Now you can use the database in your application:

### Example 1: Simple Connection

```java
import bank.Database;
import bank.Customer;
import bank.Checking;

public class MyApp {
    public static void main(String[] args) {
        // Create database connection
        Database db = new Database(
            "jdbc:mysql://localhost:3306",
            "root",              // Your username
            "your_password",     // Your password
            "mybankuml"
        );
        
        // Connect
        if (db.connect()) {
            // Create a customer
            Customer customer = new Customer(
                "CUST001",
                "password123",
                "John Doe",
                "john@example.com",
                "555-1234"
            );
            
            // Save to database
            db.saveUser(customer);
            System.out.println("Customer saved!");
            
            // Create account
            Checking account = new Checking("CHK001", customer, 1000.0);
            db.saveAccount(account);
            System.out.println("Account saved!");
            
            // Always disconnect when done
            db.disconnect();
        }
    }
}
```

### Example 2: Using Properties File

```java
import bank.Database;
import bank.DatabaseConfig;
import java.io.IOException;

public class MyApp {
    public static void main(String[] args) {
        try {
            // Load from properties file
            DatabaseConfig config = new DatabaseConfig("src/main/resources/db.properties");
            Database db = config.createDatabase();
            
            if (db.connect()) {
                // Your code here
                System.out.println("Connected!");
                
                // ... do database operations ...
                
                db.disconnect();
            }
        } catch (IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
        }
    }
}
```

---

## Step 9: Verify Data is Saved

Check that data is actually in the database:

```bash
mysql -u root -p mybankuml
```

Then in MySQL:
```sql
-- Check users
SELECT * FROM users;

-- Check accounts
SELECT * FROM accounts;

-- Check transactions
SELECT * FROM transactions;

-- Exit
EXIT;
```

---

## Step 10: Troubleshooting

### Problem: "Access denied for user"

**Solution:**
1. Verify username and password:
   ```bash
   mysql -u your_username -p
   ```
2. Check user has permissions:
   ```sql
   SHOW GRANTS FOR 'your_username'@'localhost';
   ```

### Problem: "Unknown database 'mybankuml'"

**Solution:**
```bash
mysql -u root -p < database/schema.sql
```

### Problem: "Communications link failure"

**Solution:**
1. Check MySQL is running:
   ```bash
   # macOS
   brew services list | grep mysql
   
   # Linux
   sudo systemctl status mysql
   ```
2. Check port 3306 is open:
   ```bash
   lsof -i :3306
   ```

### Problem: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"

**Solution:**
```bash
mvn dependency:copy-dependencies
```

Or manually download MySQL connector JAR and add to classpath.

---

## Quick Reference

**Connection String Format:**
```
jdbc:mysql://[host]:[port]/[database]?[parameters]
```

**Common Connection Strings:**
- Local: `jdbc:mysql://localhost:3306/mybankuml`
- With timezone: `jdbc:mysql://localhost:3306/mybankuml?serverTimezone=UTC`
- With SSL: `jdbc:mysql://localhost:3306/mybankuml?useSSL=true`

**Test Connection:**
```bash
java -cp "target/classes:target/dependency/*:libs/*" bank.QuickConnectionTest
```

**View Database:**
```bash
mysql -u root -p mybankuml
```

---

## Next Steps

Once connected, you can:
- ✅ Save users, accounts, and transactions
- ✅ Retrieve data from database
- ✅ Generate reports and statements
- ✅ Process loan requests

See `DatabaseConnectionExample.java` for more examples!

---

## Summary Checklist

- [ ] MySQL server is running
- [ ] Database `mybankuml` exists (ran schema.sql)
- [ ] Credentials are correct (tested with mysql command)
- [ ] Updated connection details in code or properties file
- [ ] Compiled the project
- [ ] Ran QuickConnectionTest successfully
- [ ] Can see data in database tables

If all checkboxes are checked, you're ready to go! 🎉

