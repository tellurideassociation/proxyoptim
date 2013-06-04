/*
 * Copyright 2006 Telluride Association.
 * All Rights Reserved.
 *
 * This software is the proprietary information of TA.
 * Use is subject to license terms.
 */

package org.telluride.proxy;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import lpsolve.AbortListener;
import lpsolve.MsgListener;

import java.util.*;
import java.util.logging.Level;

/**
 * Copyright 2006 Telluride Association. All Rights Reserved.
 * <p/>
 * This software is the proprietary information of TA.
 * Use is subject to license terms.
 *
 * constructs and runs the LpSolve problem that solves the proxies
 *
 * @author Kolin Ohi
 */
public class ProxyProblemSolver implements ProxyConstants {

    private double[] objFunArray;

    private int numVariables;
    private ArrayList absentMembers = new ArrayList();
    private ArrayList presentMembers = new ArrayList();
    private ArrayList possibleProxies = new ArrayList();

    public ProxyProblemSolver(ArrayList absentMem, ArrayList presentMem, ArrayList possProx, int numVariables) {
        this.numVariables = numVariables;
        this.absentMembers = absentMem;
        this.presentMembers = presentMem;
        this.possibleProxies = possProx;
        ProxyLogger.log(Level.INFO,"numVars="+this.numVariables+" absetMems="+ this.absentMembers.size()+" presentMems="+this.presentMembers.size());
        // initialize objFuncArray
        objFunArray = new double[numVariables];
    }


    /**
     * runs the problem
     * @throws LpSolveException
     */
    public void execute() throws LpSolveException {

        LpSolve problem = LpSolve.makeLp(0, numVariables);
        String[] colNames = new String[numVariables];

        problem.setLpName("TAProxies");
        problem.setVerbose(LpSolve.FULL);


        ProxyLogger.log(Level.INFO, "created problem with " + numVariables + " variables");
        ProxyLogger.log(Level.INFO, absentMembers.size() + " absent members");
        ProxyLogger.log(Level.INFO, presentMembers.size() + " present members");

        //ProxyLogger.logEx("Tolerance for int -> 0: " + problem.getEpsb());

        for (int i = 0; i < absentMembers.size(); i++) {

            ProxyLogger.log(Level.INFO, "Getting proxies for " + ((TAMember) absentMembers.get(i)).getLastName());
            TAMember absentia = (TAMember) absentMembers.get(i);

            if (absentia.isRepresentable()) {
                ArrayList potProx = absentia.getPotentialProxies();
                ProxyLogger.log(Level.INFO, "potential proxies: " + potProx.size());

                for (int it = 0; it < potProx.size(); it++) {
                    PossibleProxy prox = (PossibleProxy) potProx.get(it);
                    objFunArray[prox.getVariableNum()] = RANKING_INVERSE[prox.getRank()];
                    ProxyLogger.log(Level.INFO, "---variable number: " + (prox.getVariableNum()) + " " + prox.getPotentialProxy().getLastName() + " - rank=" + prox.getRank() + ", value=" + RANKING_INVERSE[prox.getRank()]);
                    colNames[prox.getVariableNum()] = prox.getMember().getLastName() + "-" + prox.getPotentialProxy().getLastName();
                }
            }
        }

        AbortListener abortfunc = new AbortListener() {
            public boolean abortfunc(LpSolve problem, Object handle) {
                /* If set to true, then solve is aborted and returncode will indicate this. */
                return false;
            }
        };
        problem.putAbortfunc(abortfunc, null);

        /* Set a message function. Again optional */
        MsgListener msgfunc = new MsgListener() {
            public void msgfunc(LpSolve problem, Object handle, int msg) {
                System.out.println("Message = " + msg);
            }
        };

        problem.putMsgfunc(msgfunc, null,
                LpSolve.MSG_PRESOLVE | LpSolve.MSG_LPFEASIBLE | LpSolve.MSG_LPOPTIMAL
                        | LpSolve.MSG_MILPEQUAL | LpSolve.MSG_MILPFEASIBLE | LpSolve.MSG_MILPBETTER);

        /* set function as string (note setting as array screws up, since their indexes seem to start at 1*/
        String objFunc = "";

        for (int i = 0; i < objFunArray.length; i++) {
            objFunc += objFunArray[i] + " ";
        }
/*        objFunc += "(" + (objFunArray[0]-ProxyConstants.UNREP_FACTOR)   + ") +" + ProxyConstants.UNREP_FACTOR;
        for (int i = 1; i < objFunArray.length; i++) {
            objFunc += " (" + (objFunArray[i]-ProxyConstants.UNREP_FACTOR) + ") "  + (objFunArray[i] + 1) + " +" + ProxyConstants.UNREP_FACTOR;
        }
*/        ProxyLogger.log(Level.INFO, "obj fun:" + objFunc);
        problem.strSetObjFn(objFunc);

        /* set to minimize */
        problem.setMaxim();
        ProxyLogger.log(Level.INFO, "Set to maximize");

        /*
        *  update column names and make them binary
        *  the output makes me think the constraints are backwards...
        *   -- actually - it's missing the first value in the index (or the last)
        *   (the constraints are all one value short...
        *   objective function also starts with value 2 for first proxy
        *  note:  the setObjFun and set constraints methods take
        *         arrays as arguments, but the indexes seem to start at 1 instead of 0,
        *         so if you actually pass an array, you'll be off by one...
        *         passing strings instead...
        */


        for (int j = 0; j < colNames.length; j++) {
            problem.setColName(j + 1, colNames[j]);
            ProxyLogger.log(Level.INFO, "Col name: " + problem.getColName(j + 1) + " " + colNames[j]);

            //binary variables are also integers, no?
            // problem.setInt(j + 1, true);
            problem.setBinary(j + 1, true);
            ProxyLogger.log(Level.INFO, "Set col " + (j + 1) + " to binary");
        }

        ProxyLogger.log(Level.INFO, "Adding constraints");

        addProblemConstraints(absentMembers, presentMembers, problem);
        ProxyLogger.log(Level.INFO, "Added constraints");
        //problem.setBasiscrash(LpSolve.CRASH_MOSTFEASIBLE);


        problem.solve();
        System.out.println(".... running printLp ....");
        problem.printLp();
        problem.writeLp("proxyLpfile");

//        System.out.println("printing tableau...");
//        problem.printTableau();

        System.out.println("Status: " + problem.getStatus());

        if (problem.getStatus() == LpSolve.OPTIMAL) {
            ProxyLogger.log(Level.INFO, "Solved - found optimal solution");
            setProxies(problem);

        }

        System.out.println(".... running printObjective....");

        problem.printObjective();

        ProxyLogger.log(Level.INFO, "Printing solution");

        problem.printSolution(1);
        System.out.println(".... running printConstraints ....");
        problem.printConstraints(1);
        System.out.println(".... running printDuals ....");
        problem.printDuals();

        cleanUp(problem);

    }

