package simulation.trading;

import simulation.colony.Colony;
import simulation.enums.ResourceType;

public class Trade {
	private Colony colony;
	
	private ResourceType resource;
	private float price;
	
	public Trade(Colony colony, ResourceType resource, float price) {
		this.colony = colony;
		this.resource = resource;
		this.price = price;
	}
	
	public Colony getColony() {
		return colony;
	}
	
	public ResourceType getResource() {
		return resource;
	}
	
	public float getPrice() {
		return price;
	}
}
