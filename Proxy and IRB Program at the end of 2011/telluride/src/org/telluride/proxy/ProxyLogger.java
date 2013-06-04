package org.telluride.proxy;

import java.util.logging.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.IOException;

 /**
 * @author Kolin
 * Copyright 2006 Telluride Association. All Rights Reserved.
 * <p/>
 * This software is the proprietary information of TA.
 * Use is subject to license terms.
 * Proxy logging - generates log files from proxy board problems.
 * Basically a wrapper around java.util.logging.*
 */
public final class ProxyLogger {

    private static final Date date = new Date();
    private static final SimpleDateFormat format = new java.text.SimpleDateFormat("Mddyyyy");
    private static final String datey = format.format(date);
    private static final Logger logger = Logger.getLogger("org.telluride.proxy");
    private static final Logger exceptionLogger = Logger.getLogger("org.telluride.proxy.ProxySolverException");
    private static FileHandler fh;
    private static FileHandler efh;


    //private static final ProxyLogger ourInstance = new ProxyLogger();

// --Commented out by Inspection START (7/4/06 4:47 PM):
//    public static ProxyLogger getInstance() {
//        return ourInstance;
//    }
// --Commented out by Inspection STOP (7/4/06 4:47 PM)

     /**
      *
      * @return Logger
      */
     public static Logger getLogger() {
        try {
            if (logger.getHandlers().length == 0) {
               fh = new FileHandler("proxyBoardLog"+datey+".txt");
               LogManager.getLogManager().readConfiguration();
               fh.setFormatter(new SimpleFormatter());
               logger.addHandler(fh);
               logger.setLevel(Level.ALL);
               logger.info("started logger");
            }

        }
        catch (IOException io) {
            io.printStackTrace();
        }
        return logger;
    }

    /**
     * get logger for exceptions (writes to separate log file)
     * @return exceptionLogger
     */
    public static Logger getExceptionLogger() {
        try {
            if (exceptionLogger.getHandlers().length == 0) {
               efh = new FileHandler("proxyBoardExceptionLog"+datey+".txt");
               //LogManager.getLogManager().readConfiguration();
               efh.setFormatter(new SimpleFormatter());
               exceptionLogger.addHandler(efh);
               // only log those set to "severe"
               exceptionLogger.setLevel(Level.ALL);
               exceptionLogger.severe("started exception logger");
            }

        }
        catch (IOException io) {
            io.printStackTrace();
        }
        return exceptionLogger;
    }

    public static void closeLog() {
        fh.close();
        efh.close();
    }

    private ProxyLogger() {
    }


    /**
     * call with String and java.util.logging.Level constant
     * @param level (java.util.logging.Level)
     * @param message string to be logged
     */
    public static void log(Level level, String message) {
        Logger theLog = getLogger();
        theLog.log(level, message);
        System.out.println(message);
    }

    /**
     * logs exception - all set to Level.SEVERE
     * @param message
     */
    public static void logEx(String message) {
        Logger exceptionLog = getExceptionLogger();
        exceptionLog.log(Level.SEVERE, message);
        System.err.println("Exception: " + message);
    }

}
