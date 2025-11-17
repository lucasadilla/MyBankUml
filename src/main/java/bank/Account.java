package bank;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public abstract class Account {
	
	//unique ID of the account
	private final String accountID;
	
	//the customer who owns this account
    private final Customer customer;
    
    //current balance of the account, BigDecimal needs to be used to avoid rounding errors with balance
    private BigDecimal balance;
    
    //opening date of the account
    private final LocalDateTime createdAt;
    
    //list of all transactions by this account
    protected final List<Transaction> transactions= new ArrayList<>();
    
    //base constructor used by subclasses (Checking, Savings), has exceptions for all invalid cases
    protected Account(String accountID, Customer customer, BigDecimal openingBalance) {
    	
    	if (accountID == null || accountID.isBlank()) {
    		throw new IllegalArgumentException("AccountID must not be null or blank");
    	}
    	if (customer == null) {
    		throw new IllegalArgumentException("Customer must not be null");
    	}
    	if (openingBalance == null || openingBalance.compareTo(BigDecimal.ZERO)<0) {
    		throw new IllegalArgumentException("Opening balance must not be null or negative");
    	}
    	
    	this.accountID=accountID;
    	this.customer=customer;
    	this.balance=openingBalance; 
    	this.createdAt =LocalDateTime.now();
    }
    
    //returns an unmodifiable (can only read) transaction list
    public List<Transaction> getTransactions(){
    	return Collections.unmodifiableList(transactions);
    }
    
    //helper method for subclasses or other methods here to append new transactions
    protected void addTransaction(Transaction transaction) {
    	if (transaction!=null) {
    		transactions.add(transaction);
    	}
    }
    
    //helper method to ensure the amount is not 0 or negative
    public void validateAmount(BigDecimal amount) {
    	if (amount == null || amount.compareTo(BigDecimal.ZERO)<=0){
    		throw new IllegalArgumentException("Amount must be positive and non zero.");
    	}
    }
    
    //adds amount to this account balance after basic validation
    public void credit(BigDecimal amount) {
    	validateAmount(amount);
    	balance = balance.add(amount);
    }
    
    //checks if this amount can be removed from the account, the account balance cannot be negative after debit
    public boolean canDebit(BigDecimal amount) {
    	validateAmount(amount);
    	return balance.compareTo(amount)>=0;
    }
    
    //subtracts amount from this account if balance is sufficient and account rules allow it
    public void debit(BigDecimal amount) {
    	validateAmount(amount);
    	if (!canDebit(amount)) {
    		throw new IllegalArgumentException("Insufficient funds or account rules do not allow this debit.");
    	}
    	balance = balance.subtract(amount);
    }
    
    //to get account type(checking, savings)
    public abstract String getAccountType();
    
}

