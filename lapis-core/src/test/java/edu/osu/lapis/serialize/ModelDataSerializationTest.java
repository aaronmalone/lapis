package edu.osu.lapis.serialize;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;

import edu.osu.lapis.data.Dimensions;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.serialization.SerializationObject;

public class ModelDataSerializationTest implements LapisSerializationTestInterface { 
	
	private static final String NOT_EQUAL_MSG = "Arrays are not equal.";
	private final Random random = new Random();
	
	private LapisSerialization lapisSerialization;

	@Override
	public void testDouble() {
		SerializationObject original = getSerializationObject(LapisDataType.DOUBLE);
		original.setData(random.nextDouble());
		doTest(original);
	}
	
	@Override
	public void testInteger() {
		SerializationObject original = getSerializationObject(LapisDataType.INTEGER);
		original.setData(random.nextInt());
		doTest(original);
	}

	@Override
	public void testByte() {
		SerializationObject original = getSerializationObject(LapisDataType.BYTE);
		original.setData((byte)random.nextInt());
		doTest(original);
	}

	@Override
	public void testBoolean() {
		SerializationObject original = getSerializationObject(LapisDataType.BOOLEAN);
		original.setData(random.nextBoolean());
		doTest(original);
	}

	@Override
	public void testLong() {
		SerializationObject original = getSerializationObject(LapisDataType.LONG);
		original.setData(random.nextLong());
		doTest(original);
	}
	
	@Override
	public void testOneDimensionalArrayOfInteger() {
		SerializationObject original = getSerializationObject(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_INTEGER);
		original.setData(new int[]{});
		doTest(original);
		original.setData(getOneDimensionalArrayOfInteger());
		doTest(original);
	}

	@Override
	public void testOneDimensionalArrayOfLong() {
		SerializationObject original = getSerializationObject(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_LONG);
		original.setData(new long[]{});
		doTest(original);
		original.setData(getOneDimensionalArrayOfLong());
		doTest(original);
	}
	
	@Override
	public void testOneDimensionalArrayOfDouble() {
		SerializationObject original = getSerializationObject(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_DOUBLE);
		original.setData(new double[]{});
		doTest(original);
		original.setData(getOneDimensionalArrayOfDouble());
		doTest(original);
	}
	
	@Override
	public void testOneDimensionalArrayOfBoolean() {
		SerializationObject original = getSerializationObject(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_BOOLEAN);
		original.setData(new boolean[]{});
		doTest(original);
		original.setData(getOneDimensionArrayOfBoolean());
		doTest(original);
	}
	
	@Override
	public void testOneDimensionalArrayOfByte() {
		SerializationObject original = getSerializationObject(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_BYTE);
		original.setData(new byte[]{});
		doTest(original);
		byte[] byteArray = new byte[random.nextInt(16)];
		random.nextBytes(byteArray);
		original.setData(byteArray);
		doTest(original);
	}
	
	@Override
	public void testTwoDimensionalArrayOfInteger() {
		SerializationObject original = getSerializationObject(LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_INTEGER);
		original.setData(new int[][]{});
		doTest(original);
		original.setData(getRandomArray(int[][].class));
		doTest(original);
	}
	
	@Override
	public void testTwoDimensionalArrayOfLong() {
		SerializationObject original = getSerializationObject(LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_LONG);
		original.setData(new long[][]{});
		doTest(original);
		original.setData(getRandomArray(long[][].class));
		doTest(original);
	}
	
	@Override
	public void testTwoDimensionalArrayOfDouble() {
		SerializationObject original = getSerializationObject(LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_DOUBLE);
		original.setData(new double[][]{});
		doTest(original);
		original.setData(getRandomArray(double[][].class));
		doTest(original);
	}
	
	@Override
	public void testTwoDimensionalArrayOfByte() {
		SerializationObject original = getSerializationObject(LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_BYTE);
		original.setData(new byte[][]{});
		doTest(original);
		original.setData(getRandomArray(byte[][].class));
		doTest(original);
	}

