package edu.osu.lapis.communicator.rest;

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

public class NetworkRestletTwo extends LapisRestletBase {

	private NetworkTable networkTable;
	private LapisSerialization lapisSerialization;
	private MediaType responseMediaType;
	
	public Restlet getNetworkRestletWithFilters() {
		LapisFilterChainRestletBase filterChain /*TODO RENAME*/ = new LapisFilterChainRestletBase();
		
		filterChain.setPutFilters(
				NetworkRestletUtils.getModelNamePresentValidator(),
				NetworkRestletUtils.getRequestBodyMatchesModelNameFilter(lapisSerialization));
		filterChain.setPutTargetRestlet(this);
		
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
		String nodeName = NetworkRestletUtils.getModelName(request);
		networkTable.removeNode(nodeName);
	}

	@Override
	public void post(Request request, Response response) {
		LapisNode node = LapisRestletUtils.getLapisNodeFromRequestBody(request, lapisSerialization);
		networkTable.updateNode(node);
	}

	@Override
	public void put(Request request, Response response) {
		LapisNode node = LapisRestletUtils.getLapisNodeFromRequestBody(request, lapisSerialization);
		networkTable.addNode(node);
	}

	@Override
	public void get(Request request, Response response) {
		String modelName = NetworkRestletUtils.getModelName(request);
		if(modelName == null) {
			LapisNode me = networkTable.getLocalNode();
			byte[] serialized = lapisSerialization.serialize(me);
			Representation entity = LapisRestletUtils.createRepresentation(serialized, responseMediaType);
			response.setEntity(entity);
		} else {
			response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, 
					"GET network/modelName should not be called on a non-coordinator node.");
		}
	}
	
}
