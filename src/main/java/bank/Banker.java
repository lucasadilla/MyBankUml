package bank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Banker extends User {
    private String bankerID;
    private Branch bankerBranch;

    public Banker(String userID, String userPassword, String userName, String userEmail, String userPhone, Branch branch) {
        super(userID, userPassword, userName, userEmail, userPhone, "banker");
        this.bankerID = userID;
        this.bankerBranch = branch;
        if (branch != null) {
            branch.assignStaff(this);
        }
    }

    public Transaction reviewTransaction(String transactionID) {
        // Implementation would query database for transaction
        // For now, return null as placeholder
        return null;
    }

    public Receipt reverseTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        // Create reverse transaction
        Transaction reverse = transaction.reverseTransaction();
        Receipt receipt = new Receipt(reverse, transaction.getSourceAccount(), 
                                     transaction.getDestinationAccount(), 
                                     transaction.getTransactionAmount());
        receipt.generateReceipt();
        return receipt;
    }

    @Override
    public void displayDashboard() {
        System.out.println("Banker Dashboard for: " + userName);
        System.out.println("Branch: " + (bankerBranch != null ? bankerBranch.getBranchName() : "Not assigned"));
    }
}

