package simulation.faction;

public class Faction {
	private String name;
	
	private FactionType factionType;
	
	public Faction(String name, FactionType factionType) {
		this.name = name;
		this.factionType = factionType;
	}
	
	public String getName() {
		return name;
	}
	
	public FactionType getFactionType() {
		return factionType;
	}
}
