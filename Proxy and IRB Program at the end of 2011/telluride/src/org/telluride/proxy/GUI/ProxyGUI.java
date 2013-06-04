/*
 * Copyright 2002 Hotwire. All Rights Reserved.
 *
 * This software is the proprietary information of Hotwire.
 * Use is subject to license terms.
 */
package org.telluride.proxy.GUI;

import org.telluride.proxy.*;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.text.NumberFormat;

import lpsolve.LpSolveException;


/**
 * Copyright 2006 Telluride Association. All Rights Reserved.
 * <p/>
 * This software is the proprietary information of TA.
 * Use is subject to license terms.
 *
 * the main gui for telluride proxies
 * @author kolin
 * updated 2010, michael
 */
public class ProxyGUI extends JPanel
        implements  ProxyConstants //,ActionListener
    {

    ProxyBoard proxyBoard;
    JFrame theFrame;
    JLabel TALogo;

    JLabel absentLabel;
    JLabel presentLabel;
    JLabel representableLabel;
    JLabel quorumLabel;
    JPanel quorumPanel;
    int representableCount = 0;

    boolean haveQuorum;

    JPanel attendancePanel;
    JPanel attendBoxPanel;
    JPanel rightSidePanel;
    JTextArea proxyOutputPanel;
    JPanel consolePanel;
    ArrayList checkboxList = new ArrayList();
    JCheckBox[] memberBoxes;
    JTextArea logArea;
    String outputText = "";


    /**
     *  constructor - get instance
     */
    public ProxyGUI() {
        super(new BorderLayout());
        proxyBoard = ProxyBoard.getInstance();

        if (!(proxyBoard.isInitialized())) {
            try {
                proxyBoard.init();
            }
            catch (IOException IO) {
                ProxyLogger.logEx(IO.getMessage());
            }
            catch (MemberNotFoundException m) {
                ProxyLogger.logEx(m.getMessage());
            }
            catch (ProxySolverException s) {
                ProxyLogger.logEx(s.getMessage());
            }
        }

    }

    ////////////////////////////////////////////////////////////////
    // METHODS

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     *
     * @param path path to the file
     * @return ImageIcon returns image
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = ProxyGUI.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }


    public void actionPerformed(ActionEvent actionEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Create and set up the window.
        theFrame = new JFrame("TA Proxy Board");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        memberBoxes = new JCheckBox[proxyBoard.getMembers().size()];

        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        BorderLayout layout = new BorderLayout();

        JPanel background = new JPanel(layout);

        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        background.setBackground(Color.white);

        JPanel titlePanel = new JPanel(new BorderLayout());
        Box buttonBox = new Box(BoxLayout.Y_AXIS);
        JButton runProxyButton = new JButton("Run proxies");
        runProxyButton.addActionListener(new RunProxyListener());

        buttonBox.add(runProxyButton);
        titlePanel.add(BorderLayout.EAST,buttonBox );


     //   JLabel titleLabel = new JLabel();
     //   titleLabel.setFont(titleLabel.getFont().deriveFont(Font.ITALIC));
     //   titleLabel.setText("TA Proxy Calculator");
     //   titleLabel.setVisible(true);

     //   titlePanel.add(BorderLayout.SOUTH,titleLabel);


        titlePanel.setBackground(Color.white);
        titlePanel.setOpaque(true);

        background.add(BorderLayout.BEFORE_FIRST_LINE,titlePanel);

        //Set up the picture.
        TALogo = new JLabel();
        TALogo.setFont(TALogo.getFont().deriveFont(Font.ITALIC));
        TALogo.setHorizontalAlignment(JLabel.CENTER);
        TALogo.setVerticalAlignment(JLabel.TOP);
        TALogo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        //A better program would compute this.
        TALogo.setPreferredSize(new Dimension(235+10, 86+10));

        ImageIcon icon = createImageIcon("images/ta_logo.gif");
        TALogo.setIcon(icon);
        TALogo.setToolTipText("Telluride Association");

        // add logo to the background (left side)
        titlePanel.add(BorderLayout.NORTH,TALogo);
        //background.setAlignmentY(JPanel.TOP_ALIGNMENT);
        background.setOpaque(true);

        // redirect System.out to a text panel
        logArea = new JTextArea();
        logArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));
        logArea.setBackground(Color.black);

        logArea.setForeground(Color.red);
        logArea.setEditable(false);
        //logArea.setLineWrap(true);
        logArea.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        logArea.setAlignmentY(JTextArea.BOTTOM_ALIGNMENT);

        OutputStream wrapper = new BufferedStreamWrapper(logArea);
        System.setErr(new PrintStream(wrapper));
        System.setOut(new PrintStream(wrapper));


        JScrollPane logScroller = new JScrollPane(logArea);
        logScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        logScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        logScroller.setBackground(Color.white);
        logScroller.setOpaque(true);
        //logScroller.setBounds(10, 10, 10, 10);
        logScroller.setBorder(BorderFactory.createEtchedBorder(15,Color.blue, Color.blue));


        logScroller.setPreferredSize(new Dimension(500,100));


        attendBoxPanel = new JPanel(new SpringLayout());


        attendBoxPanel.setBackground(Color.white);
        attendBoxPanel.setForeground(Color.red);
        attendBoxPanel.setAlignmentY(JPanel.LEFT_ALIGNMENT);


        for (int i = 0; i < proxyBoard.getMembers().size(); i++) {
            JCheckBox c = new JCheckBox();
            JLabel checkName = new JLabel();
            checkName.setBackground(Color.white);
            checkName.setForeground(Color.darkGray);
            checkName.setText(((TAMember) proxyBoard.getMembers().get(i)).getFullName());
            checkName.setAlignmentX(JLabel.LEFT_ALIGNMENT);
            c.setSelected(false);
            c.setBackground(Color.white);
            c.setForeground(Color.red);
            c.setHorizontalAlignment(JCheckBox.RIGHT);
            checkboxList.add(c);
            attendBoxPanel.add(c);
            attendBoxPanel.add(checkName);

        }

        SpringUtilities.makeCompactGrid(attendBoxPanel,
                                proxyBoard.getMembers().size(), 2, //rows, cols
                                6, 6,        //initX, initY
                                2, 2);

        attendancePanel = new JPanel(new BorderLayout());
        //attendancePanel.add(BorderLayout.EAST,nameBox);
        attendancePanel.add(attendBoxPanel,BorderLayout.CENTER);
        attendancePanel.setOpaque(true);
        attendancePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        attendancePanel.setBackground(Color.white);


        JScrollPane scroller = new JScrollPane(attendancePanel);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scroller.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);
        scroller.setBackground(Color.white);
        scroller.setOpaque(true);
        //scroller.setBounds(10, 10, 10, 10);

        theFrame.setBackground(Color.white);
        background.setBackground(Color.white);

        scroller.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        //scroller.setPreferredSize(new Dimension(300,500));

        background.add(BorderLayout.CENTER, scroller);

        proxyOutputPanel = new JTextArea();
        proxyOutputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));
        proxyOutputPanel.setBackground(Color.white);
        proxyOutputPanel.setEditable(true);
        //proxyOutputPanel.setPreferredSize(new Dimension(300,500));
        try {
            setInstructionText();
        }
        catch (IOException e) {
            ProxyLogger.logEx("Unable to read instructions");
            ProxyLogger.logEx(e.getMessage());
            outputText = "burp - IO Exception on instructions file\n";
        }

        proxyOutputPanel.append(outputText);
        JScrollPane outScroller = new JScrollPane(proxyOutputPanel);
        outScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        outScroller.setBackground(Color.white);
        outScroller.setOpaque(true);
        outScroller.setBounds(10, 10, 10, 10);

        rightSidePanel = new JPanel(new BorderLayout());
        rightSidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        rightSidePanel.add(BorderLayout.CENTER, outScroller);
        rightSidePanel.setPreferredSize(new Dimension(400,650));


        absentLabel = new JLabel();
        absentLabel.setBackground(Color.white);
        absentLabel.setText("");
        presentLabel = new JLabel();
        presentLabel.setText("");
        presentLabel.setBackground(Color.white);
        representableLabel = new JLabel();
        representableLabel.setBackground(Color.white);
        absentLabel.setText("");
        quorumLabel = new JLabel();
        quorumLabel.setBackground(Color.white);
        quorumLabel.setText("");

        quorumPanel = new JPanel();
        quorumPanel.setLayout(new BoxLayout(quorumPanel,BoxLayout.Y_AXIS));
        quorumPanel.setBackground(Color.white);

        quorumPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        quorumPanel.add(presentLabel);
        quorumPanel.add(absentLabel);
        quorumPanel.add(representableLabel);
        quorumPanel.add(quorumLabel);

        rightSidePanel.add(BorderLayout.BEFORE_FIRST_LINE,quorumPanel);

        rightSidePanel.setBackground(Color.white);
        //background.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        background.add(BorderLayout.EAST, rightSidePanel);

