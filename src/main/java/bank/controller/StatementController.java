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
@RequestMapping("/api/statements")
@CrossOrigin(origins = "*")
public class StatementController {
    
    @Autowired
    private Database database;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateStatement(@RequestBody Map<String, Object> statementData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!database.connect()) {
                response.put("success", false);
                response.put("message", "Database connection failed");
                return ResponseEntity.status(500).body(response);
            }
            
            String customerID = (String) statementData.get("customerID");
            @SuppressWarnings("unchecked")
            List<String> accountIDs = (List<String>) statementData.get("accountIDs");
            Integer year = Integer.parseInt(statementData.get("year").toString());
            Integer month = Integer.parseInt(statementData.get("month").toString());
            
            User user = database.getUser(customerID);
            if (!(user instanceof Customer)) {
                response.put("success", false);
                response.put("message", "User is not a customer");
                return ResponseEntity.status(403).body(response);
            }
            
            Customer customer = (Customer) user;
            List<Account> accounts = new ArrayList<>();
            for (String accountID : accountIDs) {
                Account account = database.getAccount(accountID);
                if (account != null) {
                    accounts.add(account);
                }
            }
            
            Statement statement = customer.generateStatement(accounts, year, month);
            database.saveStatement(statement);
            
            response.put("success", true);
            response.put("statement", Map.of(
                "statementID", statement.getStatementID(),
                "year", statement.getYear(),
                "month", statement.getMonth(),
                "startBalance", statement.getStartBalance(),
                "endBalance", statement.getEndBalance(),
                "dateIssued", statement.getDateIssued().toString()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Statement generation failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}



