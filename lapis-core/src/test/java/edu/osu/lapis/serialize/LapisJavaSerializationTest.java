package edu.osu.lapis.serialize;

import org.junit.Test;

import edu.osu.lapis.serialization.JavaSerialization;

public class LapisJavaSerializationTest implements LapisSerializationTestInterface {

	private final ModelDataSerializationTest modelDataSerializationTest;
	
	public LapisJavaSerializationTest() {
		modelDataSerializationTest = new ModelDataSerializationTest();
		modelDataSerializationTest.setLapisSerialization(new JavaSerialization());
	}
	
	@Test @Override
	public void testDouble() {
		modelDataSerializationTest.testDouble();
	}

	@Test @Override
	public void testInteger() {
		modelDataSerializationTest.testInteger();
	}

	@Test @Override
	public void testByte() {
		modelDataSerializationTest.testByte();
	}

	@Test @Override
	public void testBoolean() {
		modelDataSerializationTest.testBoolean();
	}

	@Test @Override
	public void testLong() {
		modelDataSerializationTest.testLong();
	}

	@Test @Override
	public void testOneDimensionalArrayOfInteger() {
		modelDataSerializationTest.testOneDimensionalArrayOfInteger();
	}

	@Test @Override
	public void testOneDimensionalArrayOfLong() {
		modelDataSerializationTest.testOneDimensionalArrayOfLong();
	}

	@Test @Override
	public void testOneDimensionalArrayOfDouble() {
		modelDataSerializationTest.testOneDimensionalArrayOfDouble();
	}

	@Test @Override
	public void testOneDimensionalArrayOfBoolean() {
		modelDataSerializationTest.testOneDimensionalArrayOfBoolean();
	}

	@Test @Override
	public void testOneDimensionalArrayOfByte() {
		modelDataSerializationTest.testOneDimensionalArrayOfByte();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfInteger() {
		modelDataSerializationTest.testTwoDimensionalArrayOfInteger();
	}
	
	@Test @Override
	public void testTwoDimensionalArrayOfLong() {
		modelDataSerializationTest.testTwoDimensionalArrayOfLong();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfDouble() {
		modelDataSerializationTest.testTwoDimensionalArrayOfDouble();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfByte() {
		modelDataSerializationTest.testTwoDimensionalArrayOfByte();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfBoolean() {
		modelDataSerializationTest.testTwoDimensionalArrayOfBoolean();
	}

	@Test @Override
	public void testThreeDimensionArrayOfInteger() {
		modelDataSerializationTest.testThreeDimensionArrayOfInteger();
	}

	@Test @Override
	public void testThreeDimensionArrayOfLong() {
		modelDataSerializationTest.testThreeDimensionArrayOfLong();
	}

	@Test @Override
	public void testThreeDimensionArrayOfDouble() {
		modelDataSerializationTest.testThreeDimensionArrayOfDouble();
	}

	@Test @Override
	public void testThreeDimensionArrayOfByte() {
		modelDataSerializationTest.testThreeDimensionArrayOfByte();
	}

	@Test @Override
	public void testThreeDimensionArrayOfBoolean() {
		modelDataSerializationTest.testThreeDimensionArrayOfBoolean();
	}
}
