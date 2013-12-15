package edu.osu.lapis.restlets.filters;

import static edu.osu.lapis.data.LapisPermission.READ_WRITE;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.routing.Filter;

import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.util.Attributes;

public class NotReadOnlyValidator extends Filter {
	
	final private LocalDataTable localDataTable;
	
	public NotReadOnlyValidator(LocalDataTable localDataTable) {
		this.localDataTable = localDataTable;
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		String variableName = Attributes.getVariableName(request);
		assert variableName != null;
		LapisVariable var = localDataTable.get(variableName);
		assert var != null;
		if(var.getLapisPermission() == READ_WRITE) {
			return CONTINUE;
		} else {
			String msg = "The published variable '" + variableName + "' is not writable from other nodes.";
			response.setStatus(Status.CLIENT_ERROR_FORBIDDEN, msg);
			response.setEntity(msg, MediaType.TEXT_PLAIN);
			return SKIP;
		}
	}

	
}
