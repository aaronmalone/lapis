package edu.osu.lapis.restlets.filters;

import edu.osu.lapis.util.Attributes;

public class ModelNameAttrValidator extends SaneSkipValidator {
	
	public ModelNameAttrValidator() {
		this.validatePresence(Attributes.MODEL_NAME_ATTRIBUTE);
	}
}
