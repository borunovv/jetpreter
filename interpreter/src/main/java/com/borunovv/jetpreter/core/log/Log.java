package com.borunovv.jetpreter.core.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logger
 *
 * @author borunovv
 */
public final class Log {
    private static Logger logger = LoggerFactory.getLogger(Log.class);
    private Log() {}

    public static void trace(String msg) {
        logger.trace(msg);
    }
    public static void trace(String format, Object ... args) {
        logger.trace(format, args);
    }

    public static void debug(String msg) {
        logger.debug(msg);
    }
    public static void debug(String format, Object ... args) {
        logger.debug(format, args);
    }

    public static void info(String msg) {
        logger.info(msg);
    }
    public static void info(String format, Object ... args) {
        logger.info(format, args);
    }

    public static void warn(String msg) {
        logger.warn(msg);
    }
    public static void warn(String format, Object ... args) {
        logger.warn(format, args);
    }
    public static void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    public static void error(String msg) {
        logger.error(msg);
    }
    public static void error(String format, Object ... args) {
        logger.error(format, args);
    }
    public static void error(String msg, Throwable t) {
        logger.error(msg, t);
    }
}
