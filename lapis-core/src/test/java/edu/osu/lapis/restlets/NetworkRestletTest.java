package edu.osu.lapis.restlets;

import java.io.ByteArrayInputStream;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.InputRepresentation;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialization.JsonSerialization;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.util.Attributes;
import edu.osu.lapis.util.LapisRestletUtils;

public class NetworkRestletTest {

	private Restlet networkRestletWithFilters;
	private NetworkTable networkTable;
	private LapisSerialization lapisSerialization;
	
	@Before
	public void initialize() {
		networkTable = new NetworkTable();
		networkTable.setLocalNode(new LapisNode("localNode", "whatever://"));
		lapisSerialization = new JsonSerialization();
		NetworkRestlet networkRestlet = new NetworkRestlet();
		networkRestlet.setResponseMediaType(MediaType.APPLICATION_JSON);
		networkRestlet.setLapisSerialization(lapisSerialization);
		networkRestlet.setNetworkTable(networkTable);
		networkRestletWithFilters = networkRestlet.getNetworkRestletWithFilters();
	}
	
	@Test
	public void testPut() {
		LapisNode newNode = getLapisNodeWithRandomData();
		Request request = getRequestWithModelName(Method.PUT, newNode.getNodeName());
		request.setEntity(getEntity(newNode));
		Assert.assertEquals(0, networkTable.getNodesList().size());
		Response response = handleRequestAndReturnResponse(request);
		Assert.assertTrue(response.getStatus().isSuccess());
		LapisNode nodeInTable = networkTable.getNode(newNode.getNodeName());
		Assert.assertEquals(newNode, nodeInTable);
	}
	
	@Test
	public void testGet() {	
		LapisNode localNode = getLapisNodeWithRandomData();
		networkTable.setLocalNode(localNode);
		Request request = new Request(Method.GET, "resourceUri");
		Response response = handleRequestAndReturnResponse(request);
		Assert.assertTrue(response.getStatus().isSuccess());
		byte[] bytes = LapisRestletUtils.getMessageEntityAsBytes(response);
		LapisNode retrievedNode = lapisSerialization.deserializeLapisNode(bytes);
		Assert.assertEquals(localNode, retrievedNode);		
	}
	
	@Test
	public void testPost() {
		LapisNode originalNode = getLapisNodeWithRandomData();
		String nodeName = originalNode.getNodeName();
		String originalUrl = originalNode.getUrl();
		networkTable.addNode(originalNode);
		Assert.assertEquals(originalUrl, networkTable.getNode(nodeName).getUrl());
		
		LapisNode updatedNode = new LapisNode(nodeName, RandomStringUtils.randomAlphanumeric(64));
		Request request = getRequestWithModelName(Method.POST, nodeName);
		request.setEntity(getEntity(updatedNode));
		Response response = handleRequestAndReturnResponse(request);
		Assert.assertTrue(response.getStatus().isSuccess());
		Assert.assertEquals(updatedNode, networkTable.getNode(nodeName));
		
	}
	
	@Test
	public void testDelete() {
		LapisNode deletedNode = getLapisNodeWithRandomData();
		
		String modelName = deletedNode.getNodeName();
		Request request = getRequestWithModelName(Method.DELETE, modelName);
		
		//node is not present in network table
		Response response = handleRequestAndReturnResponse(request);
		Assert.assertTrue(response.getStatus().isClientError());
		
		//with node present
		networkTable.addNode(deletedNode);
		response = handleRequestAndReturnResponse(request);
		Assert.assertTrue(response.getStatus().isSuccess());
	}
	
	private Response handleRequestAndReturnResponse(Request request) {
		Response response = new Response(request);
		networkRestletWithFilters.handle(request, response);
		return response;
	}
	
	private LapisNode getLapisNodeWithRandomData() {
		return new LapisNode(RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(40));
	}
	
	private InputRepresentation getEntity(LapisNode lapisNode) {
		byte[] data = lapisSerialization.serialize(lapisNode);
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		return new InputRepresentation(stream);
	}
	
	private Request getRequestWithModelName(Method method, String modelName) {
		Request request = new Request(method, "resourceUri");
		request.getAttributes().put(Attributes.MODEL_NAME_ATTRIBUTE, modelName);
		return request;
	}
	
	//TODO REORGANIZE MEMBERS
}
