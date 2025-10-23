package wait.notify;

// Monitor class
class Message {
	private String text;
	private boolean wrotten = false;

	public synchronized void write(String msg) {
		text = msg;
		wrotten = true;
		notify(); // Wake up waiting thread
		System.out.println("Writer sent message: " + msg);
	}

	public synchronized void read() throws InterruptedException {
		while (!wrotten) {
			System.out.println("Reader is waiting for message...");
			wait(); // Wait until message is ready
		}
		System.out.println("Reader received message: " + text);
		wrotten = false;
	}
}

class WriterThread extends Thread {
	private Message message;

	WriterThread(Message message) {
		this.message = message;
	}

	@Override
	public void run() {
		message.write("Hello from WriterThread!");
	}
}

class ReaderThread extends Thread {
	private Message message;

	ReaderThread(Message message) {
		this.message = message;
	}

	@Override
	public void run() {
		try {
			message.read();
		} catch (InterruptedException e) {
			e.printStackTrace();
			interrupt();
		}
	}
}

public class WaitNotifyExample {
	public static void main(String[] args) throws InterruptedException {
		Message message = new Message();
		new ReaderThread(message).start();
		Thread.sleep(1000); // WriterThread starts one second later (at least)
		new WriterThread(message).start();
	}
}
