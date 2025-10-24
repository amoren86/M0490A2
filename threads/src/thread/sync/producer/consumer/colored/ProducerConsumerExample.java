package thread.sync.producer.consumer.colored;

import java.util.LinkedList;
import java.util.Queue;

import ansi.colors.AnsiColor;

// The item
class Item {
	// Item properties can be defined here

	@Override
	public String toString() {
		return "Item@" + Integer.toHexString(hashCode());
	}
}

// The storage
class Storage {
	private final Queue<Item> items = new LinkedList<>();
	private boolean endOfProduction = false;

	// Producer stores an item
	public synchronized void store(Item item) throws InterruptedException {
		while (items.size() == ProducerConsumerExample.STORAGE_CAPACITY) {
			wait(); // Wait if storage is full
			System.out.printf("%sStorage is full, producer is waiting...%n%s", AnsiColor.RED, AnsiColor.RESET);
		}

		System.out.printf("Storage: item stored. Items in storage: %s%n", AnsiColor.BLACK, items, AnsiColor.RESET);
		notify(); // Notify consumer that there is at least one item

		items.add(item);
	}

	// Consumer retrieves an item
	public synchronized Item retrieve() throws InterruptedException {
		while (items.isEmpty()) {
			wait(); // Wait if storage is empty
			System.out.printf("%sStorage is empty, consumer is waiting...%s%n%s", AnsiColor.RED, AnsiColor.RESET,
					items);
		}

		System.out.printf("%sStorage: item retrieve. Items in storage: %s%n%s", AnsiColor.BLACK, items,
				AnsiColor.RESET);
		notify(); // Notify producer that there is space available

		return items.remove();
	}

	public synchronized void setEndOfProduction() {
		this.endOfProduction = true;
	}

	public synchronized boolean isEndOfConsumption() {
		return endOfProduction && items.isEmpty();
	}
}

// The producer
class Producer extends Thread {
	private Storage storage;
	private int producedCounter = 0;

	Producer(Storage storage) {
		this.storage = storage;
	}

	@Override
	public void run() {
		try {
			for (int i = 0; i < ProducerConsumerExample.ITEMS_TO_PRODUCE; i++) {
				Item item = produceItem(); // Produce an item
				storage.store(item); // Store the item
			}
			storage.setEndOfProduction(); // Indicate end of production
		} catch (InterruptedException e) {
			e.printStackTrace();
			interrupt();
		}
	}

	private Item produceItem() throws InterruptedException {
		Thread.sleep(ProducerConsumerExample.PRODUCTION_TIME); // Simulate production time
		Item item = new Item();
		System.out.printf("%sProducer: items produced = %d. Produced item = %s%n%s", AnsiColor.BLUE, ++producedCounter,
				item, AnsiColor.RESET);
		return item;
	}
}

// The consumer
class Consumer extends Thread {
	private Storage storage;
	private int consumedCounter = 0;

	Consumer(Storage storage) {
		this.storage = storage;
	}

	@Override
	public void run() {
		try {
			do {
				Item item = storage.retrieve(); // Retrieve an item
				consumeItem(item); // Consume the item
			} while (!storage.isEndOfConsumption());
		} catch (InterruptedException e) {
			e.printStackTrace();
			interrupt();
		}
	}

	private void consumeItem(Item item) throws InterruptedException {
		Thread.sleep(ProducerConsumerExample.CONSUMPTION_TIME); // Simulate consumption time
		System.out.printf("%sConsumer: items consumed = %d. Consumed item = %s%n%s", AnsiColor.PURPLE,
				++consumedCounter, item, AnsiColor.RESET);
	}
}

public class ProducerConsumerExample {
	public static final int ITEMS_TO_PRODUCE = 20;
	public static final int STORAGE_CAPACITY = 10;
	public static final int PRODUCTION_TIME = 100;
	public static final int CONSUMPTION_TIME = 150;

	public static void main(String[] args) {
		Storage storage = new Storage();
		new Producer(storage).start();
		new Consumer(storage).start();
	}
}
