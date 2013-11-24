package edu.osu.lapis;

import java.util.Properties;

//NOTE: probably only need this for MatlabLapis
public class LapisLogging {
	
	final static Properties newProps = new Properties(System.getProperties());
	static boolean isInit = false;
	
	static {
		newProps.setProperty("org.restlet.engine.loggerFacadeClass","org.restlet.ext.slf4j.Slf4jLoggerFacade");
		newProps.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
		newProps.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
		newProps.setProperty("org.slf4j.simpleLogger.log.edu.osu.lapis", "trace");
	}
	
	public static synchronized void init() {
		if(!isInit){			
			System.setProperties(newProps);
		}
	}
}