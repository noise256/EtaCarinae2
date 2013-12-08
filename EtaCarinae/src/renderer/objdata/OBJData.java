package renderer.objdata;

import java.nio.FloatBuffer;

/**
 * Encapsulates the data of an .obj file with additional storage for assigned VBOs, textures and shader programs.
 * 
 * @author DHC
 */
public class OBJData {
	private FloatBuffer vertexBuffer;
	private int vertexBufferCapacity;
	
	private String texturePath;
	
	private float[] scale;
	
	private int count;
	private int stride;
	private int[] sizes;
	private int[] pointers;
	
	private boolean normalCoords;
	private boolean texCoords;
	
	private int assignedBuffer = -1;
	
	public OBJData(FloatBuffer vertexBuffer, String texturePath, float[] scale, int count, int stride, int[] sizes, int[] pointers, boolean normalCoords, boolean texCoords) {
		this.vertexBuffer = vertexBuffer;
		this.texturePath = texturePath;
		
		this.scale = scale;
		this.count = count;
		this.stride = stride;
		this.sizes = sizes;
		this.pointers = pointers;
		
		this.normalCoords = normalCoords;
		this.texCoords = texCoords;
		
		this.vertexBufferCapacity = vertexBuffer.capacity();
	}
	
	public void dispose() {
		vertexBuffer = null;
	}
	
	public FloatBuffer getVertexBuffer() {
		return vertexBuffer;
	}
	
	public int getVertexBufferCapacity() {
		return vertexBufferCapacity;
	}
	
	public String getTexturePath() {
		return texturePath;
	}
	
	public float[] getScale() {
		return scale;
	}
	
	public int getCount() {
		return count;
	}
	
	public int getStride() {
		return stride;
	}
	
	public int[] getSizes() {
		return sizes;
	}
	
	public int[] getPointers() {
		return pointers;
	}
	
	public boolean hasNormalCoords() {
		return normalCoords;
	}

	public boolean hasTexCoords() {
		return texCoords;
	}

	public void setAssignedBuffer(int assignedBuffer) {
		this.assignedBuffer = assignedBuffer;
	}
	
	public int getAssignedBuffer() {
		return assignedBuffer;
	}
}
