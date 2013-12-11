package edu.osu.lapis;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;

import com.google.common.io.Files;

import edu.osu.lapis.comm.client.LapisDataClient;
import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.LocalDataTable;
//TODO MOVE RESTLET STUFF AWAY FROM THE SURFACE
import edu.osu.lapis.restlets.RestletServer;

public class LapisCore {
	
	private LocalDataTable localDataTable;
	private LapisDataClient lapisDataClient;
	private LapisConfiguration lapisConfiguration;
	private String name;
	private boolean shutdown;
	
	/**
	 * Start LAPIS and initialize with default properties.
	 */
	public LapisCore() {
		//default
		this(getDefaultProperties());
	}
	
	public LapisCore(String propertiesFileName) {
		this(getPropertiesFromFile(propertiesFileName));
	}

	/**
	 * Start LAPIS and initialize with the given properties
	 * @param properties the properties to use in initialization
	 */
	public LapisCore(Properties properties) {
		this.name = properties.getProperty("name");
		this.lapisConfiguration = new LapisConfiguration(properties);
		localDataTable = lapisConfiguration.getLocalDataTable();
		lapisDataClient = lapisConfiguration.getLapisDataClient();
		RestletServer restletServer = lapisConfiguration.getRestletServer();
		restletServer.initialize();
		lapisConfiguration.attemptToJoinNetwork();
	}

	public void publish(String localVariableName, LapisVariable lapisVariable) {
		localDataTable.put(localVariableName, lapisVariable);
	}
	
	public <T> T getRemoteValue(String variableFullName, Class<T> expectedClassType) {
		return lapisDataClient.getRemoteVariableValue(variableFullName, expectedClassType);
	}
	
	public Object getRemoteValue(String variableFullName) {
		return lapisDataClient.getRemoteVariableValue(variableFullName, Object.class);
	}
	
	public void setRemoteValue(String variableFullName, Object value) {
		lapisDataClient.setRemoteVariableValue(variableFullName, value);
	}
	
	public synchronized void shutdown() {
		RestletServer restletServer = this.lapisConfiguration.getRestletServer();
		if(restletServer != null && !shutdown) {
			System.err.println("Shutting down servers for node '" + name + "'.");
			restletServer.stopServer();
			shutdown = true;
		} else {
			System.err.println("Restlet Server was null, or was already shut down.");
		}
	}

	public void setLocalDataTable(LocalDataTable localDataTable) {
		this.localDataTable = localDataTable;
	}

	public void setLapisDataClient(LapisDataClient lapisDataClient) {
		this.lapisDataClient = lapisDataClient;
	}

	private static Properties getDefaultProperties() {
		// TODO implement for real
		return new Properties();
	}
	
	/**
	 * Start LAPIS and initialize with properties read from the properties file.
	 * @param propertiesFileName the name of the file from which properties will be read
	 */
	private static Properties getPropertiesFromFile(String propertiesFileName) {
		if(propertiesFileName.toLowerCase().endsWith(".json")) {
			return parseJsonPropertiesFile(propertiesFileName);
		} else {
			return parseRegularPropertiesFile(propertiesFileName);
		}
	}

	private static Properties parseJsonPropertiesFile(String propertiesFileName) {
		return JsonPropertiesParser.parseJsonProperties(propertiesFileName);
	}

	private static Properties parseRegularPropertiesFile(String propertiesFileName) {
		Properties props = new Properties();
		try {
			Reader reader = Files.newReader(new File(propertiesFileName), Charset.defaultCharset());
			props.load(reader);
			reader.close();
			return props;
		} catch (IOException e) {
			throw new RuntimeException("Error loading properties file: " + propertiesFileName, e);
		}
	}

	public String getName() {
		return name;
	}
}
