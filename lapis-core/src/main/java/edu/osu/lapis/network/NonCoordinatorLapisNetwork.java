package edu.osu.lapis.network;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import edu.osu.lapis.exception.LapisClientExceptionWithStatusCode;
import edu.osu.lapis.transmission.LapisNetworkTransmission;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class NonCoordinatorLapisNetwork implements LapisNetwork {

	private final LoadingCache<String, LapisNode> individualNodeCache;
	private final LapisNode localNode;

	public NonCoordinatorLapisNetwork(
			final LapisNetworkTransmission lapisNetworkTransmission,
			long cacheExpirationMillis,
			LapisNode localNode) {
		this.localNode = localNode;
		this.individualNodeCache = CacheBuilder
				.newBuilder()
				.expireAfterWrite(cacheExpirationMillis, TimeUnit.MILLISECONDS)
				.build(new CacheLoader<String, LapisNode>() {
					@Override
					public LapisNode load(String nodeName) throws Exception {
						return lapisNetworkTransmission.getLapisNode(nodeName);
					}
				});
	}

	//TODO CLEAN UP
	@Override
	public LapisNode getNode(String nodeName) {
		try {
			return individualNodeCache.get(nodeName);
		} catch (UncheckedExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof LapisClientExceptionWithStatusCode
					&& ((LapisClientExceptionWithStatusCode) cause).getStatusCode() == 404) {
				//the node does not exist on the network
				return null;
			} else {
				throw Throwables.propagate(e);
			}
		} catch (ExecutionException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public void removeNode(String nodeName) {
		individualNodeCache.invalidate(nodeName);
	}

	@Override
	public LapisNode getCoordinator() {
		//TODO IMPLEMENT
		throw new UnsupportedOperationException("Get coordinator from NonCoordinatorLapisNetwork?");
	}

	@Override
	public LapisNode getLocalNode() {
		return this.localNode;
	}
}
