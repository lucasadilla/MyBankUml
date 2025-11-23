package bank;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class LoanRequest {
    private String loanID;
    private Customer customer;
    private double amount;
    private String purpose;
    private String proofOfIncome;
    private BankManager reviewedBy;
    private String status; // Pending, Approved, Rejected
    private LocalDateTime dateSubmitted;
    private LocalDateTime lastUpdated;

    public LoanRequest(Customer customer, double amount, String purpose, String proofOfIncome) {
        this.loanID = UUID.randomUUID().toString();
        this.customer = customer;
        this.amount = amount;
        this.purpose = purpose;
        this.proofOfIncome = proofOfIncome;
        this.status = "Pending";
        this.dateSubmitted = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public String getRequestDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Loan ID: ").append(loanID).append("\n");
        details.append("Customer: ").append(customer.getUserName()).append("\n");
        details.append("Amount: $").append(String.format("%.2f", amount)).append("\n");
        details.append("Purpose: ").append(purpose).append("\n");
        details.append("Status: ").append(status).append("\n");
        details.append("Submitted: ").append(dateSubmitted).append("\n");
        if (reviewedBy != null) {
            details.append("Reviewed by: ").append(reviewedBy.getUserName()).append("\n");
        }
        if (lastUpdated != null) {
            details.append("Last Updated: ").append(lastUpdated).append("\n");
        }
        return details.toString();
    }

    public void storeRequest(Database database) {
        if (database != null) {
            database.saveLoanRequest(this);
        }
    }
}

