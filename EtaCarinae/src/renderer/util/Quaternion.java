package renderer.util;


public class Quaternion{
	private float x;
	private float y;
	private float z;
	private float w;
	
	public Quaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public Quaternion getConjugate() {
		return new Quaternion(-x, -y, -z, w);
	}
	
	public static Quaternion mult(Quaternion a, Quaternion b) {
	  return new Quaternion(
		  a.w * b.x + a.x * b.w + a.y * b.z - a.z * b.y,
		  a.w * b.y - a.x * b.z + a.y * b.w + a.z * b.x,
		  a.w * b.z + a.x * b.y - a.y * b.x + a.z * b.w,
		  a.w * b.w - a.x * b.x - a.y * b.y - a.z * b.z
	  );
	}

	public static float[] rotate(float[] vec, Quaternion a) {
		Quaternion vecQuat = new Quaternion(vec[0], vec[1], vec[2], 0);
		
		Quaternion result = Quaternion.mult(Quaternion.mult(a, vecQuat), a.getConjugate());
		
		return new float[] {result.getX(), result.getY(), result.getZ(), 0};
	}
	
	public float[] toMatrix() {
		return new float[] {
				1.0f - 2.0f*y*y - 2.0f*z*z, 2.0f*x*y - 2.0f*z*w, 2.0f*x*z + 2.0f*y*w, 0.0f,
				2.0f*x*y + 2.0f*z*w, 1.0f - 2.0f*x*x - 2.0f*z*z, 2.0f*y*z - 2.0f*x*w, 0.0f,
				2.0f*x*z - 2.0f*y*w, 2.0f*y*z + 2.0f*x*w, 1.0f - 2.0f*x*x - 2.0f*y*y, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f
		};
	}
	
	public float[] toMatrix2() {
		return new float[] {
			(w*w) + (x*x) - (y*y) - (z*z),
			(2*x*y) + (2*w*z),
			(2*x*z) - (2*w*y),
			0,
			(2*x*y) - (2*w*z),
			(w*w) - (x*x) + (y*y) - (z*z),
			(2*y*z) + (2*w*x),
			0,
			(2*x*z) + (2*w*y),
			(2*y*z) - (2*w*x),
			(w*w) - (x*x) - (y*y) + (z*z),
			0,
			0,
			0,
			0,
			1
		};
	}
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getW() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}
	
	@Override
	public String toString() {
		return x + " " + y + " " + z + " " + w;
	}
}
