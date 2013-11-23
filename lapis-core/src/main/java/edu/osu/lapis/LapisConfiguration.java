package edu.osu.lapis;

import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.restlet.Restlet;
import org.restlet.data.MediaType;

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
public class LapisConfiguration {
	
	private final Properties properties;
	private final LapisSerialization lapisSerialization;
	private final MediaType serializationMediaType;
	private final int port;
	private final NetworkTable networkTable;
	private final boolean isCoordinator;
	private final LapisNetworkClient lapisNetworkClient;
	private final LapisDataClient lapisDataClient;
	private final LocalDataTable localDataTable;
	private final RestletServer restletServer;
	
	public LapisConfiguration(Properties properties) {
		this.properties = properties;
		this.lapisSerialization = getLapisSerializationInternal();
		this.serializationMediaType = MediaType.APPLICATION_JSON;
		this.port = getPort();
		this.networkTable = getNetworkTable();
		this.isCoordinator = isCoordinator();
		this.lapisNetworkClient = getLapisNetworkClient();
		this.lapisDataClient = getLapisDataClientInternal();
		this.localDataTable = new LocalDataTable();
		this.restletServer = getRestletServerInternal();
	}
	
	public LapisDataClient getLapisDataClient() {
		return this.lapisDataClient;
	}

	public LocalDataTable getLocalDataTable() {
		return this.localDataTable;
	}

	public RestletServer getRestletServer() {
		return this.restletServer;
	}

	private LapisSerialization getLapisSerializationInternal() {
		JsonSerialization json = new JsonSerialization();
		json.setPrettyPrinting(true);
		return json;
	}

	private int getPort() {
		String portStr = this.properties.getProperty("port"); //TODO ADD DEFAULT
		Validate.notEmpty(portStr, "'port' property must be specified.");
		try {
			return Integer.parseInt(portStr);
		} catch(Exception e) {
			throw new RuntimeException("Unable to parse 'port' property value '" + portStr + "' as integer." );
		}
	}
	
	private NetworkTable getNetworkTable() {
		NetworkTable nt = new NetworkTable();
		nt.setLocalNode(getLocalNode());
		return nt;
	}
	
	private LapisNode getLocalNode() {
		String nodeName = this.properties.getProperty("name");
		Validate.notEmpty(nodeName, "'name' property must have non-empty value.");
		return new LapisNode(nodeName, "http://localhost:" + this.port); //TODO fix -- get the IP address to use
	}
	
	private boolean isCoordinator() {
		return Boolean.parseBoolean(this.properties.getProperty("isCoordinator"));
	}
	
	private LapisNetworkClient getLapisNetworkClient() {
		LapisNetworkClient netClient = new LapisNetworkClient();
		netClient.setNetworkTable(this.networkTable);
		netClient.setNetworkClientCommunicationImpl(getNetworkClientCommunicationImpl());
		return netClient;
	}
	
	private NetworkClientCommunicationImpl getNetworkClientCommunicationImpl() {
		NetworkClientCommunicationImpl impl = new NetworkClientCommunicationImpl();
		impl.setLapisNetworkTransmission(getLapisNetworkTransmission());
		impl.setLapisSerialization(this.lapisSerialization);
		return impl;
	}
	
	private LapisNetworkTransmission getLapisNetworkTransmission() {
		String coordinatorUrl = this.properties.getProperty("coordinator.url");
		Validate.notEmpty(coordinatorUrl, "Coordinator URL must not be empty");
		LapisNetworkTransmission netTrans = new LapisNetworkTransmission();
		netTrans.setCoordinatorBaseUrl(coordinatorUrl);
		netTrans.setLapisTransmission(new LapisTransmission()); //TODO MAYBE CREATE A BEAN
		return netTrans;
	}

	private LapisDataClient getLapisDataClientInternal() {
		LapisDataClient lapisDataClient = new LapisDataClient();
		lapisDataClient.setGlobalDataTable(new GlobalDataTable());
		lapisDataClient.setDataClientCommunicationImpl(getDataClientCommunicationImplInternal());
		return lapisDataClient;
	}
	
	private DataClientCommunicationImpl getDataClientCommunicationImplInternal() {
		DataClientCommunicationImpl impl = new DataClientCommunicationImpl();
		impl.setLapisSerialization(this.lapisSerialization);
		impl.setLapisDataTransmission(getLapisDataTransmissionInternal());
		return impl;
	}

	private LapisDataTransmission getLapisDataTransmissionInternal() {
		LapisDataTransmission lapisDataTransmission = new LapisDataTransmission();
		lapisDataTransmission.setLapisNetworkClient(this.lapisNetworkClient);
		lapisDataTransmission.setVariableMetaDataPath("metadata");
		lapisDataTransmission.setVariableValuePath("model");
		lapisDataTransmission.setLapisTransmission(new LapisTransmission());
		return lapisDataTransmission;
	}

