package edu.osu.lapis.serialize;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;

import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.serialization.SerializationObject;

//TODO LOOK FOR USE OF LAPIS RANDOMS
public class ModelDataSerializationTest implements LapisSerializationTestInterface { 
	
	private static final String NOT_EQUAL_MSG = "Arrays are not equal.";
	private final Random random = new Random();
	
	private LapisSerialization lapisSerialization;

	@Override
	public void testDouble() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), random.nextDouble());
		doTest(original);
	}
	
	@Override
	public void testInteger() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), random.nextInt());
		doTest(original);
	}

	@Override
	public void testByte() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), (byte)random.nextInt());
		doTest(original);
	}

	@Override
	public void testBoolean() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), random.nextBoolean());
		doTest(original);
	}

	@Override
	public void testLong() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), random.nextLong());
		doTest(original);
	}
	
	@Override
	public void testOneDimensionalArrayOfInteger() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), new int[]{});
		doTest(original);
		original = new SerializationObject(RandomStringUtils.randomNumeric(10), getOneDimensionalArrayOfInteger());
		doTest(original);
	}

	@Override
	public void testOneDimensionalArrayOfLong() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), new long[]{});
		doTest(original);
		original = new SerializationObject(RandomStringUtils.randomNumeric(10), getOneDimensionalArrayOfLong());
		doTest(original);
	}
	
	@Override
	public void testOneDimensionalArrayOfDouble() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), new double[]{});
		doTest(original);
		original = new SerializationObject(RandomStringUtils.randomNumeric(10), getOneDimensionalArrayOfDouble());
		doTest(original);
	}
	
	@Override
	public void testOneDimensionalArrayOfBoolean() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), new boolean[]{});
		doTest(original);
		original = new SerializationObject(RandomStringUtils.randomNumeric(10), getOneDimensionArrayOfBoolean());
		doTest(original);
	}
	
	@Override
	public void testOneDimensionalArrayOfByte() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), new byte[]{});
		doTest(original);
		byte[] byteArray = new byte[random.nextInt(16)];
		random.nextBytes(byteArray);
		original = new SerializationObject(RandomStringUtils.randomNumeric(10), byteArray);
		doTest(original);
	}
	
	@Override
	public void testTwoDimensionalArrayOfInteger() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), new int[][]{});
		doTest(original);
		original = new SerializationObject(RandomStringUtils.randomNumeric(10), getRandomArray(int[][].class));
		doTest(original);
	}
	
	@Override
	public void testTwoDimensionalArrayOfLong() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), new long[][]{});
		doTest(original);
		original = new SerializationObject(RandomStringUtils.randomNumeric(10), getRandomArray(long[][].class));
		doTest(original);
	}
	
	@Override
	public void testTwoDimensionalArrayOfDouble() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), new double[][]{});
		doTest(original);
		original = new SerializationObject(RandomStringUtils.randomNumeric(10), getRandomArray(double[][].class));
		doTest(original);
	}
	
	@Override
	public void testTwoDimensionalArrayOfByte() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), new byte[][]{});
		doTest(original);
		original = new SerializationObject(RandomStringUtils.randomNumeric(10), getRandomArray(byte[][].class));
		doTest(original);
	}

	@Override
	public void testTwoDimensionalArrayOfBoolean() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), new boolean[][]{});
		doTest(original);
		original = new SerializationObject(RandomStringUtils.randomNumeric(10), getRandomArray(boolean[][].class));
		doTest(original);
	}

	@Override
	public void testThreeDimensionArrayOfInteger() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), new int[][][]{});
		doTest(original);
		original = new SerializationObject(RandomStringUtils.randomNumeric(10), getRandomArray(int[][][].class));
		doTest(original);
	}
	
	@Override
	public void testThreeDimensionArrayOfLong() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), new long[][][]{});
		doTest(original);
		original = new SerializationObject(RandomStringUtils.randomNumeric(10), getRandomArray(long[][][].class));
		doTest(original);
	}
	
	@Override
	public void testThreeDimensionArrayOfDouble() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), new double[][][]{});
		doTest(original);
		original = new SerializationObject(RandomStringUtils.randomNumeric(10), getRandomArray(double[][][].class));
		doTest(original);
	}

	@Override
	public void testThreeDimensionArrayOfByte() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), new byte[][][]{});
		doTest(original);
		original = new SerializationObject(RandomStringUtils.randomNumeric(10), getRandomArray(byte[][][].class));
		doTest(original);
	}

	@Override
	public void testThreeDimensionArrayOfBoolean() {
		SerializationObject original = new SerializationObject(RandomStringUtils.randomNumeric(10), new boolean[][][]{});
		doTest(original);
		original = new SerializationObject(RandomStringUtils.randomNumeric(10), getRandomArray(boolean[][][].class));
		doTest(original);
	}

	private boolean[] getOneDimensionArrayOfBoolean() {
		boolean[] booleanArray = new boolean[random.nextInt(16)];
		for(int i = 0; i < booleanArray.length; ++i) {
			booleanArray[i] = random.nextBoolean();
		}
		return booleanArray;
	}

	private int[] getOneDimensionalArrayOfInteger() {
		int[] intArray = new int[random.nextInt(16)];
		for(int i = 0; i < intArray.length; ++i) {
			intArray[i] = random.nextInt();
		}
		return intArray;
	}
	
	private long[] getOneDimensionalArrayOfLong() {
		long[] longArray = new long[random.nextInt(16)];
		for(int i = 0; i < longArray.length; ++i) {
			longArray[i] = random.nextLong();
		}
		return longArray;
	}
	
	private double[] getOneDimensionalArrayOfDouble() {
		double[] doubleArray = new double[random.nextInt(16)];
		for(int i = 0; i < doubleArray.length; ++i) {
			doubleArray[i] = random.nextDouble();
		}
		return doubleArray;
	}
	
	private <A> A getRandomArray(Class<A> arrayType) {
		return getRandomArray(arrayType, 1 + random.nextInt(10));
	}
	
	@SuppressWarnings("unchecked")
	private <A> A getRandomArray(final Class<A> arrayType, final int length) {
		Class<?> componentType = arrayType.getComponentType();
		Object returnArray = Array.newInstance(componentType, length);
		if(componentType.isArray()) {
			int subLength = 1 + random.nextInt(10);
			for(int i = 0; i < length; ++i) {
				Array.set(returnArray, i, getRandomArray(componentType, subLength));
			}
		} else {
			if(componentType.equals(Boolean.TYPE)) {
				returnArray = Arrays.copyOf(getOneDimensionArrayOfBoolean(), length);
			} else if(componentType.equals(Byte.TYPE)) {
				byte[] bytes = new byte[length];
				random.nextBytes(bytes);
				returnArray = bytes;
			} else if(componentType.equals(Double.TYPE)) {
				returnArray = Arrays.copyOf(getOneDimensionalArrayOfDouble(), length);
			} else if(componentType.equals(Integer.TYPE)) {
				returnArray = Arrays.copyOf(getOneDimensionalArrayOfInteger(), length);
			} else if(componentType.equals(Long.TYPE)) {
				returnArray = Arrays.copyOf(getOneDimensionalArrayOfLong(), length);
			} else {
				throw new IllegalArgumentException("Cannot handle component type: " + componentType);
			}
		}
		return (A) returnArray;
	}
	
	private void doTest(SerializationObject original) {		
		SerializationObject deserialized = serializeAndDeserialize(original);
		Assert.assertEquals(original.getName(), deserialized.getName());
		validateData(original.getData(), deserialized.getData());
		validateSerializedObjects(original, deserialized);
	}
	
	private SerializationObject serializeAndDeserialize(SerializationObject ld) {
		byte[] serialized = lapisSerialization.serialize(ld);
		return lapisSerialization.deserializeModelData(serialized);
	}
	
	private void validateData(Object originalData, Object deserializedData) {
		if(!originalData.equals(deserializedData)) {
			if(originalData.getClass().equals(deserializedData.getClass())) {
				if(originalData.getClass().isArray()) {
					validataArrays(originalData, deserializedData);
				} else {
					throw new IllegalArgumentException("Objects have the same type, but " 
							+ "are not arrays and are not equal with the equals() method.");
				}
			} else {
				throw new IllegalArgumentException("Expected object data of type "
						+ originalData.getClass() + " but got " + deserializedData.getClass());
			}
		}
	}

	private void validataArrays(Object originalData, Object deserializedData) {
		String swit = originalData.getClass().toString().trim();
		if(swit.equals("class [I")) { //int[]
			Assert.assertTrue(NOT_EQUAL_MSG, Arrays.equals((int[])originalData, (int[])deserializedData));
		} else if(swit.equals("class [J")) {
			Assert.assertTrue(NOT_EQUAL_MSG, Arrays.equals((long[])originalData, (long[])deserializedData));
		} else if(swit.equals("class [D")) { //double[]
			Assert.assertTrue(NOT_EQUAL_MSG, Arrays.equals((double[])originalData, (double[])deserializedData));
		} else if(swit.equals("class [Z")) { //boolean[]
			Assert.assertTrue(NOT_EQUAL_MSG, Arrays.equals((boolean[])originalData, (boolean[])deserializedData));
		} else if(swit.equals("class [B")) { //byte[]
			Assert.assertTrue(NOT_EQUAL_MSG, Arrays.equals((byte[])originalData, (byte[])deserializedData));
		} else {
			Assert.assertTrue(NOT_EQUAL_MSG, Arrays.deepEquals((Object[])originalData, (Object[])deserializedData));
			//TODO LOOK AT HOW DEEP EQUALS WORKS
		}
	}
	
	private void validateSerializedObjects(SerializationObject original, SerializationObject deserialized) {
		byte[] originalSerialized = lapisSerialization.serialize(original);
		byte[] deserialSerialized = lapisSerialization.serialize(deserialized);
		Assert.assertTrue("Serialized objects should be equal.", Arrays.equals(originalSerialized, deserialSerialized));
	}
	
	public void setLapisSerialization(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}
}
