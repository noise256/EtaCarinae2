package simulation.ui;

import java.util.Collection;
import java.util.Hashtable;

public class UIPanelManager {
	private static Hashtable<String, UIPanel> uiPanels = new Hashtable<String, UIPanel>();
	
	public static void addUIPanel(String name, UIPanel uiPanel) {
		uiPanels.put(name, uiPanel);
	}
	
	public static UIPanel getUIPanel(String name) {
		return uiPanels.get(name);
	}
	
	public static Collection<UIPanel> getUIPanels() {
		return uiPanels.values();
	}
}
