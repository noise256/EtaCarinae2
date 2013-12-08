package renderer.scene;

import renderer.shader.ShaderProgram;

public abstract class SceneNode {
	private ShaderProgram shaderProgram;
	
	private float[] nodePosition;
	
	private boolean visible = false;
	
	public SceneNode(ShaderProgram shaderProgram, float[] nodePosition) {
		this.shaderProgram = shaderProgram;
		this.nodePosition = nodePosition;
	}
	
	public ShaderProgram getShaderProgram() {
		return shaderProgram;
	}
	
	public float[] getNodePosition() {
		return nodePosition;
	}
	
	public void setNodePosition(float[] nodePosition) {
		this.nodePosition = nodePosition;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
