package edu.osu.lapis;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.google.common.io.Files;

import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LapisPermission;
import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.restlets.RestletServer;

public class Lapis {
	
	private LocalDataTable localDataTable;
	private LapisDataClient lapisDataClient;

	static {
		//set this system property so we can use SLF4J in Restlet
		System.setProperty("org.restlet.engine.loggerFacadeClass","org.restlet.ext.slf4j.Slf4jLoggerFacade");
	}

	/**
	 * Start LAPIS and initialize with default properties.
	 */
	public Lapis() {
		this(getDefaultProperties());
	}

	//TODO INCLUDE LINK TO java.util.Properties doc
	/**
	 * Start LAPIS and initialize with properties read from the properties file.
	 * @param propertiesFileName the name of the file from which properties will be read
	 */
	public Lapis(String propertiesFileName) {
		this(getPropertiesFromFile(propertiesFileName));
	}
	
	/**
	 * Start LAPIS and initialize with the given properties
	 * @param properties the properties to use in initialization
	 */
	public Lapis(Properties properties) {
		ApplicationContext context = getApplicationContext(properties);
		localDataTable = context.getBean(LocalDataTable.class);
		lapisDataClient = context.getBean(LapisDataClient.class);
		RestletServer restletServer = context.getBean(RestletServer.class);
		restletServer.initialize();
	}
	
	private static Properties getDefaultProperties() {
		// TODO ADD DEFAULT PROPERTIES
		return new Properties();
	}
	
	private static Properties getPropertiesFromFile(String propertiesFileName) {
		if(propertiesFileName.toLowerCase().endsWith(".json")) {
			return parseJsonPropertiesFile(propertiesFileName);
		} else {
			return parseRegularPropertiesFile(propertiesFileName);
		}
	}
	
	private static Properties parseRegularPropertiesFile(String propertiesFileName) {
		Properties props = new Properties();
		try {
			Reader reader = Files.newReader(new File(propertiesFileName), Charset.defaultCharset());
			props.load(reader);
			return props;
		} catch (IOException e) {
			throw new RuntimeException("Error loading properties file: " + propertiesFileName, e);
		}
	}
	
	private static Properties parseJsonPropertiesFile(String propertiesFileName) {
		return JsonPropertiesParser.parseJsonProperties(propertiesFileName);
	}
	
	/**
	 * Creates and initializes a Spring Framework application context with the given properties.
	 * @param properties the properties to use
	 * @return the application context
	 */
	private static ApplicationContext getApplicationContext(Properties properties) {
		PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		placeholderConfigurer.setProperties(properties);
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(LapisConfiguration.class);
		context.addBeanFactoryPostProcessor(placeholderConfigurer);
		context.refresh();
		return context;
	}

	//TODO figure out if we really need this.
	/**
	 * Update the value of a published LAPIS variable.
	 * 
	 * Essentially, this replaces LAPIS's own copy of the value with the new value.
	 * This is necessary when the type of the published variable is a Java primitive
	 * or a Java primitive wrapper (e.g. int or java.lang.Integer). It is not necessary
	 * to use this method when values within an array have been modified. Changes within
	 * an are visible to LAPIS. 
	 * @param localName the name of the published variable
	 * @param value the new value of the variable
	 */
	public void updateValue(String localName, Object value) {
		LapisVariable localVariable = localDataTable.get(localName);
		Validate.notNull(localVariable, "The variable \"%s\" has not been published.", localName);
		LapisVariable newValue = new LapisVariable(localName, value);
		Validate.isTrue(localVariable.getType() == newValue.getType(), 
				"The type of the updated value for \"%s\" does not match the current type, %s.", 
				localName, localVariable.getType());
		localDataTable.put(localName, newValue);
	}
	
	/**
	 * Publish a LAPIS variable. This exposes the variable to the LAPIS 
	 * network, allowing it to be retrieved or set by other nodes.
	 * @param variableName the name of the variable (other nodes will 
	 * access this variable using ${variableName}@${nodeName}
	 * @param reference the object to publish
	 */
	public void publish(String variableName, Object reference) {
		publish(variableName, reference, LapisPermission.READ_WRITE, true);
	}

