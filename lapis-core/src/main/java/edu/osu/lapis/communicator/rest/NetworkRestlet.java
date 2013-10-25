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

public class NetworkRestlet extends LapisRestletBase {

	private Restlet underlyingNetworkRestletImpl;
	private LapisSerialization lapisSerialization;
	private NetworkTable networkTable;
	private MediaType responseMediaType;
	
	@Override
	public void delete(Request request, Response response) {
		Restlet deleteRestletChain = NetworkRestletUtils.createRestletFilterChain(
				NetworkRestletUtils.getModelNamePresentValidator(),
				NetworkRestletUtils.getNodePresentFilter(networkTable),
				getUnderlyingNetworkRestletImpl());
		deleteRestletChain.handle(request, response);
	}

	@Override
	public void post(Request request, Response response) {
		Restlet postRestletChain = NetworkRestletUtils.createRestletFilterChain(
				NetworkRestletUtils.getModelNamePresentValidator(),
				NetworkRestletUtils.getNodePresentFilter(networkTable),
				NetworkRestletUtils.getRequestBodyMatchesModelNameFilter(lapisSerialization),
				getUnderlyingNetworkRestletImpl());
		postRestletChain.handle(request, response);
	}

	@Override
	public void put(Request request, Response response) {
		Restlet putRestletChain = NetworkRestletUtils.createRestletFilterChain(
				NetworkRestletUtils.getModelNamePresentValidator(),
				NetworkRestletUtils.getRequestBodyMatchesModelNameFilter(lapisSerialization),
				getUnderlyingNetworkRestletImpl());
		putRestletChain.handle(request, response);
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

	private synchronized Restlet getUnderlyingNetworkRestletImpl() {
		if(underlyingNetworkRestletImpl == null) {
			underlyingNetworkRestletImpl = new LapisRestletBase() {
				
				@Override public void delete(Request request, Response response) {
					String nodeName = NetworkRestletUtils.getModelName(request);
					networkTable.removeNode(nodeName);
				}

				@Override public void post(Request request, Response response) {
					LapisNode node = LapisRestletUtils.getLapisNodeFromRequestBody(request, lapisSerialization);
					networkTable.updateNode(node);
				}

				@Override public void put(Request request, Response response) {
					LapisNode node = LapisRestletUtils.getLapisNodeFromRequestBody(request, lapisSerialization);
					networkTable.addNode(node);
				}
			};
		}
		return underlyingNetworkRestletImpl;
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
}
