package bank.controller;

import bank.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private Database database;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String userID = credentials.get("username");
        String password = credentials.get("password");
        
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
                response.put("message", "User not found. Please check your User ID or email.");
                System.out.println("Login failed: User not found - " + userID);
                return ResponseEntity.status(401).body(response);
            }
            
            if (!user.login(password)) {
                response.put("success", false);
                response.put("message", "Invalid password");
                System.out.println("Login failed: Invalid password for user - " + userID);
                return ResponseEntity.status(401).body(response);
            }
            
            if (!user.isActive()) {
                response.put("success", false);
                response.put("message", "Account is inactive");
                return ResponseEntity.status(403).body(response);
            }
            
            System.out.println("✓ Login successful: " + user.getUserID());
            response.put("success", true);
            response.put("user", Map.of(
                "userID", user.getUserID(),
                "userName", user.getUserName(),
                "userEmail", user.getUserEmail(),
                "userRole", user.getUserRole()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> userData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!database.connect()) {
                response.put("success", false);
                response.put("message", "Database connection failed");
                return ResponseEntity.status(500).body(response);
            }
            
            // Validate required fields
            if (userData.get("userID") == null || userData.get("userID").isEmpty()) {
                response.put("success", false);
                response.put("message", "User ID is required");
                return ResponseEntity.status(400).body(response);
            }
            
            // Check if user already exists
            User existingUser = database.getUser(userData.get("userID"));
            if (existingUser != null) {
                response.put("success", false);
                response.put("message", "User ID already exists. Please choose a different one.");
                return ResponseEntity.status(409).body(response);
            }
            
            // Determine desired role (defaults to customer)
            String requestedRole = userData.get("userRole");
            if (requestedRole == null || requestedRole.isEmpty()) {
                // Support alternative keys just in case the frontend sends a different name
                requestedRole = userData.get("role");
            }
            if (requestedRole == null || requestedRole.isEmpty()) {
                requestedRole = userData.get("accountType");
            }
            if (requestedRole == null || requestedRole.isEmpty()) {
                requestedRole = "customer";
            }

            System.out.println("Requested role from client: '" + requestedRole + "'");
            String normalizedRole = requestedRole.toLowerCase();

            User newUser;
            switch (normalizedRole) {
                case "admin":
                    newUser = new Admin(
                        userData.get("userID"),
                        userData.get("password"),
                        userData.get("userName"),
                        userData.get("userEmail"),
                        userData.get("userPhone")
                    );
                    break;
                case "banker":
                    newUser = new Banker(
                        userData.get("userID"),
                        userData.get("password"),
                        userData.get("userName"),
                        userData.get("userEmail"),
                        userData.get("userPhone"),
                        null // Branch can be associated later
                    );
                    break;
                case "bank_manager":
                    newUser = new BankManager(
                        userData.get("userID"),
                        userData.get("password"),
                        userData.get("userName"),
                        userData.get("userEmail"),
                        userData.get("userPhone"),
                        null // Branch can be associated later
                    );
                    break;
                case "customer":
                default:
                    newUser = new Customer(
                        userData.get("userID"),
                        userData.get("password"),
                        userData.get("userName"),
                        userData.get("userEmail"),
                        userData.get("userPhone")
                    );
                    break;
            }
            
            database.saveUser(newUser);
            System.out.println("✓ User registered: " + newUser.getUserID() + " (role: " + newUser.getUserRole() + ")");
            
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("user", Map.of(
                "userID", newUser.getUserID(),
                "userName", newUser.getUserName(),
                "userEmail", newUser.getUserEmail(),
                "userRole", newUser.getUserRole()
            ));
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(response);
        }
    }
}



