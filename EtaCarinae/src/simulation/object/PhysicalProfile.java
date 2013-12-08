package simulation.object;

public class PhysicalProfile {
	public static final PhysicalProfile DEFAULT = new PhysicalProfile(50.0f, 2.0f, 25.0f, 0.25f, 5.25f, 50.0f);
	
	private final float maxVelocity;
	private final float stoppingDistance;
	private final float mass;
	private final float engineForce;
	private final float engineTurningForce;
	private final float maxTurningVelocity;
	
	private PhysicalProfile(float maxVelocity, float stoppingDistance, float mass, float engineForce, float engineTurningForce, float maxTurningVelocity) {
		this.maxVelocity = maxVelocity;
		this.stoppingDistance = stoppingDistance;
		this.mass = mass;
		this.engineForce = engineForce;
		this.engineTurningForce = engineTurningForce;
		this.maxTurningVelocity = maxTurningVelocity;
	}
	
	public float getMaxVelocity() {
		return maxVelocity;
	}
	
	public float getStoppingDistance() {
		return stoppingDistance;
	}
	
	public float getMass() {
		return mass;
	}
	
	public float getEngineForce() {
		return engineForce;
	}
	
	public float getEngineTurningForce() {
		return engineTurningForce;
	}
	
	public float getMaxTurningVelocity() {
		return maxTurningVelocity;
	}
}