package bank;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class Statement {
    private String statementID;
    private Customer customer;
    private List<Account> accounts;
    private List<Transaction> transactions;
    private double startBalance;
    private double endBalance;
    private Bank bank;
    private Branch branch;
    private int year;
    private int month;
    private LocalDateTime dateIssued;

    public Statement(Customer customer, List<Account> accounts, int year, int month) {
        this.statementID = UUID.randomUUID().toString();
        this.customer = customer;
        this.accounts = new ArrayList<>(accounts);
        this.transactions = new ArrayList<>();
        this.year = year;
        this.month = month;
        this.dateIssued = LocalDateTime.now();
    }

    public void generateStatement() {
        // Calculate start and end balances
        startBalance = accounts.stream()
            .mapToDouble(Account::getBalance)
            .sum();

        // Collect all transactions for the period
        transactions = accounts.stream()
            .flatMap(account -> account.getTransactions().stream())
            .filter(transaction -> {
                LocalDateTime transDate = transaction.getInitiatedAt();
                return transDate.getYear() == year && transDate.getMonthValue() == month;
            })
            .collect(Collectors.toList());

        // Calculate end balance (simplified - in real system would account for all transactions)
        endBalance = accounts.stream()
            .mapToDouble(Account::getBalance)
            .sum();
    }

    public String getStatementDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Statement ID: ").append(statementID).append("\n");
        details.append("Customer: ").append(customer.getUserName()).append("\n");
        details.append("Period: ").append(month).append("/").append(year).append("\n");
        if (bank != null) {
            details.append("Bank: ").append(bank.getBankName()).append("\n");
        }
        if (branch != null) {
            details.append("Branch: ").append(branch.getBranchName()).append("\n");
        }
        details.append("Start Balance: $").append(String.format("%.2f", startBalance)).append("\n");
        details.append("End Balance: $").append(String.format("%.2f", endBalance)).append("\n");
        details.append("Number of Transactions: ").append(transactions.size()).append("\n");
        details.append("Accounts: ").append(accounts.size()).append("\n");
        return details.toString();
    }

    public void storeStatement(Database database) {
        if (database != null) {
            database.saveStatement(this);
        }
    }
}

