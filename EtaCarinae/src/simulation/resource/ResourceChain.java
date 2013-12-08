package simulation.resource;

import java.util.ArrayList;

import simulation.enums.FacilityType;
import simulation.enums.FertilityType;
import simulation.enums.ResourceType;

public enum ResourceChain {
	POPULATION(ResourceType.POPULATION, FacilityType.NONE, FertilityType.NONE, new ResourceType[] {ResourceType.FOOD, ResourceType.WATER}, false),
	HYDROGEN(ResourceType.HYDROGEN, FacilityType.HYDROGEN_REFINERY, FertilityType.LIQUID_WATER, new ResourceType[] {}, true),
	FOOD(ResourceType.FOOD, FacilityType.HYDROPONICS, FertilityType.NONE, new ResourceType[] {ResourceType.FUEL}, true),
	WATER(ResourceType.WATER, FacilityType.WATER_REFINERY, FertilityType.C20_ATMOSPHERE, new ResourceType[] {ResourceType.HYDROGEN}, true),
	FUEL(ResourceType.FUEL, FacilityType.FUEL_REFINERY, FertilityType.C20_ATMOSPHERE, new ResourceType[] {ResourceType.HYDROGEN}, true);
	
	private ResourceType resourceType;
	private FacilityType facilityType;
	private FertilityType fertilityType;
	
	private ResourceType[] requiredResources;
	
	private boolean tradeable;
	
	ResourceChain(ResourceType resourceType, FacilityType facilityType, FertilityType fertilityType, ResourceType[] requiredResources, boolean tradeable) {
		this.resourceType = resourceType;
		this.facilityType = facilityType;
		this.fertilityType = fertilityType;
		this.requiredResources = requiredResources;
		this.tradeable = tradeable;
	}
	
	public ArrayList<ResourceType> requiredResources() {
		ArrayList<ResourceType> collection = new ArrayList<ResourceType>();
		
		for (int i = 0; i < requiredResources.length; i++) {
			collection.add(requiredResources[i]);
		}
		
		return collection;
	}
	
	public ResourceType resourceType() {
		return resourceType;
	}
	
	public FacilityType facilityType() {
		return facilityType;
	}
	
	public FertilityType fertilityType() {
		return fertilityType;
	}
	
	public boolean tradeable() {
		return tradeable;
	}
}
