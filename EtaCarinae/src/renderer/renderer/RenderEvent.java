package renderer.renderer;

import java.util.ArrayList;

import control.event.UpdateEvent;
import control.event.UpdateEventType;

import renderer.scene.SceneNode;

public class RenderEvent extends UpdateEvent {
	private static final long serialVersionUID = -3808870041400671447L;
	
	private ArrayList<SceneNode> sceneNodes = new ArrayList<SceneNode>();
	
	public RenderEvent(Object source, ArrayList<SceneNode> sceneNodes) {
		super(source, UpdateEventType.OBJECT_DISPLAY);
		this.sceneNodes = sceneNodes;
	}
	
	public ArrayList<SceneNode> getSceneNodes() {
		return sceneNodes;
	}
}
