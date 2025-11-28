# Database Connection Troubleshooting

## Quick Check

The error "Database connection failed" means the backend can't connect to MySQL. Check these:

### 1. Is MySQL Running?
- Open MySQL Workbench
- Try to connect to `localhost:3306`
- If it fails, MySQL isn't running

### 2. Does the Database Exist?
Run this in MySQL Workbench or terminal:
```sql
SHOW DATABASES;
```

Look for `mybankuml` in the list. If it's not there, create it:

```bash
mysql -u root -pPassword1 < database/schema.sql
```

### 3. Check Backend Terminal
Look at your Spring Boot terminal output. You should see error messages like:
- "Connection failed: Access denied" → Wrong password
- "Unknown database 'mybankuml'" → Database doesn't exist
- "Communications link failure" → MySQL not running

### 4. Verify Credentials
Check `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=Password1  # ← Make sure this matches your MySQL password
```

### 5. Test Connection Manually
```bash
mysql -u root -pPassword1 mybankuml
```

If this works, your credentials are correct. If not, fix the password.

## Quick Fix Steps

1. **Start MySQL** (if not running)
2. **Create database:**
   ```bash
   mysql -u root -pPassword1 < database/schema.sql
   ```
3. **Verify it worked:**
   ```bash
   mysql -u root -pPassword1 -e "USE mybankuml; SHOW TABLES;"
   ```
4. **Restart backend:**
   - Stop the backend (Ctrl+C)
   - Run: `mvn spring-boot:run`
5. **Check backend logs** for connection success

## Common Issues

| Error | Solution |
|-------|----------|
| "Access denied" | Wrong password in `application.properties` |
| "Unknown database" | Run `mysql -u root -pPassword1 < database/schema.sql` |
| "Communications link failure" | Start MySQL server |
| "ClassNotFoundException" | Run `mvn dependency:copy-dependencies` |


