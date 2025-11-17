package bank;

import lombok.Getter;

import java.util.UUID;
import java.util.regex.Pattern;
import java.util.Objects;

//class for a saved recipient for a customer
//with the assumption that phone number of the recipient can be optional
@Getter
public class Recipient {
	
	private final String ID;
	
	//customer who this recipient entry belongs to
	private final Customer owner;
	
	//recipient's basic info
	private String name;
	private String email;
	private String phoneNumber;
	
	//for valid recipient email and phone check, ETransfer unit test case
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
	private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+\\-\\s]*$");
	
	
	public Recipient(Customer owner, String name, String email, String phoneNumber) {
		
		if(owner ==null) {
			throw new IllegalArgumentException("Customer with a recipient list must exist");
		}
		
		validateRecipientInfo(name,email, phoneNumber);
		
		this.ID=UUID.randomUUID().toString();
		this.owner=owner;
		this.name=name.trim();
		this.email=email.trim();
		this.phoneNumber = (phoneNumber !=null && !phoneNumber.isBlank()) ? phoneNumber.trim() :null;
	}
	
	//method to update recipient info, phone number can still be optional here
	public void updateRecipientinfo(String name, String email, String phoneNumber) {
		
		validateRecipientInfo(name,email,phoneNumber);
		
		this.name=name.trim();
		this.email=email.trim();
		this.phoneNumber = (phoneNumber !=null && !phoneNumber.isBlank()) ? phoneNumber.trim() :null;
	}
	
	//method to validate recipient info, email and phone (optional) has to be in the correct format, ETransfer unit test case
	private void validateRecipientInfo(String name, String email, String phoneNumber) {
		
	        if (name == null || name.isBlank()) {
	            throw new IllegalArgumentException("Recipient name must not be blank");
	        }
	        if (email == null || email.isBlank()) {
	            throw new IllegalArgumentException("Recipient email must not be blank");
	        }
	        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
	            throw new IllegalArgumentException("Recipient's email is not in a valid format");
	        }
	        if (phoneNumber !=null && !phoneNumber.isBlank() && !PHONE_PATTERN.matcher(phoneNumber.trim()).matches()){
	            throw new IllegalArgumentException("Recipient's phone number contains invalid characters");
	        }
	    }
	
	//helper method to check that this recipient is saved by the customer
	public boolean isOwnedBy(Customer customer) {
	        return Objects.equals(this.owner, customer);
	    }
	
	@Override
    public String toString(){
        return "\n=== Recipient ===\n" +
               "ID             : " + ID + "\n" +
               "Name 		   : " + name + "\n" +
               "Email          : " + email + "\n" +
               "PhoneNumber    : " + (phoneNumber != null ? phoneNumber : "n/a") + "\n" +
               "Saved by       : " + owner.getName();
    }
}
