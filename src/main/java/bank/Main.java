package bank;

public class Main {
    public static void main(String[] args) {
        // New customer
        Customer customer = new Customer("Shayan Aminaei");
        customer.printCustomerInfo();
        System.out.println();

        // Making different accounts
        Card card = new Card(customer);
        Check check = new Check(customer);
        Saving saving = new Saving(customer);

        // Transations for each account
        Transaction t1 = new Transaction();
        Transaction t2 = new Transaction();
        Transaction t3 = new Transaction();

        card.addTransaction(t1);
        check.addTransaction(t2);
        saving.addTransaction(t3);

        // Transactions
        card.pay();
        card.receipt();
        System.out.println();

        check.pay();
        check.receipt();
        System.out.println();

        saving.pay();
        saving.receipt();
        System.out.println();

        // Bank and branches Test
        Bank bank = new Bank("National Bank");
        Branch branch1 = new Branch("Branch no1 ", bank);
        Branch branch2 = new Branch("Branch no2 ", bank);

        bank.printBankInfo();
        System.out.println();

        // Transaction's test
        System.out.println("Card   transactions count:   " + card.getTransactions().size());
        System.out.println("Check  transactions count:   " + check.getTransactions().size());
        System.out.println("Saving transactions count:   " + saving.getTransactions().size());
    }
}
