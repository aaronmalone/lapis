package edu.osu.lapis.network;

public interface NetworkChangeCallback {
	public void onNodeAdd(LapisNode lapisNode);
	public void onNodeDelete(LapisNode lapisNode);
}
