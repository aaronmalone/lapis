package edu.osu.lapis;

import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.osu.lapis.communication.DataClientCommunicationImpl;
import edu.osu.lapis.communication.NetworkClientCommunicationImpl;
import edu.osu.lapis.communicator.rest.Attributes;
import edu.osu.lapis.communicator.rest.Notifier;
import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.restlets.CoordinatorRestlet;
import edu.osu.lapis.restlets.HeartbeatRestlet;
import edu.osu.lapis.restlets.NetworkRestlet;
import edu.osu.lapis.restlets.RestletServer;
import edu.osu.lapis.restlets.VariableMetaDataApiRestlet;
import edu.osu.lapis.restlets.VariableValueApiRestlet;
import edu.osu.lapis.serialization.JsonSerialization;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.transmission.LapisDataTransmission;
import edu.osu.lapis.transmission.LapisNetworkTransmission;

/**
 * Java-based Spring context for wiring LAPIS together.
 *
 */
@Configuration 
public class LapisConfiguration {
	
	//TODO ADD LOGGING SO THAT YOU CAN SEE WHICH METHODS ARE CALLED AND WHEN
	
	private @Value("${coordinator.url}") String coordinatorUrl;
	private @Value("${port}") int port;
	private @Value("${isCoordinator}") boolean isCoordinator;
	
	public void setCoordinatorUrl(String coordinatorUrl) {
		this.coordinatorUrl = coordinatorUrl;
	}

	@Bean
	public LapisDataClient lapisClient() {
		LapisDataClient lapisDataClient = new LapisDataClient();
		lapisDataClient.setGlobalDataTable(globalDataTable());
		lapisDataClient.setRenameMe(getDataClientCommunicationImpl()); //TODO RENAME
		return lapisDataClient;
	}
	
	private DataClientCommunicationImpl getDataClientCommunicationImpl() { //TODO RENAME
		DataClientCommunicationImpl impl = new DataClientCommunicationImpl();
		impl.setLapisSerialization(lapisSerialization());
		impl.setLapisDataTransmission(getLapisDataTransmission());
		return impl;
	}

	private LapisDataTransmission getLapisDataTransmission() {
		LapisDataTransmission lapisDataTransmission = new LapisDataTransmission();
		lapisDataTransmission.setLapisNetworkClient(getLapisNetworkClient());
		lapisDataTransmission.setSerializationMediaType(serializationMediaType());
		lapisDataTransmission.setVariableMetaDataPath("metadata");
		lapisDataTransmission.setVariableValuePath("model");
		return lapisDataTransmission;
	}

	private LapisNetworkClient getLapisNetworkClient() {
		LapisNetworkClient netClient = new LapisNetworkClient(); //TODO MAYBE RENAME
		netClient.setNetworkTable(networkTable());
		netClient.setRenameMe(getNetworkClientCommunicationImpl()); //TODO RENAME
		return netClient;
	}

	private NetworkClientCommunicationImpl getNetworkClientCommunicationImpl() { //TODO RENAME
		NetworkClientCommunicationImpl impl = new NetworkClientCommunicationImpl();
		impl.setLapisNetworkTransmission(getLapisNetworkTransmission());
		impl.setLapisSerialization(lapisSerialization());
		return impl;
	}

	private LapisNetworkTransmission getLapisNetworkTransmission() {
		LapisNetworkTransmission netTrans = new LapisNetworkTransmission();
		netTrans.setSerializationMediaType(serializationMediaType());
		netTrans.setCoordinatorBaseUrl(coordinatorUrl);
		return netTrans;
	}

	@Bean
	public LocalDataTable localDataTable() {
		return new LocalDataTable();
	}

