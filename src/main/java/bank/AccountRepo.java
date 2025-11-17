package bank;

import java.util.List;

public interface AccountRepo {

	Account findByID(String accountID);
	
	void save(Account account);
	
	//return all accounts that belong to a customer
	List<Account>findByCustomer(Customer customer);
	
}
