package edu.osu.lapis.restlets.filters;

import org.restlet.routing.Validator;

import edu.osu.lapis.communicator.rest.Attributes;

public class VariableNameAttrValidator extends Validator {
	public VariableNameAttrValidator() {
		this.validatePresence(Attributes.VARIABLE_NAME_ATTRIBUTE);
	}
}
