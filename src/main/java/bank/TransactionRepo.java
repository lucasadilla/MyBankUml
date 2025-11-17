package bank;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepo {
	
	Transaction findByID(String ID);
	
	void save(Transaction transaction);
	
	//return all transactions by an account
	List<Transaction> findByAccount(Account account);
	
    //returns all transactions initiated by the customer from, to date
    //used for daily e-transfer limit checks, technical issue #3
    List<Transaction> findByCustomerAndDateRange(Customer customer,LocalDateTime from,LocalDateTime to);
	
}
