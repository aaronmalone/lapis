package edu.osu.lapis.transmission;

public abstract class LapisTransmissionBaseImpl implements LapisTransmission {

	@Override
	public byte[] executeClientCallReturnBytes(ClientCall clientCall) {
		ClientResponse response = executeClientCall(clientCall);
		return response.getMessageBody();
	}
}
