package bank;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

public class Saving extends Account {
    private String savingsAccountID;
    private double interestRate;
    private int monthlyWithdrawalCount;
    private static final int MAX_MONTHLY_WITHDRAWALS = 5;
    private static final double MAX_TRANSACTION_LIMIT = 5000.0;
    private Map<Month, Integer> withdrawalsByMonth;

    public Saving(String accountID, Customer owner, double initialBalance, double interestRate) {
        super(accountID, owner, initialBalance);
        this.savingsAccountID = accountID;
        this.interestRate = interestRate;
        this.monthlyWithdrawalCount = 0;
        this.withdrawalsByMonth = new HashMap<>();
    }

    @Override
    public boolean canDebit(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (amount > MAX_TRANSACTION_LIMIT) {
            return false;
        }
        if (accountBalance < amount) {
            return false;
        }

        // Check monthly withdrawal limit
        Month currentMonth = LocalDate.now().getMonth();
        int currentMonthWithdrawals = withdrawalsByMonth.getOrDefault(currentMonth, 0);
        if (currentMonthWithdrawals >= MAX_MONTHLY_WITHDRAWALS) {
            return false;
        }

        return true;
    }

    @Override
    public void debit(double amount) {
        if (canDebit(amount)) {
            super.debit(amount);
            Month currentMonth = LocalDate.now().getMonth();
            withdrawalsByMonth.put(currentMonth, withdrawalsByMonth.getOrDefault(currentMonth, 0) + 1);
            monthlyWithdrawalCount = withdrawalsByMonth.get(currentMonth);
        } else {
            throw new IllegalStateException("Cannot debit: insufficient funds, limit exceeded, or monthly withdrawal limit reached");
        }
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getMonthlyWithdrawalCount() {
        Month currentMonth = LocalDate.now().getMonth();
        return withdrawalsByMonth.getOrDefault(currentMonth, 0);
    }

    public void pay() {
        System.out.println("Payment from savings account for: " + accountOwner.getUserName());
    }

    public void receipt() {
        System.out.println("Payment receipt from savings account for: " + accountOwner.getUserName());
    }
}
