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
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {
    
    @Autowired
    private Database database;

    @GetMapping("/{customerID}")
    public ResponseEntity<Map<String, Object>> getAccounts(@PathVariable String customerID) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Ensure connection is established
            if (!database.connect()) {
                response.put("success", false);
                response.put("message", "Database connection failed. Please check if MySQL is running and database 'mybankuml' exists.");
                return ResponseEntity.status(500).body(response);
            }
            
            User user = database.getUser(customerID);
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                
                // Load accounts from database
                List<Account> accounts = database.getAccountsForCustomer(customerID, customer);
                // Update customer's owned accounts
                customer.getOwnedAccounts().clear();
                customer.getOwnedAccounts().addAll(accounts);
                
                List<Map<String, Object>> accountList = new ArrayList<>();
                for (Account account : accounts) {
                    Map<String, Object> acc = new HashMap<>();
                    acc.put("accountID", account.getAccountID());
                    acc.put("accountType", account.getClass().getSimpleName());
                    acc.put("balance", account.getBalance());
                    acc.put("customerID", account.getCustomerID());
                    accountList.add(acc);
                }
                
                response.put("success", true);
                response.put("accounts", accountList);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "User is not a customer");
                return ResponseEntity.status(403).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching accounts: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createAccount(@RequestBody Map<String, Object> accountData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!database.connect()) {
                response.put("success", false);
                response.put("message", "Database connection failed");
                return ResponseEntity.status(500).body(response);
            }
            
            String customerID = (String) accountData.get("customerID");
            String accountType = (String) accountData.get("accountType"); // "Checking" or "Saving"
            String accountID = (String) accountData.get("accountID");
            Double initialBalance = accountData.get("initialBalance") != null 
                ? Double.parseDouble(accountData.get("initialBalance").toString()) 
                : 0.0;
            
            User user = database.getUser(customerID);
            if (!(user instanceof Customer)) {
                response.put("success", false);
                response.put("message", "User is not a customer");
                return ResponseEntity.status(403).body(response);
            }
            
            Customer customer = (Customer) user;
            Account account;
            
            if ("Checking".equalsIgnoreCase(accountType) || "Check".equalsIgnoreCase(accountType)) {
                account = new Checking(accountID, customer, initialBalance);
            } else if ("Saving".equalsIgnoreCase(accountType) || "Savings".equalsIgnoreCase(accountType)) {
                double interestRate = accountData.get("interestRate") != null 
                    ? Double.parseDouble(accountData.get("interestRate").toString()) 
                    : 0.02; // Default 2% interest
                account = new Saving(accountID, customer, initialBalance, interestRate);
            } else {
                response.put("success", false);
                response.put("message", "Invalid account type. Use 'Checking' or 'Saving'");
                return ResponseEntity.status(400).body(response);
            }
            
            // Add account to customer's owned accounts
            customer.getOwnedAccounts().add(account);
            
            // Save account to database
            database.saveAccount(account);
            
            // Also update the user in database to persist the account relationship
            database.saveUser(customer);
            
            System.out.println("✓ Account created: " + accountID + " for customer: " + customerID);
            
            Map<String, Object> accountInfo = new HashMap<>();
            accountInfo.put("accountID", account.getAccountID());
            accountInfo.put("accountType", account.getClass().getSimpleName());
            accountInfo.put("balance", account.getBalance());
            accountInfo.put("customerID", account.getCustomerID());
            
            response.put("success", true);
            response.put("message", "Account created successfully");
            response.put("account", accountInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating account: " + e.getMessage());
            System.err.println("Error creating account: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(response);
        }
    }
}



