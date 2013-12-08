package simulation.enums;

public enum FertilityType {
	NONE, LIQUID_WATER, C20_ATMOSPHERE;
	
	@Override
	public String toString() {
		if (this.equals(LIQUID_WATER)) {
			return new String("Liquid Water");
		}
		else if (this.equals(C20_ATMOSPHERE)) {
			return new String("C20 Atmosphere");
		}
		else {
			return new String("None");
		}
	}
}
