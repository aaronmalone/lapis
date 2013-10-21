package edu.osu.lapis.communicator.rest;

import java.util.ArrayList;
import java.util.List;

import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.VirtualHost;

public class RestletServer {
	
	private int port = Integer.MIN_VALUE;
	private boolean initialized = false;
	private Component server;
	List<UnattachedRestlet> unattachedRestlets = new ArrayList<>();
	
	public RestletServer() {
		// constructor
	}

	//TODO MOVE
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public void initialize() {
		//TODO CLEANUP
		if(!initialized) {
			initialized = true;
			server = new Component();
			server.getServers().add(Protocol.HTTP, port);
			VirtualHost virtualHost = server.getDefaultHost();
			for(UnattachedRestlet unattached : unattachedRestlets) {
				virtualHost.attach(unattached.uriPattern, unattached.restlet);
			}
			unattachedRestlets.clear();
		}
	}
	
	public void attachRestlet(String uriPattern, Restlet restlet) {
		if(initialized) {
			server.getDefaultHost().attach(uriPattern, restlet);
		} else {
			unattachedRestlets.add(new UnattachedRestlet(uriPattern, restlet));
		}
	}
	
	private static class UnattachedRestlet {
		final String uriPattern;
		final Restlet restlet;
		public UnattachedRestlet(String uriPattern, Restlet restlet) {
			this.uriPattern = uriPattern;
			this.restlet = restlet;
		}
	}
}
