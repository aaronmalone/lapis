package edu.osu.lapis.restlets;

import edu.osu.lapis.Logger;
import edu.osu.lapis.network.CoordinatorLapisNetwork;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkChangeHandler;
import edu.osu.lapis.restlets.filters.LapisNodeExtractor;
import edu.osu.lapis.restlets.filters.ModelNameAttrValidator;
import edu.osu.lapis.restlets.filters.ModelPresentValidator;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.util.Attributes;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.ByteArrayRepresentation;

import java.util.List;

import static edu.osu.lapis.restlets.filters.LapisNodeExtractor.DESERIALIZED_LAPIS_NODE_ATTR;

public class CoordinatorRestlet extends LapisRestletBase {

	private final Logger logger = Logger.getLogger(getClass());

	private final LapisSerialization lapisSerialization;
	private final CoordinatorLapisNetwork lapisNetwork;
	private final MediaType responseMediaType;
	private final NetworkChangeHandler networkChangeHandler;

	public CoordinatorRestlet(
			LapisSerialization lapisSerialization,
			CoordinatorLapisNetwork lapisNetwork,
			MediaType responseMediaType,
			NetworkChangeHandler networkChangeHandler) {
		this.lapisSerialization = lapisSerialization;
		this.lapisNetwork = lapisNetwork;
		this.responseMediaType = responseMediaType;
		this.networkChangeHandler = networkChangeHandler;
	}

	public Restlet getCoordinatorRestletWithFilters() {
		LapisFilterChainRestletBase filterChain = new LapisFilterChainRestletBase();

		filterChain.setPutFilters(
				new ModelNameAttrValidator(),
				new LapisNodeExtractor(lapisSerialization));
		filterChain.setPutTargetRestlet(this);

		filterChain.setPostFilters(
				new ModelNameAttrValidator(),
				new ModelPresentValidator(lapisNetwork),
				new LapisNodeExtractor(lapisSerialization));
		filterChain.setPostTargetRestlet(this);

		filterChain.setDeleteFilters(new ModelNameAttrValidator());
		filterChain.setDeleteTargetRestlet(this);

		filterChain.setGetTargetRestlet(this);

		return filterChain;
	}

	@Override
	public void delete(Request request, Response response) {
		String modelName = Attributes.getModelName(request);
		LapisNode removed = lapisNetwork.getNode(modelName);
		lapisNetwork.removeNode(modelName);
//		notifier.notifyNetworkOfDelete(removed);
		networkChangeHandler.onNodeDelete(removed);
	}

	@Override
	public void put(Request request, Response response) {
		LapisNode newNode = Attributes.getAttribute(request, DESERIALIZED_LAPIS_NODE_ATTR, LapisNode.class);
		logger.info("New node on network: " + newNode);
		lapisNetwork.addNode(newNode);
//		notifier.notifyNetworkOfNewNode(newNode);
		networkChangeHandler.onNodeAdd(newNode);
	}

	@Override
	public void get(Request request, Response response) {
		if (Attributes.getModelName(request) == null) {
			handleAllNodes(request, response);
		} else {
			handleSingleNode(request, response);
		}
	}

	private void handleAllNodes(Request request, Response response) {
		List<LapisNode> nodes = lapisNetwork.allNodes();
		nodes.add(lapisNetwork.getLocalNode());
		LapisNode[] nodeArray = nodes.toArray(new LapisNode[nodes.size()]);
		byte[] serialized = lapisSerialization.serialize(nodeArray);
		response.setEntity(new ByteArrayRepresentation(serialized, responseMediaType));
	}

	private void handleSingleNode(Request request, Response response) {
		String nodeName = Attributes.getModelName(request);
		LapisNode node = lapisNetwork.getNode(nodeName);
		if (node != null) {
			byte[] serialized = lapisSerialization.serialize(node);
			response.setEntity(new ByteArrayRepresentation(serialized, responseMediaType));
		} else {
			String msg = "The specified LAPIS node, \"" + nodeName + "\", is not present in the coordinator's network table.";
			logger.warn(msg);
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, msg);
			response.setEntity(msg, MediaType.TEXT_PLAIN);
		}
	}
}
