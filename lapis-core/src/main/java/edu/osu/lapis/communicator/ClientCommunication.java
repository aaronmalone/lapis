package edu.osu.lapis.communicator;

import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.serialize.SerializationObject;

public interface ClientCommunication {
	public VariableMetaData getVariableMetaData(VariableFullName fullName);
	public SerializationObject getVariableValue(VariableFullName fullName);
}
