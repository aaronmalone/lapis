package edu.osu.lapis.restlets.filters;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.routing.Filter;

import edu.osu.lapis.data.LapisVariable2;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.util.Attributes;

public class VariablePresentValidator extends Filter {

	private LocalDataTable localDataTable;
	
	public VariablePresentValidator(LocalDataTable localDataTable) {
		this.localDataTable = localDataTable;
	}
	
	@Override
	protected int beforeHandle(Request request, Response response) {
		String variableName = Attributes.getVariableName(request);
		LapisVariable2 localVariable = localDataTable.get(variableName);
		if(localVariable == null) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			response.setEntity("Variable '" + variableName + "' has not been published by this node.", 
					MediaType.TEXT_PLAIN);
			return SKIP;
		} else {
			return CONTINUE;
		}
	}	
}
