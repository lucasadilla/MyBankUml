package bank;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Bank {
    private String bankID;
    private String bankName;
    private String address;
    private String contactInfo;
    private List<Branch> branches;

    public Bank(String bankID, String bankName, String address, String contactInfo) {
        this.bankID = bankID;
        this.bankName = bankName;
        this.address = address;
        this.contactInfo = contactInfo;
        this.branches = new ArrayList<>();
    }

    public Bank(String name) {
        this.bankID = "BANK_" + System.currentTimeMillis();
        this.bankName = name;
        this.address = "";
        this.contactInfo = "";
        this.branches = new ArrayList<>();
    }

    public void addBranch(Branch branch) {
        if (branch != null && !branches.contains(branch)) {
            branches.add(branch);
        }
    }

    public List<Branch> getBranches() {
        return new ArrayList<>(branches);
    }

    public Branch findBranch(String branchID) {
        return branches.stream()
            .filter(branch -> branch.getBranchID().equals(branchID))
            .findFirst()
            .orElse(null);
    }

    public String getBankInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Bank ID: ").append(bankID).append("\n");
        info.append("Bank Name: ").append(bankName).append("\n");
        info.append("Address: ").append(address).append("\n");
        info.append("Contact: ").append(contactInfo).append("\n");
        info.append("Number of Branches: ").append(branches.size()).append("\n");
        return info.toString();
    }

    public void printBankInfo() {
        System.out.println("Bank: " + bankName);
        for (Branch branch : branches) {
            System.out.println("Branch: " + branch.getAddress());
        }
    }
}
