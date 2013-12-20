package edu.osu.lapis;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Priority;

@SuppressWarnings("deprecation")
public class Logger {
	
	private final org.apache.log4j.Logger logger; 
	
	private enum Level {
		TRACE, DEBUG, INFO, WARN, ERROR
	}
	
	public static Logger getLogger(Class<?> cls) {
		return new Logger(cls);
	}
	
	public static Logger getLogger(String name) {
		return new Logger(name);
	}
	
	private Logger(Class<?> cls) {
		logger = org.apache.log4j.Logger.getLogger(cls);
	}
	
	private Logger(String name) {
		logger = org.apache.log4j.Logger.getLogger(name);
	}
	
	public static void setLevel(String category, org.apache.log4j.Level level) {
		org.apache.log4j.Logger.getLogger(category).setLevel(level);
	}
	
	public static void setLevel(String category, String level) {
		Validate.notEmpty(level, "Level name must be specified.");
		org.apache.log4j.Logger log4jLogger = org.apache.log4j.Logger.getLogger(category);
		org.apache.log4j.Level originalLevel = log4jLogger.getLevel();
		org.apache.log4j.Level newLevel = org.apache.log4j.Level.toLevel(level, originalLevel);
		if(!newLevel.toString().toLowerCase().equals(level.toLowerCase())) {
			getLogger(Logger.class).warn("Unable to set level for category '%s' to level '%s'.", category, level);
		} else {
			setLevel(category, newLevel);
		}
	}
	
	public void trace(String msg, Object ... objects) {
		if(logger.isTraceEnabled()) 
			doLogging(Level.TRACE, msg, objects);
	}
	
	public void debug(String msg, Object ... objects) {
		if(logger.isDebugEnabled())
			doLogging(Level.DEBUG, msg, objects);
	}    
	
	public void info(String msg, Object ... objects) {
		if(logger.isInfoEnabled())
			doLogging(Level.INFO, msg, objects);
	}
	
	public void warn(String msg, Object ... objects) {
		if(logger.isEnabledFor(Priority.WARN))
			doLogging(Level.WARN, msg, objects);
	}
	
	public void error(String msg, Object ... objects) {
		if(logger.isEnabledFor(Priority.ERROR)) 
			doLogging(Level.ERROR, msg, objects);
	}
	
	public void trace(Throwable throwable, String msg, Object ... objects) {
		if(logger.isTraceEnabled()) 
			doLogging(Level.TRACE, throwable, msg, objects);
	}

    public void debug(Throwable throwable, String msg, Object ... objects) {
    	if(logger.isDebugEnabled())
			doLogging(Level.DEBUG, throwable, msg, objects);
    }

    public void info(Throwable throwable, String msg, Object ... objects) {
    	if(logger.isInfoEnabled())
			doLogging(Level.INFO, throwable, msg, objects);
    }

    public void warn(Throwable throwable, String msg, Object ... objects) {
    	if(logger.isEnabledFor(Priority.WARN))
			doLogging(Level.WARN, throwable, msg, objects);
    }

    public void error(Throwable throwable, String msg, Object ... objects) {
    	if(logger.isEnabledFor(Priority.ERROR)) 
    		doLogging(Level.ERROR, throwable, msg, objects);
    }
    
    private void doLogging(Level level, String msg, Object ... objects) {
    	String message = String.format(msg, objects);
    	switch (level) {
    	case TRACE: logger.trace(message); break;
    	case DEBUG: logger.debug(message); break;
    	case INFO: logger.info(message); break;
    	case WARN: logger.warn(message); break;
    	case ERROR: logger.error(message); break;
    	default: throw new IllegalArgumentException("Unrecognized level: " + level);
    	}
    }
    
    private void doLogging(Level level, Throwable throwable, String msg, Object ... objects) {
    	String message = String.format(msg, objects);
    	switch (level) {
    	case TRACE: logger.trace(message, throwable); break;
    	case DEBUG: logger.debug(message, throwable); break;
    	case INFO: logger.info(message, throwable); break;
    	case WARN: logger.warn(message, throwable); break;
    	case ERROR: logger.error(message, throwable); break;
    	default: throw new IllegalArgumentException("Unrecognized level: " + level);
    	}
    }
}
