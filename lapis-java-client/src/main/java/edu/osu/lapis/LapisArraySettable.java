package edu.osu.lapis;

import com.google.common.base.Preconditions;
import edu.osu.lapis.data.Settable;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Array;

public class LapisArraySettable implements Settable {

	private final Object array;
	
	public LapisArraySettable(Object array) {
		checkArray(array);
		this.array = array;
	}
	
	@Override
	public void set(Object srcArray) {
		checkArray(srcArray);
		Object destArray = this.array;
		Validate.isTrue(Array.getLength(srcArray) == Array.getLength(destArray),
				"Array to set has different length than original array.");
		System.arraycopy(srcArray, 0, destArray, 0, Array.getLength(srcArray));
	}

	private void checkArray(Object obj) {
		Class<?> cls = obj.getClass();
		Preconditions.checkArgument(cls.isArray(),"Object must be an array, but an object of type %s was passed", cls);
	}
}
