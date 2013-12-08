package renderer.vbo;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Hashtable;

import javax.media.opengl.GL;
import javax.media.opengl.GL3bc;

import renderer.objdata.OBJData;

import com.jogamp.common.nio.Buffers;

public class VBOManager {
	private static final int FLOATSIZE = Float.SIZE / Byte.SIZE;
	
	private static Hashtable<String, FloatBuffer> floatBuffers = new Hashtable<String, FloatBuffer>();
	
	public static void addFloatBuffer(String name, FloatBuffer floatBuffer) {
		floatBuffers.put(name, floatBuffer);
	}
	
	public static FloatBuffer getFloatBuffer(String name) {
		return floatBuffers.get(name);
	}
	
	public static int retrieveBuffer(GL3bc gl, OBJData objData) {
		if (objData.getAssignedBuffer() == -1) {
			IntBuffer vbo = Buffers.newDirectIntBuffer(1);
			
			gl.glGenBuffers(1, vbo);
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo.get(0));
			gl.glBufferData(GL.GL_ARRAY_BUFFER, VBOManager.FLOATSIZE * objData.getVertexBuffer().capacity(), objData.getVertexBuffer(), GL.GL_STATIC_DRAW);
			
			objData.setAssignedBuffer(vbo.get(0));
//			objData.dispose();
		}
		return objData.getAssignedBuffer();
	}
}
