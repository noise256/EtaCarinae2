package simulation.enums;

public enum ResourceType {
	FOOD, FUEL, HYDROGEN, WATER, POPULATION;
	
	@Override
	public String toString() {
		if (this.equals(FOOD)) {
			return new String("Food");
		}
		else if (this.equals(FUEL)) {
			return new String("Fuel");
		}
		else if (this.equals(HYDROGEN)) {
			return new String("Hydrogen");
		}
		else if (this.equals(WATER)) {
			return new String("Water");
		}
		else if (this.equals(POPULATION)) {
			return new String("Population");
		}
		else {
			return new String("None");
		}
	}
}
