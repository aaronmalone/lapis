package edu.osu.lapis.restlets;

import java.util.List;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.ByteArrayRepresentation;

import edu.osu.lapis.Logger;
import edu.osu.lapis.comm.Notifier;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkChangeHandler;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.restlets.filters.LapisNodeExtractor;
import edu.osu.lapis.restlets.filters.ModelNameAttrValidator;
import edu.osu.lapis.restlets.filters.ModelPresentValidator;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.util.Attributes;

public class CoordinatorRestlet extends LapisRestletBase {
	
	private final Logger logger = Logger.getLogger(getClass());
	
	private final String LAPIS_NODE_ATTRIBUTE = 
			LapisNodeExtractor.DESERIALIZED_LAPIS_NODE_ATTR;
	
	private LapisSerialization lapisSerialization;
	private NetworkTable networkTable;
	private MediaType responseMediaType;
	private Notifier notifier;
	private NetworkChangeHandler networkChangeHandler;
	
	public Restlet getCoordinatorRestletWithFilters() {
		LapisFilterChainRestletBase filterChain = new LapisFilterChainRestletBase();
		
		filterChain.setPutFilters(
				new ModelNameAttrValidator(),
				new LapisNodeExtractor(lapisSerialization));
		filterChain.setPutTargetRestlet(this);
		
		filterChain.setPostFilters(
				new ModelNameAttrValidator(),
				new ModelPresentValidator(networkTable),
				new LapisNodeExtractor(lapisSerialization));
		filterChain.setPostTargetRestlet(this);
		
		filterChain.setDeleteFilters(
				new ModelNameAttrValidator(),
				new ModelPresentValidator(networkTable));
		filterChain.setDeleteTargetRestlet(this);
		
		filterChain.setGetTargetRestlet(this);
		
		return filterChain;
	}

	@Override
	public void delete(Request request, Response response) {
		String modelName = Attributes.getModelName(request);
		LapisNode removed = networkTable.removeNode(modelName);
		notifier.notifyNetworkOfDelete(removed);
		networkChangeHandler.onNodeDelete(removed);
	}

	@Override
	public void put(Request request, Response response) {
		LapisNode newNode = Attributes.getAttribute(request, LAPIS_NODE_ATTRIBUTE, LapisNode.class);
		logger.info("New node on network: " + newNode);
		networkTable.addNode(newNode);
		notifier.notifyNetworkOfNewNode(newNode);
		networkChangeHandler.onNodeAdd(newNode);
	}

	@Override
	public void get(Request request, Response response) {
		if(Attributes.getModelName(request) == null) {
			handleAllNodes(request, response);
		} else {
			handleSingleNode(request, response);
		}
	}
	
	private void handleAllNodes(Request request, Response response) {
		List<LapisNode> nodes = networkTable.getNodesList();
		nodes.add(networkTable.getLocalNode());
		LapisNode[] nodeArray = nodes.toArray(new LapisNode[0]);
		byte[] serialized = lapisSerialization.serialize(nodeArray);
		response.setEntity(new ByteArrayRepresentation(serialized, responseMediaType));
	}
	
	private void handleSingleNode(Request request, Response response) {
		String nodeName = Attributes.getModelName(request);
		LapisNode node = networkTable.getNode(nodeName);
		if(node != null) {
			byte[] serialized = lapisSerialization.serialize(node);
			response.setEntity(new ByteArrayRepresentation(serialized, responseMediaType));
		} else {
			String msg = "The specified LAPIS node, \"" + nodeName + "\", is not present in the coordinator's network table.";
			logger.warn(msg);
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, msg);
			response.setEntity(msg, MediaType.TEXT_PLAIN);
		}
	}

	public void setLapisSerialization(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}

	public void setNetworkTable(NetworkTable networkTable) {
		this.networkTable = networkTable;
	}

	public void setResponseMediaType(MediaType responseMediaType) {
		this.responseMediaType = responseMediaType;
	}

	public void setNotifier(Notifier notifier) {
		this.notifier = notifier;
	}

	public void setNetworkChangeHandler(NetworkChangeHandler networkChangeHandler) {
		this.networkChangeHandler = networkChangeHandler;
	}
}
