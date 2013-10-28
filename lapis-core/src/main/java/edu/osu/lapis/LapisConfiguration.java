package edu.osu.lapis;

import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.osu.lapis.communicator.rest.NetworkRestlet;
import edu.osu.lapis.communicator.rest.RestletServer;
import edu.osu.lapis.communicator.rest.VariableMetaDataApiRestlet;
import edu.osu.lapis.communicator.rest.VariableValueApiRestlet;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialization.JsonSerialization;
import edu.osu.lapis.serialization.LapisSerialization;

/**
 * Java-based Spring context for wiring LAPIS together.
 *
 */
@Configuration public class LapisConfiguration {
	
	@Bean
	public LapisDataClient getLapisClient() {
		LapisDataClient lapisDataClient = new LapisDataClient();
		return lapisDataClient;
	}
	
	@Bean
	public LocalDataTable getLocalDataTable() {
		return new LocalDataTable();
	}

	@Bean 
	public RestletServer getRestletServer() {
		NetworkRestlet networkRestlet = getNetworkRestlet();
		Restlet restletWithFilters = networkRestlet.getNetworkRestletWithFilters();
		RestletServer restletServer = new RestletServer();
		restletServer.setPort(8888);
		restletServer.attachRestlet(
				"/network/{" + Constants.MODEL_NAME_ATTRIBUTE + '}', 
				restletWithFilters);
		restletServer.attachRestlet("/network", restletWithFilters);
		
		VariableMetaDataApiRestlet variableMetaDataApiRestlet = new VariableMetaDataApiRestlet();
		variableMetaDataApiRestlet.setLocalDataTable(getLocalDataTable());
		variableMetaDataApiRestlet.setLapisSerialization(getLapisSerialization());
		variableMetaDataApiRestlet.setResponseMediaType(MediaType.APPLICATION_JSON);
		
		restletServer.attachRestlet(
				"/metadata/{" + Constants.VARIABLE_NAME_ATTRIBUTE + '}', //better way to do this? buildURi?
				variableMetaDataApiRestlet);
		restletServer.attachRestlet("/metadata", variableMetaDataApiRestlet);
		
		VariableValueApiRestlet variableValueApiRestlet = new VariableValueApiRestlet();
		variableValueApiRestlet.setLapisSerialization(getLapisSerialization());
		variableValueApiRestlet.setLocalDataTable(getLocalDataTable());
		variableValueApiRestlet.setResponseMediaType(MediaType.APPLICATION_JSON);
		Restlet variableValueWithFilters = variableValueApiRestlet.getVariableValueRestletWithFilters();
		
		restletServer.attachRestlet(
				"/model/{" + Constants.VARIABLE_NAME_ATTRIBUTE + '}', variableValueWithFilters);
		return restletServer;
	}
	
	@Bean
	public NetworkRestlet getNetworkRestlet() {
		NetworkRestlet networkRestlet = new NetworkRestlet();
		networkRestlet.setLapisSerialization(getLapisSerialization());
		networkRestlet.setNetworkTable(getNetworkTable());
		networkRestlet.setResponseMediaType(MediaType.APPLICATION_JSON);
		return networkRestlet;
	}
	
	@Bean
	public NetworkTable getNetworkTable() {
		NetworkTable nt = new NetworkTable();
		nt.setLocalNode(getLocalNode());
		return nt;
	}

	@Bean 
	public LapisSerialization getLapisSerialization() {
		JsonSerialization json = new JsonSerialization();
		json.setPrettyPrinting(true);
		return json;
	}
	
	@Bean
	public LapisNode getLocalNode() {
		return new LapisNode("me", "localhost:8888");
	}
}