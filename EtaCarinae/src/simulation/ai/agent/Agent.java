package simulation.ai.agent;

import java.util.ArrayList;

import simulation.object.GameObject;
import simulation.object.ObjectGenerator;

public interface Agent extends ObjectGenerator {
	public void update();
	
	public ArrayList<GameObject> getGeneratedObjects();
}
