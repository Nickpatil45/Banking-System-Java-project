import java.io.*;
import java.util.*;

abstract class Account {
    protected String accountNumber;
    protected String accountHolderName;
    protected double balance;

    public Account(String accountNumber, String accountHolderName, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
    }

    public abstract void deposit(double amount);

    public abstract boolean withdraw(double amount);

    public abstract void displayAccountDetails();

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }
}

class SavingsAccount extends Account {
    private final double interestRate = 0.03; // 3% interest rate

    public SavingsAccount(String accountNumber, String accountHolderName, double balance) {
        super(accountNumber, accountHolderName, balance);
    }

    @Override
    public void deposit(double amount) {
        balance += amount + (amount * interestRate); // Add interest to deposits
        logTransaction("Deposit", amount);
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            logTransaction("Withdrawal", amount);
            return true;
        }
        System.out.println("Insufficient balance.");
        return false;
    }

    @Override
    public void displayAccountDetails() {
        System.out.println("Savings Account:");
        System.out.println("Account Holder: " + accountHolderName);
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Balance: " + balance);
    }

    private void logTransaction(String type, double amount) {
        TransactionLogger.log(accountNumber, type, amount, balance);
    }
}

class CheckingAccount extends Account {
    private final double overdraftLimit = 500; // Overdraft limit for checking accounts

    public CheckingAccount(String accountNumber, String accountHolderName, double balance) {
        super(accountNumber, accountHolderName, balance);
    }

    @Override
    public void deposit(double amount) {
        balance += amount;
        logTransaction("Deposit", amount);
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= balance + overdraftLimit) {
            balance -= amount;
            logTransaction("Withdrawal", amount);
            return true;
        }
        System.out.println("Overdraft limit exceeded.");
        return false;
    }

    @Override
    public void displayAccountDetails() {
        System.out.println("Checking Account:");
        System.out.println("Account Holder: " + accountHolderName);
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Balance: " + balance);
    }

    private void logTransaction(String type, double amount) {
        TransactionLogger.log(accountNumber, type, amount, balance);
    }
}

class TransactionLogger {
    private static final String LOG_FILE = "transactions.log";

    public static void log(String accountNumber, String type, double amount, double balance) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            String logEntry = String.format("%s | %s | %.2f | Balance: %.2f", accountNumber, type, amount, balance);
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to transaction log: " + e.getMessage());
        }
    }

    public static void displayLogs() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            System.out.println("Transaction History:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading transaction log: " + e.getMessage());
        }
    }
}

public class BankingSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Account> accounts = new HashMap<>();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nBanking System Menu:");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Display Account Details");
            System.out.println("6. View Transaction Logs");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> createAccount();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> transfer();
                case 5 -> displayAccountDetails();
                case 6 -> TransactionLogger.displayLogs();
                case 7 -> {
                    System.out.println("Exiting Banking System. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void createAccount() {
        System.out.println("Create Account:");
        System.out.print("Enter Account Type (1 for Savings, 2 for Checking): ");
        int accountType = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Account Holder Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter Initial Balance: ");
        double balance = scanner.nextDouble();

        Account account = (accountType == 1) ?
                new SavingsAccount(accountNumber, name, balance) :
                new CheckingAccount(accountNumber, name, balance);

        accounts.put(accountNumber, account);
        System.out.println("Account created successfully!");
    }

    private static void deposit() {
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine();
        Account account = accounts.get(accountNumber);
        if (account != null) {
            System.out.print("Enter Amount to Deposit: ");
            double amount = scanner.nextDouble();
            account.deposit(amount);
            System.out.println("Deposit successful.");
        } else {
            System.out.println("Account not found.");
        }
    }

    private static void withdraw() {
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine();
        Account account = accounts.get(accountNumber);
        if (account != null) {
            System.out.print("Enter Amount to Withdraw: ");
            double amount = scanner.nextDouble();
            if (account.withdraw(amount)) {
                System.out.println("Withdrawal successful.");
            }
        } else {
            System.out.println("Account not found.");
        }
    }

    private static void transfer() {
        System.out.print("Enter Source Account Number: ");
        String sourceAccountNumber = scanner.nextLine();
        Account sourceAccount = accounts.get(sourceAccountNumber);
        if (sourceAccount == null) {
            System.out.println("Source account not found.");
            return;
        }

        System.out.print("Enter Destination Account Number: ");
        String destinationAccountNumber = scanner.nextLine();
        Account destinationAccount = accounts.get(destinationAccountNumber);
        if (destinationAccount == null) {
            System.out.println("Destination account not found.");
            return;
        }

        System.out.print("Enter Amount to Transfer: ");
        double amount = scanner.nextDouble();
        if (sourceAccount.withdraw(amount)) {
            destinationAccount.deposit(amount);
            System.out.println("Transfer successful.");
        }
    }

    private static void displayAccountDetails() {
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine();
        Account account = accounts.get(accountNumber);
        if (account != null) {
            account.displayAccountDetails();
        } else {
            System.out.println("Account not found.");
        }
    }
}
