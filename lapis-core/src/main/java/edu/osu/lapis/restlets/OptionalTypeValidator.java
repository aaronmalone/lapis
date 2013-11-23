package edu.osu.lapis.restlets;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.routing.Filter;

import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LapisVariable2;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.util.Attributes;

public class OptionalTypeValidator extends Filter {
	
	private LocalDataTable localDataTable;
	
	public OptionalTypeValidator(LocalDataTable localDataTable) {
		this.localDataTable = localDataTable;
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		String typeFromQuery = getTypeFromQuery(request);
		if(typeFromQuery == null) {
			return CONTINUE;
		} else {
			return validateTypeFromQuery(request, response, typeFromQuery);
		}
	}
	
	private String getTypeFromQuery(Request request) {
		Reference reference = request.getResourceRef();
		Form queryForm = reference.getQueryAsForm();
		Parameter typeParameter = queryForm.getFirst("type");
		if(typeParameter != null)
			return typeParameter.getValue();
		else 
			return null;
	}

	//TODO MAYBE REFACTOR THE FOLLOWING METHOD
	private int validateTypeFromQuery(Request request, Response response, String typeStr) {
		LapisDataType expectedType = null;
		try {
			String upperCaseTypeStr = typeStr.toUpperCase(); //because LapisDataType values are enum constants
			expectedType = LapisDataType.valueOf(upperCaseTypeStr);
		} catch(IllegalArgumentException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			response.setEntity("\"" + typeStr + "\" is not a valid LAPIS data type.", MediaType.TEXT_PLAIN);
		}
		LapisDataType localVariableType = getTypeOfLocalVariable(request);
		if(expectedType == localVariableType) {
			return CONTINUE;
		} else {
			String entity = "Expected data type \"" + expectedType 
					+ "\" does not match actual data type of local variable, \"" 
					+ localVariableType + "\".";
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			response.setEntity(entity, MediaType.TEXT_PLAIN);
			return SKIP;
		}
	}
	
	private LapisDataType getTypeOfLocalVariable(Request request) {
		String variableName = Attributes.getModelName(request);
		LapisVariable2 localVariable = localDataTable.get(variableName);
		return localVariable.getLapisDataType();
	}
}
