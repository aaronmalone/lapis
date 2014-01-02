package edu.osu.lapis.data;

import java.lang.reflect.Array;

import org.apache.commons.lang3.Validate;

public class LapisSettable implements Settable {

	private final Object reference;
	
	public LapisSettable(Object ref) {
		validateIsArray(ref);
		this.reference = ref;
	}
	
	@Override
	public void set(Object srcArray) {
		validateIsArray(srcArray);
		Object destination = this.reference;
		validateIsArray(destination);
		Validate.isTrue(Array.getLength(srcArray) == Array.getLength(destination), 
				"Array to set has different length than original array.");
		System.arraycopy(srcArray, 0, destination, 0, Array.getLength(srcArray));
	}
	
	private void validateIsArray(Object obj) {
		Class<?> cls = obj.getClass();
		Validate.isTrue(cls.isArray(),"Reference must be an array, but an object of type %s was passed", cls);
	}
}
