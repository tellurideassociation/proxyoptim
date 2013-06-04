package org.telluride.proxy;

import java.util.ArrayList;

/*
* Copyright 2006 Telluride Association. All Rights Reserved.
*
* This software is the proprietary information of TA.
* Use is subject to license terms.
*/

/**
 * Holds an association member and his/her proxies
 *
 * @author Kolin
 */
public class TAMember {

    ////////////////////////////////////////////////////////////////
    // ATTRIBUTES

    private static int numPresentMembers = 0;
    private String first_name;
    private String last_name;
    private String updated;
    private String[] proxyList;
    //ArrayList holding MightRepresent
    private ArrayList potentialProxiesHeld = new ArrayList();
    private ArrayList potentialProxies = new ArrayList();

    private String gender;
    private boolean isPresent = false;
    private boolean isRepresented;

    private boolean representable = true;

    // keep the actual variable numbers
    protected ArrayList mightRepVars = new ArrayList();
    protected ArrayList mightBeRepVars = new ArrayList();


    private TAMember representedBy;
    private ArrayList representing = new ArrayList();

    ////////////////////////////////////////////////////////////////
    // CONSTRUCTORS

    /**
     * Constructor description.
     * Developer's notes.
     *
     * @param last_name
     * @param first_name
     * @param gender     Mr or Mrs
     * @param updated    date list was last updated
     * @param proxyList  list of TA members that can be proxies
     */
    public TAMember(String last_name, String first_name,  String updated, String gender, String[] proxyList) {
        // statements
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.updated = updated;
        this.proxyList = proxyList;
    }

    ////////////////////////////////////////////////////////////////
    // METHODS

    /**
     * @return isPresent
     */
    public boolean getPresence() {
        // statements
        return isPresent;
    }

    /**
     * @param presenceIn
     */
    public void setPresence(boolean presenceIn) {
        if ((isPresent) && !(presenceIn)) {
            numPresentMembers--;
        } else if (!(isPresent) && (presenceIn)) {
            numPresentMembers++;
        }
        isPresent = presenceIn;
        if (isPresent) {
            setRepresented(isPresent);
            setRepresentable(isPresent);
        }
    }


    public boolean isRepresentable() {
        return representable;
    }

    public void setRepresentable(boolean isRepresentable) {
        this.representable = isRepresentable;
    }

    /**
     * @return isRepresented
     */
    public boolean getRepresented() {
        return isRepresented;
    }

    /**
     * @param representedIn
     */
    private void setRepresented(boolean representedIn) {
        isRepresented = representedIn;
    }

    public String getFullName() {
        return this.first_name + " " + this.last_name;
    }


    /**
     * @return proxyList String[] of TAMember names
     */
    public String[] getProxyList() {
        return this.proxyList;
    }

    /**
     * @return last_name
     */
    public String getLastName() {
        return this.last_name;
    }

    /**
     * @return last_name
     */
    public String getFirstName() {
        return this.first_name;
    }

    /**
     * does the member match? (first & last name)
     *
     * @param first_name
     * @param last_name
     * @return boolean - is a match
     */
    public boolean isMatchingMember(String first_name, String last_name) {
        if (first_name.equalsIgnoreCase(this.getFirstName()) && last_name.equalsIgnoreCase(this.getLastName())) {
            return true;
        } else {
            return false;
        }
    }

    public String getUpdated() {
        return this.updated;
    }

    public String getGender() {
        return this.gender;
    }

    /**
     * @param possProx PossibleProxy potential proxy
     */
    public void addPotentialProxy(PossibleProxy possProx) {
        potentialProxies.add(possProx);
    }

    public ArrayList getPotentialProxies() {
        return potentialProxies;
    }

    public void setRepresentation(TAMember currentProxy) {
        this.representedBy = currentProxy;
    }

    public TAMember getRepresentation() {
        return this.representedBy;
    }

    public ArrayList getRepresenting() {
        return this.representing;
    }

    public void setRepresenting(TAMember hasProxyFor) {
        representing.add(hasProxyFor);
    }

    public ArrayList getPotentialProxiesHeld() {
        return potentialProxiesHeld;
    }

    public void addPotentialRepresent(PossibleProxy mightRep) {
        /*
        * note to self - even if the same member is represented, this will never work, since the
        * member will be contained in a different MightRepresent object
        */
        if (!(potentialProxiesHeld.contains(mightRep))) {
            potentialProxiesHeld.add(mightRep);
        }
    }

    /**
     * erase the attributes of the member that are set by the proxy board (clean up)
     * (Leave the member instantiated, however...)
     */

    public void cleanUpMember() {
        numPresentMembers = 0;
        potentialProxiesHeld = new ArrayList();
        potentialProxies = new ArrayList();

        isPresent = false;
        isRepresented = false;
        representable = true;

        mightRepVars = new ArrayList();
        mightBeRepVars = new ArrayList();
        representedBy = null;
        representing = new ArrayList();

    }
}