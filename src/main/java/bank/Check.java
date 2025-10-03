package bank;

public class Check extends Account {
    public Check(Customer customer) {
        super(customer);

    }
    public void title(){
        System.out.println("**Check Title**");
    }

    @Override
    public void pay() {
        //check title
        title();
        System.out.println("Check payment for customer: " + customer.getName());
    }

    @Override
    public void receipt() {
        System.out.println("Check receipt for customer: " + customer.getName());    
    }
}

