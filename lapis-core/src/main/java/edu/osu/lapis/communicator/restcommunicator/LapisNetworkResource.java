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
			throw new RuntimeException("Problem Putting", e);
		}
	}
	
	@Override
	public void post(Request request, Response response) {
		
		try {
			String net = CharStreams.toString(request.getEntity().getReader());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Problem Posting", e);
		}
		
	}
	
	
	@Override
	public void delete(Request request, Response response) {
		
		
		
	}
	
	


}
