import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Account {
    private String accountNumber;
    private String accountHolderName;
    protected double balance;
    private String pin;
    private List<String> transactionHistory = new ArrayList<>();

    public Account(String accountNumber, String accountHolderName, double initialBalance, String pin) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance;
        this.pin = pin;
        logTransaction("Account created with initial balance: ₹" + initialBalance);
    }

    public boolean authenticate(String pin) {
        return this.pin.equals(pin);
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            logTransaction("Deposited: ₹" + amount);
            System.out.println("Deposited: ₹" + amount);
        } else {
            System.out.println("Invalid deposit amount!");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            logTransaction("Withdrawn: ₹" + amount);
            System.out.println("Withdrawn: ₹" + amount);
        } else {
            System.out.println("Insufficient funds or invalid amount!");
        }
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public List<String> getTransactionHistory() {
        return transactionHistory;
    }

    private void logTransaction(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        transactionHistory.add("[" + timestamp + "] " + message);
    }
}

class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(String accountNumber, String accountHolderName, double initialBalance, String pin, double interestRate) {
        super(accountNumber, accountHolderName, initialBalance, pin);
        this.interestRate = interestRate;
    }

    public void addInterest() {
        double interest = getBalance() * (interestRate / 100);
        deposit(interest);
        System.out.println("Interest added: ₹" + interest);
    }
}

class Bank {
    private HashMap<String, Account> accounts = new HashMap<>();

    public void createAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
        System.out.println("Account created: " + account.getAccountNumber());
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public void transferFunds(String fromAccount, String toAccount, double amount) {
        Account from = accounts.get(fromAccount);
        Account to = accounts.get(toAccount);

        if (from != null && to != null) {
            if (from.getBalance() >= amount) {
                from.withdraw(amount);
                to.deposit(amount);
                from.getTransactionHistory().add("Transferred ₹" + amount + " to account " + toAccount);
                to.getTransactionHistory().add("Received ₹" + amount + " from account " + fromAccount);
                System.out.println("Transferred ₹" + amount + " from " + fromAccount + " to " + toAccount);
            } else {
                System.out.println("Insufficient funds for transfer.");
            }
        } else {
            System.out.println("Invalid account details.");
        }
    }

    public void saveAccountsToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(accounts);
            System.out.println("Accounts saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    public void loadAccountsFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            accounts = (HashMap<String, Account>) ois.readObject();
            System.out.println("Accounts loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }

    public void showExistingAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts available.");
        } else {
            System.out.println("Existing accounts:");
            for (Account account : accounts.values()) {
                System.out.println("Account Number: " + account.getAccountNumber() + ", Holder Name: " + account.getAccountHolderName());
            }
        }
    }
}

public class BankSimulation {
    public static void main(String[] args) {
        Bank bank = new Bank();
        Scanner scanner = new Scanner(System.in);
        String filename = "accounts.dat";

        // Load accounts from file
        bank.loadAccountsFromFile(filename);

        while (true) {
            System.out.println("\n==== Bank Menu ====");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer Funds");
            System.out.println("5. Check Balance");
            System.out.println("6. Add Interest");
            System.out.println("7. View Transaction History");
            System.out.println("8. Show Existing Accounts");
            System.out.println("9. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter account number: ");
                    String accountNumber = scanner.nextLine();
                    System.out.print("Enter account holder name: ");
                    String accountHolder = scanner.nextLine();
                    System.out.print("Enter initial balance: ");
                    double initialBalance = scanner.nextDouble();
                    System.out.print("Enter PIN: ");
                    String pin = scanner.next();
                    System.out.print("Enter interest rate (for savings): ");
                    double interestRate = scanner.nextDouble();

                    SavingsAccount savingsAccount = new SavingsAccount(
                            accountNumber, accountHolder, initialBalance, pin, interestRate);
                    bank.createAccount(savingsAccount);
                    break;

                case 2:
                    System.out.print("Enter account number: ");
                    accountNumber = scanner.nextLine();
                    System.out.print("Enter PIN: ");
                    pin = scanner.next();
                    System.out.print("Enter deposit amount: ");
                    double depositAmount = scanner.nextDouble();
                    Account account = bank.getAccount(accountNumber);
                    if (account != null && account.authenticate(pin)) {
                        account.deposit(depositAmount);
                    } else {
                        System.out.println("Invalid account details or PIN!");
                    }
                    break;

                case 3:
                    System.out.print("Enter account number: ");
                    accountNumber = scanner.nextLine();
                    System.out.print("Enter PIN: ");
                    pin = scanner.next();
                    System.out.print("Enter withdrawal amount: ");
                    double withdrawAmount = scanner.nextDouble();
                    account = bank.getAccount(accountNumber);
                    if (account != null && account.authenticate(pin)) {
                        account.withdraw(withdrawAmount);
                    } else {
                        System.out.println("Invalid account details or PIN!");
                    }
                    break;

                case 4:
                    System.out.print("Enter source account number: ");
                    String fromAccount = scanner.nextLine();
                    System.out.print("Enter source account PIN: ");
                    String fromPin = scanner.next();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter destination account number: ");
                    String toAccount = scanner.nextLine();
                    System.out.print("Enter amount to transfer: ");
                    double transferAmount = scanner.nextDouble();

                    Account from = bank.getAccount(fromAccount);
                    Account to = bank.getAccount(toAccount);
                    if (from != null && to != null && from.authenticate(fromPin)) {
                        bank.transferFunds(fromAccount, toAccount, transferAmount);
                    } else {
                        System.out.println("Invalid account details or PIN!");
                    }
                    break;

                case 5:
                    System.out.print("Enter account number: ");
                    accountNumber = scanner.nextLine();
                    System.out.print("Enter PIN: ");
                    pin = scanner.next();
                    account = bank.getAccount(accountNumber);
                    if (account != null && account.authenticate(pin)) {
                        System.out.println("Balance: ₹" + account.getBalance());
                    } else {
                        System.out.println("Invalid account details or PIN!");
                    }
                    break;

                case 6:
                    System.out.print("Enter account number: ");
                    accountNumber = scanner.nextLine();
                    System.out.print("Enter PIN: ");
                    pin = scanner.next();
                    account = bank.getAccount(accountNumber);
                    if (account instanceof SavingsAccount && account.authenticate(pin)) {
                        ((SavingsAccount) account).addInterest();
                    } else {
                        System.out.println("Invalid account details, PIN, or account type!");
                    }
                    break;

                case 7:
                    System.out.print("Enter account number: ");
                    accountNumber = scanner.nextLine();
                    System.out.print("Enter PIN: ");
                    pin = scanner.next();
                    account = bank.getAccount(accountNumber);
                    if (account != null && account.authenticate(pin)) {
                        System.out.println("Transaction History:");
                        for (String transaction : account.getTransactionHistory()) {
                            System.out.println(transaction);
                        }
                    } else {
                        System.out.println("Invalid account details or PIN!");
                    }
                    break;

                case 8:
                    bank.showExistingAccounts();
                    break;

                case 9:
                    System.out.println("Exiting. Saving accounts...");
                    bank.saveAccountsToFile(filename);
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
