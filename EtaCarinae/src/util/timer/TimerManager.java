package util.timer;

import java.util.Enumeration;
import java.util.Hashtable;

public class TimerManager {
	private static Hashtable<String, Timer> timers = new Hashtable<String, Timer>();
	
	public static void tickTimers() {
		Enumeration<String> keys = timers.keys();
		while (keys.hasMoreElements()) {
			timers.get(keys.nextElement()).tick();
		}
	}
	
	public static void addTimer(String name, Timer timer) {
		timers.put(name, timer);
	}
	
	public static Timer getTimer(String name) {
		return timers.get(name);
	}
}
