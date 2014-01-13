package edu.osu.lapis.transmission;

public interface LapisTransmission {
	public byte[] executeClientCallReturnBytes(ClientCall clientCall);
	public ClientResponse executeClientCall(ClientCall clientCall);
}
