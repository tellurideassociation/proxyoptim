package org.telluride.proxy;

import lpsolve.LpSolveException;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;


/**
 * @author Kolin
 * Copyright 2006 Telluride Association. All Rights Reserved.
 * <p/>
 * This software is the proprietary information of TA.
 * Use is subject to license terms.
 *
 * The 'proxy board' class - assembles TAMembers and calls ProxyProblemSolver
 *
 */

public class ProxyBoard implements ProxyConstants {

    private static ProxyBoard ourInstance = new ProxyBoard();
    private ArrayList members = new ArrayList();
    private ArrayList absentMembers = new ArrayList();
    private ArrayList presentMembers = new ArrayList();
    private ArrayList possibleProxies = new ArrayList();

    private int numVariables = 0;
    private boolean attendanceTaken = false;
    private boolean isInitialized = false;

//    private boolean areProxiesLoaded = false;

    public static ProxyBoard getInstance() {
        return ourInstance;
    }

    private ProxyBoard() {
    }


    public ArrayList getMembers() {
        return members;
    }

    public ArrayList getAbsentMembers() {
        return absentMembers;
    }

    public ArrayList getPresentMembers() {
        return presentMembers;
    }

    public ArrayList getPossibleProxies() {
        return possibleProxies;
    }

    public int getNumVariables() {
        return numVariables;
    }

    public void setAttendanceTaken(boolean isAttendanceTaken) {
        this.attendanceTaken = isAttendanceTaken;
    }

