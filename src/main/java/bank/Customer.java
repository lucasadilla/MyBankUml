package bank;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Customer extends User {
    private String customerID;
    private List<Account> ownedAccounts;
    private List<Recipient> recipients;

    public Customer(String userID, String userPassword, String userName, String userEmail, String userPhone) {
        super(userID, userPassword, userName, userEmail, userPhone, "customer");
        this.customerID = userID;
        this.ownedAccounts = new ArrayList<>();
        this.recipients = new ArrayList<>();
    }

    public List<Account> viewAccounts() {
        return new ArrayList<>(ownedAccounts);
    }

    public Receipt transferFunds(Account sourceAccount, Account destinationAccount, double amount) {
        if (!ownedAccounts.contains(sourceAccount) || !ownedAccounts.contains(destinationAccount)) {
            throw new IllegalArgumentException("Both accounts must belong to this customer");
        }

        TransferFunds transfer = new TransferFunds(sourceAccount, destinationAccount, amount, this);
        return transfer.execute();
    }

    public Receipt sendEtransfer(Account sourceAccount, Recipient recipient, double amount, String notificationMethod) {
        if (!ownedAccounts.contains(sourceAccount)) {
            throw new IllegalArgumentException("Source account must belong to this customer");
        }

        ETransfer eTransfer = new ETransfer(sourceAccount, recipient, amount, this, notificationMethod);
        return eTransfer.execute();
    }

    public Statement generateStatement(List<Account> accounts, int year, int month) {
        Statement statement = new Statement(this, accounts, year, month);
        statement.generateStatement();
        return statement;
    }

    public LoanRequest loanRequest(double amount, String purpose, String proofOfIncome) {
        LoanRequest request = new LoanRequest(this, amount, purpose, proofOfIncome);
        return request;
    }

    public void addRecipient(Recipient recipient) {
        recipients.add(recipient);
    }

    public List<Recipient> getSavedRecipients() {
        return new ArrayList<>(recipients);
    }

    @Override
    public void displayDashboard() {
        System.out.println("Customer Dashboard for: " + userName);
        System.out.println("Accounts: " + ownedAccounts.size());
    }
}