	private RestletServer getRestletServerInternal() {
		
		RestletServer restletServer = new RestletServer();
		restletServer.setPort(this.port);
		
		Restlet networkRestlet = getNetworkRestlet();
		restletServer.attachRestlet("/network/{" + Attributes.MODEL_NAME_ATTRIBUTE + '}', networkRestlet);
		restletServer.attachRestlet("/network", networkRestlet);
		
		VariableMetaDataApiRestlet variableMetaDataApiRestlet = getVariableMetaDataApiRestlet();
		restletServer.attachRestlet("/metadata/{" + Attributes.VARIABLE_NAME_ATTRIBUTE + '}',variableMetaDataApiRestlet);
		restletServer.attachRestlet("/metadata", variableMetaDataApiRestlet);
		
		Restlet variableValueApiRestlet = getVariableValueApiRestlet();
		restletServer.attachRestlet("/model/{" + Attributes.VARIABLE_NAME_ATTRIBUTE + '}', variableValueApiRestlet);
		
		restletServer.attachRestlet("/heartbeat", new HeartbeatRestlet());
		
		if(this.isCoordinator) {
			setUpCoordinatorRestlet(restletServer);
		}
		
		return restletServer;
	}
	
	private Restlet getNetworkRestlet() {
		NetworkRestlet networkRestlet = new NetworkRestlet();
		networkRestlet.setLapisSerialization(this.lapisSerialization);
		networkRestlet.setNetworkTable(this.networkTable);
		networkRestlet.setResponseMediaType(this.serializationMediaType);
		Restlet withFilters = networkRestlet.getNetworkRestletWithFilters();
		if(this.isCoordinator) {
			CoordinatorNetworkApiFilter coordinatorNetworkApiFilter = new CoordinatorNetworkApiFilter();
			coordinatorNetworkApiFilter.setNext(withFilters);
			return coordinatorNetworkApiFilter;
		} else {
			return withFilters;
		}
	}
	
	private VariableMetaDataApiRestlet getVariableMetaDataApiRestlet() {
		VariableMetaDataApiRestlet variableMetaDataApiRestlet = new VariableMetaDataApiRestlet();
		variableMetaDataApiRestlet.setLocalDataTable(this.localDataTable);
		variableMetaDataApiRestlet.setLapisSerialization(this.lapisSerialization);
		variableMetaDataApiRestlet.setResponseMediaType(this.serializationMediaType);
		return variableMetaDataApiRestlet;
	}
	
	private Restlet getVariableValueApiRestlet() {
		VariableValueApiRestlet variableValueApiRestlet = new VariableValueApiRestlet();
		variableValueApiRestlet.setLapisSerialization(this.lapisSerialization);
		variableValueApiRestlet.setLocalDataTable(this.localDataTable);
		variableValueApiRestlet.setResponseMediaType(this.serializationMediaType);
		return variableValueApiRestlet.getVariableValueRestletWithFilters();
	}
	
	private void setUpCoordinatorRestlet(RestletServer restletServer) {
		Restlet coordinatorRestlet = getCoordinatorRestlet();
		restletServer.attachRestlet("/coordinator/{" + Attributes.MODEL_NAME_ATTRIBUTE + '}', coordinatorRestlet);
		restletServer.attachRestlet("/coordinator", coordinatorRestlet);
	}
	
	private Restlet getCoordinatorRestlet() {
		CoordinatorRestlet coordinatorRestlet = new CoordinatorRestlet();
		coordinatorRestlet.setLapisSerialization(this.lapisSerialization);
		coordinatorRestlet.setNetworkTable(this.networkTable);
		coordinatorRestlet.setResponseMediaType(this.serializationMediaType);
		coordinatorRestlet.setNotifier(getNotifier());
		return coordinatorRestlet.getCoordinatorRestletWithFilters();
	}

	private Notifier getNotifier() {
		Notifier notifier = new Notifier();
		notifier.setLapisSerialization(this.lapisSerialization);
		notifier.setNetworkTable(this.networkTable);
		notifier.setLapisTransmission(new LapisTransmission());
		return notifier;
	}
	
	/**
	 * Attempts to join the network if this node is not the coordinator.
	 */
	public void attemptToJoinNetwork() {
		if(!this.isCoordinator) {
			try {				
				this.lapisNetworkClient.addNodeToNetwork(networkTable.getLocalNode());
			} catch(Exception e) {
				//TODO HANDLE BETTER
				e.printStackTrace();
			}
		}
	}
}