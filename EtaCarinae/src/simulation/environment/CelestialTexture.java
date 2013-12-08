package simulation.environment;

public enum CelestialTexture {
	BLUE_STAR("blue_star.png"), YELLOW_STAR("yellow_star.png");
	
	private String path;
	
	CelestialTexture(String path) {
		this.path = path;
	}
	
	public String path() {
		return path;
	}
}
