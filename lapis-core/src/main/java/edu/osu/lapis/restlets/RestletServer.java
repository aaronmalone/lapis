package edu.osu.lapis.restlets;

import java.util.ArrayList;
import java.util.List;

import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.VirtualHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestletServer {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private int port = Integer.MIN_VALUE;
	private boolean initialized = false;
	private Component server;
	List<UnattachedRestlet> unattachedRestlets = new ArrayList<>();
	
	public RestletServer() {
		// constructor
	}

	public synchronized void initialize() {
		if(!initialized) {
			log.debug("Initializing RestletServer...");
			initialized = true;
			server = new Component();
			server.getServers().add(Protocol.HTTP, port);
			attachRestlets();
			startServer();
		}
	}
	
	private void attachRestlets() {
		VirtualHost virtualHost = server.getDefaultHost();
		log.trace("Attaching Restlets...");
		for(UnattachedRestlet unattached : unattachedRestlets) {
			log.trace("Attaching Restlet {} to URI pattern {}...", unattached.restlet, unattached.uriPattern);
			virtualHost.attach(unattached.uriPattern, unattached.restlet);
		}
		unattachedRestlets.clear();
	}
	
	private void startServer() {
		try {
			log.debug("Starting server...");
			server.start();
		} catch (Exception e) {
			if(e instanceof RuntimeException) {
				throw (RuntimeException)e;
			} else {
				throw new RuntimeException(e);
			}
		}
	}
	
	public synchronized void attachRestlet(String uriPattern, Restlet restlet) {
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

	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
}