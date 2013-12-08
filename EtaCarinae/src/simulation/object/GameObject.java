package simulation.object;

import java.util.ArrayList;
import java.util.Hashtable;

import renderer.scene.ObjectView;
import renderer.scene.SceneNode;
import simulation.ui.UIObject;
import util.collision.Collidable;

public abstract class GameObject implements UIObject, ObjectView, Interactable, Collidable {
	protected Hashtable<String, SceneNode> sceneNodes = new Hashtable<String, SceneNode>();
	
	protected boolean alive = true;
	
	protected String[] texturePaths; //TODO replace with hashtable so we can name this shit
	protected int[] textureLengths;
	
	protected float[] zDepths;
	
	protected boolean highlighted;
	protected boolean selected;
	
	protected float[] position;
	
	protected int diameter;
	
	public GameObject(float[] position, float[] zDepths, int diameter, String[] texturePaths, int[] textureLengths) {
		if (position.length != 2) {
			throw new IllegalArgumentException("Illegal position length.");
		}
		
		this.position = position;
		this.zDepths = zDepths;
		this.diameter = diameter;
		this.texturePaths = texturePaths;
		this.textureLengths = textureLengths;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public abstract float[] getAbsolutePosition();
	
	@Override
	public ArrayList<SceneNode> getView() {
		return new ArrayList<SceneNode>(sceneNodes.values());
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}
	
	@Override
	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}
	
	@Override
	public boolean isHighlighted() {
		return highlighted;
	}
	
	public float[] getPosition() {
		return position;
	}
	
	public int getDiameter() {
		return diameter;
	}
}
