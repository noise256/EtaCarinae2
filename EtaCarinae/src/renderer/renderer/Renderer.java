package renderer.renderer;

import java.awt.Font;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL3bc;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import renderer.camera.CameraManager;
import renderer.newt.GWindow;
import renderer.objdata.OBJData;
import renderer.profiles.CameraProfile;
import renderer.profiles.LightingProfile;
import renderer.scene.SceneNode;
import renderer.scene.UISceneNode;
import renderer.scene.VBOSceneNode;
import renderer.shader.ShaderManager;
import renderer.shader.ShaderProgram;
import renderer.texture.TextureManager;
import renderer.vbo.VBOManager;
import util.math.MathBox;
import util.math.SingletonAccessException;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

import control.event.Observer;
import control.input.MouseInputQueue;

public class Renderer implements GLEventListener, Observer<RenderEvent> {
	public static final int FLOATSIZE = Float.SIZE / Byte.SIZE;
	
	private int width = GWindow.screenWidth;
	private int height = GWindow.screenHeight;
	
	private static Renderer instance;
	
	private MouseInputQueue mouseInputQueue;
	
	private LightingProfile lightingProfile;
	private CameraProfile cameraProfile;
	
	private GLU glu = new GLU();
	private GL3bc gl;
	
	private CameraManager cameraManager;
	
