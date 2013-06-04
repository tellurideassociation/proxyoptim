/*
 * Copyright 2006 Telluride Association. All Rights Reserved.
 *
 * www.tellurideassociation.org
 */
package org.telluride.proxy;

/**
 * Constants for TA proxy calculator
 * @author Kolin
 */
public interface ProxyConstants {

    public static final String PROXY_FILENAME = "proxies.csv";
    public static final String PROXY_FILE_FULL_PATH = "C:\\telluride\\data\\proxies.csv";
    public static final String PROXY_INSTRUCTIONS_FILE = "C:\\telluride\\data\\proxy_instructions.txt";

    public static final String PROXY_FILE_FULL_PATH_OLD = "C:\\telluride\\src\\csv2xml\\target\\proxies.csv";
    public static final String PROXY_INSTRUCTIONS_FILE_OLD = "C:\\telluride\\src\\csv2xml\\target\\proxy_instructions.txt";

    public static final int MAX_HOLDABLE_PROXIES = 2;
    public static final double QUORUM_PRESENT = .5;
    public static final double QUORUM_TOTAL = .75;
    public static final double UNREP_FACTOR = 1000;
    public static final double[] RANKING_INVERSE = {111,110,109,108,107,106,105,104,103,102,101};

}