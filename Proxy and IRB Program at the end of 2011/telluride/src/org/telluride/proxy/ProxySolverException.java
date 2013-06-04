/*
 * Copyright 2002 Hotwire. All Rights Reserved.
 *
 * This software is the proprietary information of Hotwire.
 * Use is subject to license terms.
 */
package org.telluride.proxy;

/**
 * exception for proxy solver stuff
 * @author Kolin Ohi
 */
public class ProxySolverException extends Exception {

    ////////////////////////////////////////////////////////////////
    // ATTRIBUTES

    ////////////////////////////////////////////////////////////////
    // CONSTRUCTORS

    /**
     * Constructor description.
     * Developer's notes.
     *   if there's time, add something to clean up the absent members, numVariables, etc.
    */
    public ProxySolverException() {
        super();
    }


    public ProxySolverException(String arg0) {
        super(arg0);
    }

}