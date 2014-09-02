package edu.osu.lapis.network;

import edu.osu.lapis.exception.LapisClientExceptionWithStatusCode;
import edu.osu.lapis.services.LapisNetworkClient;
import edu.osu.lapis.util.LapisCache;

public class NonCoordinatorLapisNetwork implements LapisNetwork {

	private final LapisCache<String, LapisNode> nodeCache;
	private final LapisNode localNode;

	public NonCoordinatorLapisNetwork(
			final LapisNetworkClient lapisNetworkClient,
			final long cacheExpirationMillis,
			LapisNode localNode) {
		this.localNode = localNode;
		this.nodeCache = new LapisCache<String, LapisNode>(cacheExpirationMillis) {
			@Override
			protected LapisNode load(String nodeName) {
				try {
					return lapisNetworkClient.getLapisNode(nodeName);
				} catch (LapisClientExceptionWithStatusCode e) {
					if(e.getStatusCode() == 404) {
						return null;
					} else {
						throw e;
					}
				}
			}
		};
	}

	@Override
	public LapisNode getNode(String nodeName) {
		return nodeCache.get(nodeName);
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
