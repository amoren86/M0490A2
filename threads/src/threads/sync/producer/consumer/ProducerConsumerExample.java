package threads.sync.producer.consumer;

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
			System.out.println("Storage is full, producer is waiting...");
		}

		System.out.printf("Storage: item stored. Items in storage: %s%n", items);
		notify(); // Notify consumer that there is at least one item

		items.add(item);
	}

	// Consumer retrieves an item
	public synchronized Item retrieve() throws InterruptedException {
		while (items.isEmpty()) {
			wait(); // Wait if storage is empty
			System.out.printf("Storage is empty, consumer is waiting...%s%n", items);
		}

		System.out.printf("Storage: item retrieve. Items in storage: %s%n", items, AnsiColor.RESET);
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
		System.out.printf("Producer: items produced = %d. Produced item = %s%n", ++producedCounter, item);
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
		System.out.printf("Consumer: items consumed = %d. Consumed item = %s%n", ++consumedCounter, item);
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
