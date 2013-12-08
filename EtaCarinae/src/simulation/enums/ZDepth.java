package simulation.enums;

/**
 * There must be a spacing of 2 between each depth listed to allow for selection nodes to 
 * have a depth of i - 1 where i is the depth of the main scene node for that object.
 * 
 * @author Douglas Hoyal Cuthill
 */
public enum ZDepth {
	SYSTEM_WELL(0), STAR(2), PLANET(4), TRADE_SHIP(6), PROJECTILE(8), SELECTION(10);
	
	private final float depth;
	
	private ZDepth(float depth) {
		this.depth = depth;
	}
	
	public float depth() {
		return depth;
	}
}
