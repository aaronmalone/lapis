package edu.osu.lapis.restlets;

import java.util.List;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.StringRepresentation;

import com.google.common.base.Joiner;

import edu.osu.lapis.Logger;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkChangeHandler;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.restlets.filters.LapisNodeExtractor;
import edu.osu.lapis.restlets.filters.ModelNameAttrValidator;
import edu.osu.lapis.restlets.filters.ModelPresentValidator;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.util.Attributes;

/**
 * Restlet to handle calls to network/* API for LAPIS nodes. The principal 
 * client of this API is the coordinator of the LAPIS network. The coordinator 
 * uses this API to keep LAPIS nodes up to date on the location of nodes on the 
 * network.
 */
public class NetworkRestlet extends LapisRestletBase {
	
	private final Logger logger = Logger.getLogger(getClass());

	private static final String DESERIALIZED_NODE_ATTR
			= LapisNodeExtractor.DESERIALIZED_LAPIS_NODE_ATTR;
	
	private NetworkTable networkTable;
	private LapisSerialization lapisSerialization;
	private MediaType responseMediaType;
	private NetworkChangeHandler networkChangeHandler;

	public Restlet getNetworkRestletWithFilters() {
		LapisFilterChainRestletBase filterChainRestlet = new LapisFilterChainRestletBase();
		
		filterChainRestlet.setPutFilters(
				new ModelNameAttrValidator(),
				new LapisNodeExtractor(lapisSerialization));
		filterChainRestlet.setPutTargetRestlet(this);
		
		filterChainRestlet.setPostFilters(
				new ModelNameAttrValidator(),
				new ModelPresentValidator(networkTable),
				new LapisNodeExtractor(lapisSerialization));
		filterChainRestlet.setPostTargetRestlet(this);
		
		filterChainRestlet.setDeleteFilters(
				new ModelNameAttrValidator(),
				new ModelPresentValidator(networkTable));
		filterChainRestlet.setDeleteTargetRestlet(this);
		
		filterChainRestlet.setGetTargetRestlet(this);
		
		return filterChainRestlet;
	}
	
	@Override
	public void delete(Request request, Response response) {
		String nodeName = Attributes.getModelName(request);
		logger.info("Received request for delete of node: %s", nodeName);
		LapisNode node = networkTable.getNode(nodeName);
		networkTable.removeNode(nodeName);
		networkChangeHandler.onNodeDelete(node);
	}

	@Override
	public void put(Request request, Response response) {
		LapisNode node = Attributes.getAttribute(request, DESERIALIZED_NODE_ATTR, LapisNode.class);
		logger.info("Received request to add new node: %s", node);
		networkTable.addNode(node);
		networkChangeHandler.onNodeAdd(node);
	}

	@Override
	public void get(Request request, Response response) {
		String modelName = Attributes.getModelName(request);
		if(modelName == null) {
			logger.info("Received request for this node's information.");
			LapisNode me = networkTable.getLocalNode();
			byte[] serialized = lapisSerialization.serialize(me);
			response.setEntity(new ByteArrayRepresentation(serialized, responseMediaType));
		} else if(modelName.equals("ALL_NODES")) {
			//undocumented feature used for debugging purposes
			logger.info("Received request for info on all nodes in network table.");
			respondWithInformationForAllNodes(response);
		} else {
			logger.warn(String.format("GET network/%s called on this node.",modelName));
			response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED); 
			response.setEntity("GET network/{modelName} should not be called on a non-coordinator node.", MediaType.TEXT_PLAIN);
		}
	}
	
	private void respondWithInformationForAllNodes(Response response) {
		List<LapisNode> nodesList = networkTable.getNodesList();
		String text = Joiner.on("\n").join(nodesList);
		response.setEntity(new StringRepresentation(text, MediaType.TEXT_PLAIN));
	}
	
	public void setNetworkTable(NetworkTable networkTable) {
		this.networkTable = networkTable;
	}

	public void setLapisSerialization(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}

	public void setResponseMediaType(MediaType responseMediaType) {
		this.responseMediaType = responseMediaType;
	}

	public void setNetworkChangeHandler(NetworkChangeHandler networkChangeHandler) {
		this.networkChangeHandler = networkChangeHandler;
	}
}