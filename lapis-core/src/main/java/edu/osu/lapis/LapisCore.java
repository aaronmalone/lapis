package edu.osu.lapis;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.Callables;

import edu.osu.lapis.comm.client.LapisDataClient;
import edu.osu.lapis.comm.client.LapisNetworkClient;
import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkChangeCallback;
import edu.osu.lapis.network.NetworkChangeHandler;
//TODO MOVE RESTLET CODE AWAY FROM THE SURFACE
import edu.osu.lapis.restlets.RestletServer;
import edu.osu.lapis.util.Sleep;

public class LapisCore {
	
	private final Logger logger = Logger.getLogger(getClass());

	private static final String READY_VARIABLE_NAME = "~READY~FLAG~";
	private static final Object READY_VARIABLE_VALUE = new double[] { 1 };
	@VisibleForTesting static long waitingForNodeRetryTime = 500;

	private final LocalDataTable localDataTable;
	private final LapisDataClient lapisDataClient;
	private final LapisNetworkClient lapisNetworkClient;
	private final LapisConfiguration lapisConfiguration;
	private final NetworkChangeHandler networkChangeHandler;
	private String name;
	private boolean shutdown;

	/**
	 * Start LAPIS and initialize with the given properties
	 * 
	 * @param properties
	 *            the properties to use in initialization
	 */
	public LapisCore(Properties properties) {
		this.name = properties.getProperty("name");
		this.lapisConfiguration = new LapisConfiguration(properties);
		localDataTable = lapisConfiguration.getLocalDataTable();
		lapisDataClient = lapisConfiguration.getLapisDataClient();
		lapisNetworkClient = lapisConfiguration.getLapisNetworkClient();
		networkChangeHandler = lapisConfiguration.getNetworkChangeHandler();
		RestletServer restletServer = lapisConfiguration.getRestletServer();
		restletServer.initialize();
		lapisConfiguration.attemptToJoinNetwork();
	}

	/**
	 * Indicates that this node is 'ready'. Other nodes on the network may check 
	 * if this node is ready and wait for it to become ready. 
	 */
	public void ready() {
		if(localDataTable.get(READY_VARIABLE_NAME) == null) {
			LapisVariable readyVariable = new LapisVariable(READY_VARIABLE_NAME, 
					true, Callables.returning(READY_VARIABLE_VALUE), null);
			this.publish(READY_VARIABLE_NAME, readyVariable);
			logger.info("Node '%s' ready.", getName());
		}
	}
	
	/**
	 * Indicates that this node is 'not ready'. Other nodes on the network may 
	 * check if this node is ready or not, and may wait for this node to become
	 * ready.
	 */
	public void notReady() {
		this.redact(READY_VARIABLE_NAME);
		logger.info("Node '%s' set to not-ready.", getName());
	}
	
