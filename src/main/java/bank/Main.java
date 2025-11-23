package bank;

public class Main {
    public static void main(String[] args) {
        // New customer with proper User constructor
        Customer customer = new Customer(
            "CUST001",
            "password123",
            "Shayan Aminaei",
            "shayan@example.com",
            "555-1234"
        );
        System.out.println("Customer: " + customer.getUserName());
        System.out.println();

        // Making different accounts
        Card card = new Card("CARD001", customer, 1000.0);
        Check check = new Check("CHECK001", customer, 500.0);
        Saving saving = new Saving("SAVING001", customer, 2000.0, 0.02);

        // Test account operations
        System.out.println("Card balance: $" + card.getBalance());
        System.out.println("Check balance: $" + check.getBalance());
        System.out.println("Saving balance: $" + saving.getBalance());
        System.out.println();

        // Test transactions
        card.pay();
        card.receipt();
        System.out.println();

        check.pay();
        check.receipt();
        System.out.println();

        saving.pay();
        saving.receipt();
        System.out.println();

        // Test transfer funds
        try {
            customer.transferFunds(card, saving, 100.0);
            System.out.println("Transfer completed!");
            System.out.println("Card balance: $" + card.getBalance());
            System.out.println("Saving balance: $" + saving.getBalance());
        } catch (Exception e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
        System.out.println();

        // Bank and branches Test
        Bank bank = new Bank("BANK001", "National Bank", "123 Main St", "555-0000");
        new Branch("BRANCH001", "Branch no1", "456 Oak Ave", "555-0001", bank);
        new Branch("BRANCH002", "Branch no2", "789 Pine St", "555-0002", bank);

        bank.printBankInfo();
        System.out.println();

        // Transaction's test
        System.out.println("Card transactions count: " + card.getTransactions().size());
        System.out.println("Check transactions count: " + check.getTransactions().size());
        System.out.println("Saving transactions count: " + saving.getTransactions().size());
    }
}
