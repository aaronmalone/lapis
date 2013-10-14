package edu.osu.lapis.communicator.restcommunicator;

import java.io.IOException;

import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.common.io.CharStreams;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialize.LapisJsonSerialization;

public class LapisNetworkResource extends ServerResource {

	private static LapisNode node;
	private static NetworkTable networkTable;
	private LapisJsonSerialization ls = new LapisJsonSerialization();

	// TODO: Add error handling for LapisNode null case
	@Get(value = "json")
	public String reportNameAndAddress() {

		// this.ls = new LapisJsonSerialization();

		return ls.serialize(this.node);

	}

	@Put("application/json")
	public Representation createNetworkTableEntry(Representation re) {

		// TODO: Implement
		try {

			String net = CharStreams.toString(re.getReader());

			System.out.println(net);
			
			LapisNode ln = ls.deserializeNetworkMessage(net);
			
			networkTable.addNode(ln);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return re;

	}

	@Post
	public Representation updateNetworkTableEntry(Representation re) {
		return re;

		// TODO: Implement

	}

	@Delete
	public void deleteNetworkTableEntry() {
		// TODO: Implement
	}

	// TODO: Add NetworkTable null Error
	public void setNetworkTable(NetworkTable nt) {

		this.networkTable = nt;
	}

	// TODO: Add LapisNode null Error
	public void setNodeInformation(LapisNode ln) {

		this.node = ln;

	}

}
