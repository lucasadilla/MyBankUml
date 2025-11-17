package bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//account repo in memory implementation version,
//to test my code by in memory implementation, without needing to use a database since it's not implemented yet
//stores accounts in a HashMap keyed by accountId.
 
public class InMemoryAccountRepo implements AccountRepo {

    private final Map<String,Account> accountsById = new HashMap<>();

    @Override
    public Account findByID(String accountID) {
        if (accountID ==null) {
            return null;
       }
        return accountsById.get(accountID);
    }

    @Override
    public void save(Account account) {
    	
        if(account == null) {
            throw new IllegalArgumentException("Account should not be null");
        }
        
        accountsById.put(account.getAccountID(), account);
    }

    @Override
    public List<Account> findByCustomer(Customer customer) {
    	
        List <Account> result = new ArrayList<>();
        
        if (customer == null) {
            return result;
        }

        for (Account account : accountsById.values()) {
            if (customer.equals(account.getCustomer())) {
                result.add(account);
            }
        }
        return result;
    }

    //helper method to list all accounts in repo, can be used for testing
    public List<Account> findAll() {
        return new ArrayList<>(accountsById.values());
    }
}
