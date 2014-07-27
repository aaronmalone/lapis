package edu.osu.lapis.client;


public interface Client {

	public byte[] doCall(ClientMethod method, String... uriParts);

	public byte[] doCall(ClientMethod method, String uri);

	public byte[] doCall(ClientMethod method, byte[] messageBody, String... uriParts);

	public byte[] doCall(ClientMethod method, byte[] messageBody, String uri);
}
