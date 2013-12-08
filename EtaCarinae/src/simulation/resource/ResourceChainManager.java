package simulation.resource;

import java.util.ArrayList;
import java.util.Hashtable;

import simulation.enums.FacilityType;
import simulation.enums.FertilityType;
import simulation.enums.ResourceType;


public class ResourceChainManager {
	private static ArrayList<ResourceChain> resourceChains = new ArrayList<ResourceChain>();
	private static Hashtable<ResourceType, Integer> priceIndex = new Hashtable<ResourceType, Integer>();
	
	public static FacilityType getRequiredFacility(ResourceType resourceType) {
		return getResourceChain(resourceType).facilityType();
	}
	
	public static FacilityType getRequiredFacility(FertilityType fertilityType) {
		return getResourceChain(fertilityType).facilityType();
	}
	
	public static FertilityType getRequiredFertility(ResourceType resourceType) {
		return getResourceChain(resourceType).fertilityType();
	}
	
	public static FertilityType getRequiredFertility(FacilityType facilityType) {
		return getResourceChain(facilityType).fertilityType();
	}
	
	public static ResourceChain getResourceChain(ResourceType resourceType) {
		for (ResourceChain resourceChain : resourceChains) {
			if (resourceChain.resourceType() == resourceType) {
				return resourceChain;
			}
		}
		return null;
	}
	
	public static ResourceChain getResourceChain(FacilityType facilityType) {
		for (ResourceChain resourceChain : resourceChains) {
			if (resourceChain.facilityType() == facilityType) {
				return resourceChain;
			}
		}
		return null;
	}
	
	public static ResourceChain getResourceChain(FertilityType fertilityType) {
		for (ResourceChain resourceChain : resourceChains) {
			if (resourceChain.fertilityType() == fertilityType) {
				return resourceChain;
			}
		}
		return null;
	}
	
	public static void addResourceChain(ResourceChain resourceChain, int price) {
		resourceChains.add(resourceChain);
		priceIndex.put(resourceChain.resourceType(), price);
	}
	
	public static void setPrice(ResourceType resource, int price) {
		priceIndex.put(resource, price);
	}
	
	public static int getPrice(ResourceType resource) {
		return priceIndex.get(resource);
	}
}
