package edu.osu.lapis.restlets.filters;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.routing.Filter;

import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.util.Attributes;

public class VariablePresentValidator extends Filter {

	private LocalDataTable localDataTable;
	
	public VariablePresentValidator(LocalDataTable localDataTable) {
		this.localDataTable = localDataTable;
	}
	
	@Override
	protected int beforeHandle(Request request, Response response) {
		//TODO CONSIDER MAKING ATTRIBUTES UTILITY METHOD FOR BELOW CALL
		String variableName = Attributes.getVariableName(request);
		LapisVariable localVariable = localDataTable.get(variableName);
		if(localVariable == null) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			response.setEntity("TODO write sane message here.", MediaType.TEXT_PLAIN);
			return SKIP;
		} else {
			return CONTINUE;
		}
	}	
}
