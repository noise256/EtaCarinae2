package simulation.projectile;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.media.opengl.GL3bc;

import renderer.objdata.OBJData;
import renderer.scene.SceneNode;
import renderer.scene.VBOSceneNode;
import renderer.shader.ShaderProgram;
import renderer.vbo.VBOManager;
import simulation.ai.agent.PhysicalAgent;
import simulation.ai.command.AgentCommand;
import simulation.ai.command.MoveCommand;
import simulation.ai.command.MoveToCommand;
import simulation.ai.command.AgentCommand.CommandType;
import simulation.object.GameObject;
import simulation.object.PhysicalObject;
import simulation.object.PhysicalProfile;
import util.collision.Circle;
import util.collision.Shape;

public class Projectile extends PhysicalObject implements PhysicalAgent {
	private ArrayList<GameObject> generatedObjects = new ArrayList<GameObject>();
	
	private AgentCommand agentCommand;
	
	private int life;
	
	public Projectile(float[] position, float[] velocity, float[] rotation, PhysicalProfile physicalProfile, ProjectileProfile projectileProfile) {
		super(position, projectileProfile.getZDepths(), projectileProfile.getDiameter(), projectileProfile.getTexturePaths(), projectileProfile.getTextureSizes(), velocity, rotation, physicalProfile);
		this.life = projectileProfile.getLife();
		
		if (projectileProfile.isDumb()) {
			agentCommand = new MoveCommand();
		}
	}

	@Override
	public void update() {
		life--;
		
		if (life <= 0) {
			alive = false;
			return;
		}
		
		if (agentCommand.getCommandType() == CommandType.MOVE_COMMAND) {
			move();
		}
		else if (agentCommand.getCommandType() == CommandType.MOVE_TO_COMMAND) {
			moveTo(((MoveToCommand) agentCommand).getDestination(), null);
		}
	}

	@Override
	public void updateView() {
		if (sceneNodes.isEmpty()) {
			ShaderProgram shaderProgram = new ShaderProgram(
					"defaultTextured", 
					"textured.vertex", 
					"textured.fragment", 
					new String[] {"attribute_Position", "attribute_TexCoord"}
			);
			
			for (int i = 0; i < texturePaths.length; i++) {
				FloatBuffer vertexBuffer = VBOManager.getFloatBuffer("DefaultTexturedQuad");
				OBJData objData = new OBJData(vertexBuffer, texturePaths[i], new float[] {textureLengths[i]/2, textureLengths[i]/2, 1.0f}, 2, 5, new int[] {3, 2}, new int[] {0, 3}, false, true);
				
				VBOSceneNode projectileSceneNode = new VBOSceneNode(shaderProgram, objData, getAbsolutePosition(), getRotationAngle(), zDepths[i], GL3bc.GL_TRIANGLES);
				projectileSceneNode.setVisible(true);
				
				sceneNodes.put(this + texturePaths[i] + "_ProjectileSceneNode", projectileSceneNode);
			}
		}
		else {
			Enumeration<SceneNode> elements = sceneNodes.elements();
			while (elements.hasMoreElements()) {
				SceneNode element = elements.nextElement();
				element.setNodePosition(getAbsolutePosition());
				
				if (element instanceof VBOSceneNode) {
					((VBOSceneNode) element).setNodeRotation(getRotationAngle());
				}
			}
		}
	}
	
	@Override
	public ArrayList<String> getUIStrings() {
		ArrayList<String> uiStrings = new ArrayList<String>();
		
		uiStrings.add("Velocity = [" + velocity[0] + ", " + velocity[1] + "]");
		uiStrings.add("Rotation = [" + rotation[0] + ", " + rotation[1] + "]");
		
		return uiStrings;
	}
	
	@Override
	public Shape getBounds() {
		return new Circle(getAbsolutePosition(), diameter/2);
	}

	@Override
	public void addInput(AgentCommand agentCommand) {
		this.agentCommand = agentCommand;
	}

	@Override
	public float[] getAbsolutePosition() {
		return position;
	}
	
	@Override
	public ArrayList<GameObject> getGeneratedObjects() {
		return generatedObjects;
	}

	@Override
	public void clearGeneratedObjects() {
		generatedObjects.clear();
	}
}
