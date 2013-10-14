package edu.osu.lapis.serialize;

import org.junit.Test;

public class LapisJavaSerializationTest implements LapisSerializationTestInterface {

	private final LapisDatumSerializationTest lapisDatumSerializationTest;
	
	public LapisJavaSerializationTest() {
		lapisDatumSerializationTest = new LapisDatumSerializationTest();
		lapisDatumSerializationTest.setLapisSerialization(new LapisJavaSerialization());
	}
	
	@Test @Override
	public void testDouble() {
		lapisDatumSerializationTest.testDouble();
	}

	@Test @Override
	public void testInteger() {
		lapisDatumSerializationTest.testInteger();
	}

	@Test @Override
	public void testByte() {
		lapisDatumSerializationTest.testByte();
	}

	@Test @Override
	public void testBoolean() {
		lapisDatumSerializationTest.testBoolean();
	}

	@Test @Override
	public void testLong() {
		lapisDatumSerializationTest.testLong();
	}

	@Test @Override
	public void testOneDimensionalArrayOfInteger() {
		lapisDatumSerializationTest.testOneDimensionalArrayOfInteger();
	}

	@Test @Override
	public void testOneDimensionalArrayOfLong() {
		lapisDatumSerializationTest.testOneDimensionalArrayOfLong();
	}

	@Test @Override
	public void testOneDimensionalArrayOfDouble() {
		lapisDatumSerializationTest.testOneDimensionalArrayOfDouble();
	}

	@Test @Override
	public void testOneDimensionalArrayOfBoolean() {
		lapisDatumSerializationTest.testOneDimensionalArrayOfBoolean();
	}

	@Test @Override
	public void testOneDimensionalArrayOfByte() {
		lapisDatumSerializationTest.testOneDimensionalArrayOfByte();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfInteger() {
		lapisDatumSerializationTest.testTwoDimensionalArrayOfInteger();
	}
	
	@Test @Override
	public void testTwoDimensionalArrayOfLong() {
		lapisDatumSerializationTest.testTwoDimensionalArrayOfLong();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfDouble() {
		lapisDatumSerializationTest.testTwoDimensionalArrayOfDouble();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfByte() {
		lapisDatumSerializationTest.testTwoDimensionalArrayOfByte();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfBoolean() {
		lapisDatumSerializationTest.testTwoDimensionalArrayOfBoolean();
	}

	@Test @Override
	public void testThreeDimensionArrayOfInteger() {
		lapisDatumSerializationTest.testThreeDimensionArrayOfInteger();
	}

	@Test @Override
	public void testThreeDimensionArrayOfLong() {
		lapisDatumSerializationTest.testThreeDimensionArrayOfLong();
	}

	@Test @Override
	public void testThreeDimensionArrayOfDouble() {
		lapisDatumSerializationTest.testThreeDimensionArrayOfDouble();
	}

	@Test @Override
	public void testThreeDimensionArrayOfByte() {
		lapisDatumSerializationTest.testThreeDimensionArrayOfByte();
	}

	@Test @Override
	public void testThreeDimensionArrayOfBoolean() {
		lapisDatumSerializationTest.testThreeDimensionArrayOfBoolean();
	}
}
