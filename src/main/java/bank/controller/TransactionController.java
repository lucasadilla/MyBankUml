package bank.controller;

import bank.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    @Autowired
    private Database database;

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transferFunds(@RequestBody Map<String, Object> transferData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!database.connect()) {
                response.put("success", false);
                response.put("message", "Database connection failed");
                return ResponseEntity.status(500).body(response);
            }
            
            String customerID = (String) transferData.get("customerID");
            String sourceAccountID = (String) transferData.get("sourceAccountID");
            String destinationAccountID = (String) transferData.get("destinationAccountID");
            Double amount = Double.parseDouble(transferData.get("amount").toString());
            
            User user = database.getUser(customerID);
            if (!(user instanceof Customer)) {
                response.put("success", false);
                response.put("message", "User is not a customer");
                return ResponseEntity.status(403).body(response);
            }
            
            Customer customer = (Customer) user;
            
            // Load customer's accounts from database
            List<Account> customerAccounts = database.getAccountsForCustomer(customerID, customer);
            customer.getOwnedAccounts().clear();
            customer.getOwnedAccounts().addAll(customerAccounts);
            
            // Find accounts from customer's owned accounts
            Account sourceAccount = null;
            Account destinationAccount = null;
            
            for (Account acc : customer.getOwnedAccounts()) {
                if (acc.getAccountID().equals(sourceAccountID)) {
                    sourceAccount = acc;
                }
                if (acc.getAccountID().equals(destinationAccountID)) {
                    destinationAccount = acc;
                }
            }
            
            if (sourceAccount == null) {
                response.put("success", false);
                response.put("message", "Source account not found. Account ID: " + sourceAccountID);
                return ResponseEntity.status(404).body(response);
            }
            
            if (destinationAccount == null) {
                response.put("success", false);
                response.put("message", "Destination account not found. Account ID: " + destinationAccountID);
                return ResponseEntity.status(404).body(response);
            }
            
            Receipt receipt = customer.transferFunds(sourceAccount, destinationAccount, amount);
            // Transaction is already saved as part of the receipt generation
            database.saveReceipt(receipt);
            database.saveAccount(sourceAccount);
            database.saveAccount(destinationAccount);
            
            response.put("success", true);
            response.put("receipt", Map.of(
                "referenceNumber", receipt.getReferenceNumber(),
                "amount", receipt.getAmount(),
                "dateTimeIssued", receipt.getDateTimeIssued().toString()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Transfer failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/etransfer")
    public ResponseEntity<Map<String, Object>> eTransfer(@RequestBody Map<String, Object> etransferData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!database.connect()) {
                response.put("success", false);
                response.put("message", "Database connection failed");
                return ResponseEntity.status(500).body(response);
            }
            
            String customerID = (String) etransferData.get("customerID");
            String sourceAccountID = (String) etransferData.get("sourceAccountID");
            String recipientEmail = (String) etransferData.get("recipientEmail");
            String recipientName = (String) etransferData.get("recipientName");
            String recipientPhone = (String) etransferData.get("recipientPhone");
            Double amount = Double.parseDouble(etransferData.get("amount").toString());
            String notificationMethod = (String) etransferData.get("notificationMethod");
            
            User user = database.getUser(customerID);
            if (!(user instanceof Customer)) {
                response.put("success", false);
                response.put("message", "User is not a customer");
                return ResponseEntity.status(403).body(response);
            }
            
            Customer customer = (Customer) user;
            
            // Load customer's accounts from database
            List<Account> customerAccounts = database.getAccountsForCustomer(customerID, customer);
            customer.getOwnedAccounts().clear();
            customer.getOwnedAccounts().addAll(customerAccounts);
            
            // Find source account from customer's owned accounts
            Account sourceAccount = null;
            for (Account acc : customer.getOwnedAccounts()) {
                if (acc.getAccountID().equals(sourceAccountID)) {
                    sourceAccount = acc;
                    break;
                }
            }
            
            if (sourceAccount == null) {
                response.put("success", false);
                response.put("message", "Source account not found. Account ID: " + sourceAccountID);
                return ResponseEntity.status(404).body(response);
            }
            
            Recipient recipient = new Recipient("REC" + System.currentTimeMillis(), 
                recipientName, recipientEmail, recipientPhone, customer);
            
            // Execute the e-transfer (debits source account)
            Receipt receipt = customer.sendEtransfer(sourceAccount, recipient, amount, notificationMethod);
            
            // Try to find recipient user by email and credit their account
            System.out.println("🔍 Looking for recipient with email: " + recipientEmail);
            User recipientUser = database.getUser(recipientEmail);
            
            if (recipientUser == null) {
                System.out.println("⚠ Recipient email not found in system: " + recipientEmail + ". Money debited from source but recipient account not credited (external recipient).");
            } else if (recipientUser instanceof Customer) {
                Customer recipientCustomer = (Customer) recipientUser;
                System.out.println("✓ Found recipient: " + recipientCustomer.getUserName() + " (ID: " + recipientCustomer.getCustomerID() + ")");
                
                // Load recipient's accounts
                List<Account> recipientAccounts = database.getAccountsForCustomer(recipientCustomer.getCustomerID(), recipientCustomer);
                recipientCustomer.getOwnedAccounts().clear();
                recipientCustomer.getOwnedAccounts().addAll(recipientAccounts);
                System.out.println("✓ Loaded " + recipientAccounts.size() + " account(s) for recipient");
                
                Account recipientAccount = null;
                
                // Use first account if available, or create a default checking account
                if (!recipientCustomer.getOwnedAccounts().isEmpty()) {
                    recipientAccount = recipientCustomer.getOwnedAccounts().get(0);
                    System.out.println("✓ Using existing account: " + recipientAccount.getAccountID() + " (Balance before: $" + recipientAccount.getBalance() + ")");
                } else {
                    // Create a default checking account for recipient if they don't have one
                    String defaultAccountID = "CHK" + recipientCustomer.getCustomerID() + System.currentTimeMillis();
                    recipientAccount = new Checking(defaultAccountID, recipientCustomer, 0.0);
                    recipientCustomer.getOwnedAccounts().add(recipientAccount);
                    database.saveAccount(recipientAccount);
                    database.saveUser(recipientCustomer);
                    System.out.println("✓ Created default account for recipient: " + defaultAccountID);
                }
                
                // Credit the recipient's account
                double balanceBefore = recipientAccount.getBalance();
                recipientAccount.credit(amount);
                double balanceAfter = recipientAccount.getBalance();
                
                System.out.println("💰 Crediting $" + amount + " to recipient account: " + recipientAccount.getAccountID());
                System.out.println("   Balance: $" + balanceBefore + " → $" + balanceAfter);
                
                // Save the updated account to database
                database.saveAccount(recipientAccount);
                System.out.println("✓ Saved recipient account to database");
            } else {
                System.out.println("⚠ Recipient found but is not a customer (role: " + recipientUser.getUserRole() + ")");
            }
            
            // Transaction is already saved as part of the receipt generation
            database.saveReceipt(receipt);
            database.saveAccount(sourceAccount);
            
            response.put("success", true);
            response.put("receipt", Map.of(
                "referenceNumber", receipt.getReferenceNumber(),
                "amount", receipt.getAmount(),
                "dateTimeIssued", receipt.getDateTimeIssued().toString()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "E-transfer failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

