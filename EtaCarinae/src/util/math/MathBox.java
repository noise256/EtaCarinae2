package util.math;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

public class MathBox {
	public static float DEFAULT_EPSILON = 0.0001f;
	public static float PI = (float) Math.PI;
	
	private static Random random = new Random();
	
	public static boolean equals(float a, float b) {
		if (MathBox.abs(a - b) < DEFAULT_EPSILON) {
			return true;
		}
		return false;
	}
	
	public static float max(float a, float b) {
		if (a >= b) {
			return a;
		}
		else {
			return b;
		}
	}
	
	public static float min(float a, float b) {
		if (a <= b) {
			return a;
		}
		else {
			return b;
		}
	}
	
	public static int max(int a, int b) {
		if (a > b) {
			return a;
		}
		else {
			return b;
		}
	}
	
	public static int min(int a, int b) {
		if (a < b) {
			return a;
		}
		else {
			return b;
		}
	}
	
	public static float abs(float value) {
		if (value < 0) {
			return value * -1;
		}
		else {
			return value;
		}
	}
	
	public static float atan2(float y, float x) {
		return (float) Math.atan2(y, x);
	}
	
	public static float cos(float angle) {
		return (float) Math.cos(angle);
	}
	
	public static float sin(float angle) {
		return (float) Math.sin(angle);
	}
	
	public static float[] angleToVector(float angle) {
		return new float[] {cos(angle), sin(angle)};
	}
	
	public static float dotProduct(float[] vector1, float[] vector2) {
		if (vector1.length != vector2.length) {
			throw new IllegalArgumentException("Vector lengths do not match.");
		}
		
		float c = 0;
		
		for (int i = 0; i < vector1.length; i++) {
			c += vector1[i] * vector2[i];
		}
		
		return c;
	}
	
	@Test
	public void testAngleToVector() {
		float[] vector = MathBox.angleToVector(0.0f);
		assertEquals(1.0f, vector[0], DEFAULT_EPSILON);
		assertEquals(0.0f, vector[1], DEFAULT_EPSILON);
	}
	
	public static boolean checkVectorEquality(float[] vector1, float[] vector2) {
		if (vector1.length != vector2.length) {
			throw new IllegalArgumentException("Vector lengths do not match.");
		}
		
		float[] distance = new float[vector1.length];
		
		for (int i = 0; i < vector1.length; i++) {
			distance[i] = vector1[i] - vector2[i];
		}
		
		if (getVectorMagnitude(distance) > DEFAULT_EPSILON) {
			return false;
		}
		
		return true;
	}
	
	@Test
	public void testCheckVectorEquality() {
		assertEquals(true, MathBox.checkVectorEquality(new float[] {1, 1}, new float[] {1, 1}));
		assertEquals(true, MathBox.checkVectorEquality(new float[] {0.000001f, 0.000001f}, new float[] {0.000001f, 0.000001f}));
		assertEquals(true, MathBox.checkVectorEquality(new float[] {0.000001f, -0.000001f}, new float[] {0.000001f, -0.000001f}));
		
		assertEquals(false, MathBox.checkVectorEquality(new float[] {1, 1}, new float[] {2, 2}));
	}
	
	public static boolean checkVectorParallel(float[] vector1, float[] vector2) {
		if (vector1.length != vector2.length) {
			throw new IllegalArgumentException("Vector lengths do not match.");
		}
		
		float[] unitVector1 = MathBox.normaliseVector(vector1);
		float[] unitVector2 = MathBox.normaliseVector(vector2);
		
		float difference = 0;
		for (int i = 0; i < vector1.length; i++) {
			difference += unitVector1[i] - unitVector2[i];
		}
		
		if (difference < DEFAULT_EPSILON) {
			return true;
		}
		return false;
	}
	
	@Test
	public void testCheckVectorParallel() {
		assertEquals(true, MathBox.checkVectorParallel(new float[] {1, 1}, new float[] {1, 1}));
		assertEquals(true, MathBox.checkVectorParallel(new float[] {-1, -1}, new float[] {1, 1}));
		assertEquals(true, MathBox.checkVectorParallel(new float[] {1, 1}, new float[] {2, 2}));
		
		assertEquals(false, MathBox.checkVectorParallel(new float[] {1, 1}, new float[] {-1, 1}));
		assertEquals(false, MathBox.checkVectorParallel(new float[] {1, 1}, new float[] {0, 1}));
		assertEquals(false, MathBox.checkVectorParallel(new float[] {1, 1}, new float[] {1, 10}));
	}
	
