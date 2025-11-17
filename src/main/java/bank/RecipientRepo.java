package bank;

import java.util.List;

public interface RecipientRepo {

	Recipient findByID(String id);
	
	void save (Recipient recipient);
	
	//returns all recipients that the customer saved
	List<Recipient> findByOwner(Customer owner);
	
	void delete(Recipient recipient);
	
}
