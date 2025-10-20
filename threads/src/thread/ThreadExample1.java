package thread;

//Example 1: Creating a thread by extending Thread
class MyThread extends Thread {
 @Override
 public void run() {
     // Code to be executed in the thread
     for (int i = 0; i < 5; i++) {
         System.out.println(getName() + " is running... iteration " + i);
         try {
             Thread.sleep(500); // Pause for 500 ms
         } catch (InterruptedException e) {
             System.out.println(getName() + " was interrupted");
         }
     }
 }
}

public class ThreadExample1 {
 public static void main(String[] args) {
     MyThread t1 = new MyThread();
     MyThread t2 = new MyThread();

     t1.start(); // Start first thread
     t2.start(); // Start second thread

     System.out.println("Main thread finished setup");
 }
}
