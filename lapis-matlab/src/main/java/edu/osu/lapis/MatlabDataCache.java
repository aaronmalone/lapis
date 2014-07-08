package edu.osu.lapis;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;

public class MatlabDataCache {
	private final Map<String, Object> dataCache;
	
	public MatlabDataCache() {
		Map<String, Object> map = Maps.newHashMap();
		dataCache = Collections.synchronizedMap(map);
	}
	
	public void setCachedValue(String name, Object value) {
		Preconditions.checkNotNull(value, "Cached value must not be null. Attempted to cache null value for %s", name);
		dataCache.put(name, value);
	}
	
	public Object getCachedValue(String name) {
		Object value = dataCache.get(name) ;
		Verify.verifyNotNull(value, "Cache does not contain any values with the key '%s'.", name);
		return value;
	}
}
