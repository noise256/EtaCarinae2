package renderer.camera;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;

import javax.media.opengl.GL3bc;

import renderer.profiles.CameraProfile;
import renderer.renderer.Renderer;

import com.jogamp.common.nio.Buffers;

import control.input.Input;
import control.input.InputType;
import control.input.KeyManager;
import control.input.MouseInputQueue;

public class CameraManager {
	private static CameraManager instance;
	
	private static final float traVelX = 250;
	private static final float traVelY = 250;
	private static final float traVelZ = 2500;

	private GL3bc gl;
	
	private MouseInputQueue mouseInputQueue;
	
	private float[] cameraPosition;
	private float[] minCameraPosition;
	private float[] maxCameraPosition;
	
	private float[] modelview = new float[16];
	private float[] projection = new float[16];
	private int[] viewport = new int[4];
	private FloatBuffer mouseDepth = Buffers.newDirectFloatBuffer(1);
	
	public static CameraManager instance(GL3bc gl, CameraProfile cameraProfile, MouseInputQueue mouseInputQueue) {
		if (instance == null) {
			instance = new CameraManager(gl, cameraProfile, mouseInputQueue);
		}
		return instance;
	}

	public static CameraManager instance() {
		if (instance == null) {
			System.err.println("Camera Manager has not been created yet. Please use the parameterised instance() method first.");
		}
		return instance;
	}

	private CameraManager(GL3bc gl, CameraProfile cameraProfile, MouseInputQueue mouseInputQueue) {
		this.gl = gl;
		this.cameraPosition = cameraProfile.getCameraPosition();
		this.minCameraPosition = cameraProfile.getMinCameraPosition();
		this.maxCameraPosition = cameraProfile.getMaxCameraPosition();
		this.mouseInputQueue = mouseInputQueue;
	}
	
	public void update() {
		/**
		 * Gather mouse data.
		 */
		Input wheelRotation = mouseInputQueue.getInput(InputType.MOUSE_WHEEL_MOVED);
		
		if (wheelRotation != null) {
			translate(wheelRotation.getWheelRotation(), 0, 0, -1);
		}
		
		if (KeyManager.instance().isKeyDown(KeyEvent.VK_W)) {
			translate(1, 0, 1, 0);
		}
		if (KeyManager.instance().isKeyDown(KeyEvent.VK_A)) {
			translate(-1, 1, 0, 0);		
		}
		if (KeyManager.instance().isKeyDown(KeyEvent.VK_S)) {
			translate(-1, 0, 1, 0);
		}
		if (KeyManager.instance().isKeyDown(KeyEvent.VK_D)) {
			translate(1, 1, 0, 0);
		}
	}
	
	public void updateTransformationMatrices() {
		gl.glGetFloatv(GL3bc.GL_MODELVIEW_MATRIX, modelview, 0);
		gl.glGetFloatv(GL3bc.GL_PROJECTION_MATRIX, projection, 0);
		gl.glGetIntegerv(GL3bc.GL_VIEWPORT, viewport, 0);
		
		Input input = mouseInputQueue.getInput(InputType.MOUSE_MOVED);
		
		if (input != null) {
			float[] mouseCoordinates = input.getCoordinates();
			mouseDepth.clear();
			Renderer.instance().getGL().glReadPixels((int) mouseCoordinates[0], (int) mouseCoordinates[1], 1, 1, GL3bc.GL_DEPTH_COMPONENT, GL3bc.GL_FLOAT, mouseDepth);
		}
	}
	
	private void translate(float dist, float x, float y, float z) {
		cameraPosition[0] += x * dist * traVelX * cameraPosition[2] / maxCameraPosition[2];
		cameraPosition[1] += y * dist * traVelY * cameraPosition[2] / maxCameraPosition[2];
		cameraPosition[2] += z * dist * traVelZ * cameraPosition[2] / maxCameraPosition[2];
		
		cameraPosition[0] = cameraPosition[0] <= minCameraPosition[0] ? minCameraPosition[0] : cameraPosition[0];
		cameraPosition[0] = cameraPosition[0] >= maxCameraPosition[0] ? maxCameraPosition[0] : cameraPosition[0];
		
		cameraPosition[1] = cameraPosition[1] <= minCameraPosition[1] ? minCameraPosition[1] : cameraPosition[1];
		cameraPosition[1] = cameraPosition[1] >= maxCameraPosition[1] ? maxCameraPosition[1] : cameraPosition[1];
		
		cameraPosition[2] = cameraPosition[2] <= minCameraPosition[2] ? minCameraPosition[2] : cameraPosition[2];
		cameraPosition[2] = cameraPosition[2] >= maxCameraPosition[2] ? maxCameraPosition[2] : cameraPosition[2];
	}

	public float[] getCameraPosition() {
		return cameraPosition;
	}
	
	public float[] getMaxCameraPosition() {
		return maxCameraPosition;
	}
	
	public float[] getModelView() {
		return modelview;
	}
	
	public float[] getProjection() {
		return projection;
	}
	
	public int[] getViewport() {
		return viewport;
	}
	
	public float getMouseDepth() {
		return mouseDepth.get(0);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
	    throw new CloneNotSupportedException(); 
	}
}
