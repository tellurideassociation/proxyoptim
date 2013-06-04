// import java.awt.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;

import javax.swing.*;


public class Election implements ActionListener {
	
	public static void main(String [] args)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Election();
            }
        });
	}
	
	public final String versionNo="v0.2";
	private JFrame frame;
	private JLabel currentElec = new JLabel("<html><font color=red>No election open.</font></html>");
	private JLabel adminHeader = new JLabel("<html><font size=5 color=blue>Election Controls</font></html>");
	
	private JPanel adminPanel,inputPanel;
	private JTextField inputText = new JTextField();
	private JButton newElection = new JButton("New Election");
	private JButton tabElection= new JButton("Tabulate Current Election");
	private JButton setOutputs= new JButton("Set Output Locations");
	private JButton getInput= new JButton("Get Input From File");
	private JButton enterCandidates= new JButton("Enter Candidates");
	private JButton inputBallots= new JButton("Input Ballots");
	private JButton addOutput = new JButton("Add Output");
	private JButton clearOuts = new JButton("Clear Output Locations");
	private JButton addCand = new JButton("Add Candidate");
	private JButton clearCands = new JButton("Clear Candidates");
	private JButton enterByNum = new JButton("Enter Ballot by Numbers");
	private JButton enterByName = new JButton("Enter Ballot by Names");
	private JButton enterBallot = new JButton("Enter Ballot");
	private JButton ballotHelp = new JButton("Instructions");
	
	private JButton authentication = new JButton("Authentication");
	private JButton toggleAuth = new JButton();
	private JButton addKeys = new JButton("Add authentication keys");
	private JButton clearKeys = new JButton("Clear authentication keys");
	private JButton ballotsKeys = new JButton("Print ballots with new keys");
	
	private RankPanel currentRankPanel = null;
	
	private boolean auth; // whether or not authentication is on
	private Q2 keys; // authentication keys
	private String adminPW; // admin password, especially for turning authentication on and off
	
	private Pemby election; // the election with which the GUI is currently dealing
	private Q2 outputs = new Q2(); // output locations, in a Q2

	
	public Election()
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		this.election = null;
		this.auth = false;
		this.keys = null;
		this.adminPW = null;
		//create the frame for the GUI
		this.frame = new JFrame("Telluride Association Instant Runoff Voting, " + this.versionNo);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // shuts down program when you close the window !!
		this.frame.getContentPane().setLayout(new BoxLayout(this.frame.getContentPane(),BoxLayout.X_AXIS)); // sets the general layout of the frame to border layout
		// build and add the admin panel

		this.adminPanel = new JPanel();
		this.adminPanel.setLayout(new BoxLayout(this.adminPanel,BoxLayout.Y_AXIS));
		this.buildAdminPanel(this.adminPanel);
		this.frame.getContentPane().add(this.adminPanel);
		
		this.inputPanel = new JPanel();
		this.inputPanel.setLayout(new BoxLayout(this.inputPanel,BoxLayout.Y_AXIS));
		this.frame.getContentPane().add(this.inputPanel);
	    
	    // set action listeners
		this.newElection.addActionListener(this);
		this.tabElection.addActionListener(this);
		this.setOutputs.addActionListener(this);
		this.getInput.addActionListener(this);
		this.enterCandidates.addActionListener(this);
		this.inputBallots.addActionListener(this);
		this.addOutput.addActionListener(this);
		this.clearOuts.addActionListener(this);
		this.addCand.addActionListener(this);
		this.clearCands.addActionListener(this);
		this.enterByNum.addActionListener(this);
		this.enterByName.addActionListener(this);
		this.enterBallot.addActionListener(this);
		this.ballotHelp.addActionListener(this);
		this.authentication.addActionListener(this);
		this.toggleAuth.addActionListener(this);
		this.addKeys.addActionListener(this);
		this.clearKeys.addActionListener(this);
		this.ballotsKeys.addActionListener(this);
		
		
		// border
		this.adminPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		this.inputPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		this.frame.pack();
		this.frame.setVisible(true);
	}
	
	// panel-building methods
	

	private void buildAdminPanel(JPanel panel)
	{
		/* Election Controls
		 * (New Election)
		 * (Get Input From File) ...  if an election is open
		 * (Input Candidates) ... if applicable
		 * (Input Ballots) ... if applicable
		 * (Tabulate Current Election) ... if an election is open
		 * (Set Output Locations)
		 * 
		 */
		panel.removeAll();
		panel.add(this.adminHeader);
		
		if(this.election != null)
			this.currentElec.setText("<html><font color=green>The current election is: " + this.election.getName() + "</font></html>");
		panel.add(currentElec);
		
		panel.add(this.newElection);
		if(this.election != null)
			panel.add(this.getInput);
		if(this.election != null && this.election.nomsOpen())
			panel.add(this.enterCandidates);
		if(this.election != null)
			panel.add(this.inputBallots);
		if(this.election != null)
			panel.add(this.tabElection);
		panel.add(this.setOutputs);
		panel.add(this.authentication);
	}
	

	//Action Listening and Button Processing Methods
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.newElection) this.makeNewElection();
		else if(e.getSource() == this.tabElection) this.tab();
		else if(e.getSource() == this.setOutputs) this.outputScreen();
		else if(e.getSource() == this.getInput) this.inputFromFile();
		else if(e.getSource() == this.enterCandidates) this.candidateScreen();
		else if(e.getSource() == this.inputBallots) this.ballotScreen();
		else if(e.getSource() == this.addOutput) this.appendOut();
		else if(e.getSource() == this.clearOuts) this.emptyOuts();
		else if(e.getSource() == this.addCand) this.appendCand();
		else if(e.getSource() == this.clearCands) this.emptyCands();
		else if(e.getSource() == this.ballotHelp) this.ballotHelpScreen();
		else if(e.getSource() == this.enterByName) this.recordByName();
		else if(e.getSource() == this.enterByNum) this.recordByNum();
		else if(e.getSource() == this.enterBallot) this.recordByButtons();
		else if(e.getSource() == this.authentication) this.authPanel();
		else if(e.getSource() == this.toggleAuth) this.authOnOff();
		else if(e.getSource() == this.addKeys) this.inptKeys();
		else if(e.getSource() == this.clearKeys) this.wipeKeys();
		else if(e.getSource() == this.ballotsKeys) this.makeKeysAndBallots();
		
	
	}
	
	private void makeKeysAndBallots() {
		// This method has two steps.  First, it creates a specified number of new keys.
		// Second, it prints ballots with those keys on top.
		// Start by making sure the user has permission to do all this stuff
		String usrpw = JOptionPane.showInputDialog(this.inputPanel,"Please enter the administrative password for this election:"); // user password
		if(usrpw == null || usrpw.length() < 1) // check that a password was attempted
			JOptionPane.showMessageDialog(this.inputPanel,"No password entered","Cannot comlete request",JOptionPane.ERROR_MESSAGE);
		else if(! usrpw.equals(this.adminPW)) // check that it's correct
			JOptionPane.showMessageDialog(this.inputPanel,"Password incorrect","Cannot complete request",JOptionPane.ERROR_MESSAGE);
		else
		{
			String tempnum = JOptionPane.showInputDialog(this.inputPanel,"How many ballots and keys?");
			int num = 0;
			try  {num = Integer.parseInt(tempnum);}
			catch (NumberFormatException nfe) { // do nothing if can't parse,... num will be 0
			}
			
			String [] codes = new String[num]; // make an array of strings which we will fill with codes to be added
			for(int i = 0; i < codes.length ; i++)
			{
				codes[i] = KeyUtilities.makeKeyString(KeyUtilities.CODELENGTH);
				this.keys.joinQ(new Key(codes[i]));
			}
			this.authPanel(); // refresh the authorization panel
			
			// first, we figure out how many ballots we can put side by side
			// this depends on the number of candidates and the length of each candidate's name:
			int longestName = 0;
			for(Binode temp = this.election.getCands().getFront() ; temp != null && temp.getData() != null ; temp = temp.getNext())
				if (((Candidate) temp.getData()).getName().length() > longestName) longestName = ((Candidate) temp.getData()).getName().length();
			int numEntrants = this.election.getCands().getLength();
			
			int widthOfOneBallot = 0;
			widthOfOneBallot += longestName + 1; // "Name:"
			widthOfOneBallot += 2 * (numEntrants + 1); // " 1"..." n"
			if(numEntrants > 9) widthOfOneBallot += numEntrants - 9; // account for double digits (don't worry about 3 digits)
			
			if(widthOfOneBallot < this.election.getName().length()) widthOfOneBallot = this.election.getName().length();
			if(widthOfOneBallot < KeyUtilities.CODELENGTH + 5) widthOfOneBallot = KeyUtilities.CODELENGTH + 5; // "Key: AAAA"
			
			int colsLeft = 80 - (widthOfOneBallot + 4);
			int numCols = 1; // number of columns of ballots we can print
			while(colsLeft >= widthOfOneBallot)
			{
				numCols++;
				colsLeft -= widthOfOneBallot + 4;
			}
			
			for(int i=0; i < codes.length ;)
			{
				// print ballots one row at a time
				
				// first, a row for the name of the election
				System.out.print(this.election.getName());
				for(int k = this.election.getName().length(); k < widthOfOneBallot ; k++) System.out.print(" ");
				for(int j = 1; j < numCols ; j++)
				{
					System.out.print("    " + this.election.getName());
					for(int k = this.election.getName().length(); k < widthOfOneBallot ; k++) System.out.print(" ");
				}
				System.out.print("\n");
				System.out.flush();
				
				// second, a row for the individual voting key
				System.out.print("Key: ");
				if(i < codes.length)
				{
					System.out.print(codes[i]);
					for(int k = codes[i].length() + 5; k < widthOfOneBallot ; k++) System.out.print(" ");
					i++;
				}
				for(int j = 1; j < numCols ; j++)
				{
					System.out.print("    Key: ");
					if(i < codes.length)
					{
						System.out.print(codes[i]);
						for(int k = codes[i].length() + 5; k < widthOfOneBallot ; k++) System.out.print(" ");
						i++;
					}
				}
				System.out.print("\n");
				System.out.flush();
				
				// lastly, a row for each of the candidates
				for(Binode temp = this.election.getCands().getFront(); temp != null && temp.getData() != null ; temp = temp.getNext())
				{
					System.out.print(((Candidate) temp.getData()).getName() + ":");
					for(int k = ((Candidate) temp.getData()).getName().length(); k < longestName ; k++ ) System.out.print(" ");
					for(int k = 0; k < numEntrants ; k++)	System.out.print(" " + (k+1));
					System.out.print(" n");
					for(int k = longestName + 1 + 2*(numEntrants + 1) ; k < widthOfOneBallot ; k++) System.out.print(" ");
					
					for(int j = 1; j < numCols ; j++)
					{
						System.out.print("    " + ((Candidate) temp.getData()).getName() + ":");
						for(int k = ((Candidate) temp.getData()).getName().length(); k < longestName ; k++ ) System.out.print(" ");
						for(int k = 0; k < numEntrants ; k++)	System.out.print(" " + (k+1));
						System.out.print(" n");
						for(int k = longestName + 1 + 2*(numEntrants + 1) ; k < widthOfOneBallot ; k++) System.out.print(" ");	
					}
					
					System.out.print("\n");
					System.out.flush();
				}
				
				//... and 3 rows of spacing:
				System.out.println("\n\n");
			}

		}
		
		
	}

	private void wipeKeys() {
		String usrpw = JOptionPane.showInputDialog(this.inputPanel,"Please enter the administrative password for this election:"); // user password
		if(usrpw == null || usrpw.length() < 1) // check that a password was attempted
			JOptionPane.showMessageDialog(this.inputPanel,"No password entered","Cannot clear keys",JOptionPane.ERROR_MESSAGE);
		else if(! usrpw.equals(this.adminPW)) // check that it's correct
			JOptionPane.showMessageDialog(this.inputPanel,"Password incorrect","Cannot clear keys",JOptionPane.ERROR_MESSAGE);
		else
		{
			this.keys.clearQ();
			this.authPanel();
		}
	}

	private void inptKeys() {
		String usrpw = JOptionPane.showInputDialog(this.inputPanel,"Please enter the administrative password for this election:"); // user password
		if(usrpw == null || usrpw.length() < 1) // check that a password was attempted
			JOptionPane.showMessageDialog(this.inputPanel,"No password entered","Cannot input keys",JOptionPane.ERROR_MESSAGE);
		else if(! usrpw.equals(this.adminPW)) // check that it's correct
			JOptionPane.showMessageDialog(this.inputPanel,"Password incorrect","Cannot input keys",JOptionPane.ERROR_MESSAGE);
		else
		{
			String place = JOptionPane.showInputDialog(this.inputPanel,"Where is the file?");
			if(place != null)
			{
				FileUtilities.parseKeys(place, this.keys);
				JOptionPane.showMessageDialog(this.inputPanel, "File parsed (file not found errors appear at console)");
				this.authPanel();
			}
		}
	}

	private void authOnOff() {
		if(this.auth) // if the election is currently authenticated, need to get the admin password to turn authentication off
		{
			String usrpw = JOptionPane.showInputDialog(this.inputPanel,"Please enter the administrative password for this election:"); // user password
			if(usrpw == null || usrpw.length() < 1) // check that a password was attempted
				JOptionPane.showMessageDialog(this.inputPanel,"No password entered","Cannot turn authentication off",JOptionPane.ERROR_MESSAGE);
			else if(! usrpw.equals(this.adminPW)) // check that it's correct
				JOptionPane.showMessageDialog(this.inputPanel,"Password incorrect","Cannot turn authentication off",JOptionPane.ERROR_MESSAGE);
			else
			{
				this.auth = false;
				this.keys = null;
				this.adminPW = null;
			}
		}
		else // prompt for an admin password, make new keys Q, turn authentication on
		{
			this.adminPW = JOptionPane.showInputDialog(this.inputPanel,"Please enter an administrative password for this election:");
			if(adminPW == null || adminPW.length() < 1)
			{
				JOptionPane.showMessageDialog(this.inputPanel,"No password entered","Cannot turn authentication on",JOptionPane.ERROR_MESSAGE);
				this.adminPW = null;
			}
			else
			{
				this.keys = new Q2();
				this.auth = true;
			}
		}
			
		this.authPanel(); // refresh the authentication panel
	}
	
	private void authPanel() {
		this.inputPanel.removeAll();
		
		if(this.auth) // if this is an authenticated election, very different panel!
		{
			this.inputPanel.add(new JLabel("<html>Authentication is currently <font color=green>ON</font> for this election.</html>"));
			this.toggleAuth.setText("<html>Turn Authentication <font color=red>OFF</font></html>");
			this.inputPanel.add(this.toggleAuth);
			
			if(this.keys == null) this.keys = new Q2();
			this.inputPanel.add(new JLabel("There are currently " + this.keys.getLength() + " keys entered."));
			this.inputPanel.add(this.addKeys); // add the "add keys" button
			this.inputPanel.add(this.clearKeys);
			this.inputPanel.add(this.ballotsKeys);
			
			// Authentication is currently ON
			// [ Turn Authentication OFF ]
			// There are currently XX keys entered
			// [Input Keys From File]
			// [Clear Keys]
			// [Print Ballots with New Keys]
			
		}
		else // not authenticated
		{
			this.inputPanel.add(new JLabel("<html>Authentication is currently <font color=red>OFF</font> for this election.</html>"));
			this.toggleAuth.setText("<html>Turn Authentication <font color=green>ON</font></html>");
			this.inputPanel.add(this.toggleAuth);
		}
		
		this.frame.pack();
		
	}

	private void recordByButtons() {
		
		if(this.auth) // if the election is currently authenticated, need to get the admin password to turn authentication off
		{
			String usrpw = this.inputText.getText(); // user password
			if(usrpw == null || usrpw.length() < 1) // check that a password was attempted
				JOptionPane.showMessageDialog(this.inputPanel,"No key entered","Cannot cast ballot",JOptionPane.ERROR_MESSAGE);
			else if(! KeyUtilities.tryKey(usrpw,this.keys)) // check that it's correct
				JOptionPane.showMessageDialog(this.inputPanel,"Invalid key","Cannot cast ballot",JOptionPane.ERROR_MESSAGE);
			else
			{
				if(this.currentRankPanel != null) // just a precaution
				{
					this.election.enterBallotByNumbers(this.currentRankPanel.extractRanks(),usrpw);
					this.inputText.setText("");
					this.currentRankPanel.resetPanel();
					this.frame.pack();
					
					JOptionPane.showMessageDialog(this.inputPanel,"Ballot cast","Democracy is awesome!",JOptionPane.INFORMATION_MESSAGE);

				}	
			}
		}
		else
		{
			if(this.currentRankPanel != null) // just a precaution
			{
				this.election.enterBallotByNumbers(this.currentRankPanel.extractRanks());
				this.currentRankPanel.resetPanel();
				this.frame.pack();
				
				JOptionPane.showMessageDialog(this.inputPanel,"Ballot cast","Democracy is awesome!",JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private void recordByNum() {
		
		if(this.auth) // if the election is currently authenticated, need to get the admin password to turn authentication off
		{
			String usrpw = JOptionPane.showInputDialog(this.inputPanel,"Please enter your ballot key:"); // user password
			if(usrpw == null || usrpw.length() < 1) // check that a password was attempted
				JOptionPane.showMessageDialog(this.inputPanel,"No key entered","Cannot cast ballot",JOptionPane.ERROR_MESSAGE);
			else if(! KeyUtilities.tryKey(usrpw,this.keys)) // check that it's correct
				JOptionPane.showMessageDialog(this.inputPanel,"Invalid key","Cannot cast ballot",JOptionPane.ERROR_MESSAGE);
			else
			{
				String [] command = this.inputText.getText().split(",");
				if(command.length > 0)
				{
					Q2 ranks = new Q2();
					boolean nfeed = false; // records whether there's been a number format exception
					for(int j=0; j < command.length && !nfeed; j++)
					{
						try {
							ranks.joinQ(new Integer(Integer.parseInt(command[j])));
						} catch(NumberFormatException nfe)
						{// we go until there's an error or we run out of numbers
							nfeed = true;
						}
					}
					this.election.enterBallotByNumbers(ranks,usrpw);
					JOptionPane.showMessageDialog(this.inputPanel,"Ballot cast","Democracy is awesome!",JOptionPane.INFORMATION_MESSAGE);	
				}
				this.inputText.setText("");
			}
		}
		else
		{
			String [] command = this.inputText.getText().split(",");
			if(command.length > 0)
			{
				Q2 ranks = new Q2();
				boolean nfeed = false; // records whether there's been a number format exception
				for(int j=0; j < command.length && !nfeed; j++)
				{
					try {
						ranks.joinQ(new Integer(Integer.parseInt(command[j])));
					} catch(NumberFormatException nfe)
					{// we go until there's an error or we run out of numbers
						nfeed = true;
					}
				}
				this.election.enterBallotByNumbers(ranks);
				JOptionPane.showMessageDialog(this.inputPanel,"Ballot cast","Democracy is awesome!",JOptionPane.INFORMATION_MESSAGE);	
			}
			this.inputText.setText("");
		}
		
	}

	private void recordByName() {
		
		if(this.auth) // if the election is currently authenticated, need to get the admin password to turn authentication off
		{
			String usrpw = JOptionPane.showInputDialog(this.inputPanel,"Please enter your ballot key:"); // user password
			if(usrpw == null || usrpw.length() < 1) // check that a password was attempted
				JOptionPane.showMessageDialog(this.inputPanel,"No key entered","Cannot cast ballot",JOptionPane.ERROR_MESSAGE);
			else if(! KeyUtilities.tryKey(usrpw,this.keys)) // check that it's correct
				JOptionPane.showMessageDialog(this.inputPanel,"Invalid key","Cannot cast ballot",JOptionPane.ERROR_MESSAGE);
			else
			{
				String [] command = this.inputText.getText().split(",");
				if(command.length > 0)
				{
					Q2 names = new Q2();
					for(int j=0; j < command.length; j++)
						names.joinQ(command[j]);
					election.enterBallotByName(names,usrpw);
					JOptionPane.showMessageDialog(this.inputPanel,"Ballot cast","Democracy is awesome!",JOptionPane.INFORMATION_MESSAGE);	
				}
				this.inputText.setText("");
			}
		}
		else
		{
			String [] command = this.inputText.getText().split(",");
			if(command.length > 0)
			{
				Q2 names = new Q2();
				for(int j=0; j < command.length; j++)
					names.joinQ(command[j]);
				election.enterBallotByName(names);
				JOptionPane.showMessageDialog(this.inputPanel,"Ballot cast","Democracy is awesome!",JOptionPane.INFORMATION_MESSAGE);	
			}
			this.inputText.setText("");
		}
	}

	private void ballotHelpScreen() {
		this.inputPanel.removeAll();
		
		String helptext = "Ballot Entry Instructions\n";
	/*	helptext += "To enter ballots by name, type the names (not case sensitive), separated by commas, in order in the text field, and press the "+ this.enterByName.getText() + " button. If you don't want to vote for a particular candidate, simply don't enter that candidate's name.\n";
		helptext += "To enter ballots by rank, type a rank for each candidate in the order they appear in the candidate list, separated by commas, and press the "+ this.enterByNum.getText() + " button.  To not vote for a candidate, give that candidate the rank 0.  If you skip a rank, your ballot will only record ranks for candidates ranked above the skipped rank.\n";
	*/	helptext += "To enter ballots using the radio buttons, assign the desired rank to each candidate and press the "+this.enterBallot.getText() + " button.  If you skip a rank, your ballot will only record ranks for candidates ranked above the skipped rank.  If this is an authenticated election, you will need to enter your ballot key in the appropriate field before casting your vote.\n";
		helptext += "To return to ballot entry, press the "+this.inputBallots.getText() + " button.\n";
		
		JTextArea helpful = new JTextArea(helptext,10,40);
		helpful.setLineWrap(true); // wrap lines
		helpful.setWrapStyleWord(true); // don't break up words with line wrapping
		helpful.setEditable(false); // don't let just anyone change the instructions
		this.inputPanel.add(helpful);
		
		this.frame.pack();
	}

	private void appendOut()
	{
		String name = JOptionPane.showInputDialog(this.inputPanel,"Where shall I write an output? (default = console)");
		if(name != null)
		{
			if(name.length() < 1) name = "console";
			this.outputs.joinQ(name);
		}
		this.outputScreen();
	}
	
	private void appendCand()
	{
		if(this.auth) // if the election is currently authenticated, need to get the admin password to turn authentication off
		{
			String usrpw = JOptionPane.showInputDialog(this.inputPanel,"Please enter the administrative password for this election:"); // user password
			if(usrpw == null || usrpw.length() < 1) // check that a password was attempted
				JOptionPane.showMessageDialog(this.inputPanel,"No password entered","Cannot turn authentication off",JOptionPane.ERROR_MESSAGE);
			else if(! usrpw.equals(this.adminPW)) // check that it's correct
				JOptionPane.showMessageDialog(this.inputPanel,"Password incorrect","Cannot turn authentication off",JOptionPane.ERROR_MESSAGE);
			else
			{
				String name = JOptionPane.showInputDialog(this.inputPanel,"Who shall I add as a candidate? (must have at least 1 letter)");
				if(name != null && name.length() > 0) this.election.enterCandidate(name);
				this.candidateScreen();
			}
		}
		else
		{
			String name = JOptionPane.showInputDialog(this.inputPanel,"Who shall I add as a candidate? (must have at least 1 letter)");
			if(name != null && name.length() > 0) this.election.enterCandidate(name);
			this.candidateScreen();
		}
	}
	
	private void emptyOuts()
	{
		int really = JOptionPane.showConfirmDialog(this.inputPanel,"Are you sure you want to clear output locations?","Really?",JOptionPane.YES_NO_OPTION);
		if(really == JOptionPane.YES_OPTION)
		{
			this.outputs.clearQ();
			this.outputScreen();
		}
	}

	private void emptyCands()
	{
		if(this.auth) // if the election is currently authenticated, need to get the admin password to turn authentication off
		{
			String usrpw = JOptionPane.showInputDialog(this.inputPanel,"Please enter the administrative password for this election:"); // user password
			if(usrpw == null || usrpw.length() < 1) // check that a password was attempted
				JOptionPane.showMessageDialog(this.inputPanel,"No password entered","Cannot turn authentication off",JOptionPane.ERROR_MESSAGE);
			else if(! usrpw.equals(this.adminPW)) // check that it's correct
				JOptionPane.showMessageDialog(this.inputPanel,"Password incorrect","Cannot turn authentication off",JOptionPane.ERROR_MESSAGE);
			else
			{
				int really = JOptionPane.showConfirmDialog(this.inputPanel,"Are you sure you want to clear candidates?","Really?",JOptionPane.YES_NO_OPTION);
				if(really == JOptionPane.YES_OPTION)
				{
					this.election.clearCandidates();
					this.candidateScreen();
				}
			}
		}
		else
		{
			int really = JOptionPane.showConfirmDialog(this.inputPanel,"Are you sure you want to clear candidates?","Really?",JOptionPane.YES_NO_OPTION);
			if(really == JOptionPane.YES_OPTION)
			{
				this.election.clearCandidates();
				this.candidateScreen();
			}
		}
		
	}
	
	private void ballotScreen() {
		this.inputPanel.removeAll();

		JPanel tempanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
	/*	c.weightx = 0.1;
		c.gridx = 0;
		c.gridy = 1;
		tempanel.add(this.enterByName,c);
		c.gridx = 1;
		tempanel.add(this.enterByNum,c); */
		c.weightx = 0.1;
		c.gridx = 0;
		c.gridy = 0;
		tempanel.add(this.enterBallot,c);
		c.gridx = 1;
		tempanel.add(this.ballotHelp,c);
		c.gridx = 0;
		c.gridy = 1;
		tempanel.add(new JLabel("Voting Key:"),c);
		c.gridx = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		tempanel.add(this.inputText,c);
		
		this.inputPanel.add(tempanel);
		
		JPanel candRadioPanel = new JPanel();
		candRadioPanel.setLayout(new BoxLayout(candRadioPanel,BoxLayout.PAGE_AXIS));
	
		
		if(this.election.getCands() == null || this.election.getCands().getLength() < 1)
			candRadioPanel.add(new JLabel("No candidates have been entered!"));
		else
		{
			this.currentRankPanel = new RankPanel(this.election.getCands());
			candRadioPanel.add(this.currentRankPanel.getPane());
		}
		
		this.inputPanel.add(candRadioPanel);
		

		
		this.frame.pack();
	}

	private void candidateScreen() { // can't be called if this.election is null
		this.inputPanel.removeAll();
		if(this.election.getCands().isEmpty())
			this.inputPanel.add(new JLabel("No candidates have been entered"));
		else
		{
			this.inputPanel.add(new JLabel("The following candidates have been entered:"));
			for(Binode temp = this.election.getCands().getFront(); temp != null && temp.getData() != null; temp = temp.getNext())
				this.inputPanel.add(new JLabel(((Candidate) temp.getData()).getName()));
		}
		this.inputPanel.add(this.addCand);
		this.inputPanel.add(this.clearCands);
		
		this.frame.pack();
		
	}

	private void inputFromFile() {
		
		if(this.auth) // if the election is currently authenticated, need to get the admin password to turn authentication off
		{
			String usrpw = JOptionPane.showInputDialog(this.inputPanel,"Please enter the administrative password for this election:"); // user password
			if(usrpw == null || usrpw.length() < 1) // check that a password was attempted
				JOptionPane.showMessageDialog(this.inputPanel,"No password entered","Cannot turn authentication off",JOptionPane.ERROR_MESSAGE);
			else if(! usrpw.equals(this.adminPW)) // check that it's correct
				JOptionPane.showMessageDialog(this.inputPanel,"Password incorrect","Cannot turn authentication off",JOptionPane.ERROR_MESSAGE);
			else
			{
				String place = JOptionPane.showInputDialog(this.inputPanel,"Where is the file?");
				if(place != null)
				{
					FileUtilities.parseFile(place, this.election);
					JOptionPane.showMessageDialog(this.inputPanel, "File parsed (file not found errors appear at console)");
					this.inputPanel.removeAll();
					this.buildAdminPanel(this.adminPanel);
					this.frame.pack();
				}
			}
		}
		else
		{
			String place = JOptionPane.showInputDialog(this.inputPanel,"Where is the file?");
			if(place != null)
			{
				FileUtilities.parseFile(place, this.election);
				JOptionPane.showMessageDialog(this.inputPanel, "File parsed (file not found errors appear at console)");
				this.inputPanel.removeAll();
				this.buildAdminPanel(this.adminPanel);
				this.frame.pack();
			}
		}
	}

	private void outputScreen() {
		this.inputPanel.removeAll();
		if(this.outputs.isEmpty())
			this.inputPanel.add(new JLabel("No ouput locations have been entered"));
		else
		{
			this.inputPanel.add(new JLabel("The following output locations have been entered:"));
			for(Binode temp = this.outputs.getFront(); temp != null && temp.getData() != null; temp = temp.getNext())
				this.inputPanel.add(new JLabel((String) temp.getData()));
		}
		this.inputPanel.add(this.addOutput);
		this.inputPanel.add(this.clearOuts);
		
		this.frame.pack();
		
	}

	private void tab() {
		
		if(this.auth) // if the election is currently authenticated, need to get the admin password to turn authentication off
		{
			String usrpw = JOptionPane.showInputDialog(this.inputPanel,"Please enter the administrative password for this election:"); // user password
			if(usrpw == null || usrpw.length() < 1) // check that a password was attempted
				JOptionPane.showMessageDialog(this.inputPanel,"No password entered","Cannot turn authentication off",JOptionPane.ERROR_MESSAGE);
			else if(! usrpw.equals(this.adminPW)) // check that it's correct
				JOptionPane.showMessageDialog(this.inputPanel,"Password incorrect","Cannot turn authentication off",JOptionPane.ERROR_MESSAGE);
			else
			{
				this.election.transcribeAllBallotsRandomly();
				
				Candidate victorious = this.election.tabulate(null);
				if(victorious != null)
					this.election.transcribe("The winner was " + victorious.getName()+"\n");
				else
					this.election.transcribe("No single winner can be determined!\n");
				
				if(this.outputs.isEmpty())
					this.outputs.joinQ("console");
				
				String [] writes = new String [this.outputs.getLength()];
				int i=0;
				for(Binode temp = this.outputs.getFront(); temp != null && temp.getData() != null; temp = temp.getNext(),i++)
					writes[i] = (String) temp.getData();
				FileUtilities.writeOuts(writes , this.election.getTrans());
			}
		}
		else
		{
			this.election.transcribeAllBallotsRandomly();
			
			Candidate victorious = this.election.tabulate(null);
			if(victorious != null)
				this.election.transcribe("The winner was " + victorious.getName()+"\n");
			else
				this.election.transcribe("No single winner can be determined!\n");
			
			if(this.outputs.isEmpty())
				this.outputs.joinQ("console");
			
			String [] writes = new String [this.outputs.getLength()];
			int i=0;
			for(Binode temp = this.outputs.getFront(); temp != null && temp.getData() != null; temp = temp.getNext(),i++)
				writes[i] = (String) temp.getData();
			FileUtilities.writeOuts(writes , this.election.getTrans());
		}
	}

	private void makeNewElection() {
		boolean canGo = false; // a boolean for recording whether we can actually go ahead with this
		if(this.election != null)
		{
			int really = JOptionPane.showConfirmDialog(this.inputPanel,"Are you sure you want to start a new election (all data from current election will be cleared)?","Really?",JOptionPane.YES_NO_OPTION);
			if(really == JOptionPane.YES_OPTION)
				canGo = true;
		}
		else canGo = true;
		
		if(canGo)
		{
			String name = JOptionPane.showInputDialog(this.inputPanel,"What is the name of the election?");
			if(name != null)
				this.election = new Pemby(name);
			this.buildAdminPanel(this.adminPanel);
			this.frame.pack();
		}
	}	
	
}