package edu.osu.lapis.communicator.rest;

import java.util.Arrays;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.transmission.LapisRestletUtils;

/**
 * Restlet to handle calls to network/* API for LAPIS nodes. The principal 
 * client of this API is the coordinator of the LAPIS network. The coordinator 
 * uses this API to keep LAPIS nodes up to date on the location of nodes on the 
 * network.
 */
public class NetworkRestlet extends LapisRestletBase {

	private NetworkTable networkTable;
	private LapisSerialization lapisSerialization;
	private MediaType responseMediaType;

	public Restlet getNetworkRestletWithFilters() {
		LapisFilterChainRestletBase filterChainRestlet = new LapisFilterChainRestletBase();
		
		filterChainRestlet.setPutFilters(
				NetworkRestletUtils.getBetterErrorResponseFilter(),
				NetworkRestletUtils.getModelNamePresentValidator(),
				NetworkRestletUtils.getRequestBodyMatchesModelNameFilter(lapisSerialization));
		filterChainRestlet.setPutTargetRestlet(this);
		
		filterChainRestlet.setPostFilters(
				NetworkRestletUtils.getBetterErrorResponseFilter(),
				NetworkRestletUtils.getModelNamePresentValidator(),
				NetworkRestletUtils.getNodePresentFilter(networkTable),
				NetworkRestletUtils.getRequestBodyMatchesModelNameFilter(lapisSerialization));
		filterChainRestlet.setPostTargetRestlet(this);
		
		filterChainRestlet.setDeleteFilters(
				NetworkRestletUtils.getBetterErrorResponseFilter(),
				NetworkRestletUtils.getModelNamePresentValidator(),
				NetworkRestletUtils.getNodePresentFilter(networkTable));
		filterChainRestlet.setDeleteTargetRestlet(this);
		
		filterChainRestlet.setGetFilters(NetworkRestletUtils.getBetterErrorResponseFilter());
		filterChainRestlet.setGetTargetRestlet(this);
		
		return filterChainRestlet;
	}
	
	@Override
	public void delete(Request request, Response response) {
		String nodeName = NetworkRestletUtils.getModelName(request);
		networkTable.removeNode(nodeName);
	}

	@Override
	public void post(Request request, Response response) {
		LapisNode node = LapisRestletUtils.getLapisNodeFromMessageBody(request, lapisSerialization);
		networkTable.updateNode(node);
	}

	@Override
	public void put(Request request, Response response) {
		LapisNode node = LapisRestletUtils.getLapisNodeFromMessageBody(request, lapisSerialization);
		networkTable.addNode(node);
	}

	@Override
	public void get(Request request, Response response) {
		System.out.println("get called:\n" + Arrays.toString(networkTable.getNodesList().toArray(new LapisNode[0]))); //TODO REMOVE
		String modelName = NetworkRestletUtils.getModelName(request);
		if(modelName == null) {
			LapisNode me = networkTable.getLocalNode();
			byte[] serialized = lapisSerialization.serialize(me);
			Representation entity = LapisRestletUtils.createRepresentation(serialized, responseMediaType);
			response.setEntity(entity);
		} else {
			response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, 
					"GET network/{modelName} should not be called on a non-coordinator node.");
		}
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
