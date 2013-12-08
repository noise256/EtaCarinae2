package simulation.enums;

//TODO rewrite all resource chain enums as classes with names and values
public enum FacilityType {
	NONE(0), FUEL_REFINERY(10), HYDROGEN_REFINERY(5), HYDROPONICS(5), WATER_REFINERY(5);
	
	private int productionTime;
	
	private FacilityType(int productionTime) {
		
	}
	
	public int getProductionTime() {
		return productionTime;
	}
	
	@Override
	public String toString() {
		if (this.equals(FUEL_REFINERY)) {
			return new String("Fuel Refinery");
		}
		else if (this.equals(HYDROGEN_REFINERY)) {
			return new String("Hydrogen Refinery");
		}
		else if (this.equals(HYDROPONICS)) {
			return new String("Hydroponics");
		}
		else if (this.equals(WATER_REFINERY)) {
			return new String("Water Refinery");
		}
		else {
			return new String("None");
		}
	}
}
