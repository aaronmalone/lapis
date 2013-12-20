package edu.osu.lapis;


//NOTE: probably only need this for MatlabLapis
public class LapisLogging {
	
	public static synchronized void init() {
		System.setProperty("org.restlet.engine.loggerFacadeClass","org.restlet.ext.slf4j.Slf4jLoggerFacade");
	}
}
