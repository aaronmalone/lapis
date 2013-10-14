package edu.osu.lapis.communicator;

import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.serialize.LapisDatum;

public interface LapisCommunication {
	public VariableMetaData getVariableMetaData(VariableFullName fullName);
	public LapisDatum getVariableValue(VariableFullName fullName);
}
