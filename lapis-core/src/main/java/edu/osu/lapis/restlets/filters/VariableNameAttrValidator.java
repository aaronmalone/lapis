package edu.osu.lapis.restlets.filters;

import edu.osu.lapis.util.Attributes;

public class VariableNameAttrValidator extends SaneSkipValidator {
	public VariableNameAttrValidator() {
		this.validatePresence(Attributes.VARIABLE_NAME_ATTRIBUTE);
	}
}
