package producer.consumer;

import java.util.LinkedList;
import java.util.List;

public class Buffer {
	private final List<Integer> queue = new LinkedList<>();
	private static final int MAX_SIZE = 5; // maximum capacity of the buffer

	public synchronized void produce(int value) throws InterruptedException {
		while (queue.size() == MAX_SIZE) {
			// Wait until consumer removes an element (buffer full)
			wait();
		}
		queue.add(value);
		System.out.println("Produced: " + value + " | Queue: " + queue);
		// Notify consumers that new data is available
		notifyAll();
	}

	public synchronized int consume() throws InterruptedException {
		while (queue.isEmpty()) {
			// Wait until producer adds an element (buffer empty)
			wait();
		}
		int value = queue.remove(0);
		System.out.println("Consumed: " + value + " | Queue: " + queue);
		// Notify producers that there is free space
		notifyAll();
		return value;
	}
}
