package edu.osu.lapis;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.Validate;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.resource.ResourceException;

import edu.osu.lapis.comm.Notifier;
import edu.osu.lapis.comm.client.HeartbeatClient;
import edu.osu.lapis.comm.client.LapisDataClient;
import edu.osu.lapis.comm.client.LapisNetworkClient;
import edu.osu.lapis.comm.serial.DataClientCommunicationImpl;
import edu.osu.lapis.comm.serial.NetworkClientCommunicationImpl;
import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkChangeHandler;
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
import edu.osu.lapis.transmission.LapisTransmissionApacheHttpClientImpl;
import edu.osu.lapis.util.Attributes;

public class LapisConfiguration {
	
	static {
		//set this system property so we can route Restlet logging through slf4j
		System.setProperty("org.restlet.engine.loggerFacadeClass",
				"org.restlet.ext.slf4j.Slf4jLoggerFacade");
	}
	
	private final Properties properties;
	private final LapisSerialization lapisSerialization;
	private final MediaType serializationMediaType;
	private final NetworkTable networkTable;
	private final boolean isCoordinator;
	private final LapisNetworkClient lapisNetworkClient;
	private final LapisDataClient lapisDataClient;
	private final LocalDataTable localDataTable;
	private final RestletServer restletServer;
	private final LapisTransmission lapisTransmission;
	private final NetworkChangeHandler networkChangeHandler;
	private final HeartbeatClient heartbeatClient;
	
	public LapisConfiguration(Properties properties) {		
		this.properties = properties;
		this.lapisSerialization = getLapisSerializationInternal();
		this.lapisTransmission = new LapisTransmissionApacheHttpClientImpl(
				new PoolingHttpClientConnectionManager());
		this.serializationMediaType = MediaType.APPLICATION_JSON;
		this.networkTable = getNetworkTable();
		this.isCoordinator = isCoordinator();
		this.lapisNetworkClient = getLapisNetworkClientInternal();
		this.lapisDataClient = getLapisDataClientInternal();
		this.localDataTable = new LocalDataTable();
		this.networkChangeHandler = createNetworkChangeHandler();
		this.restletServer = getRestletServerInternal();
		this.heartbeatClient = new HeartbeatClient(this.networkTable, this.lapisTransmission);
	}
	
