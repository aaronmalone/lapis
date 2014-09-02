package edu.osu.lapis.restlets;

import edu.osu.lapis.Logger;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.restlets.filters.LapisNodeExtractor;
import edu.osu.lapis.restlets.filters.ModelNameAttrValidator;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.util.Attributes;
import org.apache.commons.lang3.Validate;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.ByteArrayRepresentation;

/**
 * Restlet to handle calls to network/* API for LAPIS nodes. The principal
 * client of this API is the coordinator of the LAPIS network. The coordinator
 * uses this API to keep LAPIS nodes up to date on the location of nodes on the
 * network.
 */
public class NetworkRestlet extends LapisRestletBase {

	private final Logger logger = Logger.getLogger(getClass());

	private LapisSerialization lapisSerialization;
	private MediaType responseMediaType;
	private LapisNode localNode;

	public Restlet getNetworkRestletWithFilters() {
		LapisFilterChainRestletBase filterChainRestlet = new LapisFilterChainRestletBase();

		filterChainRestlet.setPutFilters(
				new ModelNameAttrValidator(),
				new LapisNodeExtractor(lapisSerialization));
		filterChainRestlet.setPutTargetRestlet(this);

		filterChainRestlet.setPostFilters(
				new ModelNameAttrValidator(),
				new LapisNodeExtractor(lapisSerialization));
		filterChainRestlet.setPostTargetRestlet(this);

		filterChainRestlet.setDeleteFilters(
				new ModelNameAttrValidator());
				filterChainRestlet.setDeleteTargetRestlet(this);

		filterChainRestlet.setGetTargetRestlet(this);

		return filterChainRestlet;
	}

	//TODO SEE IF WE STILL NEED THESE
/*	@Override
	public void delete(Request request, Response response) {
		String nodeName = Attributes.getModelName(request);
		logger.info("Received request for delete of node: %s", nodeName);
		LapisNode node = lapisNetwork.getNode(nodeName);
		lapisNetwork.removeNode(nodeName);
		networkChangeHandler.onNodeDelete(node);
	}*/

	/*@Override
	public void put(Request request, Response response) {
		LapisNode node = Attributes.getAttribute(request, DESERIALIZED_NODE_ATTR, LapisNode.class);
		logger.info("Received request to add new node: %s", node);
		lapisNetwork.addNode(node);
		networkChangeHandler.onNodeAdd(node);
	}*/

	@Override
	public void get(Request request, Response response) {
		String modelName = Attributes.getModelName(request);
		if (modelName == null) {
			logger.info("Received request for this node's information.");
			Validate.notNull(localNode);
			byte[] serialized = lapisSerialization.serialize(localNode);
			response.setEntity(new ByteArrayRepresentation(serialized, responseMediaType));
		} else {
			logger.warn(String.format("GET network/%s called on this node.", modelName));
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			response.setEntity("GET network/{modelName} should not be called on a non-coordinator node.", MediaType.TEXT_PLAIN);
		}
	}

	public void setLapisSerialization(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}

	public void setResponseMediaType(MediaType responseMediaType) {
		this.responseMediaType = responseMediaType;
	}

	public void setLocalNode(LapisNode localNode) {
		this.localNode = localNode;
	}
}
