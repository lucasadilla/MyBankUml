package bank;

import java.util.List;

public interface ETransferRepo {
	
	ETransferData findByID(String id);
	
	void save (ETransferData eTransferData);
	
	//returns all eTransfers that the customer did
	List<ETransferData> findByCustomer(Customer customer);
	
	//returns all eTransfers for a recipient
	List<ETransferData> findByRecipient(Recipient recipient);
	
	
}
