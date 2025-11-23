package bank;

import java.util.UUID;

public class TransferFunds extends Transaction {

    public TransferFunds(Account sourceAccount, Account destinationAccount, double amount, Customer customer) {
        super(UUID.randomUUID().toString(), customer, amount, sourceAccount, destinationAccount);
    }

    @Override
    public Receipt execute() {
        try {
            // Validate ownership
            if (!sourceAccount.getAccountOwner().equals(initiatedBy)) {
                throw new IllegalArgumentException("Source account does not belong to customer");
            }
            if (!destinationAccount.getAccountOwner().equals(initiatedBy)) {
                throw new IllegalArgumentException("Destination account does not belong to customer");
            }

            // Validate sufficient funds
            if (!sourceAccount.canDebit(transactionAmount)) {
                transactionFailed();
                throw new IllegalStateException("Insufficient funds or limit exceeded");
            }

            // Execute transfer
            sourceAccount.debit(transactionAmount);
            destinationAccount.credit(transactionAmount);

            transactionCompleted();

            // Generate receipt
            Receipt receipt = new Receipt(this, sourceAccount, destinationAccount, transactionAmount);
            receipt.generateReceipt();
            return receipt;

        } catch (Exception e) {
            transactionFailed();
            throw new RuntimeException("Transfer failed: " + e.getMessage(), e);
        }
    }
}

