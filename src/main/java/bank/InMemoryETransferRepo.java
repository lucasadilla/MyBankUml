package bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//ETransfer repo in memory implementation version,
//to test my code by in memory implementation, without needing to use a database since it's not implemented yet
//stores e-transfers in a HashMap keyed by accountId.

public class InMemoryETransferRepo implements ETransferRepo{

    private final Map<String, ETransferData> eTransfersByID = new HashMap<>();

    @Override
    public void save(ETransferData eTransfer) {
        if (eTransfer == null) {
            throw new IllegalArgumentException("eTransfer must not be null");
        }
        eTransfersByID.put(eTransfer.getId(), eTransfer);
    }

    @Override
    public ETransferData findByID(String id) {
        if (id == null) {
            return null;
        }
        return eTransfersByID.get(id);
    }

    @Override
    public List<ETransferData> findByCustomer(Customer customer) {
        List<ETransferData> result = new ArrayList<>();
        if (customer == null) {
            return result;
        }

        for (ETransferData eTransfer : eTransfersByID.values()) {
            //The customer who initiated the e-transfer is the initiatedBy
            if (customer.equals(eTransfer.getTransaction().getInitiatedBy())) {
                result.add(eTransfer);
            }
        }
        return result;
    }

    @Override
    public List<ETransferData> findByRecipient(Recipient recipient) {
        List<ETransferData> result = new ArrayList<>();
        if (recipient == null) {
            return result;
        }

        for (ETransferData eTransfer : eTransfersByID.values()) {
            if (recipient.equals(eTransfer.getRecipient())) {
                result.add(eTransfer);
            }
        }
        return result;
    }

 
    //helper method to list all e-transfer in repo, can be used for testing
    public List<ETransferData> findAll() {
        return new ArrayList<>(eTransfersByID.values());
    }
}
