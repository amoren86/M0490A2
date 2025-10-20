package producer.consumer;

public class Consumer extends Thread {
	private final Buffer buffer;

	public Consumer(Buffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public void run() {
		try {
			for (int i = 1; i <= 10; i++) {
				buffer.consume();
				Thread.sleep(1000); // simulate consumption time
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			interrupt();
		}
	}
}
