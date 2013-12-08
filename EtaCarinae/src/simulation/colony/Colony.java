package simulation.colony;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import simulation.ai.agent.Agent;
import simulation.enums.FacilityType;
import simulation.enums.ResourceType;
import simulation.environment.Planet;
import simulation.object.GameObject;
import simulation.resource.ResourceChainManager;
import simulation.trading.Trade;
import simulation.trading.TradeManager;
import simulation.ui.UIObject;
import util.math.MathBox;
import util.timer.TimerManager;

public class Colony implements Agent, UIObject {
	private static final int MAX_RESOURCES = 200;
	
	private ArrayList<GameObject> generatedObjects = new ArrayList<GameObject>();
	
	private String name;
	private Planet planet;
	
	private Hashtable<FacilityType, Integer> facilities = new Hashtable<FacilityType, Integer>();
	private Hashtable<ResourceType, Integer[]> resources = new Hashtable<ResourceType, Integer[]>();
	
	private ArrayList<ResourceType> requiredResources = new ArrayList<ResourceType>();
	
	private Hashtable<ResourceType, Integer> requiredTrades = new Hashtable<ResourceType, Integer>();
	
	private ArrayList<Trade> currentTrades = new ArrayList<Trade>();
	
	public Colony(String name, Planet planet) {
		this.name = name;
		this.planet = planet;
		
		//Construct facilities
		for (FacilityType facilityType : FacilityType.values()) {
			if (planet.hasFertility(ResourceChainManager.getRequiredFertility(facilityType))) {
				facilities.put(facilityType, 0);
			}
		}
	}
	
	@Override
	public void update() {
		//Produce Resources
		int currentTime = TimerManager.getTimer("mainTimer").getTime();
		
		Enumeration<FacilityType> facilityKeys = facilities.keys();
		while (facilityKeys.hasMoreElements()) {
			FacilityType facility = facilityKeys.nextElement();
			if (currentTime >= facilities.get(facility) + facility.getProductionTime()) {
				ResourceType resourceToProduce = ResourceChainManager.getResourceChain(facility).resourceType();
				if (canProduceResource(resourceToProduce) && getResourceAmount(resourceToProduce) < Colony.MAX_RESOURCES) {
					ArrayList<ResourceType> requiredResources = ResourceChainManager.getResourceChain(facility).requiredResources();
					for (ResourceType requiredResource : requiredResources) {
						removeResource(requiredResource, 1);
					}	
					addResource(resourceToProduce, 1);
				}
				facilities.put(facility, currentTime);
			}
		}
		
		//get required resources
		requiredResources = getRequiredResources();

		//Increment time without resource
		Enumeration<ResourceType> currentTradesKeys = requiredTrades.keys();
		while(currentTradesKeys.hasMoreElements()) {
			ResourceType trade = currentTradesKeys.nextElement();
			requiredTrades.put(trade, requiredTrades.get(trade) + 1);
		}
		
		//Get required trades
		ArrayList<ResourceType> newRequiredTrades = getRequiredTrades(requiredResources);
		currentTradesKeys = requiredTrades.keys();
		
		for (ResourceType newTrade : newRequiredTrades) {
			boolean contained = false;
			while (currentTradesKeys.hasMoreElements()) {
				if (currentTradesKeys.nextElement().equals(newTrade)) {
					contained = true;
				}
			}
			
			if (!contained) {
				requiredTrades.put(newTrade, 0);
			}
		}
		
		//Add trades
		currentTrades.clear();
		
		Enumeration<ResourceType> requiredTradesKeys = requiredTrades.keys();
		while(requiredTradesKeys.hasMoreElements()) {
			ResourceType requiredTrade = requiredTradesKeys.nextElement(); 
			
			//find source for trade
			Colony closestProducer = ColonyManager.getClosestSeller(requiredTrade, 1, planet.getAbsolutePosition());
			
//			if (this.equals(closestProducer)) {
//				System.err.println("Buyer equals seller.");
//			}
			
			if (closestProducer != null) {
				int basePrice = ResourceChainManager.getPrice(requiredTrade);
				float distance = (int) MathBox.getVectorDistance(closestProducer.getPlanet().getAbsolutePosition(), planet.getAbsolutePosition());
				int time = requiredTrades.get(requiredTrade);
				float buyingPrice =  distance * basePrice + time * basePrice;
				
//				System.out.println("distance = " + distance + " basePrice = " + basePrice + " time = " + time);
				
				currentTrades.add(new Trade(this, requiredTrade, buyingPrice));
			}
		}
		
		TradeManager.addTrade(this, currentTrades);
	}
	
	private ArrayList<ResourceType> getRequiredTrades(ArrayList<ResourceType> requiredResources) {
		ArrayList<ResourceType> requiredTrades = new ArrayList<ResourceType>();
		for (ResourceType potentialTrade : requiredResources) {
			if (!canProduceResource(potentialTrade)) {
				requiredTrades.add(potentialTrade);
			}
		}
		return requiredTrades;
	}
	
