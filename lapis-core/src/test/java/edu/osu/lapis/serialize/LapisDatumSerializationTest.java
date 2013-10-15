package edu.osu.lapis.serialize;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;

import edu.osu.lapis.data.Dimensions;
import edu.osu.lapis.data.LapisDataType;

public class LapisDatumSerializationTest implements LapisSerializationTestInterface { 
	
	private static final String NOT_EQUAL_MSG = "Arrays are not equal.";
	private final Random random = new Random();
	
	private LapisSerializationInterface lapisSerialization;

	@Override
	public void testDouble() {
		LapisDatum original = getLapisDatum(LapisDataType.DOUBLE);
		original.setData(random.nextDouble());
		validate(original);
	}
	
	@Override
	public void testInteger() {
		LapisDatum original = getLapisDatum(LapisDataType.INTEGER);
		original.setData(random.nextInt());
		validate(original);
	}

	@Override
	public void testByte() {
		LapisDatum original = getLapisDatum(LapisDataType.BYTE);
		original.setData((byte)random.nextInt());
		validate(original);
	}

	@Override
	public void testBoolean() {
		LapisDatum original = getLapisDatum(LapisDataType.BOOLEAN);
		original.setData(random.nextBoolean());
		validate(original);
	}

	@Override
	public void testLong() {
		LapisDatum original = getLapisDatum(LapisDataType.LONG);
		original.setData(random.nextLong());
		validate(original);
	}
	
	@Override
	public void testOneDimensionalArrayOfInteger() {
		LapisDatum original = getLapisDatum(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_INTEGER);
		original.setData(new int[]{});
		validate(original);
		original.setData(getOneDimensionalArrayOfInteger());
		validate(original);
	}

	@Override
	public void testOneDimensionalArrayOfLong() {
		LapisDatum original = getLapisDatum(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_LONG);
		original.setData(new long[]{});
		validate(original);
		original.setData(getOneDimensionalArrayOfLong());
		validate(original);
	}
	
	@Override
	public void testOneDimensionalArrayOfDouble() {
		LapisDatum original = getLapisDatum(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_DOUBLE);
		original.setData(new double[]{});
		validate(original);
		original.setData(getOneDimensionalArrayOfDouble());
		validate(original);
	}
	
	@Override
	public void testOneDimensionalArrayOfBoolean() {
		LapisDatum original = getLapisDatum(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_BOOLEAN);
		original.setData(new boolean[]{});
		validate(original);
		original.setData(getOneDimensionArrayOfBoolean());
		validate(original);
	}
	
	@Override
	public void testOneDimensionalArrayOfByte() {
		LapisDatum original = getLapisDatum(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_BYTE);
		original.setData(new byte[]{});
		validate(original);
		byte[] byteArray = new byte[random.nextInt(16)];
		random.nextBytes(byteArray);
		original.setData(byteArray);
		validate(original);
	}
	
	@Override
	public void testTwoDimensionalArrayOfInteger() {
		LapisDatum original = getLapisDatum(LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_INTEGER);
		original.setData(new int[][]{});
		validate(original);
		original.setData(getRandomArray(int[][].class));
		validate(original);
	}
	
	@Override
	public void testTwoDimensionalArrayOfLong() {
		LapisDatum original = getLapisDatum(LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_LONG);
		original.setData(new long[][]{});
		validate(original);
		original.setData(getRandomArray(long[][].class));
		validate(original);
	}
	
	@Override
	public void testTwoDimensionalArrayOfDouble() {
		LapisDatum original = getLapisDatum(LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_DOUBLE);
		original.setData(new double[][]{});
		validate(original);
		original.setData(getRandomArray(double[][].class));
		validate(original);
	}
	
	@Override
	public void testTwoDimensionalArrayOfByte() {
		LapisDatum original = getLapisDatum(LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_BYTE);
		original.setData(new byte[][]{});
		validate(original);
		original.setData(getRandomArray(byte[][].class));
		validate(original);
	}

	@Override
	public void testTwoDimensionalArrayOfBoolean() {
		LapisDatum original = getLapisDatum(LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_BOOLEAN);
		original.setData(new boolean[][]{});
		validate(original);
		original.setData(getRandomArray(boolean[][].class));
		validate(original);
	}

