package edu.osu.lapis.network;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.osu.lapis.services.LapisNetworkClient;
import edu.osu.lapis.util.NullForMissingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class NonCoordinatorLapisNetwork implements LapisNetwork {

	private final LoadingCache<String, LapisNode> nodeCache;
	private final LapisNode localNode;

	public NonCoordinatorLapisNetwork(
			final LapisNetworkClient lapisNetworkClient,
			long cacheExpirationMillis,
			LapisNode localNode) {
		this.localNode = localNode;
		LoadingCache<String, LapisNode> loadingCache = CacheBuilder
				.newBuilder()
				.expireAfterWrite(cacheExpirationMillis, TimeUnit.MILLISECONDS)
				.build(new CacheLoader<String, LapisNode>() {
					@Override
					public LapisNode load(String nodeName) throws Exception {
						return lapisNetworkClient.getLapisNode(nodeName);
					}
				});
		this.nodeCache = new NullForMissingCache<String, LapisNode>(loadingCache);
	}

	@Override
	public LapisNode getNode(String nodeName) {
		try {
			return this.nodeCache.get(nodeName);
		} catch (ExecutionException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public void removeNode(String nodeName) {
		this.nodeCache.invalidate(nodeName);
	}

	@Override
	public LapisNode getLocalNode() {
		return this.localNode;
	}
}
