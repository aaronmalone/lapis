package edu.osu.lapis.serialize;

import org.junit.Test;

import edu.osu.lapis.serialization.JsonSerialization;

public class LapisJsonSerializationTest implements LapisSerializationTestInterface {

	private final ModelDataSerializationTest modelDataSerializationTest;
	private final JsonSerialization jsonSerialization;
	
	public LapisJsonSerializationTest() {
		modelDataSerializationTest = new ModelDataSerializationTest();
		jsonSerialization = new JsonSerialization();
		modelDataSerializationTest.setLapisSerialization(jsonSerialization);
	}
	
	private void setPrettyPrinting(boolean prettyPrinting) {
		jsonSerialization.setPrettyPrinting(prettyPrinting);
	}
	
	@Test @Override
	public void testDouble() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testDouble();
		setPrettyPrinting(true);
		modelDataSerializationTest.testDouble();
	}

	@Test @Override
	public void testInteger() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testInteger();
		setPrettyPrinting(true);
		modelDataSerializationTest.testInteger();
	}

	@Test @Override
	public void testByte() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testByte();
		setPrettyPrinting(true);
		modelDataSerializationTest.testByte();
	}

	@Test @Override
	public void testBoolean() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testBoolean();
		setPrettyPrinting(true);
		modelDataSerializationTest.testBoolean();
	}

	@Test @Override
	public void testLong() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testLong();
		setPrettyPrinting(true);
		modelDataSerializationTest.testLong();
	}

	@Test @Override
	public void testOneDimensionalArrayOfInteger() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testOneDimensionalArrayOfInteger();
		setPrettyPrinting(true);
		modelDataSerializationTest.testOneDimensionalArrayOfInteger();
	}

	@Test @Override
	public void testOneDimensionalArrayOfLong() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testOneDimensionalArrayOfLong();
		setPrettyPrinting(true);
		modelDataSerializationTest.testOneDimensionalArrayOfLong();
	}

	@Test @Override
	public void testOneDimensionalArrayOfDouble() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testOneDimensionalArrayOfDouble();
		setPrettyPrinting(true);
		modelDataSerializationTest.testOneDimensionalArrayOfDouble();
	}

	@Test @Override
	public void testOneDimensionalArrayOfBoolean() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testOneDimensionalArrayOfBoolean();
		setPrettyPrinting(true);
		modelDataSerializationTest.testOneDimensionalArrayOfBoolean();
	}

	@Test @Override
	public void testOneDimensionalArrayOfByte() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testOneDimensionalArrayOfByte();
		setPrettyPrinting(true);
		modelDataSerializationTest.testOneDimensionalArrayOfByte();
	}
	
	@Test @Override
	public void testTwoDimensionalArrayOfInteger() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testTwoDimensionalArrayOfInteger();
		setPrettyPrinting(true);
		modelDataSerializationTest.testTwoDimensionalArrayOfInteger();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfLong() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testTwoDimensionalArrayOfLong();
		setPrettyPrinting(true);
		modelDataSerializationTest.testTwoDimensionalArrayOfLong();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfDouble() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testTwoDimensionalArrayOfDouble();
		setPrettyPrinting(true);
		modelDataSerializationTest.testTwoDimensionalArrayOfDouble();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfByte() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testTwoDimensionalArrayOfByte();
		setPrettyPrinting(true);
		modelDataSerializationTest.testTwoDimensionalArrayOfByte();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfBoolean() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testTwoDimensionalArrayOfBoolean();
		setPrettyPrinting(true);
		modelDataSerializationTest.testTwoDimensionalArrayOfBoolean();
	}

	@Test @Override
	public void testThreeDimensionArrayOfInteger() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testThreeDimensionArrayOfInteger();
		setPrettyPrinting(true);
		modelDataSerializationTest.testThreeDimensionArrayOfInteger();
	}

	@Test @Override
	public void testThreeDimensionArrayOfLong() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testThreeDimensionArrayOfLong();
		setPrettyPrinting(true);
		modelDataSerializationTest.testThreeDimensionArrayOfLong();
	}

	@Test @Override
	public void testThreeDimensionArrayOfDouble() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testThreeDimensionArrayOfDouble();
		setPrettyPrinting(true);
		modelDataSerializationTest.testThreeDimensionArrayOfDouble();
	}

	@Test @Override
	public void testThreeDimensionArrayOfByte() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testThreeDimensionArrayOfByte();
		setPrettyPrinting(true);
		modelDataSerializationTest.testThreeDimensionArrayOfByte();
	}

	@Test @Override
	public void testThreeDimensionArrayOfBoolean() {
		setPrettyPrinting(false);
		modelDataSerializationTest.testThreeDimensionArrayOfBoolean();
		setPrettyPrinting(true);
		modelDataSerializationTest.testThreeDimensionArrayOfBoolean();
	}
}
