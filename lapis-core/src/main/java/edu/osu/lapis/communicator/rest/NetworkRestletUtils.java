package edu.osu.lapis.communicator.rest;

import org.apache.commons.lang3.Validate;
import org.restlet.Restlet;
import org.restlet.routing.Filter;


public class NetworkRestletUtils {
	
	//TODO MAYBE MOVE THIS METHOD OUT OF THIS UTIL
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
	
	/*
	public static Validator getModelNamePresentValidator() {
		Validator v = new SaneSkipValidator();
		v.validatePresence(Constants.MODEL_NAME_ATTRIBUTE);
		return v;
	}
	
	public static Filter getNodePresentFilter(final NetworkTable networkTable) {
		return new Filter() {
			@Override protected int beforeHandle(Request request, Response response) {
				String modelName = Attributes.getModelName(request);
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
				String modelName = Attributes.getModelName(request);
				assert modelName != null; //should be called after name present validator
				LapisNode lapisNode = LapisRestletUtils.getLapisNodeFromMessageBody(request, lapisSerialization);
				if(modelName.equals(lapisNode.getNodeName())) {
					return CONTINUE;
				} else {
					response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, 
							"The model name attribute in the URI does not match model name in request payload.");
					return SKIP;
				}
			}
		};
	}
	*/
}
