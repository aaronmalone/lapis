package edu.osu.lapis.comm.client;

import static edu.osu.lapis.transmission.ClientCall.RestMethod.GET;

import org.apache.log4j.Logger;

import edu.osu.lapis.exception.LapisClientException;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.transmission.ClientCall;
import edu.osu.lapis.transmission.LapisTransmission;
import edu.osu.lapis.util.LapisRestletUtils;

public class HeartbeatClient {
	
	private Logger logger = Logger.getLogger(getClass());
	
	private final NetworkTable networkTable;
	private final LapisTransmission lapisTransmission;
	
	public HeartbeatClient(NetworkTable networkTable, LapisTransmission lapisTransmission) {
		this.networkTable = networkTable;
		this.lapisTransmission = lapisTransmission;
	}

	public boolean doHeartbeatCheckReturnLiveness(String nodeName) {
		LapisNode lapisNode = networkTable.getNode(nodeName);
		String uri = LapisRestletUtils.buildUri(lapisNode.getUrl(), "heartbeat");
		try {
			lapisTransmission.executeClientCall(new ClientCall(GET, uri));
			return true;
		} catch (LapisClientException e) {
			logger.warn("Node " + nodeName + " did not respond to heartbeat.", e);
			return false;
		}
	}
	
	
}
