package bank;

public class Card extends Account {
    public Card(Customer customer) {
        super(customer);
    }

    @Override
    public void pay() {
        System.out.println("Card payment for: " + customer.getName());
    }

    @Override
    public void receipt() {
        System.out.println("Card receipt for: " + customer.getName());
    }
}

