package thread;

//Example 2: Creating a thread by implementing Runnable
class MyRunnable implements Runnable {
	@Override
	public void run() {
		for (int i = 1; i <= 5; i++) {
			System.out.println(Thread.currentThread().getName() + " running iteration " + i);
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				System.out.println(Thread.currentThread().getName() + " interrupted");
			}
		}
	}
}

public class ThreadExample2 {
	public static void main(String[] args) {
		Thread t1 = new Thread(new MyRunnable());
		Thread t2 = new Thread(new MyRunnable());

		t1.start();
		t2.start();

		System.out.println("Main thread is doing other tasks...");
	}
}
