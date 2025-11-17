package bank;

import java.math.BigDecimal;

public class Check extends Account {
    
	//new checking account for a customer
	public Check(String accountID, Customer customer, BigDecimal openingBalance) {
		super(accountID, customer, openingBalance);
	}

	//returns the type of this account
	@Override
	public String getAccountType() {
		// TODO Auto-generated method stub
		return "CHECKING";
	}

}

