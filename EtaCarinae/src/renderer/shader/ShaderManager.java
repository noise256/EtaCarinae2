package renderer.shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Hashtable;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL3bc;

import com.jogamp.common.nio.Buffers;

public class ShaderManager {
	private static Hashtable<String, Integer> shaderPrograms = new Hashtable<String, Integer>();
	
	public static int getShaderAssignedProgram(String shaderProgramName) {
		if (shaderPrograms.containsKey(shaderProgramName)) {
			return shaderPrograms.get(shaderProgramName).intValue();
		}
		else {
			System.err.println("Error: Attempt to retrive a shader program that has not been loaded yet.");
		}
		return -1;
	}
	
	public static void loadShaderProgram(GL3bc gl, ShaderProgram shaderProgram) {
		if (!shaderPrograms.containsKey(shaderProgram.getName())) {
			int vertShader = gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER);
			int fragShader = gl.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);
			
			gl.glShaderSource(vertShader, 1, shaderFileToString(shaderProgram.getVertexPath()), null, 0);
			gl.glCompileShader(vertShader);
			
			gl.glShaderSource(fragShader, 1, shaderFileToString(shaderProgram.getFragmentPath()), null, 0);
			gl.glCompileShader(fragShader);
			
			int program = gl.glCreateProgram();
			
			gl.glAttachShader(program, vertShader);
			gl.glAttachShader(program, fragShader);
			
			String[] attributes = shaderProgram.getAttributes();
			for (int i = 0; i < attributes.length; i++) {
				gl.glBindAttribLocation(program, i, attributes[i]);
			}
			
			gl.glLinkProgram(program);
			
	        gl.glValidateProgram(program);
	        
	        ShaderManager.errorCheckLinkStatus(gl, program);
	        
	        shaderPrograms.put(shaderProgram.getName(), new Integer(program));
		}
	}
	
	private static String[] shaderFileToString(String path) {
		InputStream is = ShaderManager.class.getClassLoader().getResourceAsStream(path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		StringBuilder source = new StringBuilder();
		String line;
		
		try {
			while ((line = reader.readLine()) != null) {
				source.append(line);
				source.append("\n");
			}
			is.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return new String[] {source.toString()};
	}
	
	private static void errorCheckLinkStatus(GL3bc gl, int program) {
		IntBuffer linkStatus = Buffers.newDirectIntBuffer(1);
        gl.glGetProgramiv(program, GL2ES2.GL_LINK_STATUS, linkStatus);
        
        if (linkStatus.get(0) != 1) {
        	gl.glGetProgramiv(program, GL2ES2.GL_INFO_LOG_LENGTH, linkStatus);
            int size = linkStatus.get(0);
            System.err.println("Program link error: ");
            if (size > 0) {
                ByteBuffer error = Buffers.newDirectByteBuffer(size);
                gl.glGetProgramInfoLog(program, size, linkStatus, error);
                StringBuilder errorString = new StringBuilder();
                for (int i = 0; i < error.capacity(); i++) {
                	errorString.append((char) error.get());
                }
                System.err.println(errorString.toString());
            }
            else {
                System.out.println("Unknown");
            }
            System.exit(1);
        }
	}
}
