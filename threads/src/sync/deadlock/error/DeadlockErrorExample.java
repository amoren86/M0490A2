package sync.deadlock.error;

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

	// Deposit and withdraw methods
	public void deposit(double amount) {
		balance += amount;
	}

	public void withdraw(double amount) {
		balance -= amount;
	}

	// Get balance method
	public double getBalance() {
		return balance;
	}

	// Transfer method with deadlock possibility
	public void transfer(BankAccount destination, double amount) {
		// Potential deadlock scenario
		// Thread 1 locks acc1 then acc2
		// Thread 2 locks acc2 then acc1
		synchronized (this) {
			System.out.println(Thread.currentThread().getName() + " locked " + this.iban);

			// Potential deadlock here
			// Thread 1 might be waiting for acc2 while holding acc1
			// Thread 2 might be waiting for acc1 while holding acc2
			synchronized (destination) {
				System.out.println(Thread.currentThread().getName() + " locked " + destination.iban);

				this.withdraw(amount);
				destination.deposit(amount);

				System.out.printf("%s transferred %.2f€ from %s to %s%n", Thread.currentThread().getName(), amount,
						this.iban, destination.iban);
			}
		}
	}
}

public class DeadlockErrorExample {
	public static void main(String[] args) throws InterruptedException {
		BankAccount acc1 = new BankAccount("ES91 2100 1234 5600 0001", 1000);
		BankAccount acc2 = new BankAccount("ES91 2100 1234 5600 0002", 1000);

		Thread t1 = new Thread(() -> acc1.transfer(acc2, 500), "Transfer A");
		Thread t2 = new Thread(() -> acc2.transfer(acc1, 200), "Transfer B");

		t1.start();
		t2.start();

		t1.join();
		t2.join();

		System.out.printf("Final balances: %s = %.2f€, %s = %.2f€%n", acc1.getIban(), acc1.getBalance(), acc2.getIban(),
				acc2.getBalance());
	}
}
