package edu.osu.lapis.communicator.rest;
import org.apache.commons.lang3.Validate;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.routing.Filter;
import org.restlet.routing.Validator;

import edu.osu.lapis.Constants;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.transmission.LapisRestletUtils;


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
	
	public static Validator getModelNamePresentValidator() {
		Validator v = new SaneSkipValidator();
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
	
	//TODO COMMENT OUT
	public static Filter getBetterErrorResponseFilter() {
		return new Filter() {
			@Override protected void afterHandle(Request request, Response response) {
				Status status = response.getStatus();
				System.out.println("status code = " + status.getCode() //TODO REMOVE
						+ "\nstatus name = " + status.getName()
						+ "\nstatus description = " + status.getDescription()
						+ "\nstatus uri = " + status.getUri());
				if(status.isClientError()) {
					response.setEntity(status.getDescription(), MediaType.TEXT_PLAIN);
				}
			}
		};
	}
	
	public static String getModelName(Request req) {
		return (String) req.getAttributes().get(Constants.MODEL_NAME_ATTRIBUTE);
	}
}
