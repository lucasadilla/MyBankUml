package bank;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Account {
    protected String accountID;
    protected Customer accountOwner;
    protected double accountBalance;
    protected String customerID;
    protected List<Transaction> transactions;

    public Account(String accountID, Customer owner, double initialBalance) {
        this.accountID = accountID;
        this.accountOwner = owner;
        this.accountBalance = initialBalance;
        this.customerID = owner.getCustomerID();
        this.transactions = new ArrayList<>();
        if (owner != null) {
            owner.getOwnedAccounts().add(this);
        }
    }

    public void credit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        this.accountBalance += amount;
    }

    public void debit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        if (!canDebit(amount)) {
            throw new IllegalStateException("Insufficient funds or limit exceeded");
        }
        this.accountBalance -= amount;
    }

    public double getBalance() {
        return accountBalance;
    }

    public abstract boolean canDebit(double amount);

    public void addTransaction(Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
        }
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
}
