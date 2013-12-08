package util.collision;

public class Rectangle extends Shape {
	private float width;
	private float height;
	
	public Rectangle(float[] position, float width, float height) {
		super(ShapeType.RECTANGLE, position);
		this.width = width;
		this.height = height;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
}
