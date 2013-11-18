package edu.osu.lapis;

import javax.annotation.PostConstruct;

import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.osu.lapis.communication.DataClientCommunicationImpl;
import edu.osu.lapis.communication.NetworkClientCommunicationImpl;
import edu.osu.lapis.communication.Notifier;
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
import edu.osu.lapis.restlets.filters.CoordinatorNetworkApiFilter;
import edu.osu.lapis.serialization.JsonSerialization;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.transmission.LapisDataTransmission;
import edu.osu.lapis.transmission.LapisNetworkTransmission;
import edu.osu.lapis.transmission.LapisTransmission;
import edu.osu.lapis.util.Attributes;

/**
 * Java-based Spring context for wiring LAPIS together.
 *
 */
@Configuration 
public class LapisConfiguration {

	//properties to set
	/** the name of this LAPIS node */
	private @Value("${name}") String name;
	/** the URL for the coordinator node */
	private @Value("${coordinator.url}") String coordinatorUrl;
	/** the port on which this node will listen */
	private @Value("${port}") int port;
	/** true if this node is the coordinator on its network */
	private @Value("${isCoordinator}") boolean isCoordinator;
	/** the URL for this LAPIS node */
	private @Value("${node.url:http://localhost}") String nodeUrl;
	//TODO THERE MIGHT BE A BETTER WAY TO DO THIS
	
	@Bean
	public LapisDataClient lapisDataClient() {
		LapisDataClient lapisDataClient = new LapisDataClient();
		lapisDataClient.setGlobalDataTable(globalDataTable());
		lapisDataClient.setDataClientCommunicationImpl(getDataClientCommunicationImpl());
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
		lapisDataTransmission.setVariableMetaDataPath("metadata");
		lapisDataTransmission.setVariableValuePath("model");
		lapisDataTransmission.setLapisTransmission(new LapisTransmission());
		return lapisDataTransmission;
	}

	private LapisNetworkClient getLapisNetworkClient() {
		LapisNetworkClient netClient = new LapisNetworkClient(); //TODO MAYBE RENAME
		netClient.setNetworkTable(networkTable());
		netClient.setNetworkClientCommunicationImpl(networkClientCommunicationImpl()); //TODO RENAME
		return netClient;
	}

	@Bean
	public NetworkClientCommunicationImpl networkClientCommunicationImpl() { //TODO RENAME
		NetworkClientCommunicationImpl impl = new NetworkClientCommunicationImpl();
		impl.setLapisNetworkTransmission(getLapisNetworkTransmission());
		impl.setLapisSerialization(lapisSerialization());
		return impl;
	}

	private LapisNetworkTransmission getLapisNetworkTransmission() {
		if(isCoordinator) {
			return null; 
		} else {
			LapisNetworkTransmission netTrans = new LapisNetworkTransmission();
			netTrans.setCoordinatorBaseUrl(coordinatorUrl);
			netTrans.setLapisTransmission(new LapisTransmission()); //TODO MAYBE CREATE A BEAN
			return netTrans;
		}
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
	
	public Restlet getNetworkRestlet() {
		//TODO CLEAN UP AT SOME POINT
		NetworkRestlet networkRestlet = new NetworkRestlet();
		networkRestlet.setLapisSerialization(lapisSerialization());
		networkRestlet.setNetworkTable(networkTable());
		networkRestlet.setResponseMediaType(serializationMediaType());
		Restlet withFilters = networkRestlet.getNetworkRestletWithFilters();
		if(isCoordinator) {
			CoordinatorNetworkApiFilter coordinatorNetworkApiFilter = new CoordinatorNetworkApiFilter();
			coordinatorNetworkApiFilter.setNext(withFilters);
			return coordinatorNetworkApiFilter;
		} else {
			return withFilters;
		}
	}
	
	public VariableMetaDataApiRestlet getVariableMetaDataApiRestlet() {
		VariableMetaDataApiRestlet variableMetaDataApiRestlet = new VariableMetaDataApiRestlet();
		variableMetaDataApiRestlet.setLocalDataTable(localDataTable());
		variableMetaDataApiRestlet.setLapisSerialization(lapisSerialization());
		variableMetaDataApiRestlet.setResponseMediaType(serializationMediaType());
		return variableMetaDataApiRestlet;
	}
	
	private Restlet getVariableValueApiRestlet() {
		VariableValueApiRestlet variableValueApiRestlet = new VariableValueApiRestlet();
		variableValueApiRestlet.setLapisSerialization(lapisSerialization());
		variableValueApiRestlet.setLocalDataTable(localDataTable());
		variableValueApiRestlet.setResponseMediaType(serializationMediaType());
		return variableValueApiRestlet.getVariableValueRestletWithFilters();
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
		notifier.setNetworkTable(networkTable());
		notifier.setLapisTransmission(new LapisTransmission());
		return notifier;
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
		return new LapisNode(name, "http://localhost:" + port); //TODO fix -- get the IP address to use
	}
	
	@PostConstruct
	public void postConstruct() {
		if(!isCoordinator) {
			networkClientCommunicationImpl().addNodeToNetwork(getLocalNode());
		} else {
			//nothing to do
		}
	}

	@Bean
	public GlobalDataTable globalDataTable() {
		return new GlobalDataTable();
	}
}