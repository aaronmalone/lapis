package edu.osu.lapis.util;

import com.google.common.base.Throwables;
import com.google.common.cache.ForwardingLoadingCache;
import com.google.common.cache.LoadingCache;
import edu.osu.lapis.exception.LapisClientExceptionWithStatusCode;

public class NullForMissingCache<K, V> extends ForwardingLoadingCache<K, V> {

	private final LoadingCache<K, V> delegateCache;

	public NullForMissingCache(LoadingCache<K, V> cache) {
		this.delegateCache = cache;
	}

	@Override
	protected LoadingCache<K, V> delegate() {
		return this.delegateCache;
	}

	@Override
	public V get(K key) {
		try {
			return delegateCache.get(key);
		} catch (Exception e) {
			Throwable cause = e.getCause();
			if (cause instanceof LapisClientExceptionWithStatusCode
					&& ((LapisClientExceptionWithStatusCode) cause).getStatusCode() == 404) {
				return null;
			} else {
				throw Throwables.propagate(e);
			}
		}
	}

	@Override
	public V getUnchecked(K key) {
		return this.get(key);
	}

	@Override
	public V apply(K key) {
		return this.get(key);
	}
}
