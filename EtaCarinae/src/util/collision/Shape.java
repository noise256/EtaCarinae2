package util.collision;

public class Shape {
	private ShapeType shapeType;
	private float[] position;
	
	public Shape(ShapeType shapeType, float[] position) {
		this.shapeType = shapeType;
		this.position = position;
	}
	
	public ShapeType getShapeType() {
		return shapeType;
	}
	
	public float[] getPosition() {
		return position;
	}
}
