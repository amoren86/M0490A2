package sync.readers.writers;

public class BankAccount {
	private String iban; // International Bank Account Number (identifier)
	private double balance; // Account balance
	private int readers = 0; // number of active readers
	private boolean writing = false; // true if a writer is writing

	public BankAccount(String iban, double balance) {
		this.iban = iban;
		this.balance = balance;
	}

	public String getIban() {
		return iban;
	}

	// Deposit and withdraw methods
	public void deposit(double amount) {
		startWriting(); // Acquire write lock
		writeBalance(amount); // Update balance
		stopWriting(); // Release write lock
	}

	public void withdraw(double amount) {
		deposit(-amount); // Reuse deposit method for withdrawal
	}

	// Get balance method
	public double getBalance() {
		startReading(); // Acquire read lock
		double currentBalance = readBalance(); // Read balance
		stopReading(); // Release read lock
		return currentBalance;
	}

	// Transfer method with deadlock avoidance
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

	// --- Synchronization methods for readers-writers problem ---
	private void startWriting() {
		synchronized (this) {
			// Wait if there are active readers or another writer is writing
			while (writing || readers > 0) {
				try {
					wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			// Set writing flag
			writing = true;
		}
	}

	private void writeBalance(double amount) {
		balance += amount;
	}

	private void stopWriting() {
		synchronized (this) {
			// Clear writing flag
			writing = false;
			// Notify all waiting threads
			notifyAll();
		}
	}

	private void startReading() {
		synchronized (this) {
			// Wait if there is an active writer
			while (writing) {
				try {
					wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			// Increment the number of active readers
			readers++;
		}
	}

	private double readBalance() {
		return balance;
	}

	private void stopReading() {
		synchronized (this) {
			// Decrement the number of active readers
			readers--;
			// If no more readers, notify waiting writers
			if (readers == 0) {
				notifyAll(); // wake up waiting writers
			}
		}
	}
}
