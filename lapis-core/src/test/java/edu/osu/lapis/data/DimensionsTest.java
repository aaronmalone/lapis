package edu.osu.lapis.data;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class DimensionsTest {

	private static void test(Object dataArray, int expectedDimensionsLength, int ... expectedDimensions) {
		int[] actualDimensions = Dimensions.getDimensions(dataArray);
		Assert.assertEquals(expectedDimensionsLength, actualDimensions.length);
		Assert.assertTrue("Expected dimensions are not the same as actual dimensions.", 
				Arrays.equals(expectedDimensions, actualDimensions));
	}
	
	@Test
	public void testOneDimensionalArray() {
		test(new double[7], 1, 7);
	}

	@Test
	public void testOneDimensionalEmptyArray() {
		test(new long[0], 1, 0);
	}
	
	@Test
	public void testTwoDimensionalArray() {
		test(new byte[5][6], 2, 5, 6);
	}
	
	@Test
	public void testTwoDimensionalEmptyArray() {
		test(new float[0][], 2, 0, 0);
	}
	
	@Test
	public void testMultiDimensionalEmptyArrays() {
		test(new boolean[0][][][], 4, 0, 0, 0, 0);
		test(new int[0][][][][][][], 7, 0, 0, 0, 0, 0, 0, 0);
	}
	
	
	@Test 
	public void testMultiDimensionalArrays() {
		test(new byte[9][8][7][6], 4, 9, 8, 7, 6);
		test(new double[16][8][4][2][1][0][][][][], 10, 16, 8, 4, 2, 1, 0, 0, 0, 0, 0);
	}
}