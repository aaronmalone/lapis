package edu.osu.lapis.serialize;

import org.junit.Test;

public class LapisJsonSerializationTest implements LapisSerializationTestInterface {

	private final LapisSerializationTest lapisSerializationTest;
	private final LapisJsonSerialization lapisJsonSerialization;
	
	public LapisJsonSerializationTest() {
		lapisSerializationTest = new LapisSerializationTest();
		lapisJsonSerialization = new LapisJsonSerialization();
		lapisSerializationTest.setLapisSerialization(lapisJsonSerialization);
	}
	
	private void setPrettyPrinting(boolean prettyPrinting) {
		lapisJsonSerialization.setPrettyPrinting(prettyPrinting);
	}
	
	@Test @Override
	public void testDouble() {
		setPrettyPrinting(false);
		lapisSerializationTest.testDouble();
		setPrettyPrinting(true);
		lapisSerializationTest.testDouble();
	}

	@Test @Override
	public void testInteger() {
		setPrettyPrinting(false);
		lapisSerializationTest.testInteger();
		setPrettyPrinting(true);
		lapisSerializationTest.testInteger();
	}

	@Test @Override
	public void testByte() {
		setPrettyPrinting(false);
		lapisSerializationTest.testByte();
		setPrettyPrinting(true);
		lapisSerializationTest.testByte();
	}

	@Test @Override
	public void testBoolean() {
		setPrettyPrinting(false);
		lapisSerializationTest.testBoolean();
		setPrettyPrinting(true);
		lapisSerializationTest.testBoolean();
	}

	@Test @Override
	public void testLong() {
		setPrettyPrinting(false);
		lapisSerializationTest.testLong();
		setPrettyPrinting(true);
		lapisSerializationTest.testLong();
	}

	@Test @Override
	public void testOneDimensionalArrayOfInteger() {
		setPrettyPrinting(false);
		lapisSerializationTest.testOneDimensionalArrayOfInteger();
		setPrettyPrinting(true);
		lapisSerializationTest.testOneDimensionalArrayOfInteger();
	}

	@Test @Override
	public void testOneDimensionalArrayOfLong() {
		setPrettyPrinting(false);
		lapisSerializationTest.testOneDimensionalArrayOfLong();
		setPrettyPrinting(true);
		lapisSerializationTest.testOneDimensionalArrayOfLong();
	}

	@Test @Override
	public void testOneDimensionalArrayOfDouble() {
		setPrettyPrinting(false);
		lapisSerializationTest.testOneDimensionalArrayOfDouble();
		setPrettyPrinting(true);
		lapisSerializationTest.testOneDimensionalArrayOfDouble();
	}

	@Test @Override
	public void testOneDimensionalArrayOfBoolean() {
		setPrettyPrinting(false);
		lapisSerializationTest.testOneDimensionalArrayOfBoolean();
		setPrettyPrinting(true);
		lapisSerializationTest.testOneDimensionalArrayOfBoolean();
	}

	@Test @Override
	public void testOneDimensionalArrayOfByte() {
		setPrettyPrinting(false);
		lapisSerializationTest.testOneDimensionalArrayOfByte();
		setPrettyPrinting(true);
		lapisSerializationTest.testOneDimensionalArrayOfByte();
	}
	
	@Test @Override
	public void testTwoDimensionalArrayOfInteger() {
		setPrettyPrinting(false);
		lapisSerializationTest.testTwoDimensionalArrayOfInteger();
		setPrettyPrinting(true);
		lapisSerializationTest.testTwoDimensionalArrayOfInteger();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfLong() {
		setPrettyPrinting(false);
		lapisSerializationTest.testTwoDimensionalArrayOfLong();
		setPrettyPrinting(true);
		lapisSerializationTest.testTwoDimensionalArrayOfLong();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfDouble() {
		setPrettyPrinting(false);
		lapisSerializationTest.testTwoDimensionalArrayOfDouble();
		setPrettyPrinting(true);
		lapisSerializationTest.testTwoDimensionalArrayOfDouble();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfByte() {
		setPrettyPrinting(false);
		lapisSerializationTest.testTwoDimensionalArrayOfByte();
		setPrettyPrinting(true);
		lapisSerializationTest.testTwoDimensionalArrayOfByte();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfBoolean() {
		setPrettyPrinting(false);
		lapisSerializationTest.testTwoDimensionalArrayOfBoolean();
		setPrettyPrinting(true);
		lapisSerializationTest.testTwoDimensionalArrayOfBoolean();
	}

	@Test @Override
	public void testThreeDimensionArrayOfInteger() {
		setPrettyPrinting(false);
		lapisSerializationTest.testThreeDimensionArrayOfInteger();
		setPrettyPrinting(true);
		lapisSerializationTest.testThreeDimensionArrayOfInteger();
	}

	@Test @Override
	public void testThreeDimensionArrayOfLong() {
		setPrettyPrinting(false);
		lapisSerializationTest.testThreeDimensionArrayOfLong();
		setPrettyPrinting(true);
		lapisSerializationTest.testThreeDimensionArrayOfLong();
	}

	@Test @Override
	public void testThreeDimensionArrayOfDouble() {
		setPrettyPrinting(false);
		lapisSerializationTest.testThreeDimensionArrayOfDouble();
		setPrettyPrinting(true);
		lapisSerializationTest.testThreeDimensionArrayOfDouble();
	}

	@Test @Override
	public void testThreeDimensionArrayOfByte() {
		setPrettyPrinting(false);
		lapisSerializationTest.testThreeDimensionArrayOfByte();
		setPrettyPrinting(true);
		lapisSerializationTest.testThreeDimensionArrayOfByte();
	}

	@Test @Override
	public void testThreeDimensionArrayOfBoolean() {
		setPrettyPrinting(false);
		lapisSerializationTest.testThreeDimensionArrayOfBoolean();
		setPrettyPrinting(true);
		lapisSerializationTest.testThreeDimensionArrayOfBoolean();
	}
}
