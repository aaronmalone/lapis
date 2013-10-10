package edu.osu.lapis.serialize;

public interface LapisSerialization {
	public byte[] serialize(LapisDatum lapisDatum);
	public LapisDatum deserialize(byte[] serialized);
}
