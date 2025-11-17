package bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//recipient repo in memory implementation version,
//to test my code by in memory implementation, without needing to use a database since it's not implemented yet
//stores recipients in a HashMap keyed by accountId.

public class InMemoryRecipientRepo implements RecipientRepo {

    private final Map<String, Recipient> recipientsById = new HashMap<>();

    @Override
    public void save(Recipient recipient) {
        if (recipient == null) {
            throw new IllegalArgumentException("recipient must not be null");
        }
        recipientsById.put(recipient.getID(), recipient);
    }

    @Override
    public Recipient findByID(String id) {
        if (id == null) {
            return null;
        }
        return recipientsById.get(id);
    }

    @Override
    public List<Recipient> findByOwner(Customer owner) {
        List<Recipient> result = new ArrayList<>();
        if (owner == null) {
            return result;
        }

        for (Recipient recipient : recipientsById.values()) {
            if (recipient.isOwnedBy(owner)) {
                result.add(recipient);
            }
        }
        return result;
    }

    @Override
    public void delete(Recipient recipient) {
        if (recipient == null) {
            return;
        }
        recipientsById.remove(recipient.getID());
    }

    
    //helper method to list all recipients in repo, can be used for testing
    public List<Recipient> findAll() {
        return new ArrayList<>(recipientsById.values());
    }
}
