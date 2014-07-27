package edu.osu.lapis.restlets.filters;

import edu.osu.lapis.network.LapisNetwork;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.util.Attributes;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.routing.Filter;

public class ModelPresentValidator extends Filter {

	private final LapisNetwork lapisNetwork;

	public ModelPresentValidator(LapisNetwork lapisNetwork) {
		this.lapisNetwork = lapisNetwork;
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		String modelName = Attributes.getModelName(request);
		assert modelName != null;
		LapisNode localNode = lapisNetwork.getNode(modelName);
		if (localNode == null) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			response.setEntity("The specified LAPIS node, " + modelName
					+ ", is not present in this node's network table.", MediaType.TEXT_PLAIN);
			return SKIP;
		} else {
			return CONTINUE;
		}
	}
}
