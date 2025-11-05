package sync.readers.writers.error;

class BankAccount {
	private String iban; // International Bank Account Number (identifier)
	private double balance; // Account balance

	public BankAccount(String iban, double balance) {
		this.iban = iban;
		this.balance = balance;
	}

	public String getIban() {
		return iban;
	}

	// Deposit method
	// No synchronization, leading to potential inconsistencies
	public void deposit(double amount) {
		balance += amount; // Update balance
	}

	// Withdraw method
	// No synchronization, leading to potential inconsistencies
	public void withdraw(double amount) {
		deposit(-amount); // Reuse deposit method for withdrawal
	}

	// Get balance method
	// No synchronization, leading to potential inconsistencies
	public double getBalance() {
		return balance;
	}
}
