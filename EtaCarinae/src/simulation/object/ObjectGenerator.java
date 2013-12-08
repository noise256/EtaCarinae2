package simulation.object;

import java.util.ArrayList;

public interface ObjectGenerator {
	public ArrayList<GameObject> getGeneratedObjects();
	
	public void clearGeneratedObjects();
}
