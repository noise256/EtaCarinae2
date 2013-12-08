package renderer.objdata;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jogamp.common.nio.Buffers;

public class OBJParser {

	/**
	 * Parses a .obj file returning an OBJData object that encapsulates the vertex data of a mesh.
	 * 
	 * @param objFilePath
	 * @return
	 */
	public static OBJData parseOBJFile(String objFilePath) {
		//Construct file scanner for obj file
		Scanner fileScanner = new Scanner(OBJParser.class.getClassLoader().getResourceAsStream(objFilePath));
		
		//Parse each line of the .obj file
		ArrayList<float[]> vertexArray = new ArrayList<float[]>();
		ArrayList<float[]> textureArray = new ArrayList<float[]>();
		ArrayList<float[]> normalArray = new ArrayList<float[]>();
		ArrayList<int[]> faceIndexArray = new ArrayList<int[]>();
		ArrayList<int[]> normalIndexArray = new ArrayList<int[]>();
		ArrayList<int[]> textureIndexArray = new ArrayList<int[]>();
		
		try {
			while (fileScanner.hasNextLine()) {
				processLine(fileScanner.nextLine(), vertexArray, textureArray, normalArray, faceIndexArray, normalIndexArray, textureIndexArray);
			}
		}
		catch (NullPointerException e) {
			e.printStackTrace();
		}
		finally {
			fileScanner.close();
		}
		
		//create a new OBJData with relevant buffers
		return createOBJData(vertexArray, textureArray, normalArray, faceIndexArray, normalIndexArray, textureIndexArray);
	}
	
	/**
	 * Parses a single line of an .obj file.
	 * 
	 * @param line
	 * @param vertexArray
	 * @param textureArray
	 * @param normalArray
	 * @param faceIndexArray
	 * @param normalIndexArray
	 * @param textureIndexArray
	 */
	private static void processLine(String line, ArrayList<float[]> vertexArray, ArrayList<float[]> textureArray, ArrayList<float[]> normalArray, ArrayList<int[]> faceIndexArray, ArrayList<int[]> normalIndexArray, ArrayList<int[]> textureIndexArray) {
		//regular expression matcher for vertex coordinates
		Pattern vertexRegExp = Pattern.compile("v\\s(-?[0-9]+\\.[0-9]+)\\s(-?[0-9]+\\.[0-9]+)\\s(-?[0-9]+\\.[0-9]+)");
		Matcher vertexMatcher = vertexRegExp.matcher(line);
		
		//regular expression matcher for normal coordinates
		Pattern normalRegExp = Pattern.compile("vn\\s(-?[0-9]+\\.[0-9]+)\\s(-?[0-9]+\\.[0-9]+)\\s(-?[0-9]+\\.[0-9]+)");
		Matcher normalMatcher = normalRegExp.matcher(line);
		
		Pattern textureRegExp = Pattern.compile("vt\\s(-?[0-9]+\\.[0-9]+)\\s(-?[0-9]+\\.[0-9]+)");
		Matcher textureMatcher = textureRegExp.matcher(line);
		
		//regular expression matcher for faces defined as f v v v
		Pattern vIndexRegExp = Pattern.compile("f\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)");
		Matcher vIndexMatcher = vIndexRegExp.matcher(line);
		
		//regular expression matcher for faces defined as f v//n v//n v//n
		Pattern vnIndexRegExp = Pattern.compile("f\\s([0-9]+)//([0-9]+)\\s([0-9]+)//([0-9]+)\\s([0-9]+)//([0-9]+)");
		Matcher vnIndexMatcher = vnIndexRegExp.matcher(line);
		
		//regular expression matcher for faces defined as f v/t/n v/t/n v/t/n
		Pattern vtnIndexRegExp = Pattern.compile("f\\s([0-9]+)/([0-9]+)/([0-9]+)\\s([0-9]+)/([0-9]+)/([0-9]+)\\s([0-9]+)/([0-9]+)/([0-9]+)");
		Matcher vtnIndexMatcher = vtnIndexRegExp.matcher(line);
		
		if (vertexMatcher.matches()) {
			vertexArray.add(
				new float[] {
					Float.parseFloat(vertexMatcher.group(1)),
					Float.parseFloat(vertexMatcher.group(2)),
					Float.parseFloat(vertexMatcher.group(3))
				}
			);
		}
		else if (normalMatcher.matches()) {
			normalArray.add(
				new float[] {
					Float.parseFloat(normalMatcher.group(1)),
					Float.parseFloat(normalMatcher.group(2)),
					Float.parseFloat(normalMatcher.group(3))
				}
			);
		}
		else if (textureMatcher.matches()) {
			textureArray.add(
				new float[] {
					Float.parseFloat(textureMatcher.group(1)),
					Float.parseFloat(textureMatcher.group(2)),
				}
			);
		}
		else if (vIndexMatcher.matches()) {
			faceIndexArray.add(
				new int[] {
					Integer.parseInt(vIndexMatcher.group(1)),
					Integer.parseInt(vIndexMatcher.group(2)),
					Integer.parseInt(vIndexMatcher.group(3))
				}
			);
		}
		else if (vnIndexMatcher.matches()) {
			faceIndexArray.add(
				new int[] {
					Integer.parseInt(vnIndexMatcher.group(1)),
					Integer.parseInt(vnIndexMatcher.group(3)),
					Integer.parseInt(vnIndexMatcher.group(5))
				}
			);
			normalIndexArray.add(
				new int[] {
					Integer.parseInt(vnIndexMatcher.group(2)),
					Integer.parseInt(vnIndexMatcher.group(4)),
					Integer.parseInt(vnIndexMatcher.group(6)),
				}
			);
		}
		else if (vtnIndexMatcher.matches()) {
			faceIndexArray.add(
				new int[] {
					Integer.parseInt(vtnIndexMatcher.group(1)),
					Integer.parseInt(vtnIndexMatcher.group(4)),
					Integer.parseInt(vtnIndexMatcher.group(7))
				}
			);
			textureIndexArray.add(
				new int[] {
					Integer.parseInt(vtnIndexMatcher.group(2)),
					Integer.parseInt(vtnIndexMatcher.group(5)),
					Integer.parseInt(vtnIndexMatcher.group(8)),
				}
			);
			normalIndexArray.add(
				new int[] {
					Integer.parseInt(vtnIndexMatcher.group(3)),
					Integer.parseInt(vtnIndexMatcher.group(6)),
					Integer.parseInt(vtnIndexMatcher.group(9)),
				}
			);
		}
	}
	
