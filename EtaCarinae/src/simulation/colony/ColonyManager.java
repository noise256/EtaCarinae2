package simulation.colony;

import java.util.ArrayList;

import simulation.enums.ResourceType;
import util.math.MathBox;

public class ColonyManager {
	private static ArrayList<Colony> colonies = new ArrayList<Colony>();
	
	public static void addColony(Colony newColony) {
		colonies.add(newColony);
	}
	
	public static void addColonies(ArrayList<Colony> newColonies) {
		colonies.addAll(newColonies);
	}
	
	public static Colony getClosestSeller(ResourceType resource, int amount, float[] position) {
		Colony closest = null;
		
		ArrayList<Colony> producingColonies = new ArrayList<Colony>();
		for (Colony colony : colonies) {
			if (colony.getResourceAmount(resource) >= amount) {
				producingColonies.add(colony);
			}
		}
		
		double distance = Double.MAX_VALUE;
		for (int i = 0; i < producingColonies.size(); i++) {
			double newDistance = MathBox.getVectorDistance(position, producingColonies.get(i).getPlanet().getAbsolutePosition()); 
			if (newDistance < distance) {
				distance = newDistance;
				closest = producingColonies.get(i);
			}
		}
		
		return closest;
	}
}