	public LapisNetworkClient getLapisNetworkClient() {
		return this.lapisNetworkClient;
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
	
	public NetworkChangeHandler getNetworkChangeHandler() {
		return this.networkChangeHandler;
	}

	private LapisSerialization getLapisSerializationInternal() {
		JsonSerialization json = new JsonSerialization();
		json.setPrettyPrinting(true);
		return json;
	}

	private NetworkTable getNetworkTable() {
		NetworkTable nt = new NetworkTable();
		nt.setLocalNode(getLocalNode());
		return nt;
	}
	
	private LapisNode getLocalNode() {
		String nodeName = this.properties.getProperty("name");
		Validate.notEmpty(nodeName, "'name' property must have non-empty value.");
		LapisNode node = new LapisNode(nodeName, getLocalNodeAddress());
		return node ;
	}
	
	private String getLocalNodeAddress() {
		String address = this.properties.getProperty("localNodeAddress");
		Validate.notEmpty(address, "Local node address must not be empty.");
		return getWithHttp(address);
	}
	
	private String getWithHttp(String address) {
		if(!address.toLowerCase().startsWith("http://") && !address.contains(":/")) {
			return "http://" + address;
		} else {
			return address;
		}
	}
	
	private boolean isCoordinator() {
		return Boolean.parseBoolean(this.properties.getProperty("isCoordinator"));
	}
	
	public LapisNetworkClient getLapisNetworkClientInternal() {
		return new LapisNetworkClient(this.networkTable, getNetworkClientCommunicationImpl());
	}
	
	private NetworkClientCommunicationImpl getNetworkClientCommunicationImpl() {
		return new NetworkClientCommunicationImpl(getLapisNetworkTransmission(), this.lapisSerialization);
	}
	
	private LapisNetworkTransmission getLapisNetworkTransmission() {
		String coordinatorUrl = getCoordinatorUrl();
		LapisNetworkTransmission netTrans = new LapisNetworkTransmission();
		netTrans.setCoordinatorBaseUrl(coordinatorUrl);
		netTrans.setLapisTransmission(getLapisTransmission());
		return netTrans;
	}
	
	private String getCoordinatorUrl() {
		String coordinatorUrl = this.properties.getProperty("coordinator.url");
		Validate.notEmpty(coordinatorUrl, "Coordinator URL must not be empty");		
		return getWithHttp(coordinatorUrl);		
	}

	private LapisDataClient getLapisDataClientInternal() {
		return new LapisDataClient(new GlobalDataTable(), getDataClientCommunicationImplInternal());
	}
	
	private DataClientCommunicationImpl getDataClientCommunicationImplInternal() {
		return new DataClientCommunicationImpl(this.lapisSerialization, getLapisDataTransmissionInternal());
	}

	private LapisDataTransmission getLapisDataTransmissionInternal() {
		LapisDataTransmission lapisDataTransmission = new LapisDataTransmission();
		lapisDataTransmission.setLapisNetworkClient(this.lapisNetworkClient);
		lapisDataTransmission.setVariableMetaDataPath("metadata");
		lapisDataTransmission.setVariableValuePath("model");
		lapisDataTransmission.setLapisTransmission(getLapisTransmission());
		return lapisDataTransmission;
	}
	
	private LapisTransmission getLapisTransmission() {
		return this.lapisTransmission;
	}

	private RestletServer getRestletServerInternal() {
		
		RestletServer restletServer = new RestletServer();
		restletServer.setPort(getPort());
		
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
	
	private int getPort() {
		String address = getLocalNodeAddress();
		Validate.notEmpty(address, "Address of local node must be specified.");
		try {
			URL url = new URL(address);
			int port = url.getPort();
			return port != -1 ? port : 80;
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error parsing URL: " + address, e);
		}
	}	
	
	private Restlet getNetworkRestlet() {
		NetworkRestlet networkRestlet = new NetworkRestlet();
		networkRestlet.setLapisSerialization(this.lapisSerialization);
		networkRestlet.setNetworkTable(this.networkTable);
		networkRestlet.setResponseMediaType(this.serializationMediaType);
		networkRestlet.setNetworkChangeHandler(this.networkChangeHandler);
		Restlet withFilters = networkRestlet.getNetworkRestletWithFilters();
		if(this.isCoordinator) {
			CoordinatorNetworkApiFilter coordinatorNetworkApiFilter = new CoordinatorNetworkApiFilter();
			coordinatorNetworkApiFilter.setNext(withFilters);
			return coordinatorNetworkApiFilter;
		} else {
			return withFilters;
		}
	}
	
	private NetworkChangeHandler createNetworkChangeHandler() {
		return new NetworkChangeHandler(Executors.newSingleThreadExecutor());
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
		coordinatorRestlet.setNetworkChangeHandler(this.networkChangeHandler);
		return coordinatorRestlet.getCoordinatorRestletWithFilters();
	}

	private Notifier getNotifier() {
		return new Notifier(this.networkTable, this.lapisSerialization, getLapisTransmission());
	}
	
	//TODO MOVE THIS LOGIC ELSEWHERE, AND CLEAN IT UP
	/**
	 * Attempts to join the network if this node is not the coordinator.
	 */
	public void attemptToJoinNetwork() {
		long startTimeMillis = System.currentTimeMillis();
		if(!this.isCoordinator) {
			while(System.currentTimeMillis() < startTimeMillis + 60000) {
				try {				
					this.lapisNetworkClient.addNodeToNetwork(networkTable.getLocalNode());
					break;
				} catch(Exception e) {
					if(System.currentTimeMillis() > startTimeMillis + 60000) {
						String underlyingErrorMsg = e instanceof ResourceException ? e.toString() : e.getMessage();
						String runtimeExcMsg = "Encountered exception while trying to connect to "
								+ "connect to the LAPIS network coordinator and add this node"
								+ " to the network. The coordinator address is '" 
								+ getCoordinatorUrl() + "'. The error message of the " +
								"underlying error is: " +underlyingErrorMsg;
						throw new RuntimeException(runtimeExcMsg, e);
					} else {
						System.err.println("Unable to connect to coordinator. Retrying...");
					}
				}
			}
		}
	}

	public HeartbeatClient getHeartbeatClient() {
		return this.heartbeatClient;
	}
}