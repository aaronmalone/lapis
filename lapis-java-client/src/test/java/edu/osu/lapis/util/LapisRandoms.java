package edu.osu.lapis.util;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.Validate;

public class LapisRandoms {

	private static final Random random = new Random();
	
	private static abstract class ArrayPopulator {
		final void populateArray(Object array) {
			int length = Array.getLength(array);
			for(int i = 0; i < length; ++i) {
				setValue(array, i);
			}
		}

		abstract void setValue(Object array, int index);
	}
	
	private static final Map<Class<?>, ArrayPopulator> populatorMap = new HashMap<Class<?>, ArrayPopulator>();
	
	static {
		populatorMap.put(Integer.TYPE, new ArrayPopulator() {
			@Override void setValue(Object array, int index) {
				Array.setInt(array, index, random.nextInt());
			}
		});
		populatorMap.put(Double.TYPE, new ArrayPopulator() {
			@Override void setValue(Object array, int index) {
				Array.setDouble(array, index, random.nextDouble());
			}
		});
		populatorMap.put(Byte.TYPE, new ArrayPopulator() {
			@Override void setValue(Object array, int index) {
				byte[] oneByte = new byte[1];
				random.nextBytes(oneByte);
				Array.setByte(array, index, oneByte[0]);
			}
		});
		populatorMap.put(Boolean.TYPE, new ArrayPopulator() {
			@Override void setValue(Object array, int index) {
				Array.setBoolean(array, index, random.nextBoolean());
			}
		});
		populatorMap.put(Long.TYPE, new ArrayPopulator() {
			@Override void setValue(Object array, int index) {
				Array.setLong(array, index, random.nextLong());
			}
		});
	}
	
	public static void populateArrayWithRandomData(Object arrayObject) {
		Class<?> cls = arrayObject.getClass();
		Validate.isTrue(cls.isArray(), "Object is not an array: %s", arrayObject);
		populateArrayWithRandomDataInternal(arrayObject, cls);
	}
	
	private static void populateArrayWithRandomDataInternal(Object arrayObject, Class<?> cls) {
		assert cls.isArray();
		Class<?> componentType = cls.getComponentType();
		if(componentType.isArray()) {
			int length = Array.getLength(arrayObject);
			for(int i = 0; i < length; ++i) {
				Object interalArray = Array.get(arrayObject, i);
				populateArrayWithRandomDataInternal(interalArray, componentType);
			}
		} else {
			ArrayPopulator populator = populatorMap.get(componentType);
			populator.populateArray(arrayObject);
		}
	}
	
	private static int getRandomLength() {
		return getRandomLength(10);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T getRandomArray(Class<?> componentType) {
		Object array = Array.newInstance(componentType, getRandomLength());
		populateArrayWithRandomData(array);
		return (T) array;
	}
	
	public static long[] getOneDimensionalArrayOfLong() {
		return getRandomArray(Long.TYPE);
	}
	
	public static byte[] getOneDimensionalArrayOfByte() {
		return getRandomArray(Byte.TYPE);
	}
	
	private static int getRandomLength(int atLeast) {
		return atLeast + random.nextInt(20);
	}

	public static boolean[] getOneDimensionalArrayOfBoolean() {
		return getRandomArray(Boolean.TYPE);
	}

	public static double[] getOneDimensionalArrayOfDouble() {
		return getRandomArray(Double.TYPE);
	}

	public static int[] getOneDimensionalArrayOfInt() {
		return getRandomArray(Integer.TYPE);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getRandomArrayOfSameDimensions(Object array) {
		Class<?> cls = array.getClass();
		Validate.isTrue(cls.isArray(), "Object is not an array: %s", array);
		Class<?> componentType = cls.getComponentType();
		int length = Array.getLength(array);
		Object newArray = Array.newInstance(componentType, length);
		if(componentType.isArray()) {
			for(int i = 0; i < length; ++i) {
				Object valueInArrayArg = Array.get(array, i);
				Array.set(newArray, i, getRandomArrayOfSameDimensions(valueInArrayArg));
			}
		} else {
			populateArrayWithRandomData(newArray);
		}
		return (T) newArray;
	}
}
