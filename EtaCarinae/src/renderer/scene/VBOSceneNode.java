package renderer.scene;

import renderer.objdata.OBJData;
import renderer.shader.ShaderProgram;

public class VBOSceneNode extends SceneNode {
	protected OBJData objData;
	protected int mode;
	
	protected float nodeRotation;
	protected float nodeDepth;
	
	public VBOSceneNode(ShaderProgram shaderProgram, OBJData objData, float[] nodePosition, float nodeRotation, float nodeDepth, int mode) {
		super(shaderProgram, nodePosition);
		this.nodeRotation = nodeRotation;
		this.objData = objData;
		this.nodeDepth = nodeDepth;
		this.mode = mode;
	}
	
	public OBJData getOBJData() {
		return objData;
	}
	
	public int getMode() {
		return mode;
	}
	
	public float getNodeRotation() {
		return nodeRotation;
	}
	
	public void setNodeRotation(float nodeRotation) {
		this.nodeRotation = nodeRotation;
	}
	
	public float getNodeDepth() {
		return nodeDepth;
	}
}
