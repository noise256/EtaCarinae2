package renderer.scene;

import java.util.ArrayList;

import renderer.objdata.OBJData;
import renderer.shader.ShaderProgram;

public class UISceneNode extends SceneNode {
	private ArrayList<String> text = new ArrayList<String>();
	
	private OBJData objData;
	
	private float width;
	private float height;
	
	public UISceneNode(ShaderProgram shaderProgram, OBJData objData, float[] nodePosition, float width, float height) {
		super(shaderProgram, nodePosition);
		this.objData = objData;
		this.width = width;
		this.height = height;
	}
	
	public OBJData getOBJData() {
		return objData;
	}
	
	public void setText(ArrayList<String> text) {
		this.text = text;
	}
	
	public ArrayList<String> getText() {
		return text;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
}