	private TextRenderer textRenderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 12), true, true, null, true);

	private ArrayList<SceneNode> currentSceneNodes = new ArrayList<SceneNode>();
	
	public static Renderer instance() {
		if (instance == null) {
			throw new SingletonAccessException("The ConcreteRenderer class has not been constructed yet.");
		}
		return instance;
	}
	
	public static Renderer instance(LightingProfile lightingProfile, CameraProfile cameraProfile, MouseInputQueue mouseInputQueue) {
		if (instance == null) {
			instance = new Renderer(lightingProfile, cameraProfile, mouseInputQueue);
		}
		return instance;
	}
	
	private Renderer(LightingProfile lightingProfile, CameraProfile cameraProfile, MouseInputQueue mouseInputQueue) {
		this.lightingProfile = lightingProfile;
		this.cameraProfile = cameraProfile;
		this.mouseInputQueue = mouseInputQueue;
		
		textRenderer.setUseVertexArrays(false); //this was done to prevent an artifact where some texture would be rendered with text when renderUI() was called.
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL3bc();
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		gl.glEnable(GL3bc.GL_DEPTH_TEST);
		
		gl.glEnable(GL3bc.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL3bc.GL_GREATER, 0);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
		
		initLighting();
		
		FloatBuffer modelViewMatrix = Buffers.newDirectFloatBuffer(16);
		gl.glGetFloatv(GLMatrixFunc.GL_MODELVIEW_MATRIX, modelViewMatrix);
		
		cameraManager = CameraManager.instance(gl, cameraProfile, mouseInputQueue);
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		
		gl.glOrtho(0.0, GWindow.screenWidth, 0, GWindow.screenHeight, -1, 1);
		drawSkybox();
		
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		
		glu.gluPerspective(90, (float) width / (float) height, 1f, cameraManager.getCameraPosition()[2] + 1); //the far clipping is set like this to allow mouse picking to work
		
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glPushMatrix();
		
		gl.glTranslatef(-cameraManager.getCameraPosition()[0], -cameraManager.getCameraPosition()[1], -cameraManager.getCameraPosition()[2]);
		
//		drawDebugCube(0f, 0f, 0f);
		
		for (SceneNode sceneNode : currentSceneNodes) {
			gl.glPushMatrix();
			
			if (sceneNode instanceof VBOSceneNode) {
				gl.glTranslatef(sceneNode.getNodePosition()[0], sceneNode.getNodePosition()[1], ((VBOSceneNode) sceneNode).getNodeDepth());
				gl.glRotatef(((((VBOSceneNode) sceneNode).getNodeRotation()) - MathBox.PI/2) * 180.0f / MathBox.PI , 0.0f, 0.0f, 1.0f);
				renderVBO(gl, sceneNode.getShaderProgram(), ((VBOSceneNode) sceneNode).getOBJData(), ((VBOSceneNode) sceneNode).getMode());
			}
			else if (sceneNode instanceof UISceneNode) {
				renderUI(gl, (UISceneNode) sceneNode);
			}
			
			gl.glPopMatrix();
		}
		
		cameraManager.update();
		cameraManager.updateTransformationMatrices();
		
		gl.glPopMatrix();
	}
	
	private void initLighting() {
		gl.glLightModelfv(GL2ES1.GL_LIGHT_MODEL_AMBIENT, lightingProfile.getGlobalAmbient(), 0);
		
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, lightingProfile.getDefaultAmbient(), 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, lightingProfile.getDefaultDiffuse(), 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, lightingProfile.getDefaultSpecular(), 0);
		
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, lightingProfile.getLightPosition(), 0);
	}
	
	private void renderVBO(GL3bc gl, ShaderProgram shaderProgram, OBJData objData, int mode) {
		ShaderManager.loadShaderProgram(gl, shaderProgram);
		int shaderNumber = ShaderManager.getShaderAssignedProgram(shaderProgram.getName());
		gl.glUseProgram(shaderNumber);
		
		int vbo = VBOManager.retrieveBuffer(gl, objData);
		
		if (objData.hasTexCoords()) {
			if (!TextureManager.textureLoaded(objData.getTexturePath())) {
				TextureManager.loadTexture(objData.getTexturePath());
			}
			
			if (!TextureManager.getCurrentTextureName().equals(objData.getTexturePath())) {
				TextureManager.bindTexture(gl, objData.getTexturePath());
			}
			
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		}
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo);
		
		for (int i = 0; i < objData.getCount(); i++) {
			gl.glVertexAttribPointer(i, objData.getSizes()[i], GL.GL_FLOAT, false, objData.getStride() * Renderer.FLOATSIZE, objData.getPointers()[i] * Renderer.FLOATSIZE);
			gl.glEnableVertexAttribArray(i);
		}
		
		gl.glEnable(GL3bc.GL_BLEND);
		
		float[] scale = objData.getScale();
		gl.glScalef(scale[0], scale[1], scale[2]);
		gl.glDrawArrays(mode, 0, objData.getVertexBufferCapacity());
		
		gl.glDisable(GL3bc.GL_BLEND);
		
		TextureManager.unbindTexture(gl, objData.getTexturePath());
		
		for (int i = 0; i < objData.getCount(); i++) {
			gl.glDisableVertexAttribArray(i);
		}
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		gl.glUseProgram(0);
	}
	
	private void renderUI(GL3bc gl, UISceneNode sceneNode) {
		//Render Texture
		gl.glMatrixMode(GL3bc.GL_PROJECTION);
	    
	    gl.glPushMatrix();
		gl.glLoadIdentity();
	    
		gl.glOrtho(0.0, GWindow.screenWidth, 0, GWindow.screenHeight, -1, 1);
		
		gl.glMatrixMode(GL3bc.GL_MODELVIEW);
		
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		gl.glTranslatef(sceneNode.getNodePosition()[0], sceneNode.getNodePosition()[1], 0.0f);
		
		renderVBO(gl, sceneNode.getShaderProgram(), sceneNode.getOBJData(), GL3bc.GL_TRIANGLES);
		
		gl.glMatrixMode(GL3bc.GL_PROJECTION);
		gl.glPopMatrix();
		
		gl.glMatrixMode(GL3bc.GL_MODELVIEW);
		gl.glPopMatrix();
		
		//Render Text
		ArrayList<String> text = sceneNode.getText();
		
		if (text != null && !text.isEmpty()) {
			float[] nodePosition = sceneNode.getNodePosition();
			
			int singleStringHeight = textRenderer.getFont().getSize();
			
			int uiStringsWidth = 0;
			
			for (String line : text) {
				if (textRenderer.getBounds(line).getWidth() > uiStringsWidth) {
					uiStringsWidth = (int) textRenderer.getBounds(line).getWidth();
				}
			}
			
		    textRenderer.beginRendering(GWindow.screenWidth, GWindow.screenHeight, true);
		    
		    textRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		    
		    for (int i = 0; i < text.size(); i++) { 
		    	float yOffset = (float) (i + 1) * singleStringHeight;
		    	textRenderer.draw(text.get(i), (int) (nodePosition[0] - sceneNode.getWidth()/2), (int) (nodePosition[1] - yOffset + sceneNode.getHeight()/2));
		    }
		    
		    textRenderer.endRendering();
		}
	}
	
	private void drawSkybox() {
		gl.glUseProgram(0);
		
		if (!TextureManager.textureLoaded("dark_pink_stars.png")) {
			TextureManager.loadTexture("dark_pink_stars.png");
		}
		
		if (!TextureManager.getCurrentTextureName().equals("dark_pink_stars.png")) {
			TextureManager.bindTexture(gl, "dark_pink_stars.png");
		}
		
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glDisable(GL.GL_DEPTH_TEST);
		
		gl.glPushMatrix();
		
		gl.glTranslatef(GWindow.screenWidth/2.0f, GWindow.screenHeight/2, 0.0f);
		gl.glColor4f(1, 1, 1, 1);

		// Render the front quad
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(1024, 1024, 0.0f);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(1024, -1024, 0.0f);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(-1024, -1024, 0.0f);
			gl.glTexCoord2f(0, 1);
			gl.glVertex3f(-1024, 1024, 0.0f);
		gl.glEnd();
		
		gl.glPopMatrix();
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDisable(GL.GL_TEXTURE_2D);
	}
	
	@SuppressWarnings("unused")
	private void drawDebugCube(float x, float y, float z) {
		GLUT glut = new GLUT();
		
		gl.glPushMatrix();
		
		gl.glLineWidth(1.0f);
		gl.glTranslatef(x, y, z);

		// model objects
		gl.glColor3f(1.0f, 1.0f, 1.0f); // origin
		glut.glutWireCube(1.0f);

		gl.glPushMatrix(); // x axis
		gl.glColor3f(1f, 0f, 0f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0 + 50, 0, 0);
		gl.glEnd();
		gl.glTranslatef(0 + 50, 0, 0);
		glut.glutWireCube(10f);
		gl.glPopMatrix();

		gl.glPushMatrix(); // y axis
		gl.glColor3f(0f, 1f, 0f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, 0 + 50, 0);
		gl.glEnd();
		gl.glTranslatef(0, 0 + 50, 0);
		glut.glutWireCube(10f);
		gl.glPopMatrix();

		gl.glPushMatrix(); // z axis
		gl.glColor3f(0f, 0f, 1f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, 0, 50);
		gl.glEnd();
		gl.glTranslatef(0, 0, 50);
		glut.glutWireCube(10f);
		gl.glPopMatrix();
		
		gl.glPopMatrix();
	}
	
	public float[] screenToWorldCoordinates(int[] screenCoords) {
		int[] viewport = CameraManager.instance().getViewport();
		float[] modelview = CameraManager.instance().getModelView();
		float[] projection = CameraManager.instance().getProjection();
		float[] wcoord = new float[4];
		
		float winX = (float) screenCoords[0];
		float winY = (float) viewport[3] - screenCoords[1];
		float winZ = CameraManager.instance().getMouseDepth();
		
		GLU glu = new GLU();
		glu.gluUnProject(winX, winY, winZ, modelview, 0, projection, 0, viewport, 0, wcoord, 0);
		
		return wcoord;
	}
	
	public int[] worldToScreenCoordinates(float[] worldCoordinates) {
		int[] viewport = CameraManager.instance().getViewport();
		float[] modelview = CameraManager.instance().getModelView();
		float[] projection = CameraManager.instance().getProjection();
		
		float[] screenCoords = new float[3];
		
		float worldX = worldCoordinates[0];
		float worldY = worldCoordinates[1];
		float worldZ = worldCoordinates[2];
		
		GLU glu = new GLU();
		glu.gluProject(worldX, worldY, worldZ, modelview, 0, projection, 0, viewport, 0, screenCoords, 0);
		
		return new int[] {(int) screenCoords[0], (int) screenCoords[1]};
	}
	
	@Override
	public void dispose(GLAutoDrawable autoDrawable) {
		autoDrawable.getContext().destroy();
	}

	@Override
	public void reshape(GLAutoDrawable autoDrawable, int x, int y, int width, int height) {
		this.width = width;
		this.height = height;
		
        if (height <= 0) {
        	height = 1;
        }
 
        gl.glViewport(0, 0, width, height);
	}

	@Override
	public void update(RenderEvent event) {
		currentSceneNodes = event.getSceneNodes();
	}
	
	public GL3bc getGL() {
		return gl;
	}
}
