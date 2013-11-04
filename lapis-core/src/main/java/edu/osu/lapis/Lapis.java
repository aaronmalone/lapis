package edu.osu.lapis;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;

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

	static {
		//TODO PROBABLY MOVE THIS AT SOME POINT
		//set this system property so we can use SLF4J
		System.setProperty("org.restlet.engine.loggerFacadeClass","org.restlet.ext.slf4j.Slf4jLoggerFacade");
	}
	
	private LocalDataTable localDataTable;
	private LapisDataClient lapisDataClient;
	
	public Lapis() {
		this(getDefaultProperties());
	}

	public Lapis(String propertiesFileName) {
		this(getProperties(propertiesFileName));
	}
	
	public Lapis(Properties properties) {
		ApplicationContext context = getApplicationContext(properties);
		localDataTable = context.getBean(LocalDataTable.class);
		lapisDataClient = context.getBean(LapisDataClient.class);
		RestletServer restletServer = context.getBean(RestletServer.class);
		restletServer.initialize();
	}
	
	private static Properties getDefaultProperties() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static Properties getProperties(String propertiesFileName) {
		if(propertiesFileName.toLowerCase().endsWith(".json")) {
			return parseJsonPropertiesFile(propertiesFileName);
		} else {
			return getPropertiesFromRegularPropertiesFile(propertiesFileName);
		}
	}
	
	private static Properties getPropertiesFromRegularPropertiesFile(String propertiesFileName) {
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
	
	private static ApplicationContext getApplicationContext(Properties properties) {
		PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		placeholderConfigurer.setProperties(properties);
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(LapisConfiguration.class);
		context.addBeanFactoryPostProcessor(placeholderConfigurer);
		context.refresh();
		return context;
	}

	public void publish(String localName, Object reference) {
		publish(localName, reference, LapisPermission.READ_WRITE, true);
	}
	
	public void publish(String localName, Object reference, LapisPermission lapisPermission) {
		publish(localName, reference, lapisPermission, true);
	}
	
	public void publish(String localName, Object reference, boolean isReady) {
		publish(localName, reference, LapisPermission.READ_WRITE, isReady);
	}
	
	public void publish(String localName, Object reference, LapisPermission lapisPermission, boolean isReady) {
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
}
