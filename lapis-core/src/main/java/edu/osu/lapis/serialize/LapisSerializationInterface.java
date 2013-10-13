package edu.osu.lapis.serialize;

import edu.osu.lapis.network.LapisNode;

public interface LapisSerializationInterface {
	public byte[] serialize(LapisDatum lapisDatum);
	public LapisDatum deserialize(byte[] serialized);
	public LapisNode deserializeNetworkMessage(String serialized);
}
