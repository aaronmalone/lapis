package edu.osu.lapis.restlets;

import java.util.List;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.google.common.base.Joiner;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.restlets.filters.LapisNodeExtractor;
import edu.osu.lapis.restlets.filters.ModelNameAttrValidator;
import edu.osu.lapis.restlets.filters.ModelPresentValidator;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.util.Attributes;
import edu.osu.lapis.util.LapisRestletUtils;

/**
 * Restlet to handle calls to network/* API for LAPIS nodes. The principal 
 * client of this API is the coordinator of the LAPIS network. The coordinator 
 * uses this API to keep LAPIS nodes up to date on the location of nodes on the 
 * network.
 */
public class NetworkRestlet extends LapisRestletBase {

	private static final String DESERIALIZED_NODE_ATTR
			= LapisNodeExtractor.DESERIALIZED_LAPIS_NODE_ATTR;
	
	private NetworkTable networkTable;
	private LapisSerialization lapisSerialization;
	private MediaType responseMediaType;

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
		getLogger().info("Received request for delete of node \"" + nodeName + "\".");
		networkTable.removeNode(nodeName);
	}

	@Override
	public void post(Request request, Response response) {
		LapisNode node = Attributes.getAttribute(request, DESERIALIZED_NODE_ATTR, LapisNode.class);
		getLogger().info("Received request to update node: " + node);
		networkTable.updateNode(node);
	}

	@Override
	public void put(Request request, Response response) {
		LapisNode node = Attributes.getAttribute(request, DESERIALIZED_NODE_ATTR, LapisNode.class);
		getLogger().info("Received request to add new node: " + node);
		networkTable.addNode(node);
	}

	@Override
	public void get(Request request, Response response) {
		String modelName = Attributes.getModelName(request);
		if(modelName == null) {
			getLogger().info("Received request for this node's information.");
			LapisNode me = networkTable.getLocalNode();
			byte[] serialized = lapisSerialization.serialize(me);
			Representation entity = LapisRestletUtils.createRepresentation(serialized, responseMediaType);
			response.setEntity(entity);
		} else if(modelName.equals("ALL_NODES")) {
			//undocumented feature used for debugging purposes
			getLogger().info("Received request for info on all nodes in network table.");
			respondWithInformationForAllNodes(response);
		} else {
			getLogger().warning(String.format("GET network/%s called on this node.",modelName));
			response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED); 
			response.setEntity("GET network/{modelName} should not be called on a non-coordinator node.", MediaType.TEXT_PLAIN);
		}
	}
	
	private void respondWithInformationForAllNodes(Response response) {
		List<LapisNode> nodesList = networkTable.getNodesList();
		System.out.println("NODES LIST HAS " + nodesList.size() + " ITEMS.\nNODES LIST: " + nodesList); //TODO remove
		String text = Joiner.on("\n").join(nodesList);
		System.out.println("RESPONSE TEXT = " + text); //TODO remove
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
}