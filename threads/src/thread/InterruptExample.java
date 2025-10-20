package thread;

class InterruptibleThread extends Thread {
    @Override
    public void run() {
        while (!isInterrupted()) {
            System.out.println("Working...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Interrupted while sleeping");
                interrupt(); // Restore the interruption flag
            }
        }
        System.out.println("Thread stopped gracefully");
    }
}

public class InterruptExample {
    public static void main(String[] args) throws InterruptedException {
        InterruptibleThread thread = new InterruptibleThread();
        thread.start();

        Thread.sleep(2000); // Let it run for 2 seconds
        thread.interrupt(); // Signal interruption
    }
}
