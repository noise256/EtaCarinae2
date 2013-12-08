package simulation.environment;

public class GalaxyProfile {
	public static final GalaxyProfile DEFAULT = new GalaxyProfile(30, 300000, 30000, 6000, 10000, 2000, 3000, 50, 1, 5, 1, 1);
	
	private final int numSystems;
	
	private final int systemSpread;
	
	private final int minSystemDistance;
	
	private final int minSystemRadius;
	private final int maxSystemRadius;
	
	private final int minStarRadius;
	private final int maxStarRadius;
	
	private final int minPlanets;
	private final int maxPlanets;
	
	private final int minPlanetRadius;
	
	private final int minFertilities;
	private final int maxFertilities;
	
	private GalaxyProfile(int numSystems, int systemSpread, int minSystemDistance, int minSystemRadius, int maxSystemRadius, int minStarRadius, int maxStarRadius, int minPlanetRadius, int minPlanets, int maxPlanets, int minFertilities, int maxFertilities) {
		this.numSystems = numSystems;
		this.systemSpread = systemSpread;
		this.minSystemDistance = minSystemDistance;
		this.minSystemRadius = minSystemRadius;
		this.maxSystemRadius = maxSystemRadius;
		this.minStarRadius = minStarRadius;
		this.maxStarRadius = maxStarRadius;
		this.minPlanetRadius = minPlanetRadius;
		this.minPlanets = minPlanets;
		this.maxPlanets = maxPlanets;
		this.minFertilities = minFertilities;
		this.maxFertilities = maxFertilities;
	}

	public int getNumSystems() {
		return numSystems;
	}

	public int getSystemSpread() {
		return systemSpread;
	}

	public int getMinSystemDistance() {
		return minSystemDistance;
	}

	public int getMinSystemRadius() {
		return minSystemRadius;
	}

	public int getMaxSystemRadius() {
		return maxSystemRadius;
	}

	public int getMinStarRadius() {
		return minStarRadius;
	}

	public int getMaxStarRadius() {
		return maxStarRadius;
	}

	public int getMinPlanets() {
		return minPlanets;
	}

	public int getMaxPlanets() {
		return maxPlanets;
	}

	public int getMinPlanetRadius() {
		return minPlanetRadius;
	}

	public int getMinFertilities() {
		return minFertilities;
	}

	public int getMaxFertilities() {
		return maxFertilities;
	}
}
