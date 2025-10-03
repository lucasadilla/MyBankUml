package bank;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Account {
    protected Customer customer;
    protected List<Transaction> transactions;

    public Account(Customer customer) {
        this.customer = customer;
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public abstract void pay();
    public abstract void receipt();
}

