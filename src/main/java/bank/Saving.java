package bank;

import java.math.BigDecimal;

public class Saving extends Account {

	private final BigDecimal monthlyInterestRate;
	
	//fixed monthly interest rate of 0.1% per month for savings account
	private static final BigDecimal MONTHLY_INTEREST_RATE = new BigDecimal ("0.001");
	
	//current number of withdrawals done this month
	private int monthlyWithdrawalCount;
	
	private final int monthlyWithdrawalLimit;
	
	//maximum number of withdrawals allowed per month for savings account will be 5
	private static final int MONTHLY_WITHDRAWAL_LIMIT = 5;
	
	
	//new savings account for a customer
	public Saving(String accountID, Customer customer, BigDecimal openingBalance) {
		
		super(accountID, customer, openingBalance);
		
		this.monthlyInterestRate = MONTHLY_INTEREST_RATE;
		this.monthlyWithdrawalLimit = MONTHLY_WITHDRAWAL_LIMIT;
		this.monthlyWithdrawalCount = 0;	
	}

	//returns the type of this account
	@Override
	public String getAccountType() {
		return "SAVINGS";
	}	

	//checks if allowed to debit this amount from the savings account
	//amount must be positive and not zero, monthlyWithdrawalCount < monthlyWithdrawalLimit (5), and sufficient balance
	@Override
	public boolean canDebit(BigDecimal amount) {
		
		//validates amount (not zero and > 0)
    	validateAmount(amount);
    	
    	//checks if monthly withdrawal limit has been hit
    	if (monthlyWithdrawalCount >= monthlyWithdrawalLimit) {
    		return false;
    	}
    	
    	//checks for balance >= amount, so that balance doesn't become negative, which shouldn't happen
    	return super.canDebit(amount);
    }
 
	//debits amount from this savings account, withdrawal limit respected
	@Override
	public void debit(BigDecimal amount) {
		
		//checks if amount can be debited
		if (!canDebit(amount)) {
			throw new IllegalArgumentException("Cannot debit from this savings account. Either insufficient funds or monthly withdrawl limt has been reached.");
		}

		//using parent (Account)'s debit logic
		super.debit(amount);
		
		//Increase monthly withdrawal count by 1
		monthlyWithdrawalCount++;
	}
	
	//apply one month of interest (0.1% fixed) to the current account balance, should be called once per month
	public void applyMonthlyInterest() {
		
		//calculating interest, which is balance * monthly interest rate (0.1%)
		BigDecimal interest = getBalance().multiply(MONTHLY_INTEREST_RATE);
		
		//add interest to the account
		credit(interest);
	}

	//resets the monthly withdrawal count, should be called once per month 
	public void resetMonthlyWithdrawalCount() {
		this.monthlyWithdrawalCount=0;
	}
	
	//getters and setters
	
	public int getMonthlyWithdrawalCount() {
		return monthlyWithdrawalCount;
	}

	public void setMonthlyWithdrawalCount(int monthlyWithdrawalCount) {
		this.monthlyWithdrawalCount = monthlyWithdrawalCount;
	}

	public BigDecimal getMonthlyInterestRate() {
		return monthlyInterestRate;
	}

	public int getMonthlyWithdrawalLimit() {
		return monthlyWithdrawalLimit;
	}
	
	
    
	
	
}
