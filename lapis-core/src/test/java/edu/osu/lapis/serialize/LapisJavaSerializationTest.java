package edu.osu.lapis.serialize;

import org.junit.Test;

public class LapisJavaSerializationTest implements LapisSerializationTestInterface {

	private final LapisSerializationTest lapisSerializationTest;
	
	public LapisJavaSerializationTest() {
		lapisSerializationTest = new LapisSerializationTest();
		lapisSerializationTest.setLapisSerialization(new LapisJavaSerialization());
	}
	
	@Test @Override
	public void testDouble() {
		lapisSerializationTest.testDouble();
	}

	@Test @Override
	public void testInteger() {
		lapisSerializationTest.testInteger();
	}

	@Test @Override
	public void testByte() {
		lapisSerializationTest.testByte();
	}

	@Test @Override
	public void testBoolean() {
		lapisSerializationTest.testBoolean();
	}

	@Test @Override
	public void testLong() {
		lapisSerializationTest.testLong();
	}

	@Test @Override
	public void testOneDimensionalArrayOfInteger() {
		lapisSerializationTest.testOneDimensionalArrayOfInteger();
	}

	@Test @Override
	public void testOneDimensionalArrayOfLong() {
		lapisSerializationTest.testOneDimensionalArrayOfLong();
	}

	@Test @Override
	public void testOneDimensionalArrayOfDouble() {
		lapisSerializationTest.testOneDimensionalArrayOfDouble();
	}

	@Test @Override
	public void testOneDimensionalArrayOfBoolean() {
		lapisSerializationTest.testOneDimensionalArrayOfBoolean();
	}

	@Test @Override
	public void testOneDimensionalArrayOfByte() {
		lapisSerializationTest.testOneDimensionalArrayOfByte();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfInteger() {
		lapisSerializationTest.testTwoDimensionalArrayOfInteger();
	}
	
	@Test @Override
	public void testTwoDimensionalArrayOfLong() {
		lapisSerializationTest.testTwoDimensionalArrayOfLong();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfDouble() {
		lapisSerializationTest.testTwoDimensionalArrayOfDouble();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfByte() {
		lapisSerializationTest.testTwoDimensionalArrayOfByte();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfBoolean() {
		lapisSerializationTest.testTwoDimensionalArrayOfBoolean();
	}

	@Test @Override
	public void testThreeDimensionArrayOfInteger() {
		lapisSerializationTest.testThreeDimensionArrayOfInteger();
	}

	@Test @Override
	public void testThreeDimensionArrayOfLong() {
		lapisSerializationTest.testThreeDimensionArrayOfLong();
	}

	@Test @Override
	public void testThreeDimensionArrayOfDouble() {
		lapisSerializationTest.testThreeDimensionArrayOfDouble();
	}

	@Test @Override
	public void testThreeDimensionArrayOfByte() {
		lapisSerializationTest.testThreeDimensionArrayOfByte();
	}

	@Test @Override
	public void testThreeDimensionArrayOfBoolean() {
		lapisSerializationTest.testThreeDimensionArrayOfBoolean();
	}
}
