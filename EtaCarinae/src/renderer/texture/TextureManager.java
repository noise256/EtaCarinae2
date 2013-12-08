package renderer.texture;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.media.opengl.GL3bc;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class TextureManager {
	private static String currentTexture = "null";

	private static Hashtable<String, Texture> textures = new Hashtable<String, Texture>();

	public static void loadTexture(String path) {
		if (textures.get(path) != null) {
			return;
		}
		
		InputStream fis = TextureManager.class.getClassLoader().getResourceAsStream(path);

		Texture texture = null;
		try {
			texture = TextureIO.newTexture(fis, true, TextureIO.PNG);
		}
		catch (GLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		try {
			fis.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		currentTexture = path;
		
		textures.put(path, texture);
	}
	
	public static void bindTexture(GL3bc gl, String name) {
		textures.get(name).bind(gl);
		currentTexture = name;
	}
	
	public static void unbindTexture(GL3bc gl, String name) {
		textures.get(name).disable(gl); //TODO is this the correct method?
	}
	
	public static String getCurrentTextureName() {
		return currentTexture;
	}
	
	public static boolean textureLoaded(String name) {
		return textures.containsKey(name);
	}


}
