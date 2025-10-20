package thread;

class SleepThread extends Thread {
    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Iteration " + i);
            try {
                Thread.sleep(1000); // Sleep for 1 second
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted");
            }
        }
    }
}

public class SleepExample {
    public static void main(String[] args) {
        SleepThread t = new SleepThread();
        t.start();
    }
}
