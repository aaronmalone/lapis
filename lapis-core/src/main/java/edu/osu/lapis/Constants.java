package edu.osu.lapis;

public class Constants {

	//TODO MOVE TO SimulationFunction
	public static final String READY_VARIABLE_NAME = "~READY~FLAG~";

	public static class Properties {
		public static final String
				NAME = "name",
				COORDINATOR_URL = "coordinator.url",
				IS_COORDINATOR = "isCoordinator",
				LOCAL_NODE_ADDRESS = "localNodeAddress";
	}

	public static class SimulationFunction {
		public static final String
				READY_TO_CALCULATE_VAR_NAME = "readyToCalculate",
				FINISHED_CALCULATING_VAR_NAME = "finishedCalculating";
	}
}
