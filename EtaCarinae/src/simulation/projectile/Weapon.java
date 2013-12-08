package simulation.projectile;

import java.util.ArrayList;

import simulation.object.PhysicalProfile;
import simulation.ships.Ship;
import util.math.MathBox;
import util.timer.TimerManager;

public class Weapon {
	private Ship parent;
	
	private WeaponProfile weaponProfile;
	private ProjectileProfile projectileProfile;
	
	private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	
	private int lastFireTime;
	
	public Weapon(Ship parent, WeaponProfile weaponProfile, ProjectileProfile projectileProfile) {
		this.parent = parent;
		this.weaponProfile = weaponProfile;
		this.projectileProfile = projectileProfile;
	}
	
	public void update() {
		int currentTime = TimerManager.getTimer("mainTimer").getTime();
		
		if (currentTime >= lastFireTime + weaponProfile.getFireInterval()) {
			float[] velocity = MathBox.addVectors(parent.getVelocity(), MathBox.scalarVector(MathBox.normaliseVector(parent.getVelocity()), projectileProfile.getMuzzleVelocity()));
			projectiles.add(new Projectile(parent.getAbsolutePosition(), velocity, parent.getRotation(), PhysicalProfile.DEFAULT, projectileProfile));
		}
	}
	
	public ArrayList<Projectile> getProjectiles() {
		return projectiles;
	}
}