	@Override
	public void testThreeDimensionArrayOfInteger() {
		LapisDatum original = getLapisDatum(LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_INTEGER);
		original.setData(new int[][][]{});
		validate(original);
		original.setData(getRandomArray(int[][][].class));
		validate(original);
	}
	
	@Override
	public void testThreeDimensionArrayOfLong() {
		LapisDatum original = getLapisDatum(LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_LONG);
		original.setData(new long[][][]{});
		validate(original);
		original.setData(getRandomArray(long[][][].class));
		validate(original);
	}
	
	@Override
	public void testThreeDimensionArrayOfDouble() {
		LapisDatum original = getLapisDatum(LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_DOUBLE);
		original.setData(new double[][][]{});
		validate(original);
		original.setData(getRandomArray(double[][][].class));
		validate(original);
	}

	@Override
	public void testThreeDimensionArrayOfByte() {
		LapisDatum original = getLapisDatum(LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_BYTE);
		original.setData(new byte[][][]{});
		validate(original);
		original.setData(getRandomArray(byte[][][].class));
		validate(original);
	}

	@Override
	public void testThreeDimensionArrayOfBoolean() {
		LapisDatum original = getLapisDatum(LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_BOOLEAN);
		original.setData(new boolean[][][]{});
		validate(original);
		original.setData(getRandomArray(boolean[][][].class));
		validate(original);
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
	
	private LapisDatum getLapisDatum(LapisDataType type) {
		LapisDatum dat = new LapisDatum();
		dat.setType(type);
		dat.setName("LD-" + RandomStringUtils.randomNumeric(10));
		return dat;
	}
	
	private LapisDatum cycle(LapisDatum ld) {
		byte[] serialized = lapisSerialization.serialize(ld);
		
		System.out.println("serialized: " + new String(serialized)); //TODO REMOVE
		
		return lapisSerialization.deserializeLapisDatum(new ByteArrayInputStream(serialized));
	}
	
	private void validate(LapisDatum original) {
		//validate that we set the correct LapisDataType for the actual type of the data
		Assert.assertEquals(LapisDataType.getTypeForObject(original.getData()), original.getType());
		
		//we'll set dimensions here, so other test code doesn't have to
		original.setDimension(Dimensions.getDimensions(original.getData()));
		
		LapisDatum deserialized = cycle(original);
		Assert.assertEquals(original.getName(), deserialized.getName());
		Assert.assertEquals(original.getType(), deserialized.getType());
		Assert.assertTrue(NOT_EQUAL_MSG, Arrays.equals(original.getDimension(), deserialized.getDimension()));
		validateData(original.getData(), deserialized.getData());
		validateSerializedObjects(original, deserialized);
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
		switch(originalData.getClass().toString().trim()) {
		case "class [I": //int[]
			Assert.assertTrue(NOT_EQUAL_MSG, Arrays.equals((int[])originalData, (int[])deserializedData));
			break;
		case "class [J": //long[]
			Assert.assertTrue(NOT_EQUAL_MSG, Arrays.equals((long[])originalData, (long[])deserializedData));
			break;
		case "class [D": //double[]
			Assert.assertTrue(NOT_EQUAL_MSG, Arrays.equals((double[])originalData, (double[])deserializedData));
			break;
		case "class [Z": //boolean[]
			Assert.assertTrue(NOT_EQUAL_MSG, Arrays.equals((boolean[])originalData, (boolean[])deserializedData));
			break;
		case "class [B": //byte[]
			Assert.assertTrue(NOT_EQUAL_MSG, Arrays.equals((byte[])originalData, (byte[])deserializedData));
			break;
		default:
			Assert.assertTrue(NOT_EQUAL_MSG, Arrays.deepEquals((Object[])originalData, (Object[])deserializedData));
		}
	}
	
	private void validateSerializedObjects(LapisDatum original, LapisDatum deserialized) {
		byte[] originalSerialized = lapisSerialization.serialize(original);
		byte[] deserialSerialized = lapisSerialization.serialize(deserialized);
		Assert.assertTrue("Serialized objects should be equal.", Arrays.equals(originalSerialized, deserialSerialized));
	}
	
	public void setLapisSerialization(LapisSerializationInterface lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}
}
