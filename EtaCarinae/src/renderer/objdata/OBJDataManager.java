package renderer.objdata;

import java.util.Hashtable;


public class OBJDataManager {
	private static Hashtable<String, OBJData> objDataTable = new Hashtable<String, OBJData>();
	
	public static void loadOBJData(String path) {
		if (objDataTable.containsKey(path)) {
			System.err.println("The OBJData object designated: " + path + " already exists and has been loaded.");
		}
		else {
			objDataTable.put(path, OBJParser.parseOBJFile(path));
		}
	}
	
	public static OBJData retrieveOBJData(String path) {
		try {
			return objDataTable.get(path);
		}
		catch (NullPointerException e) {
			System.err.println("The ObjData object designated: " + path + " does not exist or has not been loaded.");
		}
		return null;
	}
}
