package simulation.object;

import util.math.MathBox;

public abstract class PhysicalObject extends GameObject implements Controllable {
	protected PhysicalProfile physicalProfile;
	
	protected float[] velocity;
	protected float[] rotation;
	
	protected boolean planned;
	protected float[] plannedDestination;
	protected float[] plannedOrientation;
	
	//old code variables
	private float turningAcceleration;
	private float turningVelocity;
	
	public PhysicalObject(float[] position, float[] zDepths, int diameter, String[] texturePaths, int[] textureSizes, float[] velocity, float[] rotation, PhysicalProfile physicalProfile) {
		super(position, zDepths, diameter, texturePaths, textureSizes);
		this.velocity = velocity;
		this.rotation = rotation;
		this.physicalProfile = physicalProfile;
	}
	
	private void accelerate(float[] vector, float magnitude) {
		velocity = MathBox.addVectors(velocity, MathBox.scalarVector(MathBox.normaliseVector(vector), magnitude));
		
		if (MathBox.getVectorMagnitude(velocity) > physicalProfile.getMaxVelocity()) {
			velocity = MathBox.scalarVector(MathBox.normaliseVector(velocity), physicalProfile.getMaxVelocity());
		}
	}
	
	private void update() {
		position = MathBox.addVectors(position, velocity);
	}
	
	private void stop() {
		float requiredAcceleration = MathBox.min(physicalProfile.getEngineForce() / physicalProfile.getMass(), MathBox.abs(MathBox.getVectorMagnitude(velocity)));
		
		accelerate(new float[] {-velocity[0], -velocity[1]}, requiredAcceleration);
	}
	
	protected void move() {
		accelerate(this.velocity, physicalProfile.getEngineForce() / physicalProfile.getMass());
		
		update();
	}
	
	private void turnTo(float[] orientation) {	
		float turningAngle = MathBox.atan2(orientation[1], orientation[0]) - getRotationAngle();
		
//		if (orientation[1] < 0.0f) {
//			turningAngle = MathBox.PI * 2 - turningAngle;
//		}
		
		float posAcceleration = physicalProfile.getEngineTurningForce() / physicalProfile.getMass();
		float reqAcceleration = turningAngle - turningVelocity * physicalProfile.getMass();
		
		//set acceleration using f = ma
		if (MathBox.abs(reqAcceleration) > posAcceleration) {
			turningAcceleration = (posAcceleration * MathBox.abs(reqAcceleration) / reqAcceleration) / physicalProfile.getMass();
		}
		else {
			turningAcceleration = reqAcceleration / physicalProfile.getMass();
		}
		
		//set velocity using v = u + at
		turningVelocity += turningAcceleration;
		
		//clamp velocity using v = min(velocity, maxVelocity)
		if (MathBox.abs(turningVelocity) > MathBox.abs(physicalProfile.getMaxTurningVelocity())) {
			if (turningVelocity < 0) {
				turningVelocity = physicalProfile.getMaxTurningVelocity() * -1;
			}
			else {
				turningVelocity = physicalProfile.getMaxTurningVelocity();
			}
		}
		
		//set new orientation using oi+1 = oi + v
		rotation = MathBox.angleToVector(getRotationAngle() + turningVelocity);
	}
	
	//TODO this does not handle stopping particularly well, i.e. objects never stop
	protected void moveTo(float[] destination, float[] orientation) {
		//get destination vector
		float[] destinationVector = MathBox.subtractVectors(destination, position);
		
		//get destination magnitude
		float destinationMagnitude = MathBox.getVectorMagnitude(destinationVector);
		
		//get velocity magnitude
		float velocityMagnitude = MathBox.getVectorMagnitude(velocity);
		
		//get max acceleration
		float maxAcceleration = physicalProfile.getEngineForce() / physicalProfile.getMass();
		
		//calculate destination norm
		float[] destinationNorm = MathBox.equals(destinationMagnitude, 0.0f) ? new float[] {0.0f, 0.0f} : MathBox.normaliseVector(destinationVector);
		
		//calculate velocity norm
		float[] velocityNorm = MathBox.equals(velocityMagnitude, 0.0f) ? new float[] {0.0f, 0.0f} : MathBox.normaliseVector(velocity);
		
		//adjust destination to cancel drift
		float[] destinationVectorNorm = MathBox.subtractVectors(destinationNorm, MathBox.subtractVectors(velocityNorm, destinationNorm));
		
		//calculate time to stop using s = ||v|| / maxAcc
		float stopTime = velocityMagnitude / maxAcceleration;
		
		//calculate time to destination
		float destinationTime = MathBox.equals(velocityMagnitude, 0.0f) ? Float.POSITIVE_INFINITY : destinationMagnitude / velocityMagnitude;
		
		//adjust rotation to face destination
		if (destinationMagnitude > physicalProfile.getStoppingDistance()) {
//			rotation = destinationVectorNorm;
			turnTo(destinationVectorNorm);
		}
		else if (orientation != null) {
//			rotation = orientation;
			turnTo(orientation);
		}
		
		//accelerate or stop depending on time til destination and time taken to full stop
		if (stopTime < destinationTime) {
			accelerate(destinationVectorNorm, maxAcceleration);
		}
		else {
			stop();
		}
		
		//update the position
		update();
	}
	
	public float getRotationAngle() {
		return MathBox.atan2(rotation[1], rotation[0]) - MathBox.atan2(0.0f, 1.0f);
//		return MathBox.atan2(rotation[1], rotation[0]);
	}

	public float[] getRotation() {
		return rotation;
	}
	
	public float[] getVelocity() {
		return velocity;
	}
	
	@Override
	public void setPlanned(boolean planned) {
		this.planned = planned;
	}

	@Override
	public boolean isPlanned() {
		return planned;
	}

	@Override
	public void addPlan(float[] plannedDestination, float[] plannedOrientation) {
		this.plannedDestination = plannedDestination;
		this.plannedOrientation = plannedOrientation;
	}
}
