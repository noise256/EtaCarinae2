package simulation.trading;

import java.util.ArrayList;
import java.util.Hashtable;

import simulation.ai.agent.Agent;
import simulation.ai.command.MoveToCommand;
import simulation.colony.Colony;
import simulation.colony.ColonyManager;
import simulation.enums.ResourceType;
import simulation.enums.ZDepth;
import simulation.environment.GalaxyProfile;
import simulation.faction.Faction;
import simulation.faction.FactionType;
import simulation.object.GameObject;
import simulation.object.PhysicalProfile;
import simulation.resource.ResourceChainManager;
import simulation.ships.ShipProfile;
import util.collision.CollisionManager;
import util.math.MathBox;

public class TradeCompany extends Faction implements Agent {
	private ArrayList<GameObject> generatedObjects = new ArrayList<GameObject>();
	
	private ArrayList<TradeShip> tradeShips = new ArrayList<TradeShip>();
	
	private int balance;
	
	
	public TradeCompany(String name, int balance) {
		super(name, FactionType.TRADE_COMPANY);
		
		this.balance = balance;
	}
	
	@Override
	public void update() {
		TradeShip unassigned = getUnassignedTradeShip();
		if (unassigned == null) {
			unassigned = createTradeShip();
		}
		
		if (unassigned != null) {
			Trade trade = findTrade(unassigned.getAbsolutePosition(), unassigned.getFreeCargo());
			if (trade != null) {
				unassigned.addTrade(trade);
				trade.getColony().reserveResources(trade.getResource(), unassigned.getFreeCargo());
			}
		}
		
		for (TradeShip tradeShip : tradeShips) {
			float[] destination = null;
			
			Trade shipTrade = tradeShip.getCurrentTrade();
			Hashtable<ResourceType, Integer> shipCargo = tradeShip.getCargo();
			int freeCargo = tradeShip.getFreeCargo();
			
			if (shipTrade != null) {
				if (!shipCargo.containsKey(tradeShip.getCurrentTrade().getResource())) {
					Colony source = ColonyManager.getClosestSeller(shipTrade.getResource(), freeCargo, tradeShip.getAbsolutePosition());
					
					//collect trade
					if (source != null) {
						//collect cargo
						if (CollisionManager.checkCollision(tradeShip, source.getPlanet())) {
							if (source.removeResource(shipTrade.getResource(), MathBox.min(freeCargo, source.getResourceAmount(shipTrade.getResource())))) {
								tradeShip.addCargo(shipTrade.getResource(), freeCargo);
							}
							else {
								System.err.println("No resources at colony.");
							}
						}
						//move to origin
						destination = source.getPlanet().getAbsolutePosition();
					}

				}
				else {
					//deliver trade
					if (CollisionManager.checkCollision(tradeShip, shipTrade.getColony().getPlanet())) {
						//deliver cargo
						shipTrade.getColony().addResource(shipTrade.getResource(), shipCargo.get(shipTrade.getResource()));
						tradeShip.removeCargo(shipTrade.getResource());
						shipTrade = null;
					}
					else {
						//move to origin
						destination = shipTrade.getColony().getPlanet().getAbsolutePosition();
					}
				}
			}
			
			if (destination != null) {
				if (!MathBox.checkVectorEquality(destination, tradeShip.getAbsolutePosition())) {
					tradeShip.addInput(new MoveToCommand(destination, null));
				}
			}
		}
	}
	
	private Trade findTrade(float[] position, int amount) {
		ArrayList<Trade> trades = TradeManager.getTrades();
		
		if (!trades.isEmpty()) {
			Trade best = null;
			float price = 0;
			for (Trade trade : trades) {
				float newPrice = trade.getPrice() - MathBox.max(0.0f, (ResourceChainManager.getPrice(trade.getResource()) * MathBox.getVectorDistance(trade.getColony().getPlanet().getAbsolutePosition(), position))); 
				if (trade.getPrice() > price && ColonyManager.getClosestSeller(trade.getResource(), amount, position) != null) {
					best = trade;
					price = newPrice;
				}
			}
			
			return best;
		}
		
		return null;
	}
	
	private TradeShip getUnassignedTradeShip() {
		for (TradeShip tradeShip : tradeShips) {
			if (tradeShip.getCurrentTrade() == null) {
				return tradeShip;
			}
		}
		return null;
	}
	
	private TradeShip createTradeShip() {
		if (balance >= ShipProfile.DEFAULT.price()) {
			float systemSpread = GalaxyProfile.DEFAULT.getSystemSpread();
			balance -= ShipProfile.DEFAULT.price();
			
			TradeShip tradeShip = new TradeShip(new float[] {MathBox.nextFloat() * systemSpread - systemSpread / 2, MathBox.nextFloat() * systemSpread - systemSpread / 2}, new float[] {ZDepth.TRADE_SHIP.depth()}, 20, new String[] {"trade_ship_dark_blue.png"}, new int[] {10}, new float[] {0.0f, 0.0f}, new float[] {1.0f, 0.0f}, PhysicalProfile.DEFAULT, ShipProfile.DEFAULT, this); 
			
			tradeShips.add(tradeShip);
			generatedObjects.add(tradeShip);
			
			return tradeShip;
		}
		return null;
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
