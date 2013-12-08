package renderer.shader;

public class ShaderProgram {
	private String name;
	
	private String vertexPath;
	private String fragmentPath;
	
	private String[] attributes;

	public ShaderProgram(String name, String vertexPath, String fragmentPath, String[] attributes) {
		this.name = name;
		this.vertexPath = vertexPath;
		this.fragmentPath = fragmentPath;
		this.attributes = attributes;
	}
	
	public String getName() {
		return name;
	}

	public String getVertexPath() {
		return vertexPath;
	}

	public String getFragmentPath() {
		return fragmentPath;
	}

	public String[] getAttributes() {
		return attributes;
	}
}
