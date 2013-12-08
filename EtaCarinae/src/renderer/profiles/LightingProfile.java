package renderer.profiles;

public class LightingProfile {
	private float[] globalAmbient;
	
	private float[] lightPosition;

	private float[] defaultAmbient;
	private float[] defaultDiffuse;
	private float[] defaultSpecular;
	
	public LightingProfile(float[] globalAmbient, float[] lightPosition, float[] defaultAmbient, float[] defaultDiffuse, float[] defaultSpecular) {
		this.globalAmbient = globalAmbient;
		this.lightPosition = lightPosition;
		this.defaultAmbient = defaultAmbient;
		this.defaultDiffuse = defaultDiffuse;
		this.defaultSpecular = defaultSpecular;
	}

	public float[] getGlobalAmbient() {
		return globalAmbient;
	}

	public float[] getLightPosition() {
		return lightPosition;
	}

	public float[] getDefaultAmbient() {
		return defaultAmbient;
	}

	public float[] getDefaultDiffuse() {
		return defaultDiffuse;
	}

	public float[] getDefaultSpecular() {
		return defaultSpecular;
	}
}
