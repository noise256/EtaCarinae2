package simulation.projectile;

public class WeaponProfile {
	public static final WeaponProfile DEFAULT = new WeaponProfile(5);
	
	private int fireInterval;
	
	private WeaponProfile(int fireInterval) {
		this.fireInterval = fireInterval;
	}
	
	public int getFireInterval() {
		return fireInterval;
	}
}
