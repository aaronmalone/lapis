package edu.osu.lapis;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.Validate;

import edu.osu.lapis.data.Settable;

public class LapisMapSettable implements Settable {

	private final Map<String, Object> map;
	
	public LapisMapSettable(Map<String, Object> map) {
		Validate.isTrue(map instanceof Map, "Not an instance of Map");
		this.map = map;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void set(Object object) {
		Validate.isTrue(object instanceof Map, "Not an instance of Map");
		synchronized (map) {
			map.clear();
			for(Entry<String, ?> entry : ((Map<String, ?>) object).entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}
		}
	}
}
