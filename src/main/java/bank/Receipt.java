package bank;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Receipt {
	
	//unique reference number for the receipt
	private final String referenceNumber;
	
	private final String transactionID;
	
	//customer who initiated the transaction
	private final Customer initiatedBy;

	private final String sourceAccountID;
	private final String destinationAccountID;
	
	//amount of money moved in the transaction
	private final BigDecimal amount;
	
	//time stamp of when the receipt was issued
	private final LocalDateTime issuedAt;
	
	//transaction activity description (ex: transfer funds between own accounts)
	private final String description;
	
	//boolean for whether the transaction completed successfully
	private final boolean successful;
	
	//failure reason if transaction failed, or empty if not
	private final String failureReason;
	
	//private constructor for immutability, shouldn't be able to change receipt info at any time by constructor
	private Receipt(String referenceNumber, String transactionID, Customer initiatedBy, String description, String sourceAccountID, 
			String destinationAccountID, BigDecimal amount, LocalDateTime issuedAt, boolean successful, String failureReason) {
		
		this.referenceNumber = referenceNumber;
		this.transactionID = transactionID;
		this.initiatedBy = initiatedBy;
		this.description = description;
		this.sourceAccountID = sourceAccountID;
		this.destinationAccountID = destinationAccountID;
		this.amount = amount;
		this.issuedAt = issuedAt;
		this.successful = successful;
		this.failureReason =(failureReason !=null) ? failureReason : "";
	}
	
	//method to produce a receipt from a transaction and accounts involved
	public static Receipt fromTransaction(Transaction transaction) {
		
		if (transaction == null) {
			throw new IllegalArgumentException("Transaction must not be null");
		}
		
		//generate a unique reference number for the receipt
		String referenceNumber = UUID.randomUUID().toString();
		
		String transactionID = transaction.getId();
		
		Customer initiatedBy = transaction.getInitiatedBy();
		String description = transaction.getDescription();
		
		Account source = transaction.getSourceAccount();
		Account destination = transaction.getDestinationAccount();
		
		//source or destination account ID can be null
		String sourceAccountID = (source !=null) ? source.getAccountID() : null;
		String destinationAccountID = (destination !=null) ? destination.getAccountID() : null;
	
		BigDecimal amount = transaction.getAmount();
		
		LocalDateTime issuedAt = LocalDateTime.now();
		
		boolean successful = transaction.getStatus() == Transaction.Status.COMPLETED;
		
		String failureReason = transaction.getFailureReason();
		
		//receipt contains these info
		return new Receipt(
	            referenceNumber, transactionID, initiatedBy,description, 
	            sourceAccountID, destinationAccountID,amount,issuedAt,successful,failureReason
	        );
	}

	@Override
	public String toString() {
	    return "\n=== Receipt ===\n" +
	            "Receipt ref     : " + referenceNumber + "\n" +
	            "Transaction ID  : " + transactionID + "\n" +
	            "Initiated by    : " + initiatedBy.getName() + "\n" +
	            "Activity        : " + description + "\n" +
	            "From account    : " + (sourceAccountID != null ? sourceAccountID : "N/A") + "\n" +
	            "To account      : " + (destinationAccountID != null ? destinationAccountID :"N/A") + "\n" +
	            "Amount          : " + amount + "\n" +
	            "Issued at       : " + issuedAt + "\n" +
	            "Success?        : " + successful + "\n" +
	            "Failure reason  : " + (failureReason.isBlank() ? "None" :failureReason);
	}

	
}
