#Quick Start: Connect Database in 5 Minutes

## Method 1: Automated Setup (Easiest)

Run the setup script:

```bash
./setup_database.sh
```

**That's it!** Then test with:
```bash
java -cp "target/classes:target/dependency/*:libs/*" bank.QuickConnectionTest
```

---

## Method 2: Manual Setup (Step-by-Step)

### ⚡ Step 1: Start MySQL

```bash
# macOS
brew services start mysql

# Linux
sudo systemctl start mysql

# Windows: Start MySQL service from Services panel
```

**Verify it's running:**
```bash
mysql --version
```

---

### ⚡ Step 2: Create Database

```bash
cd /Users/lucaspentland-hyde/Documents/GitHub/MyBankUml
mysql -u root -p < database/schema.sql
```

Enter your MySQL password when prompted.

**Verify:**
```bash
mysql -u root -p -e "USE mybankuml; SHOW TABLES;"
```

---

### ⚡ Step 3: Update Connection Details

**Option A: Edit QuickConnectionTest.java**

Open `src/main/java/bank/QuickConnectionTest.java` and change lines 18-19:

```java
String username = "root";           // ← Your MySQL username
String password = "your_password";  // ← Your MySQL password
```

**Option B: Use Properties File**

```bash
cp src/main/resources/db.properties.example src/main/resources/db.properties
```

Edit `src/main/resources/db.properties`:
```properties
db.username=root
db.password=your_password
```

---

### ⚡ Step 4: Compile

```bash
# If using Maven
mvn clean compile
mvn dependency:copy-dependencies

# OR if not using Maven
javac -cp "libs/*" src/main/java/bank/*.java -d target/classes
```

---

### ⚡ Step 5: Test Connection

```bash
java -cp "target/classes:target/dependency/*:libs/*" bank.QuickConnectionTest
```

**Success looks like:**
```
✓ SUCCESS: Connected to database!
✓ Database is ready to use.
```

---

## You're Done!

Now you can use the database in your code:

```java
Database db = new Database(
    "jdbc:mysql://localhost:3306",
    "root",
    "your_password",
    "mybankuml"
);

if (db.connect()) {
    // Your code here
    db.disconnect();
}
```

---

## Troubleshooting

| Problem | Solution |
|--------|----------|
| "Access denied" | Wrong username/password |
| "Unknown database" | Run Step 2 again |
| "MySQL not running" | Run Step 1 again |
| "ClassNotFoundException" | Run `mvn dependency:copy-dependencies` |

---

## More Details

- **Full guide:** See `STEP_BY_STEP_DB_SETUP.md`
- **Connection examples:** See `DATABASE_CONNECTION.md`
- **Database schema:** See `database/schema.sql`

---

**Need help?** Check the detailed guides or run the automated setup script!

