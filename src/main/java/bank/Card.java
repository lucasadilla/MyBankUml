package bank;

public class Card extends Account {
    private String cardAccountID;
    private static final double MAX_TRANSACTION_LIMIT = 5000.0; // Daily limit

    public Card(String accountID, Customer owner, double initialBalance) {
        super(accountID, owner, initialBalance);
        this.cardAccountID = accountID;
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
        System.out.println("Card payment for: " + accountOwner.getUserName());
    }

    public void receipt() {
        System.out.println("Card receipt for: " + accountOwner.getUserName());
    }
}
