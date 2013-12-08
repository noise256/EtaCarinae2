package simulation.ui;

import java.nio.FloatBuffer;

import renderer.objdata.OBJData;
import renderer.scene.UISceneNode;
import renderer.shader.ShaderProgram;
import renderer.vbo.VBOManager;

public class UITextPanel extends UIPanel {
	public UITextPanel(float[] position, float width, float height, String texturePath) {
		super(position, width, height, texturePath);
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
			
			FloatBuffer vertexBuffer = VBOManager.getFloatBuffer("DefaultTexturedQuad");
			
			OBJData objData = new OBJData(vertexBuffer, texturePath, new float[] {width/2, height/2, 1.0f}, 2, 5, new int[] {3, 2}, new int[] {0, 3}, false, true);
			
			UISceneNode uiSceneNode = new UISceneNode(shaderProgram, objData, position, width, height);
			uiSceneNode.setVisible(true);
			
			sceneNodes.put(texturePath + "_UIPanelSceneNode", uiSceneNode);
		}
		
		if (textSource != null) {
			((UISceneNode) sceneNodes.get(texturePath + "_UIPanelSceneNode")).setText(textSource.getUIStrings());
		}
		else {
			((UISceneNode) sceneNodes.get(texturePath + "_UIPanelSceneNode")).setText(null);
		}
	}
}
