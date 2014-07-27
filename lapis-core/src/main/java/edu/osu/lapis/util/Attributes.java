package edu.osu.lapis.util;

import org.restlet.Message;

public class Attributes {

	public static final String
			MODEL_NAME_ATTRIBUTE = "modelName",
			VARIABLE_NAME_ATTRIBUTE = "variableName";

	public static String getModelName(Message message) {
		return getAttribute(message, MODEL_NAME_ATTRIBUTE, String.class);
	}

	public static String getVariableName(Message message) {
		return getAttribute(message, VARIABLE_NAME_ATTRIBUTE, String.class);
	}

	public static <T> T getAttribute(Message message, String attrName, Class<T> cls) {
		Object val = message.getAttributes().get(attrName);
		if (val != null) {
			return cls.cast(val);
		} else {
			return null;
		}
	}
}
