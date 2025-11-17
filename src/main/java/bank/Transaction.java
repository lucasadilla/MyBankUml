package bank;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Transaction {
    
	public enum Status{
		PENDING, 
		COMPLETED, 
		FAILED
	}
	
	//unique transaction id
	private final String id;
	
	//the customer who initiated this transaction
	private final Customer initiatedBy;
	
	private final Account sourceAccount;
	private final Account destinationAccount;
	
	//amount of money being moved, should be positive and not zero
	private final BigDecimal amount;
	
	//description of transaction
	private final String description;
	
	//time stamp for when the transaction happened
	private final LocalDateTime createdAt;
	
	//current status of the transaction
	private Status status;
	
	//explanation when transaction fails
	private String failureReason;
	
	//new transaction in PENDING state, will change to COMPLETED or FAILED after validation
	public Transaction (Customer initiatedBy, Account sourceAccount, Account destinationAccount, BigDecimal amount, String description) {
		
		if(initiatedBy == null) {
			throw new IllegalArgumentException("Customer who initiated the transaction must exist");
		}
		
		if(amount == null || amount.compareTo(BigDecimal.ZERO)<=0) {
			throw new IllegalArgumentException("Transaction amount must be positive and not zero");
		}
		
		if(sourceAccount == null &&destinationAccount ==null) {
			throw new IllegalArgumentException("At least one of the source account or destination account must be valid");
		}
	
		//used to give random unique string id, no need to check if same id exists cause it's an extremely low chance 
		this.id = UUID.randomUUID().toString();
		this.initiatedBy = initiatedBy;
		this.sourceAccount = sourceAccount;
		this.destinationAccount=destinationAccount;
		this.amount = amount;
		//if description is not null, description = description, if null = ""
		this.description = (description != null) ? description : "";
		this.createdAt = LocalDateTime.now();
		this.status = Status.PENDING;
		this.failureReason=null;
	}
	
	//mark transaction as completed, called after debit/credit operations succeed
	public void markCompleted() {
		this.status = Status.COMPLETED;
		this.failureReason = null;
	}
	
	//mark transaction as failed and stores the reason (ex: insufficient funds)
	public void markFailed(String reason) {
		this.status = Status.FAILED;
		this.failureReason = (reason != null) ? reason : "";
	}
	
	//to check if transaction was successful
	public boolean isSuccessful() {
		return this.status == Status.COMPLETED;
	}
	
}
