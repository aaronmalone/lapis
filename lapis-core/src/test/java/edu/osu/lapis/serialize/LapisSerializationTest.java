package edu.osu.lapis.serialize;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;

import edu.osu.lapis.data.Dimensions;
import edu.osu.lapis.data.LapisDataType;

public class LapisSerializationTest /*TODO rename */ implements LapisSerializationTestInterface { 
	
	private static final String NOT_EQUAL_MSG = "Arrays are not equal.";
	private final Random random = new Random();
	
	private LapisSerialization lapisSerialization;
	
	//TODO MOVE SETTER
	public void setLapisSerialization(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}

	public void testDouble() {
		LapisDatum original = getLapisDatum();
		original.setType(LapisDataType.DOUBLE);
		original.setData(RandomUtils.nextDouble());
		validate(original);
	}
	
	public void testInteger() {
		LapisDatum original = getLapisDatum();
		original.setType(LapisDataType.INTEGER);
		original.setData(RandomUtils.nextInt());
		validate(original);
	}

	public void testByte() {
		LapisDatum original = getLapisDatum();
		original.setType(LapisDataType.BYTE);
		original.setData((byte)RandomUtils.nextInt());
		validate(original);
	}

	public void testBoolean() {
		LapisDatum original = getLapisDatum();
		original.setType(LapisDataType.BOOLEAN);
		original.setData(RandomUtils.nextBoolean());
		validate(original);
	}

	public void testLong() {
		LapisDatum original = getLapisDatum();
		original.setType(LapisDataType.LONG);
		original.setData(RandomUtils.nextLong());
		validate(original);
	}
	
	public void testOneDimensionalArrayOfInteger() {
		LapisDatum original = getLapisDatum();
		original.setType(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_INTEGER);
		original.setData(new int[]{});
		original.setDimension(Dimensions.getDimensions(original.getData()));
		validate(original);
		original.setData(getOneDimensionalArrayOfInteger());
		original.setDimension(Dimensions.getDimensions(original.getData()));
		validate(original);
	}

	public void testOneDimensionalArrayOfLong() {
		LapisDatum original = getLapisDatum();
		original.setType(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_LONG);
		original.setData(new long[]{});
		original.setDimension(Dimensions.getDimensions(original.getData()));
		validate(original);
		original.setData(getOneDimensionalArrayOfLong());
		original.setDimension(Dimensions.getDimensions(original.getData()));
		validate(original);
	}
	
	public void testOneDimensionalArrayOfDouble() {
		LapisDatum original = getLapisDatum();
		original.setType(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_DOUBLE);
		original.setData(new double[]{});
		original.setDimension(Dimensions.getDimensions(original.getData()));
		validate(original);
		original.setData(getOneDimensionalArrayOfDouble());
		original.setDimension(Dimensions.getDimensions(original.getData()));
		validate(original);
	}
	
	public void testOneDimensionalArrayOfBoolean() {
		LapisDatum original = getLapisDatum();
		original.setType(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_BOOLEAN);
		original.setData(new boolean[]{});
		original.setDimension(Dimensions.getDimensions(original.getData()));
		validate(original);
		original.setData(getOneDimensionArrayOfBoolean());
		original.setDimension(Dimensions.getDimensions(original.getData()));
		validate(original);
	}
	
	public void testOneDimensionalArrayOfByte() {
		LapisDatum original = getLapisDatum();
		original.setType(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_BYTE);
		original.setData(new byte[]{});
		original.setDimension(Dimensions.getDimensions(original.getData()));
		validate(original);
		byte[] byteArray = new byte[random.nextInt(16)];
		random.nextBytes(byteArray);
		original.setData(byteArray);
		original.setDimension(Dimensions.getDimensions(byteArray));
		validate(original);
	}
	
	public void testTwoDimensionalArrayOfInteger() {
		LapisDatum original = getLapisDatum();
		original.setType(LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_INTEGER);
		original.setData(new int[][]{});
		original.setDimension(Dimensions.getDimensions(original.getData()));
		validate(original);
		original.setData(getTwoDimensionalArrayOfInteger());
		original.setDimension(Dimensions.getDimensions(original.getData()));
		validate(original);
	}

	private boolean[] getOneDimensionArrayOfBoolean() {
		boolean[] booleanArray = new boolean[RandomUtils.nextInt(16)];
		for(int i = 0; i < booleanArray.length; ++i) {
			booleanArray[i] = RandomUtils.nextBoolean();
		}
		return booleanArray;
	}

	private int[] getOneDimensionalArrayOfInteger() {
		int[] intArray = new int[RandomUtils.nextInt(16)];
		for(int i = 0; i < intArray.length; ++i) {
			intArray[i] = RandomUtils.nextInt();
		}
		return intArray;
	}
	
	private long[] getOneDimensionalArrayOfLong() {
		long[] longArray = new long[RandomUtils.nextInt(16)];
		for(int i = 0; i < longArray.length; ++i) {
			longArray[i] = RandomUtils.nextLong();
		}
		return longArray;
	}
	
	private double[] getOneDimensionalArrayOfDouble() {
		double[] doubleArray = new double[RandomUtils.nextInt(16)];
		for(int i = 0; i < doubleArray.length; ++i) {
			doubleArray[i] = RandomUtils.nextDouble();
		}
		return doubleArray;
	}
	
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
	}
	
	private LapisDatum getLapisDatum() {
		LapisDatum dat = new LapisDatum();
		dat.setName("sd-" + RandomStringUtils.randomNumeric(10));
		return dat;
	}
	
	private LapisDatum cycle(LapisDatum ld) {
		byte[] serialized = lapisSerialization.serialize(ld);
		
		System.out.println("serialized: " + new String(serialized)); //TODO REMOVE
		
		return lapisSerialization.deserialize(serialized);
	}
	
	private void validate(LapisDatum original) {
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
		case "class [[I": //int[][]
			Assert.assertTrue(NOT_EQUAL_MSG, Arrays.deepEquals((int[][])originalData, (int[][])deserializedData));
			break;
		default:
			System.err.println(originalData.getClass()); //TODO REMOVE
			throw new IllegalArgumentException("Did not recognize type: " + originalData.getClass());
		}
	}
	
	private void validateSerializedObjects(LapisDatum original, LapisDatum deserialized) {
		byte[] originalSerialized = lapisSerialization.serialize(original);
		byte[] deserialSerialized = lapisSerialization.serialize(deserialized);
		Assert.assertTrue("Serialized objects should be equal.", Arrays.equals(originalSerialized, deserialSerialized));
	}
}
