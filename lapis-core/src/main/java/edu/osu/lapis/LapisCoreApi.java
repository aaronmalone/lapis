package edu.osu.lapis;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;

import com.google.common.io.Files;

import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LapisVariable2;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.data.VariableMetaData;
//TODO MOVE RESTLET STUFF AWAY FROM THE SURFACE
import edu.osu.lapis.restlets.RestletServer;

public class LapisCoreApi {
	
	private LocalDataTable localDataTable;
	private LapisDataClient lapisDataClient;
	private LapisConfiguration lapisConfiguration;
	private String name;
	private boolean shutdown;
	
	/**
	 * Start LAPIS and initialize with default properties.
	 */
	public LapisCoreApi() {
		//default
		this(getDefaultProperties());
	}
	
	public LapisCoreApi(String propertiesFileName) {
		this(getPropertiesFromFile(propertiesFileName));
	}

	/**
	 * Start LAPIS and initialize with the given properties
	 * @param properties the properties to use in initialization
	 */
	public LapisCoreApi(Properties properties) {
		this.name = properties.getProperty("name");
		this.lapisConfiguration = new LapisConfiguration(properties);
		localDataTable = lapisConfiguration.getLocalDataTable();
		lapisDataClient = lapisConfiguration.getLapisDataClient();
		RestletServer restletServer = lapisConfiguration.getRestletServer();
		restletServer.initialize();
		lapisConfiguration.attemptToJoinNetwork();
	}

	public void publish(String localVariableName, LapisVariable2 lapisVariable) {
		localDataTable.put(localVariableName, lapisVariable);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getRemoteValue(String variableFullName, Class<T> expectedClassType) {
		LapisDataType expectedLapisType = LapisDataType.getTypeForClass(expectedClassType);
		Object remoteValue = lapisDataClient.getRemoteVariableValue(variableFullName, expectedLapisType);
		if(remoteValue.getClass().equals(Double.class))
			return (T) remoteValue; //well...
		else
			return expectedClassType.cast(remoteValue);
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated //do we want to keep this
	public <T> T getRemoteValue(String variableFullName) {
		VariableMetaData metaData = lapisDataClient.getRemoteVariableMetaData(variableFullName);
		LapisDataType type = metaData.getType();
		Object remoteValue = lapisDataClient.getRemoteVariableValue(variableFullName, type);
		return (T) remoteValue;
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
