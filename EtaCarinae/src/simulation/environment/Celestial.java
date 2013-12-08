package simulation.environment;

import simulation.object.GameObject;

public abstract class Celestial extends GameObject {
	protected String name;
	
	public Celestial(float[] position, float[] zDepths, int diameter, String[] texturePaths, int[] textureSizes, String name) {
		super(position, zDepths, diameter, texturePaths, textureSizes);
		
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
