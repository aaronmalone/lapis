package edu.osu.lapis.restlets;

import com.google.common.util.concurrent.MoreExecutors;
import edu.osu.lapis.network.CoordinatorLapisNetwork;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkChangeCallback;
import edu.osu.lapis.network.NetworkChangeHandler;
import edu.osu.lapis.serialization.JsonSerialization;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.util.ClientUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.ByteArrayRepresentation;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static edu.osu.lapis.util.Attributes.MODEL_NAME_ATTRIBUTE;

public class CoordinatorRestletTest {

	private final LapisSerialization lapisSerialization = new JsonSerialization();
	private final MediaType responseMediaType = MediaType.APPLICATION_JSON;
	private final LastOpNetworkChangeCallback callback = new LastOpNetworkChangeCallback();

	private LapisNode nodeWithRandomData;
	private CoordinatorLapisNetwork coordinatorLapisNetwork;
	
	@Before 
	public void setUpNodeWithRandomData() {
		LapisNode lapisNode = new LapisNode();
		lapisNode.setNodeName(RandomStringUtils.randomAlphanumeric(7));
		lapisNode.setUrl(RandomStringUtils.randomAlphanumeric(21));
		this.nodeWithRandomData = lapisNode;
	}

	@Before
	public void setUpCoordinatorLapisNetwork() {
		this.coordinatorLapisNetwork = new CoordinatorLapisNetwork(new LapisNode("localNode", "whatever://"));
	}
	
	@Test 
	public void testDelete() {
		String modelName = nodeWithRandomData.getNodeName();
		Request request = new Request(Method.DELETE, "resourceUri");
		request.getAttributes().put(MODEL_NAME_ATTRIBUTE, modelName);
		Response response = new Response(request);
		NetworkChangeHandler networkChangeHandler = new NetworkChangeHandler(MoreExecutors.sameThreadExecutor());
		networkChangeHandler.addCallback(this.callback);
		CoordinatorRestlet coordinatorRestlet = new CoordinatorRestlet(this.lapisSerialization,
				this.coordinatorLapisNetwork, this.responseMediaType, networkChangeHandler);
		coordinatorRestlet.getCoordinatorRestletWithFilters().handle(request, response);
		Assert.assertTrue(response.getStatus().isSuccess());
		Assert.assertNull(this.coordinatorLapisNetwork.getNode(modelName));
		Assert.assertEquals(LastOpNetworkChangeCallback.DELETE, this.callback.getLastOp());
	}
	
	@Test
	public void testPut() {
		Request request = new Request(Method.PUT, "resourceUri");
		request.getAttributes().put(MODEL_NAME_ATTRIBUTE, nodeWithRandomData.getNodeName());
		request.setEntity(new ByteArrayRepresentation(lapisSerialization.serialize(nodeWithRandomData)));
		Response response = new Response(request);
		NetworkChangeHandler networkChangeHandler = new NetworkChangeHandler(MoreExecutors.sameThreadExecutor());
		networkChangeHandler.addCallback(this.callback);
		CoordinatorRestlet coordinatorRestlet = new CoordinatorRestlet(this.lapisSerialization,
				this.coordinatorLapisNetwork, this.responseMediaType, networkChangeHandler);
		coordinatorRestlet.getCoordinatorRestletWithFilters().handle(request, response);
		Assert.assertNotNull(this.coordinatorLapisNetwork.getNode(nodeWithRandomData.getNodeName()));
		Assert.assertEquals(LastOpNetworkChangeCallback.ADD, this.callback.getLastOp());
	}
	
	@Test
	public void testGetSingleNode() throws IOException {
		Request request = new Request(Method.GET, "resourceUri");
		request.getAttributes().put(MODEL_NAME_ATTRIBUTE, nodeWithRandomData.getNodeName());
		this.coordinatorLapisNetwork.addNode(nodeWithRandomData);
		Response response = new Response(request);
		CoordinatorRestlet coordinatorRestlet = new CoordinatorRestlet(this.lapisSerialization,
				this.coordinatorLapisNetwork, this.responseMediaType, null);
		coordinatorRestlet.getCoordinatorRestletWithFilters().handle(request, response);
		Assert.assertTrue(response.getStatus().isSuccess());
		InputStream input = response.getEntity().getStream();
		LapisNode responseNode = lapisSerialization.deserializeLapisNode(input);
		Assert.assertEquals(nodeWithRandomData.getNodeName(), responseNode.getNodeName());
		Assert.assertEquals(nodeWithRandomData.getUrl(), responseNode.getUrl());
	}
	
	@Test
	public void testGetAllNodes() {
		int nodesAdded = 10;
		Request request = new Request(Method.GET, "resourceUri");
		CoordinatorLapisNetwork lapisNetwork = new CoordinatorLapisNetwork(new LapisNode("localNode", "whatever://"));
		for(int i = 0; i < nodesAdded; ++i) {
			setUpNodeWithRandomData();
			lapisNetwork.addNode(nodeWithRandomData);
		}
		Response response = new Response(request);
		CoordinatorRestlet coordinatorRestlet = new CoordinatorRestlet(this.lapisSerialization, lapisNetwork,
				this.responseMediaType, new NetworkChangeHandler(MoreExecutors.sameThreadExecutor()));
		coordinatorRestlet.getCoordinatorRestletWithFilters().handle(request, response);
		byte[] responseData = ClientUtils.getMessageEntityAsBytes(response);
		List<LapisNode> nodes = lapisSerialization.deserializeNetworkData(responseData);
		Assert.assertEquals(nodesAdded + 1/*local node*/, nodes.size());
		boolean oneNodeEqualsLastArbitraryNode = false;
		for(LapisNode node : nodes) {
			if(nodeWithRandomData.getNodeName().equals(node.getNodeName())
					&& nodeWithRandomData.getUrl().equals(node.getUrl())) {
				oneNodeEqualsLastArbitraryNode = true;
				break;
			}
		}
		Assert.assertTrue(oneNodeEqualsLastArbitraryNode);
	}

	private static class LastOpNetworkChangeCallback implements NetworkChangeCallback {

		private static String
			ADD = "ADD",
			DELETE = "DELETE";

		private Object lastOp;

		@Override
		public void onNodeAdd(LapisNode lapisNode) {
			this.lastOp = ADD;
		}

		@Override
		public void onNodeDelete(LapisNode lapisNode) {
			this.lastOp = DELETE;
		}

		private Object getLastOp() {
			return this.lastOp;
		}
	}
}
