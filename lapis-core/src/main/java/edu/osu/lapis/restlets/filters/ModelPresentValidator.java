package edu.osu.lapis.restlets.filters; //TODO MOVE TO FILTERS PACKAGE

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.routing.Filter;

import edu.osu.lapis.communicator.rest.Attributes;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;

public class ModelPresentValidator extends Filter {

	private NetworkTable networkTable;
	
	public ModelPresentValidator() {
		//default
	}
	
	public ModelPresentValidator(NetworkTable networkTable) {
		this.networkTable = networkTable;
	}
	
	@Override
	protected int beforeHandle(Request request, Response response) {
		String modelName = Attributes.getModelName(request);
		assert modelName != null;
		LapisNode localNode = networkTable.getNode(modelName);
		if(localNode ==  null) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			response.setEntity("The specified LAPIS node, " + modelName 
					+ ", is not present in this node's network table.", MediaType.TEXT_PLAIN);
			return SKIP;
		} else {
			return CONTINUE;
		}
	}

	public void setNetworkTable(NetworkTable networkTable) {
		this.networkTable = networkTable;
	}
}