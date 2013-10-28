package edu.osu.lapis.communicator.rest;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.routing.Extractor;
import org.restlet.routing.Filter;
import org.restlet.routing.Validator;

import edu.osu.lapis.Constants;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.LocalDataTable;

public class VariableRestletUtils {
	
	public static Validator getVariableNamePresentValidator() {
		Validator validator = new SaneSkipValidator();
		validator.validatePresence(Constants.VARIABLE_NAME_ATTRIBUTE);
		return validator;
	}
	
	public static Filter getVariablePresentFilter(final LocalDataTable localDataTable) {
		return new Filter() {
			@Override protected int beforeHandle(Request request, Response response) {
				String variableName = getVariableName(request);
				LapisVariable lapisVariable = localDataTable.get(variableName);
				if(lapisVariable != null) {
					return CONTINUE;
				} else {
					response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Variable \""
							+ variableName + "\" not found.");
					return SKIP;
				}
			}
		};
	}
	
	public static Filter getTypeMatchesFilter(final LocalDataTable localDataTable) {
		//TODO EXTRACT THIS MONSTER OUT TO A PROPER CLASS
		return new Filter() {
			@Override protected int beforeHandle(Request request, Response response) {
				Extractor extractor = new Extractor(getContext());
				extractor.extractFromQuery(Constants.TYPE_ATTRIBUTE, "type", true);
				extractor.handle(request, response);
				String expectedTypeName = (String) request.getAttributes().get(Constants.TYPE_ATTRIBUTE);
				if(expectedTypeName != null) {
					LapisDataType expectedType = LapisDataType.valueOf(expectedTypeName);
					if(expectedType == null) {
						response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "LAPIS does not support data type \"" 
								+ expectedTypeName + "\".");
						return SKIP;
					}
					String variableName = getVariableName(request);
					assert variableName != null; //should be called after variable name validator
					LapisVariable lapisVariable = localDataTable.get(variableName);
					assert lapisVariable != null; //should be called after variable present filter
					LapisDataType actualType = lapisVariable.getVariableMetaData().getType();
					if(expectedType == actualType) {
						return CONTINUE;
					} else {
						response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, 
								"The requested type " + expectedType + " did not match the actual type"
								+ actualType + ".");
						return SKIP;
					}
				} else {
					return CONTINUE;
				}
			}
		};
	}
	
	public static String getVariableName(Request request) {
		return (String) request.getAttributes().get(Constants.VARIABLE_NAME_ATTRIBUTE);
	}
}
