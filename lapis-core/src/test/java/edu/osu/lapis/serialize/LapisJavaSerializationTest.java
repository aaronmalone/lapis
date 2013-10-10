package edu.osu.lapis.serialize;

import org.junit.Test;

public class LapisJavaSerializationTest implements LapisSerializationTestInterface {

	private final LapisSerializationTest lapisSerializationTest;
	
	public LapisJavaSerializationTest() {
		lapisSerializationTest = new LapisSerializationTest();
		lapisSerializationTest.setLapisSerialization(new LapisJavaSerialization());
	}
	
	@Test
	public void testDouble() {
		lapisSerializationTest.testDouble();
	}

	@Test
	public void testInteger() {
		lapisSerializationTest.testInteger();
	}

	@Test
	public void testByte() {
		lapisSerializationTest.testByte();
	}

	@Test
	public void testBoolean() {
		lapisSerializationTest.testBoolean();
	}

	@Test
	public void testLong() {
		lapisSerializationTest.testLong();
	}

	@Test
	public void testOneDimensionalArrayOfInteger() {
		lapisSerializationTest.testOneDimensionalArrayOfInteger();
	}

	@Test
	public void testOneDimensionalArrayOfLong() {
		lapisSerializationTest.testOneDimensionalArrayOfLong();
	}

	@Test
	public void testOneDimensionalArrayOfDouble() {
		lapisSerializationTest.testOneDimensionalArrayOfDouble();
	}

	@Test
	public void testOneDimensionalArrayOfBoolean() {
		lapisSerializationTest.testOneDimensionalArrayOfBoolean();
	}

	@Test
	public void testOneDimensionalArrayOfByte() {
		lapisSerializationTest.testOneDimensionalArrayOfByte();
	}

	@Test
	public void testTwoDimensionalArrayOfInteger() {
		lapisSerializationTest.testTwoDimensionalArrayOfInteger();
	}

}
