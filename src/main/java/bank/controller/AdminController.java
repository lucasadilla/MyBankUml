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
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private Database database;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Admin controller is working!");
        System.out.println("✓ Test endpoint called");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!database.connect()) {
                response.put("success", false);
                response.put("message", "Database connection failed");
                return ResponseEntity.status(500).body(response);
            }

            int totalUsers = database.getTotalUserCount();

            response.put("success", true);
            response.put("totalUsers", totalUsers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load stats: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/users/search")
    public ResponseEntity<Map<String, Object>> searchUsers(@RequestParam(required = false) String name,
                                                          @RequestParam(required = false) String accountNumber,
                                                          @RequestParam(required = false) String phoneNumber,
                                                          @RequestParam(required = false) String userType) {
        System.out.println("========================================");
        System.out.println("🔍 SEARCH REQUEST RECEIVED");
        System.out.println("  name: " + (name != null ? name : "null"));
        System.out.println("  accountNumber: " + (accountNumber != null ? accountNumber : "null"));
        System.out.println("  phoneNumber: " + (phoneNumber != null ? phoneNumber : "null"));
        System.out.println("  userType: " + (userType != null ? userType : "null"));
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
            
            // Build search query based on provided parameters
            System.out.println("🔍 Calling database.searchUsers...");
            List<User> foundUsers = database.searchUsers(name, accountNumber, phoneNumber, userType);
            System.out.println("✓ Database returned " + foundUsers.size() + " user(s)");
            
            // Convert users to response format
            List<Map<String, Object>> userList = new ArrayList<>();
            for (User user : foundUsers) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userID", user.getUserID());
                userMap.put("userName", user.getUserName());
                userMap.put("userEmail", user.getUserEmail());
                userMap.put("userPhone", user.getUserPhone());
                userMap.put("userRole", user.getUserRole());
                userList.add(userMap);
            }
            
            System.out.println("✓ Search found " + userList.size() + " user(s)");
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

    @PostMapping("/users/{userID}/role")
    public ResponseEntity<Map<String, Object>> assignRole(@PathVariable String userID,
                                                         @RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!database.connect()) {
                response.put("success", false);
                response.put("message", "Database connection failed");
                return ResponseEntity.status(500).body(response);
            }
            
            String newRole = data.get("role");
            User user = database.getUser(userID);
            
            if (user == null) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(404).body(response);
            }
            
            user.setUserRole(newRole);
            database.saveUser(user);
            
            response.put("success", true);
            response.put("message", "Role assigned successfully");
            response.put("user", Map.of(
                "userID", user.getUserID(),
                "userRole", user.getUserRole()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Role assignment failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/users/{userID}")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable String userID) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!database.connect()) {
                response.put("success", false);
                response.put("message", "Database connection failed");
                return ResponseEntity.status(500).body(response);
            }

            User user = database.getUser(userID);
            if (user == null) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(404).body(response);
            }

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userID", user.getUserID());
            userMap.put("userName", user.getUserName());
            userMap.put("userEmail", user.getUserEmail());
            userMap.put("userPhone", user.getUserPhone());
            userMap.put("userRole", user.getUserRole());
            userMap.put("password", user.getUserPassword());

            // If this is a customer, include account and loan info
            if (user instanceof Customer) {
                Customer customer = (Customer) user;

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
                userMap.put("accounts", accountList);

                // Loan request count
                int loanRequestCount = database.getLoanRequestCountForCustomer(customer.getCustomerID());
                userMap.put("loanRequestCount", loanRequestCount);
            }

            response.put("success", true);
            response.put("user", userMap);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load user details: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}



