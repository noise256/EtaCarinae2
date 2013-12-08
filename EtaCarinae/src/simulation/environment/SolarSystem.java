package simulation.environment;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL3bc;

import renderer.objdata.OBJData;
import renderer.scene.VBOSceneNode;
import renderer.shader.ShaderProgram;
import renderer.vbo.VBOManager;
import simulation.enums.ZDepth;
import util.collision.Circle;
import util.collision.Shape;

public class SolarSystem extends Celestial {
	private int starSize;
	
	private ArrayList<Celestial> celestials = new ArrayList<Celestial>();
	
	public SolarSystem(float[] position, float[] zDepths, int diameter, String[] texturePaths, int[] textureSizes, String name, int starSize) {
		super(position, zDepths, diameter, texturePaths, textureSizes, name);
		this.starSize = starSize;
	}

	@Override
	public void updateView() {
		if (sceneNodes.isEmpty()) {
			for (int i = 0; i < texturePaths.length; i++) {
				ShaderProgram shaderProgram = new ShaderProgram(
						"defaultTextured", 
						"textured.vertex", 
						"textured.fragment", 
						new String[] {"attribute_Position", "attribute_TexCoord"}
				);
				
				FloatBuffer vertexBuffer = VBOManager.getFloatBuffer("DefaultTexturedQuad");
				OBJData objData = new OBJData(vertexBuffer, texturePaths[i], new float[] {textureLengths[i]/2, textureLengths[i]/2, 1.0f}, 2, 5, new int[] {3, 2}, new int[] {0, 3}, false, true);
				
				VBOSceneNode solarSystemSceneNode = new VBOSceneNode(shaderProgram, objData, getAbsolutePosition(), 0.0f, zDepths[i], GL3bc.GL_TRIANGLES);
				solarSystemSceneNode.setVisible(true);
				
				sceneNodes.put(name + texturePaths[i] + "_SolarSystemSceneNode", solarSystemSceneNode);
			}
			
			ShaderProgram shaderProgram = new ShaderProgram(
					"defaultTextured", 
					"textured.vertex", 
					"textured.fragment", 
					new String[] {"attribute_Position", "attribute_TexCoord"}
			);
			
			FloatBuffer vertexBuffer = VBOManager.getFloatBuffer("DefaultTexturedQuad");
			OBJData objData = new OBJData(vertexBuffer, "selection_ring_green.png", new float[] {diameter * 0.75f, diameter * 0.75f, 1.0f}, 2, 5, new int[] {3, 2}, new int[] {0, 3}, false, true);
			
			sceneNodes.put(name + "_Selection" + "_SolarSystemSceneNode", new VBOSceneNode(shaderProgram, objData, getAbsolutePosition(), 0.0f, ZDepth.SELECTION.depth(), GL3bc.GL_TRIANGLES));
		}
		
		if (selected || highlighted) {
			sceneNodes.get(name + "_Selection" + "_SolarSystemSceneNode").setVisible(true);
		}
		else {
			sceneNodes.get(name + "_Selection" + "_SolarSystemSceneNode").setVisible(false);
		}
	}
	
	public ArrayList<Celestial> getCelestials() {
		return celestials;
	}
	
	public void addCelestial(Celestial celestial) {
		celestials.add(celestial);
	}
	
	public int getStarSize() {
		return starSize;
	}
	
	@Override
	public float[] getAbsolutePosition() {
		return position;
	}
	
	@Override
	public ArrayList<String> getUIStrings() {
		ArrayList<String> uiStrings = new ArrayList<String>();
		
		uiStrings.add("Name: " + name);
		
		if (!celestials.isEmpty()) {
			uiStrings.add("--------");
			uiStrings.add("Celestials:");
			uiStrings.add("--------");
			for (Celestial celestial : celestials) {
				uiStrings.add(celestial.getName());
			}
		}
		
		return uiStrings;
	}

	@Override
	public Shape getBounds() {
		return new Circle(getAbsolutePosition(), diameter/2);
	}
}
