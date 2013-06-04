/*
 * Copyright 2006 Telluride Association. All Rights Reserved.
 *
 * This software is the proprietary information of TA.
 * Use is subject to license terms.
 */
package org.telluride.proxy;

import lpsolve.*;
import java.io.IOException;


/**
 * Tester - calls ProxyBoard
 *
 * @author Kolin
 */
public class TestProxySolve {

    public static ProxyBoard board = ProxyBoard.getInstance();

    public TestProxySolve() {
    }

    public static void main (String[] args) throws IOException, MemberNotFoundException, LpSolveException,ProxySolverException {
        // statements
        board.solveProxies();


    }

}