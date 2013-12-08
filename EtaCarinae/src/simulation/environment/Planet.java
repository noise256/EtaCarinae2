package simulation.environment;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL3bc;

import renderer.objdata.OBJData;
import renderer.scene.VBOSceneNode;
import renderer.shader.ShaderProgram;
import renderer.vbo.VBOManager;
import simulation.colony.Colony;
import simulation.enums.FertilityType;
import simulation.enums.ZDepth;
import util.collision.Circle;
import util.collision.Shape;
import util.math.MathBox;

public class Planet extends Celestial {
	private SolarSystem solarSystem;
	
	private ArrayList<Colony> colonies = new ArrayList<Colony>();
	private ArrayList<FertilityType> fertilities = new ArrayList<FertilityType>();
	
	public Planet(float[] position, float[] zDepths, int diameter, String[] texturePaths, int[] textureSizes, String name, SolarSystem solarSystem, ArrayList<FertilityType> fertilities) {
		super(position, zDepths, diameter, texturePaths, textureSizes, name);
		
		this.solarSystem = solarSystem;
		this.fertilities = fertilities != null ? fertilities : this.fertilities;
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
				
				VBOSceneNode planetSceneNode = new VBOSceneNode(shaderProgram, objData, getAbsolutePosition(), 0.0f, zDepths[i], GL3bc.GL_TRIANGLES);
				planetSceneNode.setVisible(true);
				
				sceneNodes.put(name + texturePaths[i] + "_PlanetSceneNode", planetSceneNode);
			}
			
			FloatBuffer vertexBuffer = VBOManager.getFloatBuffer("DefaultTexturedQuad");
			OBJData objData = new OBJData(vertexBuffer, "selection_ring_green.png", new float[] {diameter * 0.75f, diameter * 0.75f, 1.0f}, 2, 5, new int[] {3, 2}, new int[] {0, 3}, false, true);
			
			VBOSceneNode selectionSceneNode = new VBOSceneNode(shaderProgram, objData, getAbsolutePosition(), 0.0f, ZDepth.SELECTION.depth(), GL3bc.GL_TRIANGLES);
			selectionSceneNode.setVisible(true);
			
			sceneNodes.put(name + "_Selection" + "_PlanetSceneNode", selectionSceneNode);
		}
		
		if (selected || highlighted) {
			sceneNodes.get(name + "_Selection" + "_PlanetSceneNode").setVisible(true);
		}
		else {
			sceneNodes.get(name + "_Selection" + "_PlanetSceneNode").setVisible(false);
		}
	}
	
	@Override
	public float[] getAbsolutePosition() {
		return MathBox.addVectors(solarSystem.getAbsolutePosition(), position);
	}
	
	public ArrayList<FertilityType> getFertilities() {
		return fertilities;
	}
	
	public boolean hasFertility(FertilityType fertilityType) {
		return (fertilities.contains(fertilityType) || fertilityType == FertilityType.NONE);
	}
	
	public void addColony(Colony colony) {
		colonies.add(colony);
	}
	
	@Override
	public ArrayList<String> getUIStrings() {
		ArrayList<String> uiStrings = new ArrayList<String>();
		
		uiStrings.add("Planet Name: " + name);
		
		if (!fertilities.isEmpty()) {
			uiStrings.add("------------");
			uiStrings.add("Fertilities:");
			uiStrings.add("------------");
			for (FertilityType fertility : fertilities) {
				uiStrings.add(fertility.toString());
			}
		}
		
		if (!colonies.isEmpty()) {
			uiStrings.add("---------");
			uiStrings.add("Colonies:");
			uiStrings.add("---------");
			for (Colony colony : colonies) {
				uiStrings.addAll(colony.getUIStrings());
			}
		}
		
		return uiStrings;
	}
	
	public SolarSystem getSolarSystem() {
		return solarSystem;
	}

	@Override
	public Shape getBounds() {
		return new Circle(getAbsolutePosition(), diameter/2);
	}
}
