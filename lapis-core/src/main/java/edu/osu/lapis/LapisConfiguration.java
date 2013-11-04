package edu.osu.lapis;

import javax.annotation.PostConstruct;

import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import edu.osu.lapis.restlets.filters.CoordinatorNetworkApiFilter;
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

	private static final Logger log = LoggerFactory.getLogger(LapisConfiguration.class);
	
	private @Value("${name}") String name;
	private @Value("${coordinator.url}") String coordinatorUrl;
	private @Value("${port}") int port;
	private @Value("${isCoordinator}") boolean isCoordinator;
	
	public void setCoordinatorUrl(String coordinatorUrl) {
		this.coordinatorUrl = coordinatorUrl;
	}

	@Bean
	public LapisDataClient lapisClient() {
		log.trace("lapisClient() called."); 
		LapisDataClient lapisDataClient = new LapisDataClient();
		lapisDataClient.setGlobalDataTable(getGlobalDataTable());
		lapisDataClient.setRenameMe(getDataClientCommunicationImpl()); //TODO RENAME
		return lapisDataClient;
	}
	
	private DataClientCommunicationImpl getDataClientCommunicationImpl() { //TODO RENAME
		log.trace("getDataClientCommunicationImpl() called."); //TODO RENAME
		DataClientCommunicationImpl impl = new DataClientCommunicationImpl();
		impl.setLapisSerialization(lapisSerialization());
		impl.setLapisDataTransmission(getLapisDataTransmission());
		return impl;
	}

	private LapisDataTransmission getLapisDataTransmission() {
		log.trace("getLapisDataTransmission() called");
		LapisDataTransmission lapisDataTransmission = new LapisDataTransmission();
		lapisDataTransmission.setLapisNetworkClient(getLapisNetworkClient());
		lapisDataTransmission.setSerializationMediaType(serializationMediaType());
		lapisDataTransmission.setVariableMetaDataPath("metadata");
		lapisDataTransmission.setVariableValuePath("model");
		return lapisDataTransmission;
	}

	private LapisNetworkClient getLapisNetworkClient() {
		log.trace("getLapisNetworkClient() called.");
		LapisNetworkClient netClient = new LapisNetworkClient(); //TODO MAYBE RENAME
		netClient.setNetworkTable(networkTable());
		netClient.setRenameMe(networkClientCommunicationImpl()); //TODO RENAME
		return netClient;
	}

	@Bean
	public NetworkClientCommunicationImpl networkClientCommunicationImpl() { //TODO RENAME
		log.trace("getNetworkClientCommunicationImpl() called.");
		NetworkClientCommunicationImpl impl = new NetworkClientCommunicationImpl();
		impl.setLapisNetworkTransmission(getLapisNetworkTransmission());
		impl.setLapisSerialization(lapisSerialization());
		return impl;
	}


	private LapisNetworkTransmission getLapisNetworkTransmission() {
		log.trace("getLapisNetworkTransmission() called.");
		if(isCoordinator) {
			return null; 
		} else {
			LapisNetworkTransmission netTrans = new LapisNetworkTransmission();
			netTrans.setSerializationMediaType(serializationMediaType());
			netTrans.setCoordinatorBaseUrl(coordinatorUrl);
			return netTrans;
		}
	}

	@Bean
	public LocalDataTable localDataTable() {
		log.trace("localDataTable() called.");
		return new LocalDataTable();
	}

	@Bean 
	public RestletServer getRestletServer() {
		log.trace("getRestletServer() called.");
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
		log.trace("getNetworkRestlet() called.");
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
		log.trace("getVariableMetaDataApiRestlet() called.");
		VariableMetaDataApiRestlet variableMetaDataApiRestlet = new VariableMetaDataApiRestlet();
		variableMetaDataApiRestlet.setLocalDataTable(localDataTable());
		variableMetaDataApiRestlet.setLapisSerialization(lapisSerialization());
		variableMetaDataApiRestlet.setResponseMediaType(serializationMediaType());
		return variableMetaDataApiRestlet;
	}
	
	private Restlet getVariableValueApiRestlet() {
		log.trace("getVariableValueApiRestlet() called.");
		VariableValueApiRestlet variableValueApiRestlet = new VariableValueApiRestlet();
		variableValueApiRestlet.setLapisSerialization(lapisSerialization());
		variableValueApiRestlet.setLocalDataTable(localDataTable());
		variableValueApiRestlet.setResponseMediaType(serializationMediaType());
		return variableValueApiRestlet.getVariableValueRestletWithFilters();
	}
	
	private void setUpCoordinatorRestlet(RestletServer restletServer) {
		log.trace("setUpCoordinatorRestlet() called.");
		Restlet coordinatorRestlet = getCoordinatorRestlet();
		restletServer.attachRestlet("/coordinator/{" + Attributes.MODEL_NAME_ATTRIBUTE + '}', coordinatorRestlet);
		restletServer.attachRestlet("/coordinator", coordinatorRestlet);
	}
	
	private Restlet getCoordinatorRestlet() {
		log.trace("getCoordinatorRestlet() called.");
		CoordinatorRestlet coordinatorRestlet = new CoordinatorRestlet();
		coordinatorRestlet.setLapisSerialization(lapisSerialization());
		coordinatorRestlet.setNetworkTable(networkTable());
		coordinatorRestlet.setResponseMediaType(serializationMediaType());
		coordinatorRestlet.setNotifier(getNotifier());
		return coordinatorRestlet.getCoordinatorRestletWithFilters();
	}

	private Notifier getNotifier() {
		log.trace("getNotifier() called.");
		Notifier notifier = new Notifier();
		notifier.setLapisSerialization(lapisSerialization());
		notifier.setMediaType(serializationMediaType());
		notifier.setNetworkTable(networkTable());
		return notifier;
	}
	
	@Bean
	public NetworkTable networkTable() {
		log.trace("networkTable() called.");
		NetworkTable nt = new NetworkTable();
		nt.setLocalNode(getLocalNode());
		return nt;
	}

	@Bean
	public MediaType serializationMediaType() {
		log.trace("serializationMediaType() called.");
		return MediaType.APPLICATION_JSON;
	}
	
	@Bean 
	public LapisSerialization lapisSerialization() {
		log.trace("lapisSerialization() called.");
		JsonSerialization json = new JsonSerialization();
		json.setPrettyPrinting(true);
		return json;
	}
	
	@Bean
	public LapisNode getLocalNode() {
		log.trace("getLocalNode() called.");
		return new LapisNode(name, "http://localhost:" + port); //TODO fix
	}
	
	@PostConstruct
	public void postConstruct() {
		log.trace("postConstruct() called.");
		if(!isCoordinator) {
			networkClientCommunicationImpl().addNodeToNetwork(getLocalNode());
		} else {
			log.trace("Coordinator. Nothing to do.");
		}
	}

	private GlobalDataTable getGlobalDataTable() {
		log.trace("getGlobalDataTable() called.");
		return new GlobalDataTable();
	}
}