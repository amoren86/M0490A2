package thread.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class Counter {
	private int count = 0;

	public void increment() {
		count++; // Not atomic operation
	}

	public int getCount() {
		return count;
	}
}

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
		Counter counter = new Counter();

		List<Thread> threads = new ArrayList<>();

		for (int i = 0; i < THREADS; i++) {
			CounterThread t = new CounterThread(counter);
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
