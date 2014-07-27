package edu.osu.lapis.network;

public interface LapisNetwork {

	/**
	 * Gets the named node. Returns null if there is no node on the network with
	 * the given name.
	 */
	public LapisNode getNode(String nodeName);

	public void removeNode(String nodeName);

	public LapisNode getCoordinator();

	public LapisNode getLocalNode();
}
