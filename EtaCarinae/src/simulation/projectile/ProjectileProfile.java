package simulation.projectile;

import simulation.enums.ZDepth;

public class ProjectileProfile {
	public static final ProjectileProfile DEFAULT_DUMB = new ProjectileProfile(true, new float[] {ZDepth.PROJECTILE.depth()}, 10, new String[] {"dumb_projectile.png"}, new int[] {2}, 100, 2);
	public static final ProjectileProfile DEFAULT_SEEKER = new ProjectileProfile(false, new float[] {ZDepth.PROJECTILE.depth()}, 1, new String[] {"seeker_projectile.png"}, new int[] {1}, 100, 2);
	
	private final boolean dumb;
	
	private final float[] zDepths;
	
	private final int diameter;
	
	private final String[] texturePaths;
	private final int[] textureSizes;
	
	private final int life;
	private final float muzzleVelocity;
	
	private ProjectileProfile(boolean dumb, float[] zDepths, int diameter, String[] texturePaths, int[] textureSizes, int life, float muzzleVelocity) {
		this.dumb = dumb;
		this.zDepths = zDepths;
		this.diameter = diameter;
		this.texturePaths = texturePaths;
		this.textureSizes = textureSizes;
		this.life = life;
		this.muzzleVelocity = muzzleVelocity;
	}

	public boolean isDumb() {
		return dumb;
	}

	public float[] getZDepths() {
		return zDepths;
	}

	public int getDiameter() {
		return diameter;
	}

	public String[] getTexturePaths() {
		return texturePaths;
	}

	public int[] getTextureSizes() {
		return textureSizes;
	}
	
	public int getLife() {
		return life;
	}
	
	public float getMuzzleVelocity() {
		return muzzleVelocity;
	}
}
