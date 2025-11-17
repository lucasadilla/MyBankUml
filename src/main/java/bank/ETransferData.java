package bank;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

//similar to and uses transaction for money moving out of source account, 
//but adds recipient information (instead of destination account) and its own status
@Getter
public class ETransferData {
	
	//SENT is new for eTransfer, in transaction it's COMPLETED
	public enum Status {
		PENDING, SENT, FAILED
	}
	
	private final String id;
	private final Transaction transaction;
	private final Recipient recipient;
	
	//message if customer wants to write something to recipient (optional)
	private final String message;
	
	//when eTransfer was created
	private final LocalDateTime createdAt;
	
	//current status of the eTransfer
	private Status status;
	
	//if sending eTransfer fails, explains reason
	private String failureReason;
	
	//would be in PENDING state when created
	public ETransferData(Transaction transaction, Recipient recipient, String message) {
		
		if(transaction ==null) {
			throw new IllegalArgumentException("Source account should not be empty");
		}
		
		if(recipient ==null) {
			throw new IllegalArgumentException("Recipient should not be empty");
		}
		
		this.id= UUID.randomUUID().toString();
		this.transaction= transaction;
		this.recipient = recipient;
		this.message = (message !=null) ? message : "";
		this.createdAt = LocalDateTime.now();
		this.status = Status.PENDING;
		this.failureReason = null;
	}
	
	public void markSent() {
		this.status = Status.SENT;
		this.failureReason= null;
	}
	
	public void markFailed(String reason) {
		this.status= Status.FAILED;
		this.failureReason= (reason !=null) ? reason : "";
	}
	
	//to check whether e-transfer is sent successfully
	public boolean isSent() {
		return this.status == Status.SENT && this.transaction.getStatus()== Transaction.Status.COMPLETED;
	}
	
	//for accessing the amount being transferred
	public BigDecimal getAmount() {
		return transaction.getAmount();
	}
	
}
