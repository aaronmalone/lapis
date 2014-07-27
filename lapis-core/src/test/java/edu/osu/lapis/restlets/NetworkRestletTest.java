package edu.osu.lapis.restlets;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.serialization.JsonSerialization;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.util.ClientUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;

public class NetworkRestletTest {

	private final LapisSerialization lapisSerialization = new JsonSerialization();

	private Restlet networkRestletWithFilters;
	private LapisNode localNode;

	@Before
	public void initialize() {
		this.localNode = new LapisNode("localNode", "whatever://");
		NetworkRestlet networkRestlet = new NetworkRestlet();
		networkRestlet.setResponseMediaType(MediaType.APPLICATION_JSON);
		networkRestlet.setLapisSerialization(lapisSerialization);
		networkRestlet.setLocalNode(localNode);
		networkRestletWithFilters = networkRestlet.getNetworkRestletWithFilters();
	}
	
	@Test
	public void testGet() {	
		Request request = new Request(Method.GET, "resourceUri");
		Response response = handleRequestAndReturnResponse(request);
		Assert.assertTrue(response.getStatus().isSuccess());
		byte[] bytes = ClientUtils.getMessageEntityAsBytes(response);
		LapisNode retrievedNode = lapisSerialization.deserializeLapisNode(bytes);
		Assert.assertEquals(this.localNode, retrievedNode);
	}
	
	private Response handleRequestAndReturnResponse(Request request) {
		Response response = new Response(request);
		networkRestletWithFilters.handle(request, response);
		return response;
	}
	
	private LapisNode getLapisNodeWithRandomData() {
		return new LapisNode(RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(40));
	}
}
