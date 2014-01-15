package edu.osu.lapis.transmission;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Context;

//TODO UNFINISHED
public class LapisNetworkTransmissionTest {
	
	private final String
		COORDINATOR_URL = "http://coordinatorBaseUrl",
		DELETE_NODE_NAME = "deleteMe";

	private LapisNetworkTransmission lapisNetworkTransmission;
	
	public LapisNetworkTransmissionTest() {
		lapisNetworkTransmission = new LapisNetworkTransmission();
		lapisNetworkTransmission.setCoordinatorBaseUrl(COORDINATOR_URL);
		lapisNetworkTransmission.setLapisTransmission(new LapisTransmissionBaseImpl() {
			@Override public ClientResponse executeClientCall(ClientCall clientCall) {
				switch (clientCall.getMethod()) {
				case DELETE:
					String coordinatorBase = COORDINATOR_URL+"/"+LapisNetworkTransmission.COORDINATOR+"/";
					String nodeName = clientCall.getUri().substring(coordinatorBase.length());
					Assert.assertEquals(DELETE_NODE_NAME, nodeName);
					break;
				default:
					throw new IllegalArgumentException("Unexpected REST method: " + clientCall.getMethod());
				}
				return null;
			}
		});
	}
	
	@Before
	public void setUpContext() {
		Context.setCurrent(new Context());
	}
	
	@Test
	public void testDeleteNodeFromNetwork() {
		lapisNetworkTransmission.deleteNodeFromNetwork(DELETE_NODE_NAME);
	}
	
	@Test
	public void testAddNodeToNetwork() {
		//TODO IMPLEMENT
	}
}