    public boolean isAttendanceTaken() {
        return attendanceTaken;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Loads TAMembers from a file (specified in ProxyConstants)
     * Currently - proxy file is expected to be a csv in directory C:\telluride\data
     * @see ProxyConstants
     * @param filename
     * @throws IOException
     * @throws MemberNotFoundException
     */
    protected void loadTAMembers(String filename) throws IOException, MemberNotFoundException {
        String memberText;
        BufferedReader input = new BufferedReader(new FileReader(filename));
        while ((memberText = input.readLine()) != null) {
            String[] memberInfo = memberText.split(",");
            try {
                registerTAMember(loadTAMember(memberInfo));
            }
            catch (MemberNotFoundException e) {
                System.out.println(e.getStackTrace());
            }
        }
    }

    /**
     * add loaded member to ArrayList of members
     * @param member
     * @throws MemberNotFoundException
     */
    protected void registerTAMember(TAMember member) throws MemberNotFoundException {
        if (!(MemberLoaded(members, member))) {
            members.add(member);
        }
    }

    /**
     * parse member data (from member csv and create a TAMember)
     * @param memberData
     * @return TAMember
     */
    protected TAMember loadTAMember(String[] memberData) {

        //System.out.println("Loading " + memberData[0]);
        ProxyLogger.log(Level.INFO, "Loading " + memberData[0]);

        String lastName = memberData[0];
        String firstName = memberData[1];
        String gender = memberData[2];
        String updateDate = memberData[3];
        int j = 0;
        String[] proxies = new String[memberData.length - 4];
        for (int i = 4; i < memberData.length; i++)
            if (memberData[i] != null) {
                proxies[j] = memberData[i];
                j++;
            }
        ProxyLogger.log(Level.INFO,"Finished " + memberData[0]);

        return new TAMember(lastName, firstName, updateDate, gender, proxies);
    }


    /**
     * is a member in the ArrayList of TAMembers?
     * @param members
     * @param theMember
     * @return isLoaded
     */
    public boolean MemberLoaded(ArrayList members, TAMember theMember) {
        for (int i = 0; i < members.size(); i++) {
            if (((TAMember) members.get(i)).getLastName().equalsIgnoreCase(theMember.getLastName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * initialize proxy board
     * @throws MemberNotFoundException
     * @throws IOException
     * @throws ProxySolverException
     */
    public void init() throws MemberNotFoundException, IOException, ProxySolverException {

        ProxyLogger.log(Level.INFO, "initializing proxyBoard");
        try {
            try {
                loadTAMembers(ProxyConstants.PROXY_FILE_FULL_PATH);
            }
            catch (IOException io) {
                io.printStackTrace();
                ProxyLogger.logEx(io.getLocalizedMessage());
                throw new ProxySolverException("Failed to load proxy file");
            }

            isInitialized = true;
        }
        // fill in meaningful exception handling
        catch (MemberNotFoundException memberex) {
            ProxyLogger.log(Level.WARNING, memberex.getLocalizedMessage());
            ProxyLogger.logEx(memberex.getLocalizedMessage());
            memberex.printStackTrace();
        }

        catch (ProxySolverException proxex) {
            ProxyLogger.logEx(proxex.getMessage());
            proxex.printStackTrace();
        }
    }


    /**
     * Finder method - finds TAMember in loaded list by last name
     *
     * @param last_name
     * @return TAMember
     * @throws MemberNotFoundException
     */
    public TAMember findByLastName(String last_name) throws MemberNotFoundException {
        if (members.isEmpty())
            throw new MemberNotFoundException("No members loaded");
        for (int i = 0; i < members.size(); i++) {
            if (((TAMember) members.get(i)).getLastName().equalsIgnoreCase(last_name)) {
                return (TAMember) members.get(i);
            }
        }
        throw new MemberNotFoundException("Member " + last_name + " does not match any loaded member");
    }

    /**
     * Finds TAMember w/first and last name search
     *
     * @param first_name
     * @param last_name
     * @return TAMember
     * @throws MemberNotFoundException
     */
    public TAMember findByName(String first_name, String last_name) throws MemberNotFoundException {
        if (members.isEmpty())
            throw new MemberNotFoundException("No members loaded");
        for (int i = 0; i < members.size(); i++) {
            if (((TAMember) members.get(i)).isMatchingMember(first_name, last_name)) {
                return (TAMember) members.get(i);
            }
        }
        throw new MemberNotFoundException("Member " + first_name + " " + last_name + " does not match any loaded member");
    }

    /**
     * set the data structures for proxies - if proxy is present
     * add it to list of possible proxies, etc
     */
    public void setPresentProxies() {
        for (int j = 0; j < members.size(); j++) {
            TAMember currentMember = (TAMember) members.get(j);
            if (!(currentMember.getPresence())) {
                // get potential proxies (who are present) and add to potential proxy list
                ProxyLogger.log(Level.INFO, "setting proxies for " + currentMember.getLastName());
                String[] proxyList = currentMember.getProxyList();
                int proxyNum = 1;
                for (int idx = 0; idx < proxyList.length; idx++) {
                    try {
                        TAMember proxyMember = findByLastName(proxyList[idx]);
                        ProxyLogger.log(Level.INFO, "found possible proxy: " + proxyMember.getLastName());

                        if (proxyMember.getPresence()) {
                            ProxyLogger.log(Level.INFO, proxyMember.getLastName() + " is present - adding as possible proxy, rank " + proxyNum);
                            PossibleProxy possProx = new PossibleProxy(proxyNum, currentMember, proxyMember, numVariables);
                            // add possible proxy to both members
                            currentMember.addPotentialProxy(possProx);
                            proxyMember.addPotentialRepresent(possProx);
                            possProx.incrementProxCounts();
                            possibleProxies.add(possProx);
                            proxyNum++;
                            numVariables++;
                        }

                    }
                    catch (MemberNotFoundException e) {
                        ProxyLogger.logEx(e.getLocalizedMessage());
                    }
                }
                // if proxy list is empty, the member is not representable
                if (currentMember.getPotentialProxies().isEmpty()) {
                    currentMember.setRepresentable(false);
                    ProxyLogger.logEx("Member " + currentMember.getLastName() + " is not representable - no proxies present");

                }
                //add current member to arraylist of absent members
                ProxyLogger.log(Level.INFO, "Adding " + currentMember.getLastName() + " to list of absent members.");
                absentMembers.add(currentMember);
            } else {
                presentMembers.add(currentMember);
                ProxyLogger.log(Level.INFO, "Adding " + currentMember.getLastName() + " to list of present members.");
            }
        }
    }


    /**
     * debugging - when run on the command line, setting attendance
     * @throws IOException
     */
    public void takeManualAttendance() throws IOException {
        for (int i = 0; i < members.size(); i++) {
            TAMember currentMember = (TAMember) members.get(i);
            if (manualAttendance(currentMember).equalsIgnoreCase("y")) {
                currentMember.setPresence(true);
            } else {
                // make them unrepresentable - will be changed to TRUE if a proxy is found
                currentMember.setPresence(false);
            }

        }
        attendanceTaken = true;
    }

    /**
     * reads y/n from stdin
     * @param theMember
     * @return
     * @throws IOException
     */
    public String manualAttendance(TAMember theMember) throws IOException {

        System.out.print("\n" + theMember.getLastName() + " " + theMember.getFirstName() + " [y/n or type 'q' to quit] ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
/*        try {
            if (theMember.getLastName().indexOf("z") == -1) return "n";
            else return "y";
//        }*/
        try {
            String line = br.readLine();
            if (line == null || "q".equals(line.trim())) {
                System.out.println("Program terminated.");
                System.exit(0);
            } else
                return line.trim();
        }

        catch (IOException e) {
            System.exit(0);
        }

        System.out.println("");
        return "aborted";
    }

    /**
     * creates problem and calls LpSolve to solve the problem
     * @throws MemberNotFoundException
     * @throws IOException
     * @throws LpSolveException
     * @throws ProxySolverException
     */
    public void solveProxies() throws MemberNotFoundException, IOException, LpSolveException, ProxySolverException {
        //init();
    	if (!isInitialized) {
            init();
            // if no attendance taken, consider this a debug run and take it manually
            if (!attendanceTaken) {
                takeManualAttendance();
                //throw new ProxySolverException("no attendance taken - can't solve");
            }
        }

        setPresentProxies();
        ProxyLogger.log(Level.INFO, "Ran setPresentProxies()");

        // this should go into the 'main' or execution method
        ProxyProblemSolver solver = new ProxyProblemSolver(absentMembers, presentMembers, possibleProxies, numVariables);
        solver.execute();

    }

    /**
     * reset variables, etc.
     */
    public void cleanUpBoard() {
        for (int i=0; i< members.size(); i++) {
            ((TAMember) members.get(i)).cleanUpMember();
        }

        absentMembers = new ArrayList();
        presentMembers = new ArrayList();
        possibleProxies = new ArrayList();

        numVariables = 0;
        attendanceTaken = false;

    }

}