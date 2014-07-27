package edu.osu.lapis.restlets.filters;

import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.util.Attributes;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.routing.Filter;

public class VariablePresentValidator extends Filter {

	private LocalDataTable localDataTable;

	public VariablePresentValidator(LocalDataTable localDataTable) {
		this.localDataTable = localDataTable;
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		String variableName = Attributes.getVariableName(request);
		LapisVariable localVariable = localDataTable.get(variableName);
		if (localVariable == null) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			response.setEntity("The variable '" + variableName + "' has not been published by this node.",
					MediaType.TEXT_PLAIN);
			return SKIP;
		} else {
			return CONTINUE;
		}
	}
}
