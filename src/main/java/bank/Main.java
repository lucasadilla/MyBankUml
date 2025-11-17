package bank;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {

    	//the code below tests integration test case 1,2 and 3, and unit test case Transfers 1-3
    	
        //set up repositories (in-memory)
        AccountRepo accountRepo = new InMemoryAccountRepo();
        TransactionRepo transactionRepo = new InMemoryTransactionRepo();
        RecipientRepo recipientRepo = new InMemoryRecipientRepo();
        ETransferRepo eTransferRepo = new InMemoryETransferRepo();

        //create the transfer funds service
        TransferFunds transferService = new TransferFunds(accountRepo, transactionRepo);

        //create the e-transfer service
        ETransfer eTransferService = new ETransfer(accountRepo,recipientRepo, transactionRepo, eTransferRepo);
        
        //create a customer and two accounts for customer
        Customer john = new Customer("John");

        Account checking = new Check(
            "CHECKING-0001",
            john,
            //opening balance
            BigDecimal.valueOf(1000)
        );

        Account savings = new Saving(
            "SAVINGS-0001",
            john,
            //opening balance
            BigDecimal.valueOf(500)
        );

        //create recipient saved by john
        Recipient luke = new Recipient(john,"Luke", "luke@gmail.com", "514-000-0000");
        
        //save accounts in the repository so the service can find them
        accountRepo.save(checking);
        accountRepo.save(savings);

        //save recipient so the service can find him
        recipientRepo.save(luke);
            
        //integration test 1 output start
        System.out.println("--- Before transfer ---");
        System.out.println("Checking balance: " + checking.getBalance());
        System.out.println("Savings  balance: " + savings.getBalance());

        BigDecimal amountToTransfer = BigDecimal.valueOf(200);

        Transaction transacation = transferService.transferFunds(
            john,
            checking.getAccountID(),
            savings.getAccountID(),
            amountToTransfer,
            null
        );
        
        //generate a receipt from the transaction
        Receipt receipt = Receipt.fromTransaction(transacation);

        System.out.println("\n--- After transfer ---");
        System.out.println("Checking balance: " + checking.getBalance());
        System.out.println("Savings  balance: " + savings.getBalance());

        System.out.println("\n---Transaction result ---");
        System.out.println("Transaction ID: " + transacation.getId());
        System.out.println("Status       : " + transacation.getStatus());
        System.out.println("Failure reason (if any): " + transacation.getFailureReason());
        
        System.out.println(receipt);
        
        //integration test 2 start

        System.out.println("\n--- Before transfer ---");
        System.out.println("Checking balance: " + checking.getBalance());
        System.out.println("Savings  balance: " + savings.getBalance());
        
        Transaction transacation2 = transferService.transferFunds(
        		john,
                checking.getAccountID(),
                savings.getAccountID(),
                amountToTransfer,
                null
            );
        
        System.out.println("\n--- After transfer ---");
        System.out.println("Checking balance: " + checking.getBalance());
        System.out.println("Savings  balance: " + savings.getBalance());
        
        System.out.println("\n---Transaction result ---");
        System.out.println("Transaction ID: " + transacation2.getId());
        System.out.println("Status       : " + transacation2.getStatus());
        System.out.println("Failure reason (if any): " + transacation2.getFailureReason());
        
        Receipt receipt2 = Receipt.fromTransaction(transacation2);
        System.out.println(receipt2);

        //e-transfer testing now, integration test #3
        System.out.println();
        System.out.println("=== Before e-transfer ===");
        System.out.println("Checking balance: " + checking.getBalance());
        
        //send an e-transfer of 150 from John's checking account to Luke
        BigDecimal amountToSend = BigDecimal.valueOf(150);
        
        ETransferData eTransfer = eTransferService.sendETransfer(john, checking.getAccountID(), luke.getID(), 
        		amountToSend, "Ski trip payment");
        
        Transaction transaction2 = eTransfer.getTransaction();
        Receipt receiptE = Receipt.fromTransaction(transaction2);
        
        System.out.println("\n=== After e-transfer ===");
        System.out.println("Checking balance: " + checking.getBalance());
        BigDecimal remainingBefore = eTransferService.getRemainingDailyETransferLimit(john);
        System.out.println("Remaining daily e-transfer limit: " + remainingBefore);
        
        System.out.println("\n=== E-Transfer result ===");
        System.out.println("E-Transfer ID   : " + eTransfer.getId());
        System.out.println("Status          : " + eTransfer.getStatus());
        System.out.println("Failure reason  : " + eTransfer.getFailureReason());

        System.out.println("\n=== Underlying Transaction ===");
        System.out.println("Transaction ID  : " + transaction2.getId());
        System.out.println("Status          : " + transaction2.getStatus());
        System.out.println("Failure reason  : " + transaction2.getFailureReason());
        
        System.out.println(receiptE);
        
        
    }
}
