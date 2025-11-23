package bank;

public class Checking extends Account {
    private String checkingAccountID;
    private static final double MAX_TRANSACTION_LIMIT = 10000.0; // Daily limit

    public Checking(String accountID, Customer owner, double initialBalance) {
        super(accountID, owner, initialBalance);
        this.checkingAccountID = accountID;
    }

    @Override
    public boolean canDebit(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (amount > MAX_TRANSACTION_LIMIT) {
            return false;
        }
        return accountBalance >= amount;
    }

    public void pay() {
        System.out.println("Checking account payment for: " + accountOwner.getUserName());
    }

    public void receipt() {
        System.out.println("Checking account receipt for: " + accountOwner.getUserName());
    }
}

