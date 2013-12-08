package simulation.trading;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import simulation.colony.Colony;
import util.math.MathBox;

public class TradeManager {
	private static Hashtable<Colony, ArrayList<Trade>> tradeList = new Hashtable<Colony, ArrayList<Trade>>();
	
	public static void addTrade(Colony colony, ArrayList<Trade> trades) {
		tradeList.put(colony, trades);
	}
	
	public static ArrayList<Trade> getTrades() {
		ArrayList<Trade> trades = new ArrayList<Trade>();
		
		Enumeration<Colony> keys = tradeList.keys();
		
		while (keys.hasMoreElements()) {
			trades.addAll(tradeList.get(keys.nextElement()));
		}
		
		return trades;
	}
	
	public static ArrayList<Trade> getClosestTrades(float[] position) {
		Colony closestColony = null;
		float distance = Float.MAX_VALUE;
		
		Enumeration<Colony> keys = tradeList.keys();
		
		while (keys.hasMoreElements()) {
			Colony key = keys.nextElement();
			float keyDistance = MathBox.getVectorDistance(key.getPlanet().getAbsolutePosition(), position); 
			if (keyDistance < distance) {
				closestColony = key;
				distance = keyDistance;
			}
		}
		
		return tradeList.get(closestColony);
	}
}