package bank;

import lombok.Getter;

@Getter
public class Branch {
    private final String address;
    private final Bank bank;

    public Branch(String address, Bank bank) {
        this.address = address;
        this.bank = bank;
        // to add to the bank
        bank.addBranch(this);
    }

    public void printBranchInfo() {
        System.out.println("Branch " + address + " From Bank " + bank.getName());
    }
}
