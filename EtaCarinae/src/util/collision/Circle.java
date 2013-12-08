package util.collision;

public class Circle extends Shape {
	private float radius;
	
	public Circle(float[] position, float radius) {
		super(ShapeType.CIRCLE, position);
		this.radius = radius;
	}
	
	public float getRadius() {
		return radius;
	}
}
