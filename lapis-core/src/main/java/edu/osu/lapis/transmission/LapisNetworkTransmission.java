package edu.osu.lapis.transmission;

import static edu.osu.lapis.transmission.ClientCall.RestMethod.DELETE;
import static edu.osu.lapis.transmission.ClientCall.RestMethod.GET;
import static edu.osu.lapis.transmission.ClientCall.RestMethod.PUT;
import edu.osu.lapis.Logger;
import edu.osu.lapis.util.LapisRestletUtils;

public class LapisNetworkTransmission {
	
	private Logger logger = Logger.getLogger(getClass());
	
	private final String COORDINATOR = "coordinator";
	private LapisTransmission lapisTransmission;
	
	private String coordinatorBaseUrl;

	public void deleteNodeFromNetwork(String nodeName) {
		String uri = LapisRestletUtils.buildUri(coordinatorBaseUrl, COORDINATOR, nodeName);
		lapisTransmission.executeClientCall(new ClientCall(DELETE, uri));
	}
	
	public void addNodeToNetwork(String nodeName, byte[] nodeData) {
		String uri = LapisRestletUtils.buildUri(coordinatorBaseUrl, COORDINATOR, nodeName);
		ClientCall clientCall = new ClientCall(PUT, uri, nodeData);
		lapisTransmission.executeClientCall(clientCall);
	}
	
	public byte[] getAllLapisNodesOnNetwork() {
		logger.debug("Retrieving information for all nodes.");
		String uri = LapisRestletUtils.buildUri(coordinatorBaseUrl, COORDINATOR);
		return lapisTransmission.executeClientCallReturnBytes(new ClientCall(GET, uri));
	}
	
	public byte[] getLapisNode(String nodeName) {
		logger.debug("Retrieving node information for node: '%s'", nodeName);
		String uri = LapisRestletUtils.buildUri(coordinatorBaseUrl, COORDINATOR, nodeName);
		return lapisTransmission.executeClientCallReturnBytes(new ClientCall(GET, uri));
	}
	
	public String getCoordinatorBaseUrl() {
		return coordinatorBaseUrl;
	}

	public void setCoordinatorBaseUrl(String coordinatorBaseUrl) {
		this.coordinatorBaseUrl = coordinatorBaseUrl;
	}

	public void setLapisTransmission(LapisTransmission lapisTransmission) {
		this.lapisTransmission = lapisTransmission;
	}
}