package bank;

public class Saving extends Account {
    public Saving(Customer customer) {
        super(customer);
    }
    public void title(){
        System.out.println("**Payments**");
    };

    @Override
    public void pay() {
        title();
        System.out.println("Payment From saving account For: " + customer.getName());
    }

    @Override
    public void receipt() {
        System.out.println("Payment receipt from saving account for: " + customer.getName());
    }
}
