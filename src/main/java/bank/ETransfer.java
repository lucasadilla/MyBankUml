package bank;

import java.util.UUID;

public class ETransfer extends Transaction {
    private Recipient recipient;
    private String notificationMethod; // SMS or Email

    public ETransfer(Account sourceAccount, Recipient recipient, double amount, Customer customer, String notificationMethod) {
        super(UUID.randomUUID().toString(), customer, amount, sourceAccount, null);
        this.recipient = recipient;
        this.notificationMethod = notificationMethod;
    }

    public boolean validateRecipient() {
        if (recipient == null) {
            return false;
        }
        return recipient.validateContact();
    }

    public boolean validateBalance() {
        if (sourceAccount == null) {
            return false;
        }
        return sourceAccount.canDebit(transactionAmount);
    }

    @Override
    public Receipt execute() {
        try {
            // Validate recipient
            if (!validateRecipient()) {
                transactionFailed();
                throw new IllegalArgumentException("Invalid recipient information");
            }

            // Validate balance
            if (!validateBalance()) {
                transactionFailed();
                throw new IllegalStateException("Insufficient funds or limit exceeded");
            }

            // Validate ownership
            if (!sourceAccount.getAccountOwner().equals(initiatedBy)) {
                transactionFailed();
                throw new IllegalArgumentException("Source account does not belong to customer");
            }

            // Execute transfer (debit from source, recipient account would be credited in real system)
            sourceAccount.debit(transactionAmount);
            // In a real system, we would credit the recipient's account here
            // For now, we'll just debit the source

            transactionCompleted();

            // Generate receipt
            Receipt receipt = new Receipt(this, sourceAccount, null, transactionAmount);
            receipt.setRecipient(recipient);
            receipt.setNotificationMethod(notificationMethod);
            receipt.generateReceipt();
            return receipt;

        } catch (Exception e) {
            transactionFailed();
            throw new RuntimeException("E-Transfer failed: " + e.getMessage(), e);
        }
    }
}

