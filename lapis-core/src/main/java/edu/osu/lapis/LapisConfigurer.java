package edu.osu.lapis;

import edu.osu.lapis.client.ApacheHttpClientImpl;
import edu.osu.lapis.client.Client;
import edu.osu.lapis.services.HeartbeatClient;
import edu.osu.lapis.services.LapisDataClient;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.network.*;
import edu.osu.lapis.restlets.*;
import edu.osu.lapis.restlets.filters.CoordinatorNetworkApiFilter;
import edu.osu.lapis.serialization.JsonSerialization;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.services.LapisDataClientHelper;
import edu.osu.lapis.services.LapisNetworkClient;
import edu.osu.lapis.util.Attributes;
import edu.osu.lapis.util.Sleep;
import org.apache.commons.lang3.Validate;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.resource.ResourceException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Executors;

import static edu.osu.lapis.Constants.Properties.*;

public class LapisConfigurer {

	static {
		//set this system property so we can route Restlet logging through slf4j
		System.setProperty("org.restlet.engine.loggerFacadeClass",
				"org.restlet.ext.slf4j.Slf4jLoggerFacade");
	}

	private final Properties properties;
	private final LapisSerialization lapisSerialization;
	private final MediaType serializationMediaType;
	private final boolean isCoordinator;
	private final LapisDataClient lapisDataClient;
	private final LocalDataTable localDataTable;
	private final RestletServer restletServer;
	private final NetworkChangeHandler networkChangeHandler;
	private final HeartbeatClient heartbeatClient;
	private final Client client;
	private final LapisNetwork lapisNetwork;
	private final LapisNetworkClient lapisNetworkClient;

	public LapisConfigurer(Properties properties) {
		this.properties = properties;
		this.lapisSerialization = getLapisSerializationInternal();
		this.client = new ApacheHttpClientImpl(new PoolingHttpClientConnectionManager());
		this.serializationMediaType = MediaType.APPLICATION_JSON;
		this.isCoordinator = isCoordinator();
		this.lapisNetworkClient = getLapisNetworkTransmissionInternal();
		this.lapisNetwork = getLapisNetworkInternal();
		this.lapisDataClient = new LapisDataClient(getLapisDataTransmission());
		this.localDataTable = new LocalDataTable();
		this.networkChangeHandler = new NetworkChangeHandler(Executors.newSingleThreadExecutor());
		this.restletServer = getRestletServerInternal();
		this.heartbeatClient = new HeartbeatClient(this.lapisNetwork, this.client);
	}

	private LapisSerialization getLapisSerializationInternal() {
		JsonSerialization json = new JsonSerialization();
		json.setPrettyPrinting(true);
		return json;
	}

	private boolean isCoordinator() {
		return Boolean.parseBoolean(this.properties.getProperty(IS_COORDINATOR));
	}

	private LapisNetworkClient getLapisNetworkTransmissionInternal() {
		return new LapisNetworkClient(this.client, this.lapisSerialization, getCoordinatorUrl());
	}

	private LapisNetwork getLapisNetworkInternal() {
		if (isCoordinator()) {
			return new CoordinatorLapisNetwork(getLocalNode());
		} else {
			LapisNetworkClient lapisNetworkClient
					= new LapisNetworkClient(this.client, this.lapisSerialization, getCoordinatorUrl());
			return new NonCoordinatorLapisNetwork(lapisNetworkClient, 5000 /*TODO MAKE CONFIGURABLE*/,
					getLocalNode());
		}
	}

	private LapisNode getLocalNode() {
		String nodeName = this.properties.getProperty(NAME);
		Validate.notEmpty(nodeName, "'name' property must have non-empty value.");
		return new LapisNode(nodeName, getLocalNodeAddress());
	}

	private String getLocalNodeAddress() {
		String address = this.properties.getProperty(LOCAL_NODE_ADDRESS);
		Validate.notEmpty(address, "Local node address must not be empty.");
		return getWithHttp(address);
	}

