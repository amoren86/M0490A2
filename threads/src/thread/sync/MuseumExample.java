package thread.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// A museum, the shared resource
class Museum {
	private String name;

	Museum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}

// Access to a museum. Controls the number of visitors
class MuseumAccess {
	private int visitors = 0;
	private Museum museum;

	MuseumAccess(Museum museum) {
		this.museum = museum;
	}

	public void enter() {
		// Only one thread per museum can increase visitors at a time
		synchronized (museum) {
			visitors++;
		}
	}

	public int getVisitors() {
		// Only one thread per museum can read visitors at a time
		synchronized (museum) {
			return visitors;
		}
	}
}

//Each visitor runs in its own thread and enters multiple times
class VisitorThread extends Thread {

	private MuseumAccess access;

	public VisitorThread(MuseumAccess access) {
		this.access = access;
	}

	@Override
	public void run() {
		for (int i = 0; i < MuseumExample.TIMES_X_VISITOR; i++) {
			access.enter();
		}
	}
}

// Simulation of multiple visitors to multiple museums
public class MuseumExample {
	private static Museum[] museums = { new Museum("National Museum"), new Museum("Art Museum"),
			new Museum("Science Museum") };

	public static final int TIMES_X_VISITOR = 25;
	public static final int VISITORS = 10000 * museums.length; // Make it a multiple of the number of museums

	public static void main(String[] args) throws InterruptedException {
		List<MuseumAccess> accesses = new ArrayList<>();
		for (Museum museum : museums) {
			accesses.add(new MuseumAccess(museum));
		}

		List<Thread> visitors = new ArrayList<>();

		for (int i = 0; i < VISITORS; i++) {
			Thread visitor = new VisitorThread(accesses.get(i % museums.length)); // Share visitors among museums
			visitors.add(visitor);
			visitor.start();
		}

		for (Thread visitor : visitors) {
			visitor.join();
		}

		for (int i = 0; i < museums.length; i++) {
			System.out.printf(Locale.forLanguageTag("es-ES"), "%s -> Visits per year: %,d (expected %,d)%n",
					museums[i].getName(), accesses.get(i).getVisitors(), TIMES_X_VISITOR * VISITORS / museums.length);
		}
	}
}
