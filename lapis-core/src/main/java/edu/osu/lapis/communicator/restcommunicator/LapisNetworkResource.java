package edu.osu.lapis.communicator.restcommunicator;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.restlet.Request;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

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

			List<String> jsonStringList = IOUtils.readLines(re.getStream());
			
			
			StringBuilder json = new StringBuilder();
			String sep = "";
			for (String s : jsonStringList) {
			    
				json.append(sep).append(s);
			   
			}
			String net = json.toString();

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
