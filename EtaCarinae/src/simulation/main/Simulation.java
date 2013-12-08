package simulation.main;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import renderer.newt.GWindow;
import renderer.vbo.VBOManager;
import simulation.colony.Colony;
import simulation.colony.ColonyManager;
import simulation.enums.FertilityType;
import simulation.enums.ResourceType;
import simulation.enums.ZDepth;
import simulation.environment.CelestialTexture;
import simulation.environment.GalaxyProfile;
import simulation.environment.Planet;
import simulation.environment.SolarSystem;
import simulation.object.PhysicalProfile;
import simulation.projectile.ProjectileProfile;
import simulation.projectile.Weapon;
import simulation.projectile.WeaponProfile;
import simulation.resource.ResourceChain;
import simulation.resource.ResourceChainManager;
import simulation.ships.ShipProfile;
import simulation.trading.TradeCompany;
import simulation.trading.TradeShip;
import simulation.ui.UIPanelManager;
import simulation.ui.UITextPanel;
import util.math.MathBox;
import util.timer.Timer;
import util.timer.TimerManager;

import com.jogamp.common.nio.Buffers;

public class Simulation extends SimulationManager {
	//TODO alternative method of generating water and fuel if the planet has the liquid water fertility
	@Override
	protected void generate() {
		//Create FloatBuffers
		FloatBuffer defaultTexturedQuad = Buffers.newDirectFloatBuffer(30);
	    
		defaultTexturedQuad.put(-1.0f);
		defaultTexturedQuad.put(-1.0f);
		defaultTexturedQuad.put(0.0f);
		
		defaultTexturedQuad.put(0.0f);
		defaultTexturedQuad.put(0.0f);
		
		defaultTexturedQuad.put(1.0f);
		defaultTexturedQuad.put(-1.0f);
		defaultTexturedQuad.put(0.0f);
		
		defaultTexturedQuad.put(1.0f);
		defaultTexturedQuad.put(0.0f);
		
		defaultTexturedQuad.put(-1.0f);
		defaultTexturedQuad.put(1.0f);
		defaultTexturedQuad.put(0.0f);
		
		defaultTexturedQuad.put(0.0f);
		defaultTexturedQuad.put(1.0f);
		
		defaultTexturedQuad.put(1.0f);
		defaultTexturedQuad.put(-1.0f);
		defaultTexturedQuad.put(0.0f);
		
		defaultTexturedQuad.put(1.0f);
		defaultTexturedQuad.put(0.0f);
		
		defaultTexturedQuad.put(-1.0f);
		defaultTexturedQuad.put(1.0f);
		defaultTexturedQuad.put(0.0f);
		
		defaultTexturedQuad.put(0.0f);
		defaultTexturedQuad.put(1.0f);
		
		defaultTexturedQuad.put(1.0f);
		defaultTexturedQuad.put(1.0f);
		defaultTexturedQuad.put(0.0f);
		
		defaultTexturedQuad.put(1.0f);
		defaultTexturedQuad.put(1.0f);
		
		defaultTexturedQuad.rewind();
		
		VBOManager.addFloatBuffer("DefaultTexturedQuad", defaultTexturedQuad);
		
		//Create UI
		UIPanelManager.addUIPanel("SelectedPanel", new UITextPanel(new float[] {100.0f, GWindow.screenHeight/4}, 200.0f, GWindow.screenHeight/2, "square_box_3.png"));
		
		//Generate resource chains
		ResourceChainManager.addResourceChain(ResourceChain.FOOD, 1);
		ResourceChainManager.addResourceChain(ResourceChain.HYDROGEN, 1);
		ResourceChainManager.addResourceChain(ResourceChain.WATER, 1);
		ResourceChainManager.addResourceChain(ResourceChain.FUEL, 1);
		ResourceChainManager.addResourceChain(ResourceChain.POPULATION, 1);
		
		//generate random galaxy
		int numSystems = GalaxyProfile.DEFAULT.getNumSystems();
		int systemSpread = GalaxyProfile.DEFAULT.getSystemSpread();
		
		int minSystemRadius = GalaxyProfile.DEFAULT.getMinSystemRadius();
		int maxSystemRadius = GalaxyProfile.DEFAULT.getMaxSystemRadius();
		
		int minStarRadius = GalaxyProfile.DEFAULT.getMinStarRadius();
		int maxStarRadius = GalaxyProfile.DEFAULT.getMaxStarRadius();
		
		int minSystemDistance = GalaxyProfile.DEFAULT.getMinSystemDistance();
		
		//generate origin system
		ArrayList<SolarSystem> solarSystems = new ArrayList<SolarSystem>();
		
		int systemRadius = MathBox.nextInt(maxSystemRadius - minSystemRadius) + minSystemRadius;
		int starRadius = MathBox.nextInt(maxStarRadius - minStarRadius) + minStarRadius;
		
		float[] systemPosition = new float[] {500.0f, 0.0f};
		
		String starTexture = CelestialTexture.values()[MathBox.nextInt(CelestialTexture.values().length)].path();
		SolarSystem system = new SolarSystem(systemPosition, new float[] {ZDepth.SYSTEM_WELL.depth(), ZDepth.STAR.depth()}, systemRadius*2, new String[] {"system_well_low_opacity.png", starTexture}, new int[] {systemRadius*2, starRadius*2}, "System " + Integer.toString(0), starRadius*2);
		
		solarSystems.add(system);
		
		System.err.println("Finished generating origin system.");
		
		//generate random systems
		for (int i = 1; i < numSystems; i++) {
			systemRadius = MathBox.nextInt(maxSystemRadius - minSystemRadius) + minSystemRadius;
			starRadius = MathBox.nextInt(maxStarRadius - minStarRadius) + minStarRadius;
			
			systemPosition = null;
			boolean foundPosition = false;
			while(!foundPosition) {
				foundPosition = true;
				
				systemPosition = new float[] {MathBox.nextFloat() * MathBox.nextSign(), MathBox.nextFloat() * MathBox.nextSign()};
				systemPosition = MathBox.scalarVector(MathBox.normaliseVector(systemPosition), MathBox.nextFloat() * systemSpread - systemSpread / 2);
				
				for (int j = 0; j < solarSystems.size(); j++) {
					if (MathBox.getVectorDistance(solarSystems.get(j).getAbsolutePosition(), systemPosition) < minSystemDistance) {
						foundPosition = false;
					}
				}
			}
			
			starTexture = CelestialTexture.values()[MathBox.nextInt(CelestialTexture.values().length)].path();
			system = new SolarSystem(systemPosition, new float[] {ZDepth.SYSTEM_WELL.depth(), ZDepth.STAR.depth()}, systemRadius*2, new String[] {"system_well_low_opacity.png", starTexture}, new int[] {systemRadius*2, starRadius*2}, "System " + Integer.toString(i), starRadius*2);
			
			solarSystems.add(system);
		}
		
		addGameObjects(solarSystems);
		
		System.err.println("Finished generating " + solarSystems.size() + " systems.");
		
		//generate planets
		int minPlanets = GalaxyProfile.DEFAULT.getMinPlanets();
		int maxPlanets = GalaxyProfile.DEFAULT.getMaxPlanets();
		
		int minPlanetRadius = GalaxyProfile.DEFAULT.getMinPlanetRadius();
		
		int minFertilities = GalaxyProfile.DEFAULT.getMinFertilities();
		int maxFertilities = GalaxyProfile.DEFAULT.getMaxFertilities();
		
		FertilityType[] fertilityList = FertilityType.values();
		
		ArrayList<Planet> planets = new ArrayList<Planet>();
		for (SolarSystem solarSystem : solarSystems) {
			int numPlanets = MathBox.nextInt(maxPlanets - minPlanets) + minPlanets;
			int maxPlanetRadius = MathBox.max(solarSystem.getStarSize() / 10, 2); 
			for (int j = 0; j < numPlanets; j++) {
				
				int planetSize = MathBox.nextInt(maxPlanetRadius - minPlanetRadius) + minPlanetRadius;
				int numFertilities = MathBox.nextInt(maxFertilities - minFertilities + 1) + minFertilities;
				
				float[] planetPosition = new float[] {
						(MathBox.nextFloat() * (solarSystem.getDiameter()/2 - solarSystem.getStarSize()/2 - planetSize) + (solarSystem.getStarSize()/2 + planetSize) - solarSystem.getDiameter()/10) * (MathBox.nextBoolean() ? 1 : -1), 
						(MathBox.nextFloat() * (solarSystem.getDiameter()/2 - solarSystem.getStarSize()/2 - planetSize) + (solarSystem.getStarSize()/2 + planetSize) - solarSystem.getDiameter()/10) * (MathBox.nextBoolean() ? 1 : -1)}
				;
				
				ArrayList<FertilityType> fertilities = new ArrayList<FertilityType>();
				for (int i = 0; i < numFertilities; i++) {
					fertilities.add(fertilityList[MathBox.nextInt(fertilityList.length - 1) + 1]);
				}
				
				Planet planet = new Planet(planetPosition, new float[] {ZDepth.PLANET.depth()}, planetSize*2, new String[] {"ice_planet.png"}, new int[] {planetSize*2}, "Planet " + (j + 1), solarSystem, fertilities);
				solarSystem.addCelestial(planet);
				planets.add(planet);
			}
		}
		
		addGameObjects(planets);
		
		System.err.println("Finished generating " + planets.size() + " planets");
		
		
		//generate colonies
		int maxColonies = 1;
		ArrayList<Colony> colonies = new ArrayList<Colony>();
		for (Planet planet : planets) {
			int numColonies = planet.getFertilities().size() > 0 ? MathBox.nextInt(maxColonies) + 1 : 0;
			
			for (int i = 0; i < numColonies; i++) {
				Colony colony = new Colony(planet.getSolarSystem().getName() + " " + planet.getName(), planet);
				colony.addResource(ResourceType.POPULATION, 10);
				
				colonies.add(colony);
				planet.addColony(colony);
			}
		}
		
		ColonyManager.addColonies(colonies);
		addAgents(colonies);
		
		System.err.println("Finished generating " + colonies.size() + " colonies.");
		
		//Generate Trade Ships
		TradeCompany tradeCompany1 = new TradeCompany("Test Trade Company", 100000);
		addAgent(tradeCompany1);
		
		System.err.println("Finished generating trade companies.");
		
		//Generate controlled ships
		TradeShip controlledShip = new TradeShip(new float[] {0, 0}, new float[] {ZDepth.TRADE_SHIP.depth()}, 100, new String[] {"trade_ship_dark_blue.png"}, new int[] {100}, new float[] {0.0f, 0.0f}, new float[] {1.0f, 0.0f}, PhysicalProfile.DEFAULT, ShipProfile.DEFAULT, null);
		controlledShip.addWeapon(new Weapon(controlledShip, WeaponProfile.DEFAULT, ProjectileProfile.DEFAULT_DUMB));
		
		addAgent(controlledShip);
		addGameObject(controlledShip);
		
		TradeShip controlledShip2 = new TradeShip(new float[] {100, 0}, new float[] {ZDepth.TRADE_SHIP.depth()}, 100, new String[] {"trade_ship_dark_blue.png"}, new int[] {100}, new float[] {0.0f, 0.0f}, new float[] {1.0f, 0.0f}, PhysicalProfile.DEFAULT, ShipProfile.DEFAULT, null);
		addAgent(controlledShip2);
		addGameObject(controlledShip2);
		
		System.err.println("Finished generating controlled ships.");
		
		TimerManager.addTimer("mainTimer", new Timer(1));
	}
}
