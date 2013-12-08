package simulation.object;

public interface Controllable {
	public void setPlanned(boolean planned);
	
	public boolean isPlanned();
	
	public void addPlan(float[] plannedDestination, float[] plannedOrientation);
}
