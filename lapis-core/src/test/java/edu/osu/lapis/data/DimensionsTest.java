package edu.osu.lapis.data;

import org.junit.Assert;
import org.junit.Test;

public class DimensionsTest {

	//TODO add more tests
	
	@Test
	public void testOneDimensionalArray() {
		double[] doubleArray = new double[7];
		int[] dimensions = Dimensions.getDimensions(doubleArray);
		Assert.assertEquals(1, dimensions.length);
		Assert.assertEquals(7, dimensions[0]);
	}

	@Test
	public void testOneDimensionalEmptyArray() {
		long[] longArray = new long[0];
		int[] dimensions = Dimensions.getDimensions(longArray);
		Assert.assertEquals(1, dimensions.length);
		Assert.assertEquals(0, dimensions[0]);
	}
	
	@Test
	public void testTwoDimensionalArray() {
		//TODO IMPLEMENT
	}
	
	@Test
	public void testTwoDimensionalEmptyArray() {
		float[][] floatArray = new float[0][];
		int[] dimensions = Dimensions.getDimensions(floatArray);
		Assert.assertEquals(2, dimensions.length);
		Assert.assertEquals(0, dimensions[0]);
		Assert.assertEquals(0, dimensions[1]);
	}
	
	@Test
	public void testMultiDimensionalEmptyArrays() {
		int[][][] threeDimEmpty = new int[][][]{};
		int[] dimensions = Dimensions.getDimensions(threeDimEmpty);
		Assert.assertArrayEquals(new int[]{0, 0, 0}, dimensions);
	}
	
	@Test 
	public void testMultiDimensionalArrays() {
		byte[][][][] byteArray = new byte[9][8][7][6];
		int[] dimensions = Dimensions.getDimensions(byteArray);
		Assert.assertEquals(4, dimensions.length);
		Assert.assertEquals(9, dimensions[0]);
		Assert.assertEquals(8, dimensions[1]);
		Assert.assertEquals(7, dimensions[2]);
		Assert.assertEquals(6, dimensions[3]);
	}
	
}