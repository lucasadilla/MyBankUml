package bank;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Receipt {
    private String referenceNumber;
    private LocalDateTime dateTimeIssued;
    private double amount;
    private Customer initiator;
    private Customer recipientCustomer;
    private Recipient recipient;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private Bank bank;
    private String notificationMethod;

    public Receipt(Transaction transaction, Account sourceAccount, Account destinationAccount, double amount) {
        this.referenceNumber = UUID.randomUUID().toString();
        this.dateTimeIssued = LocalDateTime.now();
        this.amount = amount;
        this.initiator = transaction.getInitiatedBy();
        if (sourceAccount != null) {
            this.sourceAccountNumber = sourceAccount.getAccountID();
        }
        if (destinationAccount != null) {
            this.destinationAccountNumber = destinationAccount.getAccountID();
            this.recipientCustomer = destinationAccount.getAccountOwner();
        }
    }

    public void generateReceipt() {
        // Receipt is generated when this method is called
        // In a real system, this would format and store the receipt
    }

    public String getReceiptDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Receipt Reference: ").append(referenceNumber).append("\n");
        details.append("Date/Time: ").append(dateTimeIssued).append("\n");
        details.append("Amount: $").append(String.format("%.2f", amount)).append("\n");
        details.append("From: ").append(initiator != null ? initiator.getUserName() : "N/A").append("\n");
        if (recipientCustomer != null) {
            details.append("To: ").append(recipientCustomer.getUserName()).append("\n");
        } else if (recipient != null) {
            details.append("To: ").append(recipient.getName()).append("\n");
            details.append("Contact: ").append(recipient.getEmail() != null ? recipient.getEmail() : recipient.getPhoneNumber()).append("\n");
        }
        details.append("Source Account: ").append(sourceAccountNumber != null ? sourceAccountNumber : "N/A").append("\n");
        details.append("Destination Account: ").append(destinationAccountNumber != null ? destinationAccountNumber : "N/A").append("\n");
        if (notificationMethod != null) {
            details.append("Notification: ").append(notificationMethod).append("\n");
        }
        if (bank != null) {
            details.append("Bank: ").append(bank.getBankName()).append("\n");
        }
        return details.toString();
    }

    public void storeReceipt(Database database) {
        if (database != null) {
            database.saveReceipt(this);
        }
    }
}

