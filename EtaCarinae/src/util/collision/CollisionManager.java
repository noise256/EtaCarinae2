package util.collision;

import util.math.MathBox;

public class CollisionManager {
	public static boolean checkCollision(Collidable collidable, float[] point) {
		Shape bounds = collidable.getBounds();
		
		if (bounds.getShapeType() == ShapeType.CIRCLE) {
			return compareCircleBounds((Circle) bounds, point);
		}
		else if (bounds.getShapeType() == ShapeType.RECTANGLE) {
			return compareRectangleBounds((Rectangle) bounds, point);
		}
		else {
			return false;
		}
	}

	public static boolean checkCollision(Collidable first, Collidable second) {
		Shape bounds1 = first.getBounds();
		Shape bounds2 = second.getBounds();
		
		if (bounds1.getShapeType() != bounds2.getShapeType()) {
			throw new IllegalArgumentException("Shape types do not match.");
		}
		
		if (bounds1.getShapeType() == ShapeType.CIRCLE) {
			return compareCircleBounds((Circle) bounds1, (Circle) bounds2);
		}
		else if (bounds1.getShapeType() == ShapeType.RECTANGLE) {
			return compareRectangleBounds((Rectangle) bounds1, (Rectangle) bounds2);
		}
		else {
			return false;
		}
	}
	
	private static boolean compareCircleBounds(Circle circle, float[] point) {
		float distance = MathBox.getVectorDistance(circle.getPosition(), point);
		
		return distance < circle.getRadius() ? true : false;
	}
	
	private static boolean compareCircleBounds(Circle first, Circle second) {
		float distance = MathBox.getVectorDistance(first.getPosition(), second.getPosition());
		
		return distance < first.getRadius() + second.getRadius() ? true : false;
	}
	
	private static boolean compareRectangleBounds(Rectangle rectangle, float[] point) {
		if (MathBox.max(rectangle.getPosition()[0] - rectangle.getWidth()/2, point[0]) <= MathBox.min(rectangle.getPosition()[0] + rectangle.getWidth()/2, point[0]) &&
			MathBox.max(rectangle.getPosition()[1] - rectangle.getHeight()/2, point[1]) <= MathBox.min(rectangle.getPosition()[1] + rectangle.getHeight()/2, point[1])) {
			return true;
		}
		return false;
	}
	
	private static boolean compareRectangleBounds(Rectangle first, Rectangle second) {
		if (MathBox.max(first.getPosition()[0], second.getPosition()[0]) <= MathBox.min(first.getPosition()[0] + first.getWidth()/2, second.getPosition()[0] + second.getWidth()/2) &&
			MathBox.max(first.getPosition()[1], second.getPosition()[1]) <= MathBox.min(first.getPosition()[1] + first.getHeight()/2, second.getPosition()[1] + second.getHeight()/2)) {
			return true;
		}
		return false;
	}
}
