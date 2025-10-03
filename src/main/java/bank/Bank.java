package bank;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Bank {
    private final String name;
    private final List<Branch> branches;

    public Bank(String name) {
        this.name = name;
        this.branches = new ArrayList<>();
    }

    public void addBranch(Branch branch) {
        branches.add(branch);
    }

    public void printBankInfo() {
        System.out.println("Bank: " + name);
        for (Branch branch : branches) {
            System.out.println("Branch: " + branch.getAddress());
        }
    }
}
