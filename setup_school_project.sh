#!/bin/bash

# Setup script for school_project database

echo "=========================================="
echo "Setting up MyBankUML tables in school_project"
echo "=========================================="
echo ""

# Database credentials
MYSQL_USER="root"
MYSQL_PASS="Password1"
DATABASE="school_project"

echo "Database: $DATABASE"
echo "Username: $MYSQL_USER"
echo ""

# Check if MySQL is accessible
if ! command -v mysql &> /dev/null; then
    echo "MySQL command not found. Please run this manually:"
    echo "mysql -u $MYSQL_USER -p$MYSQL_PASS $DATABASE < database/schema_school_project.sql"
    exit 1
fi

# Create tables
echo "Creating tables in school_project database..."
if mysql -u "$MYSQL_USER" -p"$MYSQL_PASS" "$DATABASE" < database/schema_school_project.sql 2>/dev/null; then
    echo "✓ Tables created successfully!"
    
    # Verify tables
    echo ""
    echo "Verifying tables..."
    TABLE_COUNT=$(mysql -u "$MYSQL_USER" -p"$MYSQL_PASS" -D "$DATABASE" -e "SHOW TABLES;" 2>/dev/null | grep -E "(users|accounts|transactions|banks|branches)" | wc -l)
    echo "✓ Found $TABLE_COUNT MyBankUML tables"
    
    echo ""
    echo "=========================================="
    echo "Setup Complete!"
    echo "=========================================="
    echo ""
    echo "Your database is ready to use!"
    echo ""
    echo "Test the connection with:"
    echo "  java -cp 'target/classes:target/dependency/*:libs/*' bank.QuickConnectionTest"
    echo ""
else
    echo "✗ Error creating tables"
    echo ""
    echo "Please run manually:"
    echo "  mysql -u $MYSQL_USER -p$MYSQL_PASS $DATABASE < database/schema_school_project.sql"
    exit 1
fi