	/**
	 * Creates a new OBJData object with buffers representing mesh data.
	 * 
	 * @param vertexArray
	 * @param textureArray
	 * @param normalArray
	 * @param faceIndexArray
	 * @param normalIndexArray
	 * @param textureIndexArray
	 * @return
	 */
	private static OBJData createOBJData(ArrayList<float[]> vertexArray, ArrayList<float[]> textureArray, ArrayList<float[]> normalArray, ArrayList<int[]> faceIndexArray, ArrayList<int[]> normalIndexArray, ArrayList<int[]> textureIndexArray) {
		FloatBuffer vertexBuffer = null;
		
		if (normalIndexArray.isEmpty() && textureIndexArray.isEmpty()) {
			vertexBuffer = Buffers.newDirectFloatBuffer(faceIndexArray.size() * 9 + faceIndexArray.size() * 12);
			
			for (int i = 0; i < faceIndexArray.size(); i++) {
				float[] vert1 = vertexArray.get(faceIndexArray.get(i)[0] - 1);
				vert1 = new float[] {vert1[0], vert1[1], vert1[2], 1.0f};
				
				float[] vert2 = vertexArray.get(faceIndexArray.get(i)[1] - 1);
				vert2 = new float[] {vert2[0], vert2[1], vert2[2], 1.0f};
				
				float[] vert3 = vertexArray.get(faceIndexArray.get(i)[2] - 1);
				vert3 = new float[] {vert3[0], vert3[1], vert3[2], 1.0f};
				
				float[] col1 = new float[] {0.5f, 0.5f, 0.5f, 1.0f};
				float[] col2 = new float[] {0.5f, 0.5f, 0.5f, 1.0f};
				float[] col3 = new float[] {0.5f, 0.5f, 0.5f, 1.0f};
				
				vertexBuffer.put(vert1);
				vertexBuffer.put(col1);
				
				vertexBuffer.put(vert2);
				vertexBuffer.put(col2);
				
				vertexBuffer.put(vert3);
				vertexBuffer.put(col3);
			}
			vertexBuffer.rewind();
			
			return new OBJData(vertexBuffer, null, new float[] {1.0f, 1.0f, 1.0f}, 2, 8, new int[] {4, 4}, new int[] {0, 4}, false, false);
		}
		else if (!normalIndexArray.isEmpty() && textureIndexArray.isEmpty()) {
			vertexBuffer = Buffers.newDirectFloatBuffer(faceIndexArray.size() * 9 + normalIndexArray.size() * 9 + faceIndexArray.size() * 12);
			
			for (int i = 0; i < faceIndexArray.size(); i++) {
//				float[] vert1 = vertexArray.get(faceIndexArray.get(i)[0] - 1);
//				float[] vert2 = vertexArray.get(faceIndexArray.get(i)[1] - 1);
//				float[] vert3 = vertexArray.get(faceIndexArray.get(i)[2] - 1);
				float[] vert1 = vertexArray.get(faceIndexArray.get(i)[0] - 1);
				vert1 = new float[] {vert1[0], vert1[1], vert1[2], 1.0f};
				float[] vert2 = vertexArray.get(faceIndexArray.get(i)[1] - 1);
				vert2 = new float[] {vert2[0], vert2[1], vert2[2], 1.0f};
				float[] vert3 = vertexArray.get(faceIndexArray.get(i)[2] - 1);
				vert3 = new float[] {vert3[0], vert3[1], vert3[2], 1.0f};
				
				float[] norm1 = normalArray.get(normalIndexArray.get(i)[0] - 1);
				float[] norm2 = normalArray.get(normalIndexArray.get(i)[1] - 1);
				float[] norm3 = normalArray.get(normalIndexArray.get(i)[2] - 1);
				
				float[] col1 = new float[] {1.0f, 1.0f, 1.0f, 0.0f};
				float[] col2 = new float[] {1.0f, 1.0f, 1.0f, 0.0f};
				float[] col3 = new float[] {1.0f, 1.0f, 1.0f, 0.0f};
				
				vertexBuffer.put(vert1);
				vertexBuffer.put(norm1);
				vertexBuffer.put(col1);
				
				vertexBuffer.put(vert2);
				vertexBuffer.put(norm2);
				vertexBuffer.put(col2);
				
				vertexBuffer.put(vert3);
				vertexBuffer.put(norm3);
				vertexBuffer.put(col3);
			}
			vertexBuffer.rewind();
			
			return new OBJData(vertexBuffer, null, new float[] {1.0f, 1.0f, 1.0f}, 3, 11, new int[] {4, 3, 4}, new int[] {0, 4, 7}, true, false);
		}
		else if (!normalIndexArray.isEmpty() && !textureIndexArray.isEmpty()) {
			vertexBuffer = Buffers.newDirectFloatBuffer(faceIndexArray.size() * 12 + normalIndexArray.size() * 9 + textureIndexArray.size() * 6);
			
			for (int i = 0; i < faceIndexArray.size(); i++) {
//				float[] vert1 = vertexArray.get(faceIndexArray.get(i)[0] - 1);
//				float[] vert2 = vertexArray.get(faceIndexArray.get(i)[1] - 1);
//				float[] vert3 = vertexArray.get(faceIndexArray.get(i)[2] - 1);
				float[] vert1 = vertexArray.get(faceIndexArray.get(i)[0] - 1);
				vert1 = new float[] {vert1[0], vert1[1], vert1[2]};
				float[] vert2 = vertexArray.get(faceIndexArray.get(i)[1] - 1);
				vert2 = new float[] {vert2[0], vert2[1], vert2[2]};
				float[] vert3 = vertexArray.get(faceIndexArray.get(i)[2] - 1);
				vert3 = new float[] {vert3[0], vert3[1], vert3[2]};
				
				float[] norm1 = normalArray.get(normalIndexArray.get(i)[0] - 1);
				float[] norm2 = normalArray.get(normalIndexArray.get(i)[1] - 1);
				float[] norm3 = normalArray.get(normalIndexArray.get(i)[2] - 1);
				
				float[] tex1 = textureArray.get(textureIndexArray.get(i)[0] - 1);
				float[] tex2 = textureArray.get(textureIndexArray.get(i)[1] - 1);
				float[] tex3 = textureArray.get(textureIndexArray.get(i)[2] - 1);
				
				vertexBuffer.put(vert1);
				vertexBuffer.put(norm1);
				vertexBuffer.put(tex1);
				
				vertexBuffer.put(vert2);
				vertexBuffer.put(norm2);
				vertexBuffer.put(tex2);
				
				vertexBuffer.put(vert3);
				vertexBuffer.put(norm3);
				vertexBuffer.put(tex3);
			}
			vertexBuffer.rewind();
			
			return new OBJData(vertexBuffer, "ship1.png", new float[] {1.0f, 1.0f, 1.0f}, 3, 8, new int[] {3, 3, 2}, new int[] {0, 3, 6}, true, true);
		}
		
		return null;
	}
}
