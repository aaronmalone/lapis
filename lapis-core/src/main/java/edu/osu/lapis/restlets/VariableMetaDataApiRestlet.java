package edu.osu.lapis.restlets;

import com.google.common.collect.Lists;
import edu.osu.lapis.Logger;
import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.util.Attributes;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.ByteArrayRepresentation;

import java.util.List;

public class VariableMetaDataApiRestlet extends LapisRestletBase {

	private final Logger logger = Logger.getLogger(getClass());

	private LocalDataTable localDataTable;
	private LapisSerialization lapisSerialization;
	private MediaType responseMediaType;

	@Override
	public void get(Request request, Response response) {
		String variableName = Attributes.getVariableName(request);
		if (variableName == null) {
			respondWithMetaDataForAllVariables(response);
		} else {
			respondWithMetaDataForOneVariable(response, variableName);
		}
	}

	private void respondWithMetaDataForOneVariable(Response response, String variableName) {
		logger.debug("Call to retrieve variable meta-data for variable: %s", variableName);
		LapisVariable localVariable = localDataTable.get(variableName);
		if (localVariable != null) {
			byte[] serialized = lapisSerialization.serialize(getVariableMetaData(localVariable));
			response.setEntity(new ByteArrayRepresentation(serialized, responseMediaType));
		} else {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			response.setEntity(String.format("The variable \"%s\" has not been published by this node.", variableName), MediaType.TEXT_PLAIN);
		}
	}

	private void respondWithMetaDataForAllVariables(Response response) {
		logger.debug("Call to retrieve variable meta-data for all variables");
		List<VariableMetaData> metaList = Lists.newArrayList();
		for (LapisVariable local : localDataTable.getAll()) {
			metaList.add(getVariableMetaData(local));
		}
		byte[] serialized = lapisSerialization.serialize(metaList);
		response.setEntity(new ByteArrayRepresentation(serialized, responseMediaType));
		response.setStatus(Status.SUCCESS_OK);
	}

	private VariableMetaData getVariableMetaData(LapisVariable var) {
		try {
			Class<?> dataClass = var.getCallable().call().getClass();
			return new VariableMetaData(var.getName(), dataClass, var.isReadOnly());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setLocalDataTable(LocalDataTable localDataTable) {
		this.localDataTable = localDataTable;
	}

	public void setLapisSerialization(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}

	public void setResponseMediaType(MediaType responseMediaType) {
		this.responseMediaType = responseMediaType;
	}
}
