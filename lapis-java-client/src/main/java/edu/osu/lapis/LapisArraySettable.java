package edu.osu.lapis;

import java.lang.reflect.Array;

import org.apache.commons.lang3.Validate;

import edu.osu.lapis.data.Settable;

//TODO MAYBE MOVE TO JAVA CLIENT?
public class LapisArraySettable implements Settable {

	private final Object array;
	
	public LapisArraySettable(Object obj) {
		validateIsArray(obj);
		this.array = obj;
	}
	
	@Override
	public void set(Object srcArray) {
		validateIsArray(srcArray);
		Object destArray = this.array;
		Validate.isTrue(Array.getLength(srcArray) == Array.getLength(destArray), 
				"Array to set has different length than original array.");
		System.arraycopy(srcArray, 0, destArray, 0, Array.getLength(srcArray));
	}
	
	private void validateIsArray(Object obj) {
		Class<?> cls = obj.getClass();
		Validate.isTrue(cls.isArray(),"Object must be an array, but an object of type %s was passed", cls);
	}
}
