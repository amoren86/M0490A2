package thread;

class JoinThread extends Thread {
	@Override
	public void run() {
		System.out.println("Thread thread started");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Thread interrupted");
		}
		System.out.println("Thread thread finished");
	}
}

public class JoinExample {
	public static void main(String[] args) throws InterruptedException {
		JoinThread thread = new JoinThread();
		thread.start();

		System.out.println("Main thread waiting for thread to finish...");
		thread.join(); // Wait for the thread thread to finish
		System.out.println("Main thread resumes after thread finished");
	}
}
