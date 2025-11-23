package bank;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class Admin extends User {
    private String adminID;

    public Admin(String userID, String userPassword, String userName, String userEmail, String userPhone) {
        super(userID, userPassword, userName, userEmail, userPhone, "admin");
        this.adminID = userID;
    }

    public void assignRole(User user, String newRole) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (!isValidRole(newRole)) {
            throw new IllegalArgumentException("Invalid role: " + newRole);
        }
        user.setUserRole(newRole);
    }

    public List<User> searchUsers(String searchCriteria, String searchType) {
        // This would query the database
        // For now, return empty list as placeholder
        return List.of();
    }

    private boolean isValidRole(String role) {
        return role.equalsIgnoreCase("customer") || 
               role.equalsIgnoreCase("banker") || 
               role.equalsIgnoreCase("bank_manager") || 
               role.equalsIgnoreCase("admin");
    }

    @Override
    public void displayDashboard() {
        System.out.println("Admin Dashboard for: " + userName);
    }
}

