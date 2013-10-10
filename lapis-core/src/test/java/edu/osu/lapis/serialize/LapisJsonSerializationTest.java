package edu.osu.lapis.serialize;

import org.junit.Test;

public class LapisJsonSerializationTest implements LapisSerializationTestInterface{

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
	
	@Test
	public void testDouble() {
		setPrettyPrinting(false);
		lapisSerializationTest.testDouble();
		setPrettyPrinting(true);
		lapisSerializationTest.testDouble();
	}

	@Test
	public void testInteger() {
		setPrettyPrinting(false);
		lapisSerializationTest.testInteger();
		setPrettyPrinting(true);
		lapisSerializationTest.testInteger();
	}

	@Test
	public void testByte() {
		setPrettyPrinting(false);
		lapisSerializationTest.testByte();
		setPrettyPrinting(true);
		lapisSerializationTest.testByte();
	}

	@Test
	public void testBoolean() {
		setPrettyPrinting(false);
		lapisSerializationTest.testBoolean();
		setPrettyPrinting(true);
		lapisSerializationTest.testBoolean();
	}

	@Test
	public void testLong() {
		setPrettyPrinting(false);
		lapisSerializationTest.testLong();
		setPrettyPrinting(true);
		lapisSerializationTest.testLong();
	}

	@Test
	public void testOneDimensionalArrayOfInteger() {
		setPrettyPrinting(false);
		lapisSerializationTest.testOneDimensionalArrayOfInteger();
		setPrettyPrinting(true);
		lapisSerializationTest.testOneDimensionalArrayOfInteger();
	}

	@Test
	public void testOneDimensionalArrayOfLong() {
		setPrettyPrinting(false);
		lapisSerializationTest.testOneDimensionalArrayOfLong();
		setPrettyPrinting(true);
		lapisSerializationTest.testOneDimensionalArrayOfLong();
	}

	@Test
	public void testOneDimensionalArrayOfDouble() {
		setPrettyPrinting(false);
		lapisSerializationTest.testOneDimensionalArrayOfDouble();
		setPrettyPrinting(true);
		lapisSerializationTest.testOneDimensionalArrayOfDouble();
	}

	@Test
	public void testOneDimensionalArrayOfBoolean() {
		setPrettyPrinting(false);
		lapisSerializationTest.testOneDimensionalArrayOfBoolean();
		setPrettyPrinting(true);
		lapisSerializationTest.testOneDimensionalArrayOfBoolean();
	}

	@Test
	public void testOneDimensionalArrayOfByte() {
		setPrettyPrinting(false);
		lapisSerializationTest.testOneDimensionalArrayOfByte();
		setPrettyPrinting(true);
		lapisSerializationTest.testOneDimensionalArrayOfByte();
	}
	
	@Test
	public void testTwoDimensionalArrayOfInteger() {
		setPrettyPrinting(false);
		lapisSerializationTest.testTwoDimensionalArrayOfInteger();
		setPrettyPrinting(true);
		lapisSerializationTest.testTwoDimensionalArrayOfInteger();
	}
}
