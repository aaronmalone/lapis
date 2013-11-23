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
	public void set(Object obj) {
		validateIsArray(obj);
		Object source = obj;
		Object destination = this.reference;
		System.arraycopy(source, 0, destination, 0, Array.getLength(source));
	}
	
	private void validateIsArray(Object obj) {
		Class<?> cls = obj.getClass();
		Validate.isTrue(cls.isArray(),"Reference must be an array, but an object of type %s was passed", cls);
	}
}
