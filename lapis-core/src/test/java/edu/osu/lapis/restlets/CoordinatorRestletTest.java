package edu.osu.lapis.restlets;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.Representation;

import edu.osu.lapis.communication.Notifier;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.restlets.CoordinatorRestlet;
import edu.osu.lapis.serialization.JsonSerialization;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.util.Attributes;
import edu.osu.lapis.util.LapisRestletUtils;

public class CoordinatorRestletTest {

	private final NetworkTable networkTable;
	private final LapisSerialization lapisSerialization;
	private final Restlet coordinatorRestlet;
	private final AtomicBoolean 
			update = new AtomicBoolean(false),
			newNode = new AtomicBoolean(false),
			delete = new AtomicBoolean(false);
	
	private LapisNode arbitraryNode;
	
	@Before
	public void resetNotifiers() {
		update.set(false);
		newNode.set(false);
		delete.set(false);
	}
	
	@Before 
	public void setUpNodeWithRandomData() {
		LapisNode lapisNode = new LapisNode();
		lapisNode.setNodeName(RandomStringUtils.randomAlphanumeric(7));
		lapisNode.setUrl(RandomStringUtils.randomAlphanumeric(21));
		arbitraryNode = lapisNode;
	}
	
	@Before
	public void clearNetworkTable() {
		List<LapisNode> nodes = new ArrayList<LapisNode>(networkTable.getNodesList());
		for(LapisNode node : nodes) {
			networkTable.removeNode(node);
		}
	}
	
	public CoordinatorRestletTest() {
		networkTable = new NetworkTable();
		networkTable.setLocalNode(new LapisNode("localNode", "whatever://"));
		lapisSerialization = new JsonSerialization();
		MediaType responseMediaType = MediaType.APPLICATION_JSON;
		Notifier notifier = new Notifier() {
			@Override public void notifyNetworkOfUpdate(LapisNode updatedNode) {
				update.set(true);
			}
			@Override public void notifyNetworkOfNewNode(LapisNode node) {
				newNode.set(true);
			}
			@Override public void notifyNetworkOfDelete(LapisNode node) {
				delete.set(true);
			}
		};
		CoordinatorRestlet coordinator = new CoordinatorRestlet();
		coordinator.setLapisSerialization(lapisSerialization);
		coordinator.setNetworkTable(networkTable);
		coordinator.setResponseMediaType(responseMediaType);
		coordinator.setNotifier(notifier);
		coordinatorRestlet = coordinator.getCoordinatorRestletWithFilters();
	}
	
	@Test 
	public void testDelete() {
		Request request = new Request(Method.DELETE, "resourceUri");
		String modelName = arbitraryNode.getNodeName();
		networkTable.addNode(arbitraryNode);
		Assert.assertNotNull(networkTable.getNode(modelName));
		request.getAttributes().put(Attributes.MODEL_NAME_ATTRIBUTE, modelName);
		Response response = new Response(request);
		coordinatorRestlet.handle(request, response);
		Assert.assertNull(networkTable.getNode(modelName));
		Assert.assertTrue(delete.get());
		Assert.assertEquals(0, networkTable.getNodesList().size());
	}
	
	@Test
	public void testPost() {
		networkTable.addNode(arbitraryNode);
		String originalUrl = arbitraryNode.getUrl();
		Assert.assertEquals(originalUrl, networkTable.getNode(arbitraryNode.getNodeName()).getUrl());
		
		LapisNode updatedNode = new LapisNode();
		updatedNode.setNodeName(arbitraryNode.getNodeName());
		updatedNode.setUrl(RandomStringUtils.randomAlphanumeric(64));
		
		Request request = new Request(Method.POST, "resourceUri");
		request.getAttributes().put(Attributes.MODEL_NAME_ATTRIBUTE, arbitraryNode.getNodeName());
		Representation entity = LapisRestletUtils.createRepresentation(lapisSerialization.serialize(updatedNode));
		request.setEntity(entity);
		
		coordinatorRestlet.handle(request, new Response(request));
		Assert.assertEquals(updatedNode.getUrl(), networkTable.getNode(arbitraryNode.getNodeName()).getUrl());
		Assert.assertTrue(update.get());
	}
	
	@Test
	public void testPut() {
		Assert.assertEquals(0, networkTable.getNodesList().size());
		Request request = new Request(Method.PUT, "resourceUri");
		request.getAttributes().put(Attributes.MODEL_NAME_ATTRIBUTE, arbitraryNode.getNodeName());
		Representation entity = LapisRestletUtils.createRepresentation(lapisSerialization.serialize(arbitraryNode));
		request.setEntity(entity);
		Response response = new Response(request);
		coordinatorRestlet.handle(request, response);
		Assert.assertTrue(newNode.get());
		Assert.assertNotNull(networkTable.getNode(arbitraryNode.getNodeName()));
	}
	
	@Test
	public void testGetSingleNode() {
		Request request = new Request(Method.GET, "resourceUri");
		request.getAttributes().put(Attributes.MODEL_NAME_ATTRIBUTE, arbitraryNode.getNodeName());
		networkTable.addNode(arbitraryNode);
		Response response = new Response(request);
		coordinatorRestlet.handle(request, response);
		try {
			InputStream input = response.getEntity().getStream();
			LapisNode responseNode = lapisSerialization.deserializeLapisNode(input);
			Assert.assertEquals(arbitraryNode.getNodeName(), responseNode.getNodeName());
			Assert.assertEquals(arbitraryNode.getUrl(), responseNode.getUrl());
		} catch(Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetAllNodes() {
		int nodesAdded = 10;
		Request request = new Request(Method.GET, "resourceUri");
		for(int i = 0; i < nodesAdded; ++i) {
			setUpNodeWithRandomData();
			networkTable.addNode(arbitraryNode);
		}
		Response response = new Response(request);
		coordinatorRestlet.handle(request, response);
		byte[] responesData = LapisRestletUtils.getMessageEntityAsBytes(response);
		List<LapisNode> nodes = lapisSerialization.deserializeNetworkData(responesData);
		Assert.assertEquals(nodesAdded + 1/*local node*/, nodes.size());
		boolean oneNodeEqualsLastArbitraryNode = false;
		for(LapisNode node : nodes) {
			if(arbitraryNode.getNodeName().equals(node.getNodeName())
					&& arbitraryNode.getUrl().equals(node.getUrl())) {
				oneNodeEqualsLastArbitraryNode = true;
				break;
			}
		}
		Assert.assertTrue(oneNodeEqualsLastArbitraryNode);
	}
}
