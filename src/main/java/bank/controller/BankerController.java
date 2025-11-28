package bank.controller;

import bank.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/banker")
@CrossOrigin(origins = "*")
public class BankerController {
    
    @Autowired
    private Database database;

    @GetMapping("/users/search")
    public ResponseEntity<Map<String, Object>> searchCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) String phoneNumber) {
        System.out.println("========================================");
        System.out.println("🔍 BANKER/MANAGER CUSTOMER SEARCH REQUEST");
        System.out.println("  name: " + (name != null ? name : "null"));
        System.out.println("  accountNumber: " + (accountNumber != null ? accountNumber : "null"));
        System.out.println("  phoneNumber: " + (phoneNumber != null ? phoneNumber : "null"));
        System.out.println("  userType: customer (hardcoded)");
        System.out.println("========================================");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("🔍 Attempting database connection...");
            if (!database.connect()) {
                System.err.println("✗ Database connection failed!");
                response.put("success", false);
                response.put("message", "Database connection failed");
                return ResponseEntity.status(500).body(response);
            }
            System.out.println("✓ Database connected");
            
            // Search with userType hardcoded to "customer" - bankers/managers can only search customers
            System.out.println("🔍 Calling database.searchUsers with userType='customer'...");
            List<User> foundUsers = database.searchUsers(name, accountNumber, phoneNumber, "customer");
            System.out.println("✓ Database returned " + foundUsers.size() + " customer(s)");
            
            // Convert users to response format
            List<Map<String, Object>> userList = new ArrayList<>();
            for (User user : foundUsers) {
                // Double-check that we only return customers
                if (user instanceof Customer) {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("userID", user.getUserID());
                    userMap.put("userName", user.getUserName());
                    userMap.put("userEmail", user.getUserEmail());
                    userMap.put("userPhone", user.getUserPhone());
                    userMap.put("userRole", user.getUserRole());
                    userList.add(userMap);
                }
            }
            
            System.out.println("✓ Search found " + userList.size() + " customer(s)");
            System.out.println("========================================");
            response.put("success", true);
            response.put("users", userList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("✗ SEARCH ERROR: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Search failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/customers/{customerID}")
    public ResponseEntity<Map<String, Object>> getCustomerDetails(@PathVariable String customerID) {
        System.out.println("========================================");
        System.out.println("👤 BANKER/MANAGER CUSTOMER DETAILS REQUEST");
        System.out.println("  customerID: " + customerID);
        System.out.println("========================================");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!database.connect()) {
                response.put("success", false);
                response.put("message", "Database connection failed");
                return ResponseEntity.status(500).body(response);
            }
            
            User user = database.getUser(customerID);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Customer not found");
                return ResponseEntity.status(404).body(response);
            }
            
            if (!(user instanceof Customer)) {
                response.put("success", false);
                response.put("message", "User is not a customer");
                return ResponseEntity.status(403).body(response);
            }
            
            Customer customer = (Customer) user;
            
            // Build customer info
            Map<String, Object> customerMap = new HashMap<>();
            customerMap.put("userID", customer.getUserID());
            customerMap.put("userName", customer.getUserName());
            customerMap.put("userEmail", customer.getUserEmail());
            customerMap.put("userPhone", customer.getUserPhone());
            customerMap.put("userRole", customer.getUserRole());
            
            // Load accounts
            List<Account> accounts = database.getAccountsForCustomer(customer.getCustomerID(), customer);
            List<Map<String, Object>> accountList = new ArrayList<>();
            for (Account account : accounts) {
                Map<String, Object> acc = new HashMap<>();
                acc.put("accountID", account.getAccountID());
                acc.put("accountType", account.getClass().getSimpleName());
                acc.put("balance", account.getBalance());
                accountList.add(acc);
            }
            customerMap.put("accounts", accountList);
            
            // Load transactions
            List<Map<String, Object>> transactions = database.getTransactionsForCustomer(customer.getCustomerID());
            customerMap.put("transactions", transactions);
            
            System.out.println("✓ Loaded customer details: " + customer.getUserID());
            System.out.println("  Accounts: " + accountList.size());
            System.out.println("  Transactions: " + transactions.size());
            System.out.println("========================================");
            
            response.put("success", true);
            response.put("customer", customerMap);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("✗ ERROR loading customer details: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to load customer details: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<Map<String, Object>> getTransactions(
            @RequestParam(required = false) String customerID) {
        System.out.println("========================================");
        System.out.println("📋 BANKER TRANSACTIONS REQUEST");
        System.out.println("  customerID filter: " + (customerID != null ? customerID : "all"));
        System.out.println("========================================");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!database.connect()) {
                response.put("success", false);
                response.put("message", "Database connection failed");
                return ResponseEntity.status(500).body(response);
            }
            
            List<Map<String, Object>> transactions = database.getAllTransactions(customerID);
            
            System.out.println("✓ Loaded " + transactions.size() + " transaction(s)");
            if (transactions.size() == 0) {
                System.out.println("⚠️  No transactions found in database");
            }
            System.out.println("========================================");
            
            response.put("success", true);
            response.put("transactions", transactions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("✗ ERROR loading transactions: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to load transactions: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

