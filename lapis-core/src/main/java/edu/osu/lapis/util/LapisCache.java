package edu.osu.lapis.util;

import edu.osu.lapis.Logger;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

/**
 * Guava's cache was causing something to blow up in Matlab, for reasons
 * we couldn't figure out. I've implemented my own, simpler cache here.
 * <p/>
 * Note that this implementation does not remove expired entries, and is,
 * therefore, memory-leaky. We do not anticipate that this will be a problem
 * in LAPIS.
 */
public abstract class LapisCache<K, V> {

	private final Logger logger = Logger.getLogger(getClass());

	private final Map<K, Mapping> internalMap = new HashMap<K, Mapping>();

	private final long expirationMillis;

	protected LapisCache(long expirationMillis) {
		this.expirationMillis = expirationMillis;
	}

	protected abstract V load(K key);

	public V get(final K key) {
		logger.debug("Called get(%s)", key);
		Mapping mapping;
		synchronized (internalMap) {
			mapping = internalMap.get(key);
			if (mapping == null) {
				logger.debug("Creating new mapping for key %s", key);
				mapping = new Mapping(key);
				internalMap.put(key, mapping);
			}
		}
		return mapping.getValue();
	}

	public void invalidate(K key) {
		synchronized (internalMap) {
			internalMap.remove(key);
		}
	}

	private class Mapping {
		final K key;
		V value;
		long timeCreated;

		Mapping(K key) {
			this.key = key;
		}

		synchronized V getValue() {
			if (value == null || timeCreated + expirationMillis < currentTimeMillis()) {
				logger.debug("Mapping for key %s was %s. Loading again.", key, value == null ? "null" : "expired");
				value = load(key);
				timeCreated = currentTimeMillis();
			}
			return value;
		}
	}
}