	@Bean 
	public RestletServer getRestletServer() {
		RestletServer restletServer = new RestletServer();
		restletServer.setPort(port);
		
		Restlet networkRestlet = getNetworkRestlet();
		restletServer.attachRestlet("/network/{" + Attributes.MODEL_NAME_ATTRIBUTE + '}', networkRestlet);
		restletServer.attachRestlet("/network", networkRestlet);
		
		VariableMetaDataApiRestlet variableMetaDataApiRestlet = getVariableMetaDataApiRestlet();
		restletServer.attachRestlet("/metadata/{" + Attributes.VARIABLE_NAME_ATTRIBUTE + '}',variableMetaDataApiRestlet);
		restletServer.attachRestlet("/metadata", variableMetaDataApiRestlet);
		
		Restlet variableValueApiRestlet = getVariableValueApiRestlet();
		restletServer.attachRestlet("/model/{" + Attributes.VARIABLE_NAME_ATTRIBUTE + '}', variableValueApiRestlet);
		
		restletServer.attachRestlet("/heartbeat", new HeartbeatRestlet());
		
		if(isCoordinator) {
			setUpCoordinatorRestlet(restletServer);
		}
		
		return restletServer;
	}
	
	private void setUpCoordinatorRestlet(RestletServer restletServer) {
		Restlet coordinatorRestlet = getCoordinatorRestlet();
		restletServer.attachRestlet("/coordinator/{" + Attributes.MODEL_NAME_ATTRIBUTE + '}', coordinatorRestlet);
		restletServer.attachRestlet("/coordinator", coordinatorRestlet);
	}
	
	private Restlet getCoordinatorRestlet() {
		CoordinatorRestlet coordinatorRestlet = new CoordinatorRestlet();
		coordinatorRestlet.setLapisSerialization(lapisSerialization());
		coordinatorRestlet.setNetworkTable(networkTable());
		coordinatorRestlet.setResponseMediaType(serializationMediaType());
		coordinatorRestlet.setNotifier(getNotifier());
		return coordinatorRestlet.getCoordinatorRestletWithFilters();
	}

	private Notifier getNotifier() {
		Notifier notifier = new Notifier();
		notifier.setLapisSerialization(lapisSerialization());
		notifier.setMediaType(serializationMediaType());
		notifier.setNetworkTable(networkTable());
		return notifier;
	}

	private Restlet getVariableValueApiRestlet() {
		VariableValueApiRestlet variableValueApiRestlet = new VariableValueApiRestlet();
		variableValueApiRestlet.setLapisSerialization(lapisSerialization());
		variableValueApiRestlet.setLocalDataTable(localDataTable());
		variableValueApiRestlet.setResponseMediaType(serializationMediaType());
		return variableValueApiRestlet.getVariableValueRestletWithFilters();
	}
	
	public Restlet getNetworkRestlet() {
		NetworkRestlet networkRestlet = new NetworkRestlet();
		networkRestlet.setLapisSerialization(lapisSerialization());
		networkRestlet.setNetworkTable(networkTable());
		networkRestlet.setResponseMediaType(serializationMediaType());
		return networkRestlet.getNetworkRestletWithFilters();
	}
	
	public VariableMetaDataApiRestlet getVariableMetaDataApiRestlet() {
		VariableMetaDataApiRestlet variableMetaDataApiRestlet = new VariableMetaDataApiRestlet();
		variableMetaDataApiRestlet.setLocalDataTable(localDataTable());
		variableMetaDataApiRestlet.setLapisSerialization(lapisSerialization());
		variableMetaDataApiRestlet.setResponseMediaType(serializationMediaType());
		return variableMetaDataApiRestlet;
	}
	
	@Bean
	public NetworkTable networkTable() {
		NetworkTable nt = new NetworkTable();
		nt.setLocalNode(getLocalNode());
		return nt;
	}

	@Bean
	public MediaType serializationMediaType() {
		return MediaType.APPLICATION_JSON;
	}
	
	@Bean 
	public LapisSerialization lapisSerialization() {
		JsonSerialization json = new JsonSerialization();
		json.setPrettyPrinting(true);
		return json;
	}
	
	@Bean
	public LapisNode getLocalNode() {
		return new LapisNode("me", "localhost:8888");
	}

	private GlobalDataTable globalDataTable() {
		return new GlobalDataTable();
	}
}