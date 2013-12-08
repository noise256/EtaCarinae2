package util.timer;

public class Timer {
	private int time;
	
	private long interval;
	private long timeAtLastTick;
	
	public Timer(long interval) {
		this.interval = interval;
	}
	
	public void tick() {
		long currentTime = System.currentTimeMillis();
		
		if (currentTime - timeAtLastTick > interval) {
			time++;
			timeAtLastTick = currentTime;
		}
	}
	
	public int getTime() {
		return time;
	}
}
