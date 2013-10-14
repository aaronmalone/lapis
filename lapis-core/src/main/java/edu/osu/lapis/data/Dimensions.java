package edu.osu.lapis.data;

import java.lang.reflect.Array;

public class Dimensions {
	
	private Dimensions() {
		// do not instantiate
	}
	
	public static int[] getDimensions(Object obj) {
		if(obj.getClass().isArray()) {
			int length = Array.getLength(obj); 
			if(length > 0) {
				Object zeroth = Array.get(obj, 0);
				return prependArray(length, getDimensions(zeroth));
			} else {
				return getDimensionsForZeroLengthArray(obj);
			}
		} else {
			return null;
		}
	}

	private static int[] prependArray(int firstElement, int[] otherElements) {
		if(otherElements != null) {
			int[] newArray = new int[otherElements.length+1];
			newArray[0] = firstElement;
			if(otherElements.length > 0) {
				System.arraycopy(otherElements, 0, newArray, 1, otherElements.length);
			}
			return newArray;
		} else {
			return new int[] { firstElement };
		}
	}
	
	private static int[] getDimensionsForZeroLengthArray(Object array) {
		int zeroesCount = 1;
		Class<?> componentType = array.getClass().getComponentType();
		while(componentType.isArray()) {
			zeroesCount++;
			componentType = componentType.getComponentType();
		}
		return new int[zeroesCount];
	}
}
