package renderer.profiles;

public class CameraProfile {
	private float[] cameraPosition;
	
	private float[] minCameraPosition;
	private float[] maxCameraPosition;
	
	public CameraProfile(float[] cameraPosition, float[] minCameraPosition, float[] maxCameraPosition) {
		this.cameraPosition = cameraPosition;
		this.minCameraPosition = minCameraPosition;
		this.maxCameraPosition = maxCameraPosition;
	}
	
	public float[] getCameraPosition() {
		return cameraPosition;
	}
	
	public float[] getMinCameraPosition() {
		return minCameraPosition;
	}
	
	public float[] getMaxCameraPosition() {
		return maxCameraPosition;
	}
}
