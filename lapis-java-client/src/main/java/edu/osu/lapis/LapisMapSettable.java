package edu.osu.lapis;

import com.google.common.base.Preconditions;
import edu.osu.lapis.data.Settable;
import org.apache.commons.lang3.Validate;

import java.util.Map;

public class LapisMapSettable implements Settable {

	private final Map<String, Object> map;
	
	public LapisMapSettable(Map<String, Object> map) {
		Preconditions.checkArgument(map instanceof Map, "Not an instance of Map");
		this.map = map;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void set(Object object) {
		Validate.isTrue(object instanceof Map, "Not an instance of Map");
		synchronized (map) {
			map.clear();
			map.putAll((Map<String, ?>) object);
		}
	}
}