	@Override
	public void testTwoDimensionalArrayOfBoolean() {
		SerializationObject original = getSerializationObject(LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_BOOLEAN);
		original.setData(new boolean[][]{});
		doTest(original);
		original.setData(getRandomArray(boolean[][].class));
		doTest(original);
	}

	@Override
	public void testThreeDimensionArrayOfInteger() {
		SerializationObject original = getSerializationObject(LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_INTEGER);
		original.setData(new int[][][]{});
		doTest(original);
		original.setData(getRandomArray(int[][][].class));
		doTest(original);
	}
	
	@Override
	public void testThreeDimensionArrayOfLong() {
		SerializationObject original = getSerializationObject(LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_LONG);
		original.setData(new long[][][]{});
		doTest(original);
		original.setData(getRandomArray(long[][][].class));
		doTest(original);
	}
	
	@Override
	public void testThreeDimensionArrayOfDouble() {
		SerializationObject original = getSerializationObject(LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_DOUBLE);
		original.setData(new double[][][]{});
		doTest(original);
		original.setData(getRandomArray(double[][][].class));
		doTest(original);
	}

	@Override
	public void testThreeDimensionArrayOfByte() {
		SerializationObject original = getSerializationObject(LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_BYTE);
		original.setData(new byte[][][]{});
		doTest(original);
		original.setData(getRandomArray(byte[][][].class));
		doTest(original);
	}

	@Override
	public void testThreeDimensionArrayOfBoolean() {
		SerializationObject original = getSerializationObject(LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_BOOLEAN);
		original.setData(new boolean[][][]{});
		doTest(original);
		original.setData(getRandomArray(boolean[][][].class));
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
	
	/*
	private int[][] getTwoDimensionalArrayOfInteger() {
		int innerArrayDimension = 1+random.nextInt(10);
		int[][] twoDimIntArray = new int[1+random.nextInt(10)][];
		for(int i = 0; i < twoDimIntArray.length; ++i ) {
			twoDimIntArray[i] = new int[innerArrayDimension];
			for(int j = 0; j < twoDimIntArray[i].length; ++j) {
				twoDimIntArray[i][j] = random.nextInt();
			}
		}
		return twoDimIntArray;
		return getRandomArray(int[][].class);
	}

	private long[][] getTwoDimensionalArrayOfLong() {
		int innerArrayDimension = 1 + random.nextInt(10);
		long[][] twoDimLongArray = new long[1+random.nextInt(10)][];
		for(int i = 0; i < twoDimLongArray.length; ++i) {
			twoDimLongArray[i] = new long[innerArrayDimension];
			for(int j = 0; j < twoDimLongArray[i].length; ++j) {
				twoDimLongArray[i][j] = random.nextLong();
			}
		}
		return twoDimLongArray;
		return getRandomArray(long[][].class);
	}

	private double[][] getTwoDimensionalArrayOfDouble() {
		int innerArrayDimension = 1 + random.nextInt(10);
		double[][] twoDimDoubleArray = new double[1+random.nextInt(10)][];
		for(int i = 0; i < twoDimDoubleArray.length; ++i) {
			twoDimDoubleArray[i] = new double[innerArrayDimension];
			for(int j = 0; j < twoDimDoubleArray[i].length; ++j) {
				twoDimDoubleArray[i][j] = random.nextDouble();
			}
		}
		return twoDimDoubleArray;
		return getRandomArray(double[][].class);
	}
	 */
	
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
	
	private SerializationObject getSerializationObject(LapisDataType type) {
		SerializationObject dat = new SerializationObject();
		dat.setType(type);
		dat.setName("LD-" + RandomStringUtils.randomNumeric(10));
		return dat;
	}
	
	private void doTest(SerializationObject original) {		
		//we'll set dimensions here, so other test code doesn't have to
		original.setDimension(Dimensions.getDimensions(original.getData()));
		
		SerializationObject deserialized = serializeAndDeserialize(original);
		Assert.assertEquals(original.getName(), deserialized.getName());
		Assert.assertEquals(original.getType(), deserialized.getType());
		Assert.assertTrue(NOT_EQUAL_MSG, Arrays.equals(original.getDimension(), deserialized.getDimension()));
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
