package sync.race.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Counter interface
interface Counter {
	void increment();

	int getCount();
}

// Counter implementation with synchronization
class SafeCounter implements Counter {
	private int count = 0;

	@Override
	public synchronized void increment() {
		count++; // Not atomic operation
	}

	@Override
	public synchronized int getCount() {
		return count;
	}
}

//Counter implementation with partial synchronization
class PartialSyncCounter implements Counter {
	private int count = 0;

	@Override
	public void increment() {
		synchronized (this) {
			count++; // Not atomic operation

		}
	}

	@Override
	public int getCount() {
		synchronized (this) {
			return count;
		}
	}
}

// Thread that increments the counter
class CounterThread extends Thread {

	private Counter counter;

	public CounterThread(Counter counter) {
		this.counter = counter;
	}

	@Override
	public void run() {
		for (int i = 0; i < RaceConditionExample.INC_X_THREAD; i++) {
			counter.increment();
		}
	}
}

public class RaceConditionExample {
	public static final int INC_X_THREAD = 2000;
	public static final int THREADS = 10;

	public static void main(String[] args) throws InterruptedException {
		// Safe counter to avoid race conditions
		Counter counter = new SafeCounter();
		// Change to PartialSyncCounter to see different behavior
		// Counter counter = new PartialSyncCounter();

		List<Thread> threads = new ArrayList<>();

		for (int i = 0; i < THREADS; i++) {
			Thread t = new CounterThread(counter);
			threads.add(t);
			t.start();
		}

		for (Thread t : threads) {
			t.join();
		}

		System.out.printf(Locale.forLanguageTag("es-ES"), "Final count: %,d (expected %,d)", counter.getCount(),
				INC_X_THREAD * THREADS);
	}
}
