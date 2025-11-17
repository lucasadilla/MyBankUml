package bank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

//for sending eTransfer from customer's account to one of their saved recipients
public class ETransfer {
	
	private final AccountRepo accountRepo;
	private final RecipientRepo recipientRepo;
	private final TransactionRepo transactionRepo;
	private final ETransferRepo eTransferRepo;
	
	//these two below are for technical issue #3
	//maximum amount for a single e-transfer (5000$)
	private static final BigDecimal MAX_ETRANSFER_PER_TRANSACTION = new BigDecimal("5000.00");
	
	//maximum daily amount a customer can send via e-transfer
	private static final BigDecimal DAILY_ETRANSFER_LIMIT = new BigDecimal("10000.00");
	
	public ETransfer(AccountRepo accountRepo, RecipientRepo recipientRepo,TransactionRepo transactionRepo, ETransferRepo eTransferRepo) {
		
		if (accountRepo ==null) {
			throw new IllegalArgumentException("Account repository should exist");
		}
		
		if (recipientRepo ==null) {
			throw new IllegalArgumentException("Recipient repository should exist");
		}
		
		if (transactionRepo ==null) {
			throw new IllegalArgumentException("Transaction repository should exist");
		}
		
		if (eTransferRepo ==null) {
			throw new IllegalArgumentException("ETransfer repository should exist");
		}
		
		this.accountRepo = accountRepo;
		this.recipientRepo= recipientRepo;
		this.transactionRepo = transactionRepo;
		this.eTransferRepo = eTransferRepo;
	}
	
	//method to send eTransfer, requires all except message (for recipient), which is optional
	public ETransferData sendETransfer (Customer customer, String sourceAccountID, String recipientID, BigDecimal amount, String message ) {
		
		//error checking, validation step below
		
		if(customer == null) {
			throw new IllegalArgumentException("Customer should exist");
		}
		
		if(sourceAccountID == null || sourceAccountID.isBlank()) {
			throw new IllegalArgumentException("Source account should exist");
		}
		
		if(recipientID == null || recipientID.isBlank()) {
			throw new IllegalArgumentException("Recipient should exist");
		}
		
		if(amount == null || amount.compareTo(BigDecimal.ZERO) <=0) {
			throw new IllegalArgumentException("Amount has to be positive and not zero");
		}
		
		enforceETransferAmountLimits(customer, amount);
		
		// loading account and recipient 
		
		Account source = accountRepo.findByID(sourceAccountID);
		if(source==null) {
			throw new IllegalArgumentException("Source account not found: " + sourceAccountID);
		}
		
		Recipient recipient = recipientRepo.findByID(recipientID);
		if (recipient ==null) {
			throw new IllegalArgumentException("Recipient not found: " + sourceAccountID);
		}
		
		//ownership validation, similar to integration test 2
		
		if(!source.getCustomer().equals(customer)) {
			throw new IllegalArgumentException("Customer does not own the source account");
		}
		
		if(!recipient.isOwnedBy(customer)) {
			throw new IllegalArgumentException("Customer does not have recipient saved");
		}
		
		//checking that the source account can be debited
		
		if(!source.canDebit(amount)) {
			
			//if can't debit, record the attempt as a failed e-transfer and a failed transaction 
			Transaction failedTransaction = new Transaction(customer, source, null, amount, "E-Transfer to " + recipient.getName());
			failedTransaction.markFailed("Insufficient funds or account rules do not allow this debit");
			transactionRepo.save(failedTransaction);
			
			ETransferData failedETransfer = new ETransferData(failedTransaction, recipient, message);
			failedETransfer.markFailed("Insufficient funds or account rules do not allow this debit");
			
			return failedETransfer;
		}
		
		//create a transaction in PENDING state, the third is null since it's for destination account, but since it's to a recipient, it's null
		
		Transaction transaction = new Transaction(customer, source, null, amount, "E-Transfer to " + recipient.getName());
		
		//ETransfer object for adding recipient to transaction
		ETransferData eTransfer = new ETransferData(transaction, recipient,message);
		
		//perform the send e-transfer method
		try {
			
			//this can throw IllegalArgumentException due to savings account monthly limits
			source.debit(amount);
			
			//add transaction to account's history
			source.addTransaction(transaction);
			
			//e-transfer success!
			transaction.markCompleted();
			eTransfer.markSent();
			
			//save updated account, transaction and e-transfer to their repos
			accountRepo.save(source);
			transactionRepo.save(transaction);
			eTransferRepo.save(eTransfer);
		}
		
		catch(IllegalArgumentException e) {
			
			transaction.markFailed(e.getMessage());
			transactionRepo.save(transaction);
			eTransfer.markFailed(e.getMessage());
			eTransferRepo.save(eTransfer);
			
		}
		
		return eTransfer;
		
	}
	
	//Returns how much remaining e-transfer amount the customer has today, 
	//GUI can call this to display that info as per technical issue #3 recommendation

    public BigDecimal getRemainingDailyETransferLimit(Customer customer) {
    	
        if (customer == null) {
            throw new IllegalArgumentException("Customer must not be null");
        }

        BigDecimal usedToday = calculateUsedETransferAmountToday(customer);
        BigDecimal remaining = DAILY_ETRANSFER_LIMIT.subtract(usedToday);

        return (remaining.compareTo(BigDecimal.ZERO) >0) ? remaining: BigDecimal.ZERO;
    }

    //helper methods for e-transfer limits

    private void enforceETransferAmountLimits(Customer customer, BigDecimal amount) {
    	
        //per e-transfer limit
        if (amount.compareTo(MAX_ETRANSFER_PER_TRANSACTION) > 0) {
            throw new IllegalArgumentException("Amount exceeds the maximum allowed per e-transfer (" +
                MAX_ETRANSFER_PER_TRANSACTION + ").");

        }

        //daily total limit
        BigDecimal remainingToday = getRemainingDailyETransferLimit(customer);
        if (amount.compareTo(remainingToday)> 0) {
            throw new IllegalArgumentException("Amount exceeds the remaining daily e-transfer limit. " +
                "Remaining today: " + remainingToday);
        }
    }

    
    //calculates how much the customer has already sent today via e-transfer
    private BigDecimal calculateUsedETransferAmountToday(Customer customer) {
    	
    	//today's time range, from 0:00:00 to 23:59:59
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = today.atTime(LocalTime.MAX);

        //fetch all transactions for the Customer today
        List<Transaction> todaysTx = transactionRepo.findByCustomerAndDateRange(customer, from, to);

        BigDecimal total = BigDecimal.ZERO;

        for (Transaction tx : todaysTx) {
        	
        	//only count completed transactions, not failed ones
            if (tx.getStatus() != Transaction.Status.COMPLETED) {
                continue;
            }

            Account src = tx.getSourceAccount();
            Account dest = tx.getDestinationAccount();

            //only count e-transfers sent by putting destination account as null (e-transfer characteristic)
            if (src != null && dest ==null && customer.equals(src.getCustomer())) {
            	
                total = total.add(tx.getAmount());
            }
        }

        return total;
    }
	
}
