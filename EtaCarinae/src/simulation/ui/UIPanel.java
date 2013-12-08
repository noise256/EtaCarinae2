package simulation.ui;

import java.util.ArrayList;
import java.util.Hashtable;

import renderer.scene.ObjectView;
import renderer.scene.SceneNode;
import simulation.object.GameObject;
import util.collision.Collidable;
import util.collision.Rectangle;
import util.collision.Shape;

public abstract class UIPanel implements ObjectView, Collidable {
	protected Hashtable<String, SceneNode> sceneNodes = new Hashtable<String, SceneNode>();
	
	protected GameObject textSource;
	
	protected String texturePath;
	
	protected float[] position;
	
	protected float width;
	protected float height;
	
	private boolean visible;
	
	public UIPanel(float[] position, float width, float height, String texturePath) {
		this.position = position;
		this.width = width;
		this.height = height;
		this.texturePath = texturePath;
	}

	@Override
	public ArrayList<SceneNode> getView() {
		return new ArrayList<SceneNode>(sceneNodes.values());
	}
	
	public float[] getPosition() {
		return position;
	}
	
	public void setTextSource(GameObject textSource) {
		this.textSource = textSource;
	}
	
	public boolean getVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public Shape getBounds() {
		return new Rectangle(position, width, height);
	}
}
