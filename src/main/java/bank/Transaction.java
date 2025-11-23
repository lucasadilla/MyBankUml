package bank;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public abstract class Transaction {
    protected String transactionID;
    protected String transactionStatus; // Completed, Failed, Pending
    protected Customer initiatedBy;
    protected LocalDateTime initiatedAt;
    protected double transactionAmount;
    protected Account sourceAccount;
    protected Account destinationAccount;

    public Transaction(String transactionID, Customer initiatedBy, double amount, Account sourceAccount, Account destinationAccount) {
        this.transactionID = transactionID;
        this.initiatedBy = initiatedBy;
        this.transactionAmount = amount;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.initiatedAt = LocalDateTime.now();
        this.transactionStatus = "Pending";
    }

    public void transactionCompleted() {
        this.transactionStatus = "Completed";
        if (sourceAccount != null) {
            sourceAccount.addTransaction(this);
        }
        if (destinationAccount != null) {
            destinationAccount.addTransaction(this);
        }
    }

    public void transactionFailed() {
        this.transactionStatus = "Failed";
    }

    public abstract Receipt execute();

    public Transaction reverseTransaction() {
        // Create a reverse transaction
        Transaction reverse = new TransferFunds(
            destinationAccount,
            sourceAccount,
            transactionAmount,
            initiatedBy
        );
        reverse.setTransactionID("REV_" + transactionID);
        return reverse;
    }
}
