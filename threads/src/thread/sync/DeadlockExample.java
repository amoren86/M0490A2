package thread.sync;

class BankAccount {
	private String iban;
	private double balance;

	public BankAccount(String iban, double balance) {
		this.iban = iban;
		this.balance = balance;
	}

	public String getIban() {
		return iban;
	}

	public synchronized void deposit(double amount) {
		balance += amount;
	}

	public synchronized void withdraw(double amount) {
		balance -= amount;
	}

	public synchronized double getBalance() {
		return balance;
	}

	// Transfer method with potential deadlock
	public void transfer(BankAccount destination, double amount) {
		BankAccount firstLock;
		BankAccount secondLock;

		// Determine lock order based on IBAN
		int comparison = this.getIban().compareTo(destination.getIban());

		if (comparison < 0) {
			firstLock = this;
			secondLock = destination;
		} else if (comparison > 0) {
			firstLock = destination;
			secondLock = this;
		} else {
			return; // same account, no transfer needed
		}

		synchronized (firstLock) {
			System.out.println(Thread.currentThread().getName() + " locked " + firstLock.iban);

			synchronized (secondLock) {
				System.out.println(Thread.currentThread().getName() + " locked " + secondLock.iban);

				this.withdraw(amount);
				destination.deposit(amount);

				System.out.printf("%s transferred %.2f€ from %s to %s%n", Thread.currentThread().getName(), amount,
						this.iban, destination.iban);
			}
		}
		synchronized (this) {
			System.out.println(Thread.currentThread().getName() + " locked " + this.iban);

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
