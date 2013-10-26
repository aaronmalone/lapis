package edu.osu.lapis.communicator.rest;

import java.util.List;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.transmission.LapisRestletUtils;

public class CoordinatorRestlet extends LapisRestletBase {
	
	private LapisSerialization lapisSerialization;
	private NetworkTable networkTable;
	private MediaType responseMediaType;
	private Notifier notifier;
	
	public Restlet getCoordinatorRestletWithFilters() {
		LapisFilterChainRestletBase filterChain = new LapisFilterChainRestletBase(); //TODO MAYBE RENAME
		
		filterChain.setPutFilters(
				NetworkRestletUtils.getModelNamePresentValidator(),
				NetworkRestletUtils.getRequestBodyMatchesModelNameFilter(lapisSerialization));
		filterChain.setPostTargetRestlet(this);
		
		filterChain.setPostFilters(
				NetworkRestletUtils.getModelNamePresentValidator(),
				NetworkRestletUtils.getNodePresentFilter(networkTable),
				NetworkRestletUtils.getRequestBodyMatchesModelNameFilter(lapisSerialization));
		filterChain.setPostTargetRestlet(this);
		
		filterChain.setDeleteFilters(
				NetworkRestletUtils.getModelNamePresentValidator(),
				NetworkRestletUtils.getNodePresentFilter(networkTable));
		filterChain.setDeleteTargetRestlet(this);
		
		filterChain.setGetTargetRestlet(this);
		
		return filterChain;
	}

	@Override
	public void delete(Request request, Response response) {
		assert Method.DELETE.equals(request.getMethod());
		String modelName = NetworkRestletUtils.getModelName(request);
		LapisNode removed = networkTable.removeNode(modelName);
		notifier.notifyNetworkOfDelete(removed);
	}

	@Override
	public void post(Request request, Response response) {
		assert Method.POST.equals(request.getMethod());
		LapisNode updatedNode = LapisRestletUtils.getLapisNodeFromRequestBody(request, lapisSerialization);
		networkTable.updateNode(updatedNode);
		notifier.notifyNetworkOfUpdate(updatedNode);
	}

	@Override
	public void put(Request request, Response response) {
		assert Method.PUT.equals(request.getMethod());
		LapisNode newNode = LapisRestletUtils.getLapisNodeFromRequestBody(request, lapisSerialization);
		networkTable.addNode(newNode);
		notifier.notifyNetworkOfNewNode(newNode);
	}

	@Override
	public void get(Request request, Response response) {
		if(NetworkRestletUtils.getModelName(request) == null) {
			handleAllNodes(request, response);
		} else {
			handleSingleNode(request, response);
		}
	}
	
	private void handleAllNodes(Request request, Response response) {
		List<LapisNode> nodes = networkTable.getNodesList();
		LapisNode[] nodeArray = nodes.toArray(new LapisNode[0]);
		byte[] serialized = lapisSerialization.serialize(nodeArray);
		Representation entity = LapisRestletUtils.createRepresentation(serialized, responseMediaType);
		response.setEntity(entity);
	}
	
	private void handleSingleNode(Request request, Response response) {
		String nodeName = NetworkRestletUtils.getModelName(request);
		assert nodeName != null;
		LapisNode node = networkTable.getNode(nodeName);
		if(node != null) {
			byte[] serialized = lapisSerialization.serialize(node);
			Representation entity = LapisRestletUtils.createRepresentation(serialized, responseMediaType);
			response.setEntity(entity);
		} else {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "The specified LAPIS node, " 
					+ nodeName + ", is not present in the network table.");
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
}
