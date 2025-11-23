package bank;

public class Check extends Account {
    private String checkAccountID;

    public Check(String accountID, Customer owner, double initialBalance) {
        super(accountID, owner, initialBalance);
        this.checkAccountID = accountID;
    }

    @Override
    public boolean canDebit(double amount) {
        if (amount <= 0) {
            return false;
        }
        return accountBalance >= amount;
    }

    public void title() {
        System.out.println("**Check Title**");
    }

    public void pay() {
        title();
        System.out.println("Check payment for customer: " + accountOwner.getUserName());
    }

    public void receipt() {
        System.out.println("Check receipt for customer: " + accountOwner.getUserName());
    }
}