	public boolean canProduceResource(ResourceType resourceType) {
		if (!planet.hasFertility(ResourceChainManager.getRequiredFertility(resourceType))) {
			return false;
		}
		
		ArrayList<ResourceType> requiredResources = ResourceChainManager.getResourceChain(resourceType).requiredResources();
		
		for (ResourceType requiredResource : requiredResources) {
			if (getResourceAmount(requiredResource) <= 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public void addResource(ResourceType resource, int amount) {
		if (!resources.containsKey(resource)) {
			resources.put(resource, new Integer[] {MathBox.min(MAX_RESOURCES, amount), 0});
		}
		else {
			resources.put(resource, new Integer[] {MathBox.min(MAX_RESOURCES, resources.get(resource)[0] + amount), resources.get(resource)[1]});
		}
		
		requiredTrades.put(resource, 0);
	}
	
	public boolean removeResource(ResourceType resource, int amount) {
		if (!resources.containsKey(resource)) {
			return false;
		}
		else if (resources.get(resource)[0] < amount) {
			return false;
		}
		else {
			resources.put(resource, new Integer[] {resources.get(resource)[0] - amount, resources.get(resource)[1]});
		}
		return true;
	}
	
	private ArrayList<ResourceType> getRequiredResources() {
		ArrayList<ResourceType> requiredResources = new ArrayList<ResourceType>();
		
		Enumeration<ResourceType> resourceTypes = resources.keys();
		while (resourceTypes.hasMoreElements()) {
			ResourceType resource = resourceTypes.nextElement();

			ArrayList<ResourceType> neededResources = getRequiredResources(new ArrayList<ResourceType>(), resource);
			
			for (ResourceType neededResource : neededResources) {
				if (!requiredResources.contains(neededResource)) {
					if (!planet.hasFertility(ResourceChainManager.getRequiredFertility(neededResource)) && getResourceAmount(neededResource) < Colony.MAX_RESOURCES) {
						requiredResources.add(neededResource);
					}
				}
			}
		}
		
		return requiredResources;
	}
	
	private ArrayList<ResourceType> getRequiredResources(ArrayList<ResourceType> requiredResources, ResourceType resource) {
		ArrayList<ResourceType> neededResources = ResourceChainManager.getResourceChain(resource).requiredResources();
		
		neededResources.removeAll(requiredResources);
		
		if (neededResources.size() == 0) {
			return requiredResources;
		}
		
		else {
			requiredResources.addAll(neededResources);
			
			for (ResourceType resourceType : neededResources) {
				getRequiredResources(requiredResources, resourceType);
			}
		}
		
		return requiredResources;
	}
	
	public void reserveResources(ResourceType resource, int amount) {
		if (this.getResourceAmount(resource) >= amount) {
			resources.put(resource, new Integer[] {resources.get(resource)[0] - amount, resources.get(resource)[1] + amount});
		}
	}
	
	@Override
	public ArrayList<String> getUIStrings() {
		ArrayList<String> uiStrings = new ArrayList<String>();
		
		uiStrings.add("Colony Name: " + name);
		uiStrings.add("Population: " + resources.get(ResourceType.POPULATION)[0].toString());
		
		if (!facilities.isEmpty()) {
			uiStrings.add("-----------");
			uiStrings.add("Facilities:");
			uiStrings.add("-----------");
			Enumeration<FacilityType> facilityKeys = facilities.keys();
			while (facilityKeys.hasMoreElements()) {
				uiStrings.add(facilityKeys.nextElement().toString());
			}
		}
		
		if (!resources.isEmpty()) {
			uiStrings.add("---------------------");
			uiStrings.add("Resources Stockpiled:");
			uiStrings.add("---------------------");
			Enumeration<ResourceType> resourceKeys = resources.keys();
			while (resourceKeys.hasMoreElements()) {
				ResourceType resource = resourceKeys.nextElement();
				if (!resource.equals(ResourceType.POPULATION)) {
					uiStrings.add(resource.toString() + " : " + resources.get(resource)[0].toString());
				}
				
			}
		}
		
		if (!requiredResources.isEmpty()) {
			uiStrings.add("-------------------");
			uiStrings.add("Required Resources:");
			uiStrings.add("-------------------");
			for (ResourceType resource : requiredResources) {
				uiStrings.add(resource.toString());
			}
		}
		
		return uiStrings;
	}
	
	public String getName() {
		return name;
	}
	
	public Hashtable<FacilityType, Integer> getFacilities() {
		return facilities;
	}
	
	public boolean hasFacility(FacilityType facilityType) {
		return facilities.contains(facilityType);
	}
	
	public Planet getPlanet() {
		return planet;
	}
	
	public int getResourceAmount(ResourceType resourceType) {
		if (resources.containsKey(resourceType)) {
			return resources.get(resourceType)[0] - resources.get(resourceType)[1];
		}
		else {
			return 0;
		}
	}

	@Override
	public ArrayList<GameObject> getGeneratedObjects() {
		return generatedObjects;
	}

	@Override
	public void clearGeneratedObjects() {
		generatedObjects.clear();
	}
}