	/**
	 * Wait until a node on the network becomes ready. This method will wait 
	 * indefinitely. If the waited-upon node is not yet on the LAPIS network,
	 * this will wait until the node has joined, and then poll the node's 
	 * ready flag.
	 * @param nodeName the name of the node to wait on
	 */
	public void waitForReadyNode(String nodeName) {
		try {
			waitForReadyNode(nodeName, -1);
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Wait until a node on the network becomes ready. This method will wait 
	 * until the node becomes ready or a certain number of milliseconds has 
	 * elapsed. If the waited-upon node is not yet on the LAPIS network,
	 * this will wait until the node has joined, and then poll the node's 
	 * ready flag.
	 * @param nodeName the name of the node to wait on
	 * @param timeout the number of milliseconds to wait for the node
	 * @throws TimeoutException 
	 */
	public void waitForReadyNode(final String nodeName, final long timeout) throws TimeoutException {
		logger.info("Will wait for node '%s' to be ready.", nodeName);
		long untilTimeMillis = timeout < 0 ? Long.MAX_VALUE : System.currentTimeMillis() + timeout;
		boolean nodeReady = checkIfNodeIsReadyUntil(nodeName, untilTimeMillis);
		if(!nodeReady) {
			throw new TimeoutException("Timed out while waiting for node '" 
					+ nodeName + "' to become ready.");
		}
	}
	
	/**
	 * Checks if the node is ready. This method will check repeatedly until the 
	 * node becomes ready or the timeout is reached.
	 * @param nodeName the name of the node to check
	 * @param untilTimeMillis the time (see System.currentTimeMillis()) at 
	 * which this check will time out
	 * @return true if the node is ready; false is the timeout is reached before
	 * the node becomes ready
	 */
	private boolean checkIfNodeIsReadyUntil(final String nodeName, final long untilTimeMillis) {
		logger.debug("Checking if node '%s' is ready. Will continue checking until %d.", nodeName, untilTimeMillis);
		while(System.currentTimeMillis() < untilTimeMillis) {
			if(nodeIsOnNetwork(nodeName) && hasPublishedReadyFlag(nodeName)) {
				return true;
			}
			Sleep.sleep(waitingForNodeRetryTime);
		}
		return false; //timed out
	}
	
	/**
	 * Checks if the node is on the network.
	 * @param nodeName the name of the node to check for
	 * @return true if the node is on the LAPIS network
	 */
	private boolean nodeIsOnNetwork(String nodeName) {
		//note: as currently implemented, this results in unnecessary HTTP calls
		//  if the current node IS the network coordinator
		List<LapisNode> nodes = lapisNetworkClient.getAllNetworkNodesForceRefresh(); 
		for(LapisNode node : nodes) {
			if(node.getNodeName().equals(nodeName)) {
				logger.trace("Node '%s' is on the network.", nodeName);
				return true;
			}
		}
		logger.trace("Node '%s' is not on the network.", nodeName);
		return false;
	}
	
	/**
	 * Checks if the node has published its 'ready' flag.
	 * NOTE: this will throw an exception if the node is not on the network
	 * @param nodeName the node to check
	 * @return true if the node has published its 'ready' flag.
	 */
	private boolean hasPublishedReadyFlag(String nodeName) {
		List<VariableMetaData> metas = lapisDataClient.getVariableMetaDataForNode(nodeName);
		for(VariableMetaData meta : metas) {
			if (meta.getName().equals(READY_VARIABLE_NAME)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Un-publish a variable. The variable will no longer be exposed through the
	 * REST interface or available to other network nodes.
	 * @param localVariableName the name of the variable
	 */
	public void redact(String localVariableName) {
		logger.info("Redacting variable '%s'.", localVariableName);
		localDataTable.remove(localVariableName);
	}

	/**
	 * Publish a variable. This results in the variable being exposed through 
	 * the REST interface and available to other LAPIS nodes on the same network.
	 * @param localVariableName the name of the variable
	 * @param lapisVariable the variable wrapper in a LapisVariable object
	 */
	public void publish(String localVariableName, LapisVariable lapisVariable) {
		String varType = lapisVariable.isReadOnly() ? "read-only" : "normal";
		logger.info("Publishing %s variable '%s'.", varType, localVariableName);
		localDataTable.put(localVariableName, lapisVariable);
	}

	/**
	 * Retrieve the value of a published variable on another LAPIS node.
	 * @param variableFullName the "full name" of the variable
	 * @param expectedClassType the expected type of the value
	 * @return the remote variable value
	 */
	public <T> T getRemoteValue(String variableFullName, Class<T> expectedClassType) {
		return lapisDataClient.getRemoteVariableValue(variableFullName, expectedClassType);
	}

	/**
	 * Retrieve the value of a published variable on another LAPIS node.
	 * @param variableFullName the "full name" of the variable
	 * @return the remote variable value
	 */
	public Object getRemoteValue(String variableFullName) {
		return lapisDataClient.getRemoteVariableValue(variableFullName, Object.class);
	}

	/**
	 * Set the value of a variable published on another LAPIS node.
	 * @param variableFullName the "full name" of the variable
	 * @param value the value to set
	 */
	public void setRemoteValue(String variableFullName, Object value) {
		lapisDataClient.setRemoteVariableValue(variableFullName, value);
	}
	
	/**
	 * Register a callback to be executed when this node is notified of a change
	 * in the LAPIS network.
	 */
	public void registerNetworkChangeCallback(NetworkChangeCallback callback) {
		networkChangeHandler.addCallback(callback);
	}

	/**
	 * Shut down this LAPIS node.
	 */
	public synchronized void shutdown() {
		RestletServer restletServer = this.lapisConfiguration.getRestletServer();
		if (restletServer != null && !shutdown) {
			System.err.println("Shutting down servers for node '" + name + "'.");
			restletServer.stopServer();
			shutdown = true;
		} else {
			System.err.println("Restlet Server was null, or was already shut down.");
		}
	}

	/**
	 * Get the name of this LAPIS node
	 * @return the name of this LAPIS node
	 */
	public String getName() {
		return name;
	}
}