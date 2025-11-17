package bank;

import java.math.BigDecimal;

public class TransferFunds {

	//repos for accounts and transactions
	private final AccountRepo accountRepo;
	private final TransactionRepo transactionRepo;
	
	//constructor with required repos
	public TransferFunds(AccountRepo accountRepo,TransactionRepo transactionRepo) {
		
		if (accountRepo ==null) {
			throw new IllegalArgumentException("account repository should exist");
		}
		
		if (transactionRepo == null) {
			throw new IllegalArgumentException("transaction repository should exist");
		}
		
		this.accountRepo = accountRepo;
		this.transactionRepo = transactionRepo;
	}
	
	//transfer funds method between two accounts that are owned by the customer, has all kinds of exception throwing ifs to prevent invalid transfer of funds
	public Transaction transferFunds(Customer customer, String sourceAccountID, String destinationAccountID, BigDecimal amount, String description) {
		
		if(customer == null) {
			throw new IllegalArgumentException("Customer should not be null");
		}
		
		if(sourceAccountID == null || sourceAccountID.isBlank()) {
			throw new IllegalArgumentException("Source account ID must exist");
		}
		
		if(destinationAccountID== null || destinationAccountID.isBlank()) {
			throw new IllegalArgumentException("Destination account ID must exist");
		}
		
		if(sourceAccountID.equals(destinationAccountID)) {
			throw new IllegalArgumentException("Source and destination accounts cannot be the same");
		}
		
		if(amount == null || amount.compareTo(BigDecimal.ZERO)<=0) {
			throw new IllegalArgumentException("Transfer amount must be positive and not zero");
		}
		
		//load accounts from repos
		Account source = accountRepo.findByID(sourceAccountID);
		Account destination = accountRepo.findByID(destinationAccountID);
		
		//transaction description, also used for receipt, will describe what transaction it's doing and what accounts are doing it
		if (description == null || description.isBlank()){
	        description = "Transfer funds from "+ source.getAccountType() +" to "+ destination.getAccountType();
	    }
		
		if(source == null) {
			throw new IllegalArgumentException("Source account not found: " + sourceAccountID);
		}
		
		if(destination == null) {
			throw new IllegalArgumentException("Destination account not found: " + destinationAccountID);
		}
		
		if(!source.getCustomer().equals(customer)) {
			throw new IllegalArgumentException("Customer does not own the source account");
		}
		
		if(!destination.getCustomer().equals(customer)) {
			throw new IllegalArgumentException("Customer does not own the destination account");
		}
		
		if(!source.canDebit(amount)) {
			//recording failed attempt as a failed transaction for logging purposes
			Transaction failedTransaction = new Transaction(customer,source,destination,amount,description);
			failedTransaction.markFailed("Insufficient funds or account rules do not allow this debit");
			transactionRepo.save(failedTransaction);
			
			return failedTransaction;
		}
		
		//creating a new transaction in PENDING state
		Transaction transaction = new Transaction(customer,source,destination,amount,description);
		
		//debit and credit operations
		try {
			
			//can throw IllegalArgumentException due to savings withdrawal limits
			source.debit(amount);
			destination.credit(amount);
			
			//adding the transaction to both accounts' histories
			source.addTransaction(transaction);
			destination.addTransaction(transaction);
			
			//mark as complete then save everything to relevant repos
			transaction.markCompleted();
			
			accountRepo.save(source);
			accountRepo.save(destination);
			transactionRepo.save(transaction);
		}
		
		catch (IllegalArgumentException e) {
			//This is for if anything fails, mark transaction as FAILED status then save the transaction for logging purposes
			transaction.markFailed(e.getMessage());
			transactionRepo.save(transaction);
		}
		
		return transaction;		
	}
	
}
