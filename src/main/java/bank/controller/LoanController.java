package bank.controller;

import bank.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "*")
public class LoanController {
    
    @Autowired
    private Database database;

    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> requestLoan(@RequestBody Map<String, Object> loanData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!database.connect()) {
                response.put("success", false);
                response.put("message", "Database connection failed");
                return ResponseEntity.status(500).body(response);
            }
            
            String customerID = (String) loanData.get("customerID");
            Double amount = Double.parseDouble(loanData.get("amount").toString());
            String purpose = (String) loanData.get("purpose");
            String proofOfIncome = (String) loanData.get("proofOfIncome");
            
            User user = database.getUser(customerID);
            if (!(user instanceof Customer)) {
                response.put("success", false);
                response.put("message", "User is not a customer");
                return ResponseEntity.status(403).body(response);
            }
            
            Customer customer = (Customer) user;
            LoanRequest loanRequest = customer.loanRequest(amount, purpose, proofOfIncome);
            database.saveLoanRequest(loanRequest);
            
            response.put("success", true);
            response.put("loanRequest", Map.of(
                "loanID", loanRequest.getLoanID(),
                "amount", loanRequest.getAmount(),
                "status", loanRequest.getStatus(),
                "dateSubmitted", loanRequest.getDateSubmitted().toString()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Loan request failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingLoans() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!database.connect()) {
                response.put("success", false);
                response.put("message", "Database connection failed");
                return ResponseEntity.status(500).body(response);
            }
            
            List<LoanRequest> pendingLoans = database.getPendingLoanRequests();
            
            response.put("success", true);
            response.put("loans", pendingLoans);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching pending loans: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/{loanID}/approve")
    public ResponseEntity<Map<String, Object>> approveLoan(@PathVariable String loanID, 
                                                          @RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!database.connect()) {
                response.put("success", false);
                response.put("message", "Database connection failed");
                return ResponseEntity.status(500).body(response);
            }
            
            String managerID = data.get("managerID");
            User user = database.getUser(managerID);
            if (!(user instanceof BankManager)) {
                response.put("success", false);
                response.put("message", "User is not a bank manager");
                return ResponseEntity.status(403).body(response);
            }
            
            LoanRequest loanRequest = database.getLoanRequest(loanID);
            if (loanRequest == null) {
                response.put("success", false);
                response.put("message", "Loan request not found");
                return ResponseEntity.status(404).body(response);
            }
            
            BankManager manager = (BankManager) user;
            manager.approveLoan(loanRequest);
            database.saveLoanRequest(loanRequest);

            // Credit approved amount to customer's checking account
            Customer customer = loanRequest.getCustomer();
            String customerID = customer.getCustomerID();

            // Load customer's accounts
            List<Account> accounts = database.getAccountsForCustomer(customerID, customer);
            customer.getOwnedAccounts().clear();
            customer.getOwnedAccounts().addAll(accounts);

            Account targetAccount = null;
            // Prefer an existing checking account
            for (Account acc : accounts) {
                if (acc instanceof Checking) {
                    targetAccount = acc;
                    break;
                }
            }

            // If no checking account exists, use first account, or create a default checking
            if (targetAccount == null) {
                if (!accounts.isEmpty()) {
                    targetAccount = accounts.get(0);
                } else {
                    String newAccountID = "LOAN" + customerID + System.currentTimeMillis();
                    targetAccount = new Checking(newAccountID, customer, 0.0);
                    customer.getOwnedAccounts().add(targetAccount);
                    database.saveAccount(targetAccount);
                }
            }

            double amount = loanRequest.getAmount();
            double before = targetAccount.getBalance();
            targetAccount.credit(amount);
            double after = targetAccount.getBalance();

            System.out.println("💰 Loan approved: crediting $" + amount + " to account " + targetAccount.getAccountID()
                + " (balance " + before + " -> " + after + ")");
            database.saveAccount(targetAccount);

            response.put("success", true);
            response.put("message", "Loan approved and funds credited");
            response.put("account", Map.of(
                "accountID", targetAccount.getAccountID(),
                "newBalance", targetAccount.getBalance()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Approval failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/{loanID}/reject")
    public ResponseEntity<Map<String, Object>> rejectLoan(@PathVariable String loanID,
                                                          @RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!database.connect()) {
                response.put("success", false);
                response.put("message", "Database connection failed");
                return ResponseEntity.status(500).body(response);
            }
            
            String managerID = data.get("managerID");
            User user = database.getUser(managerID);
            if (!(user instanceof BankManager)) {
                response.put("success", false);
                response.put("message", "User is not a bank manager");
                return ResponseEntity.status(403).body(response);
            }
            
            LoanRequest loanRequest = database.getLoanRequest(loanID);
            if (loanRequest == null) {
                response.put("success", false);
                response.put("message", "Loan request not found");
                return ResponseEntity.status(404).body(response);
            }
            
            BankManager manager = (BankManager) user;
            manager.rejectLoan(loanRequest);
            database.saveLoanRequest(loanRequest);
            
            response.put("success", true);
            response.put("message", "Loan rejected");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Rejection failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}



