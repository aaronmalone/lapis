package edu.osu.lapis.communicator.rest;

import java.util.List;
import java.util.Map;

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

public class CoordinatorRestlet extends Restlet {
	
	private NetworkTable networkTable;
	private Map<Method, Restlet> methodToRestletMap;
	private LapisSerialization lapisSerialization;
	private MediaType responseMediaType;
	private Restlet underlyingCoordinatorRestletImpl;
	
	@Override
	public void handle(Request request, Response response) {
		Method meth = request.getMethod();
		Restlet restlet = getRestletToMethodMap().get(meth);
		if(restlet != null) {
			restlet.handle(request, response);
		} else {
			response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
		}
	}
	
	private synchronized Map<Method, Restlet> getRestletToMethodMap() {
		if(methodToRestletMap == null) {
			methodToRestletMap.put(Method.PUT, getPutRestletChain());
			methodToRestletMap.put(Method.POST, getPostRestletChain());
			methodToRestletMap.put(Method.DELETE, getDeleteRestletChain());
			methodToRestletMap.put(Method.GET, getUnderlyingCoordinatorRestletImpl());
		}
		return methodToRestletMap;
	}
	
	private Restlet getPutRestletChain() {
		return NetworkRestletUtils.createRestletFilterChain(
				NetworkRestletUtils.getModelNamePresentValidator(),
				NetworkRestletUtils.getRequestBodyMatchesModelNameFilter(lapisSerialization),
				getUnderlyingCoordinatorRestletImpl());
	}
	
	private Restlet getPostRestletChain() {
		return NetworkRestletUtils.createRestletFilterChain(
				NetworkRestletUtils.getModelNamePresentValidator(),
				NetworkRestletUtils.getNodePresentFilter(networkTable),
				NetworkRestletUtils.getRequestBodyMatchesModelNameFilter(lapisSerialization),
				getUnderlyingCoordinatorRestletImpl());
	}
	
	private Restlet getDeleteRestletChain() {
		return NetworkRestletUtils.createRestletFilterChain(
				NetworkRestletUtils.getModelNamePresentValidator(),
				NetworkRestletUtils.getNodePresentFilter(networkTable),
				getUnderlyingCoordinatorRestletImpl());
	}
	
	private synchronized Restlet getUnderlyingCoordinatorRestletImpl() {
		if(underlyingCoordinatorRestletImpl == null) {
			underlyingCoordinatorRestletImpl = new LapisRestletBase() {

				@Override public void delete(Request request, Response response) {
					assert Method.DELETE.equals(request.getMethod());
					String modelName = NetworkRestletUtils.getModelName(request);
					LapisNode removed = networkTable.removeNode(modelName);
					notifyNetworkOfDelete(removed);
				}

				@Override public void post(Request request, Response response) {
					assert Method.POST.equals(request.getMethod());
					LapisNode updatedNode = LapisRestletUtils.getLapisNodeFromRequestBody(request, lapisSerialization);
					networkTable.updateNode(updatedNode);
					notifyNetworkOfUpdate(updatedNode);
				}

				@Override public void put(Request request, Response response) {
					assert Method.PUT.equals(request.getMethod());
					LapisNode newNode = LapisRestletUtils.getLapisNodeFromRequestBody(request, lapisSerialization);
					networkTable.addNode(newNode);
					notifyNetworkOfNewNode(newNode);
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
				
			};
		}
		return underlyingCoordinatorRestletImpl;
	}	
	
	//TODO MOVE and add other notification methods
	private void notifyNetworkOfUpdate(LapisNode updatedNode) {
		// TODO Auto-generated method stub
		
	}
	
	private void notifyNetworkOfNewNode(LapisNode lapisNode) {
		// TODO Auto-generated method stub
		
	}
	
	private void notifyNetworkOfDelete(LapisNode node) {
		// TODO Auto-generated method stub
		
	}
}
