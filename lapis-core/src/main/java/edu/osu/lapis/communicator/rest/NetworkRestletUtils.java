package edu.osu.lapis.communicator.rest;
import org.apache.commons.lang3.Validate;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Status;
import org.restlet.routing.Filter;
import org.restlet.routing.Validator;

import edu.osu.lapis.Constants;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.transmission.LapisRestletUtils;


public class NetworkRestletUtils {
	public static Restlet createRestletFilterChain(Restlet ... restlets) { //TODO TEST
		Validate.isTrue(restlets.length > 0, "Must provide at least one Restlet.");
		Filter previousFilter = null;
		Filter currentFilter =  null;
		for(int i = 0; i < restlets.length - 1; ++i) {
			currentFilter = (Filter) restlets[i];
			if(previousFilter != null) {
				previousFilter.setNext(currentFilter);
			}
			previousFilter = currentFilter;
		}
		if(currentFilter != null) {
			currentFilter.setNext(restlets[restlets.length-1]);
		}
		return restlets[0];
	}
	
	public static Validator getModelNamePresentValidator() {
		Validator v = new Validator() {
			@Override protected int beforeHandle(Request request, Response response) {
				int returnValue = super.beforeHandle(request, response);
				if(returnValue == CONTINUE && response.getStatus().isClientError()) {
					returnValue = SKIP;
				}
				return returnValue;
			}
		};
		v.validatePresence(Constants.MODEL_NAME_ATTRIBUTE);
		return v;
	}
	
	public static Filter getNodePresentFilter(final NetworkTable networkTable) {
		return new Filter() {
			@Override protected int beforeHandle(Request request, Response response) {
				String modelName = getModelName(request);
				assert modelName != null; //should be called after name present validator
				LapisNode node = networkTable.getNode(modelName);
				if(node != null) {
					return CONTINUE;
				} else {
					response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "The specified LAPIS node, " 
							+ modelName + ", is not present in this node's network table.");
					return SKIP;
				}
			}
		};
	}
	
	public static Filter getRequestBodyMatchesModelNameFilter(final LapisSerialization lapisSerialization) {
		return new Filter() {
			@Override protected int beforeHandle(Request request, Response response) {
				String modelName = getModelName(request);
				assert modelName != null; //should be called after name present validator
				LapisNode lapisNode = LapisRestletUtils.getLapisNodeFromRequestBody(request, lapisSerialization);
				if(modelName.equals(lapisNode.getNodeName())) {
					return CONTINUE;
				} else {
					response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, 
							"Model name attribute does not match model name in response body.");
					return SKIP;
				}
			}
		};
	}
	
	public static String getModelName(Request req) {
		return (String) req.getAttributes().get(Constants.MODEL_NAME_ATTRIBUTE);
	}
}
