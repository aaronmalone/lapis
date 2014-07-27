package edu.osu.lapis.restlets;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import edu.osu.lapis.Logger;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;

public class RestletServer {

	private final Logger logger = Logger.getLogger(getClass());

	private final Component server;

	public RestletServer(int port) {
		Preconditions.checkArgument(port > 0);
		server = new Component();
		server.getServers().add(Protocol.HTTP, port);
	}

	public void startServer() {
		try {
			logger.trace("Starting server...");
			server.start();
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	public void attachRestlet(String uriPattern, Restlet restlet) {
		server.getDefaultHost().attach(uriPattern, restlet);
	}

	public void stopServer() {
		try {
			server.stop();
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}
}