/*
        JPanel logPanel = new TextAreaLogger(true, true);
*/

        theFrame.getContentPane().add(BorderLayout.SOUTH, logScroller);
//        background.add(BorderLayout.SOUTH,logScroller);

        theFrame.getContentPane().add(BorderLayout.CENTER,background);

        //Display the window.
        theFrame.setBounds(150, 150, 125, 125);
        theFrame.pack();
        theFrame.setVisible(true);

    }

    /**
     * update text about whether there's a quorum
     */
    public void updateQuorumText() {

        representableCount = 0;
        NumberFormat percFormat = NumberFormat.getPercentInstance();
        NumberFormat numForm = NumberFormat.getNumberInstance();
        double totalMembers = proxyBoard.getMembers().size();
        double presentMembers = proxyBoard.getPresentMembers().size();
        double absentMembers = proxyBoard.getAbsentMembers().size();

        for (int i = 0; i < proxyBoard.getAbsentMembers().size(); i++) {
            if (((TAMember) proxyBoard.getAbsentMembers().get(i)).isRepresentable()) representableCount++;

        }

        presentLabel.setText(numForm.format(presentMembers) + " members present (" + percFormat.format((presentMembers / totalMembers)) + " of " + numForm.format(totalMembers) + " total members.");
        absentLabel.setText(numForm.format(absentMembers) + " members absent");
        representableLabel.setText(numForm.format(presentMembers + representableCount) + " members are representable (" + percFormat.format((presentMembers + representableCount) / totalMembers) + ")" + "\n");

        if ((presentMembers / totalMembers > .33333) && (((presentMembers + representableCount) / totalMembers) > .5)) {
            quorumLabel.setForeground(Color.red);
            quorumLabel.setText("Have Quorum!");
        } else {
            quorumLabel.setForeground(Color.red);
            quorumLabel.setText("!!! NO QUORUM !!!");
        }

    }


    /**
     * interpret the checkboxes to set attendance in ProxyBoard.java
     * @see ProxyBoard
     */
    public void takeAttendance() {
        for (int i = 0; i < checkboxList.size(); i++) {
            TAMember mem = (TAMember) (proxyBoard.getMembers().get(i));
            if (((JCheckBox) checkboxList.get(i)).isSelected()) {
                mem.setPresence(true);
                ProxyLogger.log(Level.INFO, mem.getLastName() + " is present");
            } else {
                mem.setPresence(false);
                ProxyLogger.log(Level.INFO, mem.getLastName() + " is absent");

            }
        }
        proxyBoard.setAttendanceTaken(true);
    }


    /**
     * main - runs createAndShowGUI() (through invokeLater for thread safety)
     * @param args
     */
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ProxyGUI().createAndShowGUI();
            }
        });
    }

    /**
     * @throws IOException
     */
    private void setInstructionText() throws IOException {
        String filename = PROXY_INSTRUCTIONS_FILE;
        String texty;
        BufferedReader input = new BufferedReader(new FileReader(filename));
        while ((texty = input.readLine()) != null) {
            outputText += texty + "\n";
        }

    }

    /**
     * fills in output from proxy board (present/absent/representable)
     * @return proxyTest (String)
     */
    public String updateOutputText() {

        String proxyText = "";

        for (int j = 0; j < proxyBoard.getMembers().size(); j++) {
            TAMember curMem = (TAMember) proxyBoard.getMembers().get(j);
            if (curMem.getPresence()) {
                proxyText += (curMem.getGender() + " " + curMem.getLastName() + " is present\n");
            } else {
                if (curMem.isRepresentable()) {
                    proxyText += (curMem.getGender() + " " + curMem.getLastName() + " is represented by " + curMem.getRepresentation().getGender() + " " + curMem.getRepresentation().getLastName() + "\n");
                } else {
                    proxyText += (curMem.getFullName() + " is not representable\n");
                }
            }
        }

        return proxyText;

    }


    /**
     * Acts as an OutputStream but it intercepts all write calls and adds data to a text area if a certain
     * pattern is found in buffer when flushing
     */
    private class BufferedStreamWrapper extends OutputStream {
        private JTextArea outputSource = new JTextArea();
        private StringBuffer buffer;  //A place to buffer all chars until a line break is encountered



        public BufferedStreamWrapper(JTextArea aOutputSource) {
            outputSource = aOutputSource;
            outputSource.setAlignmentY(JTextArea.BOTTOM_ALIGNMENT);
            //outputSource.setPreferredSize(new Dimension(300,50));
            outputSource.setEditable(false);
            buffer = new StringBuffer();
        }

        public void write(int b) throws IOException {
            if (b == (int) '\n')     //Flush buffer when a line break is encountered
                flush();
            else if (b < 0)         //Buffer question mark instead of illegal char
                buffer.append('?');
            else                    //Buffer chars
                buffer.append((char) b);
        }

        public void write(byte[] b) throws IOException {
            for (int i = 0; i < b.length; i++)
                write(b[i]);
        }

        public void write(byte[] b, int off, int len) throws IOException {
            for (int i = off; i < off + len; i++)
                write(b[i]);
        }

        public void flush() throws IOException {
            //Check to see if buffer matches "pattern" (use uppercase to avoid possible casing problems)
            buffer.append('\n'); //Add a line break
            outputSource.append(buffer.toString());

            buffer.setLength(0); //Ensure buffer is flushed to get a clean start for more chars
        }
    }


    class RunProxyListener implements ActionListener {
        /**
         *
         * @param e
         *
         */
        public void actionPerformed(ActionEvent e) {
            try {
                proxyBoard.cleanUpBoard();
                takeAttendance();
                proxyOutputPanel.setText("");
                proxyBoard.solveProxies();
                proxyOutputPanel.append(updateOutputText());
                updateQuorumText();

            }
            catch (MemberNotFoundException m) {
                ProxyLogger.logEx(m.getMessage());
            }
            catch (IOException io) {
                ProxyLogger.logEx(io.getMessage());
            }
            catch (ProxySolverException p) {
                ProxyLogger.logEx(p.getMessage());
            }
            catch (LpSolveException lp) {
                ProxyLogger.logEx(lp.getMessage());
            }

        }
    }
}