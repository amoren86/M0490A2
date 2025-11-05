package sync.deadlock;

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

	// Transfer method with deadlock avoidance
	public void transfer(BankAccount destination, double amount) {
		BankAccount firstLock; // Smaller IBAN
		BankAccount secondLock; // Larger IBAN

		// Determine lock order based on IBAN
		int comparison = this.getIban().compareTo(destination.getIban());

		if (comparison < 0) {
			// this.iban < destination.iban (thread 1)
			firstLock = this; // this has smaller IBAN (thread 1: acc1)
			secondLock = destination; // destination has larger IBAN (thread 1: acc2)
		} else if (comparison > 0) {
			// this.iban > destination.iban (thread 2)
			firstLock = destination; // destination has smaller IBAN (thread 2: acc1)
			secondLock = this; // this has larger IBAN (thread 2: acc2)
		} else {
			return; // same account, no transfer needed
		}

		// Acquire locks in consistent order to avoid deadlock
		// Thread 1 and Thread 2 will always lock the accounts in the same order
		// firstLock is always the account with the smaller IBAN (acc1 in both cases)
		synchronized (firstLock) {
			System.out.println(Thread.currentThread().getName() + " locked " + firstLock.iban);

			// No deadlock here since both threads lock accounts in the same order
			// secondLock is always the account with the larger IBAN (acc2 in both cases)
			synchronized (secondLock) {
				System.out.println(Thread.currentThread().getName() + " locked " + secondLock.iban);

				this.withdraw(amount);
				destination.deposit(amount);

				System.out.printf("%s transferred %.2f€ from %s to %s%n", Thread.currentThread().getName(), amount,
						this.iban, destination.iban);
			}
		}
	}
}

public class DeadlockExample {
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
