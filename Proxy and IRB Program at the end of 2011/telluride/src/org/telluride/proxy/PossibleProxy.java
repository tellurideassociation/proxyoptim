/*
 * Copyright 2006 Telluride Association. All Rights Reserved.
 *
 * This software is the proprietary information of TA.
 * Use is subject to license terms.
 */
package org.telluride.proxy;

/**
 * Class containing a possible proxy.
 * Contains 2 TAMembers - one that's absent and another that's a potential proxy.
 *
 * @author Kolin Ohi
 */
public class PossibleProxy {


    ////////////////////////////////////////////////////////////////
    // ATTRIBUTES
    private int variableNum;
    private int rank;
    private TAMember member;
    private TAMember potentialProxy;

    private int memberNumProx = 0;
    private int possProxNumProx = 0;


    ////////////////////////////////////////////////////////////////
    // CONSTRUCTORS

    /**
     *
     * @param rank
     * @param member TAMember
     * @param potentialProxy TAMember
     * @param variableNum
     */
    public PossibleProxy(int rank, TAMember member, TAMember potentialProxy, int variableNum) {
        this.rank = rank;
        this.potentialProxy = potentialProxy;
        this.variableNum = variableNum;
        this.member = member;

        potentialProxy.mightRepVars.add(new Integer(variableNum));
        member.mightBeRepVars.add(new Integer(variableNum));

    }

    ////////////////////////////////////////////////////////////////
    // METHODS

    public int getVariableNum() {
        return variableNum;
    }

    public int getRank() {
        return rank;
    }

    public TAMember getMember() {
        return member;
    }

    public TAMember getPotentialProxy() {
        return potentialProxy;
    }

    public void incrementProxCounts() {
        memberNumProx++;
        possProxNumProx++;
    }

    public int getMemberNumProx() {
        return memberNumProx;
    }

    public int getPossProxNumProx() {
        return possProxNumProx;
    }


}