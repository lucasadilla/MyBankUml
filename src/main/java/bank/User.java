package bank;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public abstract class User {
    protected String userID;
    protected String userPassword;
    protected String userName;
    protected String userEmail;
    protected String userPhone;
    protected String userRole; // customer, banker, bank_manager, admin
    protected LocalDateTime createdAt;
    protected boolean isActive;

    public User(String userID, String userPassword, String userName, String userEmail, String userPhone, String userRole) {
        this.userID = userID;
        this.userPassword = userPassword;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userRole = userRole;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    public boolean login(String password) {
        if (this.userPassword.equals(password) && this.isActive) {
            return true;
        }
        return false;
    }

    public void logout() {
        // Session management can be added here
    }

    public boolean isInRole(String role) {
        return this.userRole.equalsIgnoreCase(role);
    }

    public abstract void displayDashboard();
}

