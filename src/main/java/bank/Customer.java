package bank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Customer {

    private String name;

    // Constructor
    public Customer(String name) {
        this.name = name;
    }

    // Display customers info
    public void printCustomerInfo() {
        System.out.println("Customer's info: " );
        System.out.println("name: "+ name);
    }
}

