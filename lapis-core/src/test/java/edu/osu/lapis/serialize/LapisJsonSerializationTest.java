package edu.osu.lapis.serialize;

import org.junit.Test;

public class LapisJsonSerializationTest implements LapisSerializationTestInterface {

	private final LapisDatumSerializationTest lapisDatumSerializationTest;
	private final LapisJsonSerialization lapisJsonSerialization;
	
	public LapisJsonSerializationTest() {
		lapisDatumSerializationTest = new LapisDatumSerializationTest();
		lapisJsonSerialization = new LapisJsonSerialization();
		lapisDatumSerializationTest.setLapisSerialization(lapisJsonSerialization);
	}
	
	private void setPrettyPrinting(boolean prettyPrinting) {
		lapisJsonSerialization.setPrettyPrinting(prettyPrinting);
	}
	
	@Test @Override
	public void testDouble() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testDouble();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testDouble();
	}

	@Test @Override
	public void testInteger() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testInteger();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testInteger();
	}

	@Test @Override
	public void testByte() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testByte();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testByte();
	}

	@Test @Override
	public void testBoolean() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testBoolean();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testBoolean();
	}

	@Test @Override
	public void testLong() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testLong();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testLong();
	}

	@Test @Override
	public void testOneDimensionalArrayOfInteger() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testOneDimensionalArrayOfInteger();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testOneDimensionalArrayOfInteger();
	}

	@Test @Override
	public void testOneDimensionalArrayOfLong() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testOneDimensionalArrayOfLong();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testOneDimensionalArrayOfLong();
	}

	@Test @Override
	public void testOneDimensionalArrayOfDouble() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testOneDimensionalArrayOfDouble();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testOneDimensionalArrayOfDouble();
	}

	@Test @Override
	public void testOneDimensionalArrayOfBoolean() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testOneDimensionalArrayOfBoolean();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testOneDimensionalArrayOfBoolean();
	}

	@Test @Override
	public void testOneDimensionalArrayOfByte() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testOneDimensionalArrayOfByte();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testOneDimensionalArrayOfByte();
	}
	
	@Test @Override
	public void testTwoDimensionalArrayOfInteger() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testTwoDimensionalArrayOfInteger();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testTwoDimensionalArrayOfInteger();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfLong() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testTwoDimensionalArrayOfLong();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testTwoDimensionalArrayOfLong();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfDouble() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testTwoDimensionalArrayOfDouble();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testTwoDimensionalArrayOfDouble();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfByte() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testTwoDimensionalArrayOfByte();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testTwoDimensionalArrayOfByte();
	}

	@Test @Override
	public void testTwoDimensionalArrayOfBoolean() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testTwoDimensionalArrayOfBoolean();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testTwoDimensionalArrayOfBoolean();
	}

	@Test @Override
	public void testThreeDimensionArrayOfInteger() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testThreeDimensionArrayOfInteger();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testThreeDimensionArrayOfInteger();
	}

	@Test @Override
	public void testThreeDimensionArrayOfLong() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testThreeDimensionArrayOfLong();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testThreeDimensionArrayOfLong();
	}

	@Test @Override
	public void testThreeDimensionArrayOfDouble() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testThreeDimensionArrayOfDouble();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testThreeDimensionArrayOfDouble();
	}

	@Test @Override
	public void testThreeDimensionArrayOfByte() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testThreeDimensionArrayOfByte();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testThreeDimensionArrayOfByte();
	}

	@Test @Override
	public void testThreeDimensionArrayOfBoolean() {
		setPrettyPrinting(false);
		lapisDatumSerializationTest.testThreeDimensionArrayOfBoolean();
		setPrettyPrinting(true);
		lapisDatumSerializationTest.testThreeDimensionArrayOfBoolean();
	}
}
