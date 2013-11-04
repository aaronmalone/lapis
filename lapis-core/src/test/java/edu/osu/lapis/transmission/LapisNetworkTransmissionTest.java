package edu.osu.lapis.transmission;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Protocol;

public class LapisNetworkTransmissionTest {

	private LapisNetworkTransmission lapisNetworkTransmission;
	
	public LapisNetworkTransmissionTest() {
		lapisNetworkTransmission = new LapisNetworkTransmission();
		lapisNetworkTransmission.setCoordinatorBaseUrl("http://coordinatorBaseUrl/coordinator/");
	}
	
	@Before
	public void setUpContext() {
		Context.setCurrent(new Context());
	}
	
	@Test
	public void testDeleteNodeFromNetwork() {
		final String nodeName = RandomStringUtils.randomAlphanumeric(20);
		Context.getCurrent().setClientDispatcher(new Client(Protocol.HTTP) {
			@Override public void handle(Request request, Response response) {
				Assert.assertEquals(Method.DELETE, request.getMethod());
				Assert.assertTrue(request.getResourceRef().getPath().endsWith(nodeName));
			}
		});
		lapisNetworkTransmission.deleteNodeFromNetwork(nodeName);
	}
	
	@Test
	public void testANodeToNetwork() {
		//TODO IMPLEMENT
	}
}
