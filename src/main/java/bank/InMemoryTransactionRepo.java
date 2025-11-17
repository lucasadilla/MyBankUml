package bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

//transaction repo in memory implementation version,
//to test my code by in memory implementation, without needing to use a database since it's not implemented yet
//stores transactions in a HashMap keyed by accountId.

public class InMemoryTransactionRepo implements TransactionRepo {

    private final Map<String, Transaction> transactionsById = new HashMap<>();

    @Override
    public Transaction findByID(String ID) {
    	
        if (ID == null) {
            return null;
        }
        
        return transactionsById.get(ID);
    }
    
    @Override
    public void save(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction should not be null");
        }
        
        transactionsById.put(transaction.getId(), transaction);
    }

    @Override
    public List<Transaction> findByAccount(Account account) {
    	
        List<Transaction> result = new ArrayList<>();
        
        if (account == null) {
            return result;
        }

        for (Transaction tx : transactionsById.values()) {
            if (account.equals(tx.getSourceAccount()) ||account.equals(tx.getDestinationAccount())){
                result.add(tx);
            }
        }
        return result;
    }

    //helper method to list all transactions in repo, can be used for testing
    public List<Transaction> findAll() {
        return new ArrayList<>(transactionsById.values());
    }
    
    //returns all transactions initiated by the customer from, to date
    //used for daily e-transfer limit checks, technical issue #3
    @Override
    public List<Transaction> findByCustomerAndDateRange(Customer customer,LocalDateTime from,LocalDateTime to) {
    	
        List<Transaction> result = new ArrayList<>();
        
        if (customer == null || from ==null || to ==null) {
            return result;
        }

        //iterate through all stored transactions
        for (Transaction tx : transactionsById.values()) {
        	
        	//check if the transaction belongs to the customer
            if (!customer.equals(tx.getInitiatedBy())) {
                continue;
            }
            
            //check if the timestamp is within the date range
            LocalDateTime t = tx.getCreatedAt();
            if (t != null && !t.isBefore(from)&& !t.isAfter(to)) {
                result.add(tx);
            }
        }
        return result;
    }

}
