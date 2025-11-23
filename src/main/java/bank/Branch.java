package bank;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Branch {
    private String branchID;
    private String branchName;
    private String address;
    private String contactInfo;
    private Bank bank;
    private List<User> staff; // Bankers and BankManagers

    public Branch(String branchID, String branchName, String address, String contactInfo, Bank bank) {
        this.branchID = branchID;
        this.branchName = branchName;
        this.address = address;
        this.contactInfo = contactInfo;
        this.bank = bank;
        this.staff = new ArrayList<>();
        if (bank != null) {
            bank.addBranch(this);
        }
    }

    public Branch(String address, Bank bank) {
        this.branchID = "BRANCH_" + System.currentTimeMillis();
        this.branchName = address;
        this.address = address;
        this.contactInfo = "";
        this.bank = bank;
        this.staff = new ArrayList<>();
        if (bank != null) {
            bank.addBranch(this);
        }
    }

    public void assignStaff(User staffMember) {
        if (staffMember != null && !staff.contains(staffMember)) {
            staff.add(staffMember);
        }
    }

    public void removeStaff(User staffMember) {
        if (staffMember != null) {
            staff.remove(staffMember);
        }
    }

    public List<User> getStaff() {
        return new ArrayList<>(staff);
    }

    public String getBranchInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Branch ID: ").append(branchID).append("\n");
        info.append("Branch Name: ").append(branchName).append("\n");
        info.append("Address: ").append(address).append("\n");
        info.append("Contact: ").append(contactInfo).append("\n");
        info.append("Bank: ").append(bank != null ? bank.getBankName() : "N/A").append("\n");
        info.append("Staff Count: ").append(staff.size()).append("\n");
        return info.toString();
    }

    public void printBranchInfo() {
        System.out.println("Branch " + address + " From Bank " + (bank != null ? bank.getBankName() : "N/A"));
    }
}