    /**
     * Adds constraints to problem -- each absent member should be represented by 1 person
     * each present member can represent at most 2 absent members
     *
     * @param absent  ArrayList of absent members
     * @param present ArrayList of present memberse
     * @param problem LpSolve problem
     * @throws LpSolveException
     */
    private void addProblemConstraints(ArrayList absent, ArrayList present, LpSolve problem) throws LpSolveException {
        try {
            ProxyLogger.log(Level.INFO, "Circling through absent members");

            for (int p = 0; p < absent.size(); p++) {
                TAMember absMember = (TAMember) absent.get(p);
                double[] absCons = new double[numVariables];
                for (int i = 0; i < numVariables; i++) {
                    absCons[i] = 0;
                }
                for (int k = 0; k < absMember.mightBeRepVars.size(); k++) {
                    absCons[((Integer) absMember.mightBeRepVars.get(k)).intValue()] = 1;
                }
                // each absent member can be represented by only one person
                ProxyLogger.log(Level.INFO, "adding absent constraint");

                String absentConstraint = "";
                for (int l = 0; l < absCons.length; l++) {
                    absentConstraint += " " + absCons[l];
                }
                ProxyLogger.log(Level.INFO, "Absent constraint: " + absentConstraint.trim());

                problem.strAddConstraint(absentConstraint.trim(), LpSolve.LE, 1);
                ProxyLogger.log(Level.INFO, "Added absent constraint (<=1)");

                //problem.addConstraint(absCons, LpSolve.EQ, 1);
            }
        }
        catch (LpSolveException absEx) {
            ProxyLogger.logEx("Hit exception adding absent constraints" + absEx.getLocalizedMessage());
            absEx.printStackTrace();
        }

        try {
            Iterator presentIter = present.iterator();

            while (presentIter.hasNext()) {
                TAMember presMember = (TAMember) presentIter.next();
                double[] presCons = new double[numVariables];
                for (int i = 0; i < numVariables; i++) {
                    presCons[i] = 0;
                }
                for (int k = 0; k < presMember.mightRepVars.size(); k++) {
                    presCons[((Integer) presMember.mightRepVars.get(k)).intValue()] = 1;
                }
                // each present member can represent by only twp people
                String presentConstraint = "";
                for (int i = 0; i < presCons.length; i++) {
                    presentConstraint += " " + presCons[i];
                }

                presentConstraint = presentConstraint.trim();

                ProxyLogger.log(Level.INFO, "Adding present constraint: " + presentConstraint + "<=2");

                problem.strAddConstraint(presentConstraint, LpSolve.LE, 2);
//                problem.addConstraint(presCons, LpSolve.LE, 2);
//                ProxyLogger.log(Level.INFO, "Present constraint: " + presCons.length + "<=2");

            }
        }
        catch (LpSolveException presEx) {
            ProxyLogger.logEx("Hit exception adding present constraints" + presEx.getLocalizedMessage());
            presEx.printStackTrace();
        }
    }

    private void cleanUp(LpSolve problem) {
        if (problem.getLp() != 0)
            problem.deleteLp();
    }

    /**
     * set proxies based on absent members' proxy lists - checks presence
     * of proxies and adds them to an ArrayList of PossibleProxy
     * @param problem LpSolve
     * @throws LpSolveException
     */
    private void setProxies(LpSolve problem) throws LpSolveException {
        // HACK - setting all absent members to unrepresentable
        for (int d=0; d < absentMembers.size(); d++){
            ((TAMember) absentMembers.get(d)).setRepresentable(false);
        }


        double[] variables = new double[numVariables];
        problem.getVariables(variables);

        for (int i = 0; i < variables.length; i++) {

            System.out.println(problem.getColName(i + 1) + " = " + variables[i]);
            PossibleProxy possy = (PossibleProxy) possibleProxies.get(i);
            TAMember absenty = possy.getMember();
            TAMember presenty = possy.getPotentialProxy();

            if (variables[i] == 1) {
                absenty.setRepresentable(true);
                absenty.setRepresentation(presenty);
                presenty.setRepresenting(absenty);
            }

        }

        for (int j = 0; j < absentMembers.size(); j++) {
            TAMember theMember = (TAMember) absentMembers.get(j);
            if (theMember.isRepresentable()) {
                System.out.println(theMember.getLastName() + " represented by " + theMember.getRepresentation().getLastName());
            } else {
                System.out.println(theMember.getLastName() + " is not representable ");
            }
        }

    }

}