package bank;

import lombok.Getter;
import lombok.Setter;
import java.util.regex.Pattern;

@Getter
@Setter
public class Recipient {
    private String recipientID;
    private String name;
    private String email;
    private String phoneNumber;
    private Customer customer; // The customer who saved this recipient

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,9}$"
    );

    public Recipient(String recipientID, String name, String email, String phoneNumber, Customer customer) {
        this.recipientID = recipientID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.customer = customer;
    }

    public boolean validateContact() {
        if (email != null && !email.isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                return false;
            }
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
                return false;
            }
        }
        return true;
    }

    public String getRecipientInfo() {
        return String.format("Recipient: %s, Email: %s, Phone: %s", name, email, phoneNumber);
    }
}

