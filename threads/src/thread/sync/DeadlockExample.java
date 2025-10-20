package thread.sync;

class BankAccount {
	private String name;
	private double balance;

	public BankAccount(String name, double balance) {
		this.name = name;
		this.balance = balance;
	}

	public String getName() {
		return name;
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
		synchronized (this) {
			System.out.println(Thread.currentThread().getName() + " locked " + this.name);

			synchronized (destination) {
				System.out.println(Thread.currentThread().getName() + " locked " + destination.name);

				this.withdraw(amount);
				destination.deposit(amount);

				System.out.printf("%s transferred %.2f€ from %s to %s%n", Thread.currentThread().getName(), amount,
						this.name, destination.name);
			}
		}
	}
}

public class DeadlockExample {
	public static void main(String[] args) throws InterruptedException {
		BankAccount acc1 = new BankAccount("Account A", 1000);
		BankAccount acc2 = new BankAccount("Account B", 1000);

		Thread t1 = new Thread(() -> acc1.transfer(acc2, 500), "T1");
		Thread t2 = new Thread(() -> acc2.transfer(acc1, 200), "T2");

		t1.start();
		t2.start();

		t1.join();
		t2.join();

		System.out.printf("Final balances: %s = %.2f€, %s = %.2f€%n", acc1.getName(), acc1.getBalance(), acc2.getName(),
				acc2.getBalance());
	}
}
