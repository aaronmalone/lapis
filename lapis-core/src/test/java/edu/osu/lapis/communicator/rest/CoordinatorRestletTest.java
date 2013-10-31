package edu.osu.lapis.communicator.rest;

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
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.Representation;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.restlets.CoordinatorRestlet;
import edu.osu.lapis.serialization.JsonSerialization;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.transmission.LapisRestletUtils;

public class CoordinatorRestletTest {

	private final NetworkTable networkTable;
	private final LapisSerialization lapisSerialization;
	private final CoordinatorRestlet coordinatorRestlet;
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
		List<LapisNode> nodes = new ArrayList<>(networkTable.getNodesList());
		for(LapisNode node : nodes) {
			networkTable.removeNode(node);
		}
	}
	
	public CoordinatorRestletTest() {
		networkTable = new NetworkTable();
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
		coordinatorRestlet = new CoordinatorRestlet();
		coordinatorRestlet.setLapisSerialization(lapisSerialization);
		coordinatorRestlet.setNetworkTable(networkTable);
		coordinatorRestlet.setResponseMediaType(responseMediaType);
		coordinatorRestlet.setNotifier(notifier);
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
		Request request = new Request(Method.POST, "resourceUri");
		request.getAttributes().put(Attributes.MODEL_NAME_ATTRIBUTE, arbitraryNode.getNodeName());
		LapisNode lapisNode = new LapisNode();
		lapisNode.setNodeName(arbitraryNode.getNodeName());
		lapisNode.setUrl(RandomStringUtils.randomAlphanumeric(64));
		Representation entity = LapisRestletUtils.createRepresentation(lapisSerialization.serialize(lapisNode));
		request.setEntity(entity);
		networkTable.addNode(arbitraryNode);
		String originalUrl = arbitraryNode.getUrl();
		Assert.assertEquals(originalUrl, networkTable.getNode(arbitraryNode.getNodeName()).getUrl());
		Response response = new Response(request);
		coordinatorRestlet.post(request, response);
		Assert.assertEquals(lapisNode.getUrl(), networkTable.getNode(arbitraryNode.getNodeName()).getUrl());
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
		try (InputStream input = response.getEntity().getStream()){
			LapisNode responseNode = lapisSerialization.deserializeLapisNode(input);
			Assert.assertEquals(arbitraryNode.getNodeName(), responseNode.getNodeName());
			Assert.assertEquals(arbitraryNode.getUrl(), responseNode.getUrl());
		} catch(Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetAllNodes() {
		Request request = new Request(Method.GET, "resourceUri");
		for(int i = 0; i < 10; ++i) {
			setUpNodeWithRandomData();
			networkTable.addNode(arbitraryNode);
		}
		Response response = new Response(request);
		coordinatorRestlet.handle(request, response);
		try (InputStream input = response.getEntity().getStream()){
			List<LapisNode> nodes = lapisSerialization.deserializeNetworkData(input);
			Assert.assertEquals(10, nodes.size());
			boolean oneEqualsArbitraryNode = false;
			for(LapisNode node : nodes) {
				if(arbitraryNode.getNodeName().equals(node.getNodeName())
						&& arbitraryNode.getUrl().equals(node.getUrl())) {
					oneEqualsArbitraryNode = true;
					break;
				}
			}
			Assert.assertTrue(oneEqualsArbitraryNode);
		} catch(Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
