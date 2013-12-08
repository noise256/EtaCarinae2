package simulation.trading;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import simulation.ai.agent.Agent;
import simulation.enums.ResourceType;
import simulation.object.PhysicalProfile;
import simulation.ships.Ship;
import simulation.ships.ShipProfile;
import util.math.MathBox;

public class TradeShip extends Ship implements Agent {
	private Trade currentTrade;
	
	private Hashtable<ResourceType, Integer> cargo = new Hashtable<ResourceType, Integer>();
	private int used;
	
	public TradeShip(float[] position, float[] zDepths, int diameter, String[] texturePaths, int[] textureSizes, float[] velocity, float[] rotation, PhysicalProfile physicalSpecification, ShipProfile shipSpecification, TradeCompany tradeCompany) {
		super(position, zDepths, diameter, texturePaths, textureSizes, velocity, rotation, physicalSpecification, shipSpecification);
	}

	public void addTrade(Trade trade) {
		this.currentTrade = trade;
	}
	
	public void addCargo(ResourceType resource, int amount) {
		if (cargo.containsKey(resource)) {
			cargo.put(resource, cargo.get(resource) + amount);
		}
		else {
			cargo.put(resource, amount);
		}
	}
	
	public void removeCargo(ResourceType resource) {
		cargo.remove(resource);
	}
	
	public int getFreeCargo() {
		return shipSpecification.capacity() - used;
	}
	
	public void removeTrade() {
		this.currentTrade = null;
	}

	public Trade getCurrentTrade() {
		return currentTrade;
	}
	
	public Hashtable<ResourceType, Integer> getCargo() {
		return cargo;
	}
	
	@Override 
	public ArrayList<String> getUIStrings() {
		ArrayList<String> uiStrings = new ArrayList<String>();
		
		uiStrings.add("Trade Ship Capacity: " + shipSpecification.capacity());
		
		uiStrings.add("Position: " + getAbsolutePosition()[0] + " " + getAbsolutePosition()[1]);
		uiStrings.add("Velocity: " + MathBox.getVectorMagnitude(velocity));
		uiStrings.add("Rotation: [" + rotation[0] + ", " + rotation[1] + "]");
		
		if (!cargo.isEmpty()) {
			uiStrings.add("------");
			uiStrings.add("Cargo:");
			uiStrings.add("------");
			Enumeration<ResourceType> keys = cargo.keys();
			while (keys.hasMoreElements()) {
				ResourceType key = keys.nextElement();
				uiStrings.add(key + " " + cargo.get(key).toString());
			}
		}
		
		if (currentTrade != null) {
			uiStrings.add("------");
			uiStrings.add("Trade: " + currentTrade.getResource());
			uiStrings.add("Source: " + currentTrade.getColony().getName());
			uiStrings.add("Price: " + currentTrade.getPrice());
			uiStrings.add("------");
		}
		return uiStrings;
	}
}
