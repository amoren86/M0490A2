package methods;

class PriorityThread extends Thread {
	private final String name;

	public PriorityThread(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		System.out.println("Running thread: " + name + " with priority " + getPriority());
	}
}

public class PriorityExample {
	public static void main(String[] args) {
		Thread highPriorityThread = new PriorityThread("High Priority");
		Thread lowPriorityThread = new PriorityThread("Low Priority");

		highPriorityThread.setPriority(Thread.MAX_PRIORITY);
		lowPriorityThread.setPriority(Thread.MIN_PRIORITY);

		highPriorityThread.start();
		lowPriorityThread.start();
	}
}