	public LapisDataClientHelper getLapisDataTransmission() {
		return new LapisDataClientHelper(this.lapisSerialization, this.lapisNetwork, this.client);
	}

	private String getCoordinatorUrl() {
		String coordinatorUrl = this.properties.getProperty(COORDINATOR_URL);
		Validate.notEmpty(coordinatorUrl, "Coordinator URL must not be empty");
		return getWithHttp(coordinatorUrl);
	}

	private String getWithHttp(String address) {
		if (!address.toLowerCase().startsWith("http://") && !address.contains(":/")) {
			return "http://" + address;
		} else {
			return address;
		}
	}

	private RestletServer getRestletServerInternal() {
		RestletServer restletServer = new RestletServer(getPort());

		Restlet networkRestlet = getNetworkRestlet();
		restletServer.attachRestlet("/network/{" + Attributes.MODEL_NAME_ATTRIBUTE + '}', networkRestlet);
		restletServer.attachRestlet("/network", networkRestlet);

		VariableMetaDataApiRestlet variableMetaDataApiRestlet = getVariableMetaDataApiRestlet();
		restletServer.attachRestlet("/metadata/{" + Attributes.VARIABLE_NAME_ATTRIBUTE + '}', variableMetaDataApiRestlet);
		restletServer.attachRestlet("/metadata", variableMetaDataApiRestlet);

		Restlet variableValueApiRestlet = getVariableValueApiRestlet();
		restletServer.attachRestlet("/model/{" + Attributes.VARIABLE_NAME_ATTRIBUTE + '}', variableValueApiRestlet);

		restletServer.attachRestlet("/heartbeat", new HeartbeatRestlet());

		if (this.isCoordinator) {
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
		networkRestlet.setResponseMediaType(this.serializationMediaType);
		networkRestlet.setLocalNode(getLocalNode());
		Restlet withFilters = networkRestlet.getNetworkRestletWithFilters();
		if (this.isCoordinator) {
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
		if (lapisNetwork instanceof CoordinatorLapisNetwork) {
			CoordinatorLapisNetwork coordinatorLapisNetwork = (CoordinatorLapisNetwork) lapisNetwork;
			CoordinatorRestlet coordinatorRestlet = new CoordinatorRestlet(this.lapisSerialization,
					coordinatorLapisNetwork, this.serializationMediaType, this.networkChangeHandler);
			return coordinatorRestlet.getCoordinatorRestletWithFilters();
		} else {
			throw new IllegalStateException("LapisNetwork instance should be CoordinatorLapisNetwork");
		}
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

	//TODO MOVE THIS LOGIC ELSEWHERE, AND CLEAN IT UP

	/**
	 * Attempts to join the network if this node is not the coordinator.
	 */
	public void attemptToJoinNetwork() {
		long startTimeMillis = System.currentTimeMillis();
		if (!this.isCoordinator) {
			LapisNode localNode = getLocalNode();
			String nodeName = localNode.getNodeName();
			byte[] nodeData = lapisSerialization.serialize(localNode);
			while (System.currentTimeMillis() < startTimeMillis + 60000) {
				try {
					this.lapisNetworkClient.addNodeToNetwork(nodeName, nodeData);
					break;
				} catch (Exception e) {
					if (System.currentTimeMillis() > startTimeMillis + 60000) {
						String underlyingErrorMsg = e instanceof ResourceException ? e.toString() : e.getMessage();
						String runtimeExcMsg = "Encountered exception while trying to connect to "
								+ "connect to the LAPIS network coordinator and add this node"
								+ " to the network. The coordinator address is '"
								+ getCoordinatorUrl() + "'. The error message of the " +
								"underlying error is: " + underlyingErrorMsg;
						throw new RuntimeException(runtimeExcMsg, e);
					} else {
						System.err.println("Unable to connect to coordinator. Retrying...");
					}
				}
				Sleep.sleep(1000);
			}
		}
	}

	public HeartbeatClient getHeartbeatClient() {
		return this.heartbeatClient;
	}

	public LapisNetwork getLapisNetwork() {
		return this.lapisNetwork;
	}
}
