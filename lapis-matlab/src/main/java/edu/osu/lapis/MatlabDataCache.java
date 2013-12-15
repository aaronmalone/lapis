package edu.osu.lapis;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Maps;

public class MatlabDataCache {
	private final Map<String, Object> dataCache;
	
	public MatlabDataCache() {
		Map<String, Object> map = Maps.newHashMap();
		dataCache = Collections.synchronizedMap(map);
	}
	
	public void setCachedValue(String name, Object value) {
		Validate.notNull(value, "Cached value must not be null. Attempted to cache null value for %s", name);
		dataCache.put(name, value);
	}
	
	public Object getCachedValue(String name) {
		Object value = dataCache.get(name) ;
		Validate.notNull(value, "Cache does not contain any values with the key '%s'.", name);
		return value;
	}
}
