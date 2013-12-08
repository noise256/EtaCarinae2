package simulation.ships;

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
import simulation.ai.command.AgentCommand.CommandType;
import simulation.ai.command.AttackCommand;
import simulation.ai.command.MoveToCommand;
import simulation.enums.ZDepth;
import simulation.object.GameObject;
import simulation.object.PhysicalObject;
import simulation.object.PhysicalProfile;
import simulation.projectile.Weapon;
import util.collision.Circle;
import util.collision.Shape;
import util.math.MathBox;

public abstract class Ship extends PhysicalObject implements PhysicalAgent {
	private ArrayList<GameObject> generatedObjects = new ArrayList<GameObject>();
	
	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	
	private AgentCommand agentCommand;
	
	protected ShipProfile shipSpecification;
	
	public Ship(float[] position, float[] zDepths, int diameter, String[] texturePaths, int[] textureSizes, float[] velocity, float[] rotation, PhysicalProfile physicalSpecification, ShipProfile shipSpecification) {
		super(position, zDepths, diameter, texturePaths, textureSizes, velocity, rotation, physicalSpecification);
		
		this.shipSpecification = shipSpecification;
	}

	public ShipProfile getShipSpecification() {
		return shipSpecification;
	}
	
	public void addWeapon(Weapon weapon) {
		weapons.add(weapon);
	}
	
	@Override
	public void update() {
		if (agentCommand != null) {
			if (agentCommand.getCommandType() == CommandType.MOVE_TO_COMMAND) {
				moveTo(((MoveToCommand) agentCommand).getDestination(), ((MoveToCommand) agentCommand).getOrientation());
			}
			else if (agentCommand.getCommandType() == CommandType.ATTACK_COMMAND) {
				PhysicalObject target = ((AttackCommand) agentCommand).getTarget();
				
				float[] destinationVector = MathBox.subtractVectors(target.getAbsolutePosition(), getAbsolutePosition());
				float destinationMagnitude = MathBox.getVectorMagnitude(destinationVector);
				
				float[] attackDestination = MathBox.scalarVector(MathBox.normaliseVector(destinationVector), destinationMagnitude - 250.0f);
				
				moveTo(attackDestination, null);
				
				for (Weapon weapon : weapons) {
					weapon.update();
					generatedObjects.addAll(weapon.getProjectiles());
					weapon.getProjectiles().clear();
				}
			}
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
				
				VBOSceneNode shipSceneNode = new VBOSceneNode(shaderProgram, objData, getAbsolutePosition(), getRotationAngle(), zDepths[i], GL3bc.GL_TRIANGLES);
				shipSceneNode.setVisible(true);
				
				sceneNodes.put(this + texturePaths[i] + "_ShipSceneNode", shipSceneNode);
			}
			
			FloatBuffer selectionBuffer = VBOManager.getFloatBuffer("DefaultTexturedQuad");
			
			OBJData selectionObjData = new OBJData(selectionBuffer, "selection_ring_green.png", new float[] {diameter * 0.75f, diameter * 0.75f, 1.0f}, 2, 5, new int[] {3, 2}, new int[] {0, 3}, false, true);
			sceneNodes.put("Selection" + "_ShipSceneNode", new VBOSceneNode(shaderProgram, selectionObjData, getAbsolutePosition(), 0.0f, ZDepth.SELECTION.depth(), GL3bc.GL_TRIANGLES));
			
			FloatBuffer planBuffer = VBOManager.getFloatBuffer("DefaultTexturedQuad");
			
			OBJData plannedObjData = new OBJData(planBuffer, texturePaths[0], new float[] {textureLengths[0]/2, textureLengths[0]/2, 1.0f}, 2, 5, new int[] {3, 2}, new int[] {0, 3}, false, true);
			sceneNodes.put("Plan" + "_ShipSceneNode", new VBOSceneNode(shaderProgram, plannedObjData, plannedDestination, 0.0f, ZDepth.TRADE_SHIP.depth(), GL3bc.GL_TRIANGLES));
		}
		else {
			//TODO set these for specific nodes by name, like this we set the position of the planned node to the position of the actual ship and then change it again afterwards
			Enumeration<SceneNode> elements = sceneNodes.elements();
			while (elements.hasMoreElements()) {
				SceneNode element = elements.nextElement();
				element.setNodePosition(getAbsolutePosition());
				
				if (element instanceof VBOSceneNode) {
					((VBOSceneNode) element).setNodeRotation(getRotationAngle());
				}
			}
			
			if (selected || highlighted) {
				sceneNodes.get("Selection" + "_ShipSceneNode").setVisible(true);
			}
			else {
				sceneNodes.get("Selection" + "_ShipSceneNode").setVisible(false);
			}
			
			if (planned) {
				sceneNodes.get("Plan" + "_ShipSceneNode").setVisible(true);				
				sceneNodes.get("Plan" + "_ShipSceneNode").setNodePosition(plannedDestination);
				((VBOSceneNode) sceneNodes.get("Plan" + "_ShipSceneNode")).setNodeRotation(MathBox.atan2(plannedOrientation[1], plannedOrientation[0]) - MathBox.PI/2);
			}
			else {
				sceneNodes.get("Plan" + "_ShipSceneNode").setVisible(false);
			}
		}
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
	
	@Override
	public Shape getBounds() {
		return new Circle(getAbsolutePosition(), diameter/2);
	}
	
	@Override
	public void addInput(AgentCommand agentCommand) {
		this.agentCommand = agentCommand;
	}
}