	public static boolean checkVectorDirection(float[] vector1, float[] vector2) {
		return checkVectorEquality(normaliseVector(vector1), normaliseVector(vector2));
	}
	
	@Test
	public void testCheckVectorDirection() {
		assertEquals(true, MathBox.checkVectorDirection(new float[] {1.0f, 1.0f}, new float[] {1.0f, 1.0f}));
		assertEquals(true, MathBox.checkVectorDirection(new float[] {1.0f, 1.0f}, new float[] {2.0f, 2.0f}));
		
		assertEquals(false, MathBox.checkVectorDirection(new float[] {1.0f, 1.0f}, new float[] {-1.0f, -1.0f}));
		assertEquals(false, MathBox.checkVectorDirection(new float[] {1.0f, 1.0f}, new float[] {1.0f, 2.0f}));
	}
	
	public static double[][] unflattenMatrixN(double[] matrix, int length) {
		if (matrix.length % length != 0) {
			throw new IllegalArgumentException("Invalid matrix length. Matrix has a length of [" + matrix.length + "]. Length parameter supplied was [" + length + "].");
		}
		
		double[][] unflat = new double[length][length];

		int c = 0;
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				unflat[i][j] = matrix[c];
				c++;
			}
		}

		return unflat;
	}
	
	//TODO write proper flat multiplication, don't use 2d matrix multiplication
	public static double[][] multiplyFlatMatrices(double[] a, double[] b, int length) {
		return multiplyMatrices(unflattenMatrixN(a, length), unflattenMatrixN(b, length));
	}
	
	public static double[][] multiplyMatrices(double a[][], double b[][]) {

		int aRows = a.length, aColumns = a[0].length, bRows = b.length, bColumns = b[0].length;

		if (aColumns != bRows) {
			throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
		}

		double[][] resultant = new double[aRows][bColumns];

		for (int i = 0; i < aRows; i++) { // aRow
			for (int j = 0; j < bColumns; j++) { // bColumn
				for (int k = 0; k < aColumns; k++) { // aColumn
					resultant[i][j] += a[i][k] * b[k][j];
				}
			}
		}

		return resultant;
	}
	
    public static double[] rowMultiplyVectorMatrix(double[][] A, double[] x) {
        int m = A.length;
        int n = A[0].length;
        if (x.length != n) throw new IllegalArgumentException("Illegal matrix dimensions.");
        double[] y = new double[m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                y[i] += (A[i][j] * x[j]);
        return y;
    }

	public static float nextFloat() {
		return random.nextFloat();
	}
	
	public static int nextInt(int n) {
		return random.nextInt(n);
	}
	
	public static boolean nextBoolean() {
		return random.nextBoolean();
	}
	
	public static int nextSign() {
		return random.nextBoolean() ? 1 : -1;
	}
	
	public static float getVectorDistance(float[] a, float[] b) {
		if (a.length != b.length) {
			throw new IllegalArgumentException("Vector lengths do not match.");
		}
		
		float sum = 0.0f;
		for (int i = 0; i < a.length; i++) {
			sum += (a[i] - b[i]) * (a[i] - b[i]);
		}
		
		return (float) Math.sqrt((double) sum);
	}
	
	public static float[] addVectors(float[] a, float[] b) {
		if (a.length != b.length) {
			throw new IllegalArgumentException("Vector lengths do not match.");
		}
		
		float[] c = new float[a.length];
		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] + b[i];
		}
		return c;
	}
	
	public static float[] subtractVectors(float[] a, float[] b) {
		if (a.length != b.length) {
			throw new IllegalArgumentException("Vector lengths do not match.");
		}
		
		float[] c = new float[a.length];
		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] - b[i];
		}
		return c;
	}
	
	public static float[] normaliseVector(float[] a) {
		float[] b = new float[a.length];
		
		float m = getVectorMagnitude(a);
		
		if (m == 0.0f) {
			return new float[] {0.0f, 0.0f};
		}
		
		for (int i = 0; i < a.length; i++) {
			b[i] = a[i] / m;
		}
		
		return b;
	}

	public static float[] scalarVector(float[] a, float s) {
		float[] b = new float[a.length];
		for (int i = 0; i < a.length; i++) {
			b[i] = a[i] * s;
		}
		return b;
	}
	
	@Test
	public void testGetVectorMagnitudeWithZero() {
		Assert.assertEquals(0.0f, getVectorMagnitude(new float[] {0.0f,  0.0f}), 0.0f);
	}
	public static float getVectorMagnitude(float[] a) {
		float m = 0;
		for (int i = 0; i < a.length; i++) {
			m += a[i] * a[i];
		}
		return (float) Math.sqrt(m);
	}
}
