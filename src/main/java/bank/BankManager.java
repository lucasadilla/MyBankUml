package bank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankManager extends User {
    private String managerID;
    private Branch managerBranch;

    public BankManager(String userID, String userPassword, String userName, String userEmail, String userPhone, Branch branch) {
        super(userID, userPassword, userName, userEmail, userPhone, "bank_manager");
        this.managerID = userID;
        this.managerBranch = branch;
        if (branch != null) {
            branch.assignStaff(this);
        }
    }

    public void approveLoan(LoanRequest loanRequest) {
        if (loanRequest == null) {
            throw new IllegalArgumentException("Loan request cannot be null");
        }
        loanRequest.setStatus("Approved");
        loanRequest.setReviewedBy(this);
        loanRequest.setLastUpdated(java.time.LocalDateTime.now());
    }

    public void rejectLoan(LoanRequest loanRequest) {
        if (loanRequest == null) {
            throw new IllegalArgumentException("Loan request cannot be null");
        }
        loanRequest.setStatus("Rejected");
        loanRequest.setReviewedBy(this);
        loanRequest.setLastUpdated(java.time.LocalDateTime.now());
    }

    @Override
    public void displayDashboard() {
        System.out.println("Bank Manager Dashboard for: " + userName);
        System.out.println("Branch: " + (managerBranch != null ? managerBranch.getBranchName() : "Not assigned"));
    }
}