	//VISIBILITY REDUCED - DO WE WANT TO EXPOSE THIS YET?
	void publish(String localName, Object reference, LapisPermission lapisPermission) {
		publish(localName, reference, lapisPermission, true);
	}
	
	//VISIBILITY REDUCED - DO WE WANT TO EXPOSE THIS YET?
	void publish(String localName, Object reference, boolean isReady) {
		publish(localName, reference, LapisPermission.READ_WRITE, isReady);
	}
	
	//VISIBILITY REDUCED - DO WE WANT TO EXPOSE THIS YET?
	void publish(String localName, Object reference, LapisPermission lapisPermission, boolean isReady) {
		LapisVariable meta = createLapisVariable(localName, reference, lapisPermission, isReady);
		localDataTable.put(localName, meta);
	}

	private LapisVariable createLapisVariable(String name, Object reference, LapisPermission lapisPermission, boolean isReady) {
		assert LapisPermission.READ_WRITE == lapisPermission : "READ_ONLY permissions have not been imlemented yet.";
		LapisVariable meta = new LapisVariable(name, reference);
		meta.setReady(isReady);
		return meta;
	}
	
	//all the get methods
	public int getInt(String fullName) {
		return (int) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.INTEGER);
	}

	public long getLong(String fullName) {
		return (long) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.LONG);
	}

	public double getDouble(String fullName) {
		return (double) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.DOUBLE);
	}

	public byte getByte(String fullName) {
		return (byte) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.BYTE);
	}

	public boolean getBoolean(String fullName) {
		return (boolean) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.BOOLEAN);
	}

	public int[] getArrayOfInt(String fullName) {
		return (int[]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_INTEGER);
	}

	public long[] getArrayOfLong(String fullName) {
		return (long[]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_LONG);
	}

	public double[] getArrayOfDouble(String fullName) {
		return (double[]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_DOUBLE);
	}

	public byte[] getArrayOfByte(String fullName) {
		return (byte[]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_BYTE);
	}

	public boolean[] getArrayOfBoolean(String fullName) {
		return (boolean[]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_BOOLEAN);
	}

	public int[][] getTwoDimensionalArrayOfInt(String fullName) {
		return (int[][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_INTEGER);
	}

	public long[][] getTwoDimensionalArrayOfLong(String fullName) {
		return (long[][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_LONG);
	}

	public double[][] getTwoDimensionalArrayOfDouble(String fullName) {
		return (double[][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_DOUBLE);
	}

	public byte[][] getTwoDimensionalArrayOfByte(String fullName) {
		return (byte[][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_BYTE);
	}

	public boolean[][] getTwoDimensionalArrayOfBoolean(String fullName) {
		return (boolean[][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_BOOLEAN);
	}

	public int[][][] getThreeDimensionalArrayOfInt(String fullName) {
		return (int[][][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_INTEGER);
	}

	public long[][][] getThreeDimensionalArrayOfLong(String fullName) {
		return (long[][][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_LONG);
	}

	public double[][][] getThreeDimensionalArrayOfDouble(String fullName) {
		return (double[][][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_DOUBLE);
	}

	public byte[][][] getThreeDimensionalArrayOfByte(String fullName) {
		return (byte[][][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_BYTE);
	}

	public boolean[][][] getThreeDimensionalArrayOfBoolean(String fullName) {
		return (boolean[][][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_BOOLEAN);
	}
	
	/**
	 * Set the value of the remote variable. The type and dimensions of the 
	 * new value must match that of the remote variable, or an exception will be thrown. 
	 * @param fullName the full name of the variable ${variableName}@${nodeName}
	 * @param value the new value
	 */
	public void set(String fullName, Object value) {
		lapisDataClient.setRemoteVariableValue(fullName, value);
	}
}