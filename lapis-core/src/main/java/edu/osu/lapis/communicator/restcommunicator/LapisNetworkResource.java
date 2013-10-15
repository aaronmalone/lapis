package edu.osu.lapis.communicator.restcommunicator;

import java.io.IOException;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;

import com.google.common.io.CharStreams;

import edu.osu.lapis.examples.LapisRestlet;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialize.LapisJsonSerialization;

public class LapisNetworkResource extends LapisRestlet {//extends ServerResource {

	private LapisNode node;
	private NetworkTable networkTable;
	private LapisJsonSerialization ls = new LapisJsonSerialization();

	@Override
	public void get(Request request, Response response) {
		response.setEntity(ls.serialize(this.node), MediaType.APPLICATION_JSON);
	}

	@Override
	public void put(Request request, Response response) {
		try {
			String net = CharStreams.toString(request.getEntity().getReader());
			LapisNode ln = ls.deserializeNetworkMessage(net);
			networkTable.addNode(ln);
		} catch (IOException e) {
			throw new RuntimeException("fuhhh", e);
		}
	}

	// TODO: Add NetworkTable null Error
	public void setNetworkTable(NetworkTable nt) {

		this.networkTable = nt;
	}

	// TODO: Add LapisNode null Error
	public void setNodeInformation(LapisNode ln) {

		this.node = ln;

	}

	public void setNode(LapisNode node) {
		this.node = node;
	}

	public void setLs(LapisJsonSerialization ls) {
		this.ls = ls;
	}
}
