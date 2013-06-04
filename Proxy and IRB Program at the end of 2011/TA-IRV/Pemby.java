import java.util.Calendar;

public class Pemby {
	
	private final String [] ballotTranscriptText = {"B|","A|","L|","L|","O|","T|","S|"};
	
	private int ballotsRecorded = 0;
	private int liveBallots = 0; // for election decision rules deciding majorities based only on ballots with live candidates
	
	private String name; // name of election or office
	private String transcript=""; // printout of candidates and ballots, as well as messages from other processes, such as when tiebreakers are used
	private Q2 ballots; // a Q of Ballots entered
	private Q2 entrants; // a master list of the Candidates in the election: Q2 of candidates
	private boolean nominationsOpen; // whether we can add candidates
	
	// constructor
	public Pemby(String name)
	{
		this.name = name;
		this.transcript += name + "\n";
		this.ballots = new Q2();
		this.entrants = new Q2();
		this.transcript += this.datestamp(); // record on the transcript when the election was started
		this.nominationsOpen = true;
	}
	
	public Pemby()
	{
		this("Untitled Election");
	}
	
	// for use in constructor
	public String datestamp()
	{
		Calendar start = Calendar.getInstance();
		String month = "Monthuary";
		if(start.get(Calendar.MONTH) == Calendar.JANUARY) month = "Jan";
		else if(start.get(Calendar.MONTH) == Calendar.FEBRUARY) month = "Feb";
		else if(start.get(Calendar.MONTH) == Calendar.MARCH) month = "Mar";
		else if(start.get(Calendar.MONTH) == Calendar.APRIL) month = "Apr";
		else if(start.get(Calendar.MONTH) == Calendar.MAY) month = "May";
		else if(start.get(Calendar.MONTH) == Calendar.JUNE) month = "Jun";
		else if(start.get(Calendar.MONTH) == Calendar.JULY) month = "Jul";
		else if(start.get(Calendar.MONTH) == Calendar.AUGUST) month = "Aug";
		else if(start.get(Calendar.MONTH) == Calendar.SEPTEMBER) month = "Sep";
		else if(start.get(Calendar.MONTH) == Calendar.OCTOBER) month = "Oct";
		else if(start.get(Calendar.MONTH) == Calendar.NOVEMBER) month = "Nov";
		else if(start.get(Calendar.MONTH) == Calendar.DECEMBER) month = "Dec";
		String minute = "" + start.get(Calendar.MINUTE);
		if(start.get(Calendar.MINUTE) < 10) minute = "0" + minute;
		return "Election started at " + start.get(Calendar.HOUR_OF_DAY) + ":" + minute + " on " + start.get(Calendar.DAY_OF_MONTH) + " " + month + ", " + start.get(Calendar.YEAR) + "\n";	
	}
	
	// accessors
	public String getName()
	{
		return this.name;
	}
	
	public String getTrans() // returns transcript
	{
		return this.transcript;
	}
	
	public boolean nomsOpen()
	{
		return this.nominationsOpen;
	}
	
	public Q2 getCands()
	{
		return this.entrants;
	}
	
	// methods for entering and transcribing data
	
	public void transcribe(String text)
	{ // so other methods can add to the transcript
		this.transcript += text;
	}
	
	
	public void enterCandidate(Candidate newbie) // enter a candidate in the election
	{
		if(nominationsOpen)
		{
			this.entrants.joinQ(newbie);
			this.transcribe("Candidate-" + this.entrants.getLength() + " is " + newbie.getName() + "\n");
		}
	}
	
	public void enterCandidate(String name) // creates a candidate to pass to the main enterCandidate method above
	{
		this.enterCandidate(new Candidate(name));
	}
	
	public void clearCandidates()
	{
		this.entrants.clearQ();
		this.transcribe("Candidates cleared.\n");
	}
	
	public void enterBallot(Ballot theballot) // enter a fully formed ballot; all ballot entries ultimately pass through this method
	{
		if(this.nominationsOpen)
			this.nominationsOpen = false; // if we're entering ballots, no new candidates!

		this.ballots.joinQ(theballot);
	}
	
	public void enterBallot(Q2 candlist)
	{
		this.enterBallot(new Ballot(candlist));
	}

	public void enterBallot(Q2 candlist,String text)
	{
		this.enterBallot(new Ballot(candlist,text));
	}
	
	public void transcribeAllBallots()
	{
		if(this.ballots.getFront() == null || this.ballots.getFront().getData() == null)
			this.transcribe("No candidates have been entered.");
		else
		{
			this.ballotTransHeader();
			this.ballotsRecorded = 0; // reset number of ballots recorded
			for(Binode temp = this.ballots.getFront() ; temp != null && temp.getData() != null ; temp = temp.getNext())
				this.transcribe(this.transcribeBallot((Ballot) temp.getData()));
		}
	}
	
	public void transcribeAllBallotsRandomly()
	{
		if(this.ballots.getFront() == null || this.ballots.getFront().getData() == null)
			this.transcribe("No candidates have been entered.");
		else
		{
			this.ballotTransHeader();
			this.ballotsRecorded = 0; // reset number of ballots recorded
			Q2 randomized = this.ballots.randomizedCopy();
			for(Binode temp = randomized.getFront() ; temp != null && temp.getData() != null ; temp = temp.getNext())
				this.transcribe(this.transcribeBallot((Ballot) temp.getData()));
		}
	}
	
	public void enterBallot(Candidate [] candidates) // enter candidates in an array
	{
		Q2 candlist = new Q2();
		for(int i = 0; i < candidates.length ; i++) // go down the array and add elements in order to the Q
			candlist.joinQ(candidates[i]);
		this.enterBallot(new Ballot(candlist)); // pass the fully formed ballot to the appropriate method
	}
	public void enterBallot(Candidate [] candidates, String text) // enter candidates in an array
	{
		Q2 candlist = new Q2();
		for(int i = 0; i < candidates.length ; i++) // go down the array and add elements in order to the Q
			candlist.joinQ(candidates[i]);
		this.enterBallot(new Ballot(candlist,text)); // pass the fully formed ballot to the appropriate method
	}
	public void enterBallotByName(String [] names) // enter candidate names in an array
	{
		Q2 candlist = new Q2();
		for(int i = 0; i < names.length ; i++)
			candlist.joinQ(findByName(names[i]));
		this.enterBallot(new Ballot(candlist));
	}
	public void enterBallotByName(String [] names, String text) // enter candidate names in an array
	{
		Q2 candlist = new Q2();
		for(int i = 0; i < names.length ; i++)
			candlist.joinQ(findByName(names[i]));
		this.enterBallot(new Ballot(candlist,text));
	}
	public void enterBallotByName(Q2 names) // enter candidate names in a Q2
	{
		Q2 candlist = new Q2();
		for(Binode temp = names.getFront() ; temp != null && temp.getData() != null ; temp = temp.getNext())
			candlist.joinQ(findByName((String) temp.getData()));
		this.enterBallot(new Ballot(candlist));
	}
	public void enterBallotByName(Q2 names,String text) // enter candidate names in a Q2
	{
		Q2 candlist = new Q2();
		for(Binode temp = names.getFront() ; temp != null && temp.getData() != null ; temp = temp.getNext())
			candlist.joinQ(findByName((String) temp.getData()));
		this.enterBallot(new Ballot(candlist,text));
	}
	public void enterBallotByNumbers(int [] ranks) // respective ranks for the candidates in the master order (0 = n)
	{ // search for number 1 and add that candidate to the ballot, then search for number 2, etc
		Q2 candlist = new Q2();
		boolean done = false;
		for(int i=1; !done; i++) // i is the rank we're searching for
		{
			boolean found = false;
			for(int j=0; j < ranks.length && !found ; j++)
			{
				if(ranks[j] == i)
				{
					candlist.joinQ(this.findByNumber(j+1)); // rank[0] is the rank for Candidate-1 etc.
					found = true;
				} // if not found, keep looking til reach end of array
			}
			if(!found) done = true; // if a rank is not represented, then we're done filling in Candidates
		}
		this.enterBallot(new Ballot(candlist));
	}
	public void enterBallotByNumbers(int [] ranks, String text) // respective ranks for the candidates in the master order (0 = n)
	{ // search for number 1 and add that candidate to the ballot, then search for number 2, etc
		Q2 candlist = new Q2();
		boolean done = false;
		for(int i=1; !done; i++) // i is the rank we're searching for
		{
			boolean found = false;
			for(int j=0; j < ranks.length && !found ; j++)
			{
				if(ranks[j] == i)
				{
					candlist.joinQ(this.findByNumber(j+1)); // rank[0] is the rank for Candidate-1 etc.
					found = true;
				} // if not found, keep looking til reach end of array
			}
			if(!found) done = true; // if a rank is not represented, then we're done filling in Candidates
		}
		this.enterBallot(new Ballot(candlist,text));
	}
	public void enterBallotByNumbers(Q2 ranks)
	{
		Q2 ballot = new Q2();
		boolean done = false;
		for(int i=1; !done; i++) // i is the rank we're searching for
		{
			Binode current = ranks.getFront();
			boolean found = false;
			for(int j=0; (current != null && current.getData() != null) && !found ; j++, current = current.getNext())
			{
				if(((Integer)current.getData()).intValue() == i)
				{
					ballot.joinQ(this.findByNumber(j+1)); // rank[0] is the rank for Candidate-1 etc.
					found = true;
				} // if not found, keep looking til reach end of array
			}
			if(!found) done = true; // if a rank is not represented, then we're done filling in Candidates
		}
		this.enterBallot(new Ballot(ballot));
	}
	public void enterBallotByNumbers(Q2 ranks, String text)
	{
		Q2 ballot = new Q2();
		boolean done = false;
		for(int i=1; !done; i++) // i is the rank we're searching for
		{
			Binode current = ranks.getFront();
			boolean found = false;
			for(int j=0; (current != null && current.getData() != null) && !found ; j++, current = current.getNext())
			{
				if(((Integer)current.getData()).intValue() == i)
				{
					ballot.joinQ(this.findByNumber(j+1)); // rank[0] is the rank for Candidate-1 etc.
					found = true;
				} // if not found, keep looking til reach end of array
			}
			if(!found) done = true; // if a rank is not represented, then we're done filling in Candidates
		}
		this.enterBallot(new Ballot(ballot,text));
	}
	
	private Candidate findByName(String name) // returns the first candidate on the master list with the given name
	{
		for(Binode current = this.entrants.getFront() ; current != null && current.getData() != null ; current = current.getNext()) // check of current's data just being over-cautious; go through entrants
			if(((Candidate)current.getData()).getName().equalsIgnoreCase(name)) // check candidate's name against test name; not case-sensitive
				return (Candidate) current.getData(); // this will return the candidate if the name matches, exiting the method; o'wise it keeps searching
		return null;
	}
	
	private Candidate findByNumber(int n) // key to remember: Candidates start at 1 but arrays start at 0!
	{
		Binode current = this.entrants.getFront(); // might be null
		for(int i = 1; i < n && (current != null && current.getData() != null) ; i++)
			current = current.getNext();
		if(current != null && current.getData() != null) // if the number corresponds to an entrant
			return (Candidate) current.getData();
		return null;
	}
	
	private String transcribeBallot(Ballot ball)
	{
		String transcription = "";
		
		if(this.ballotsRecorded < this.ballotTranscriptText.length)
			transcription += this.ballotTranscriptText[this.ballotsRecorded];
		else transcription += "||";
		
		transcription += ball.transcribeVotes(this.entrants);
		
		transcription += "\n";
		this.ballotsRecorded++;
		return transcription;
	}
	
	private void ballotTransHeader() // ballot transcript header
	{ // the ballot transcription header depends on how many candidates we have
		Calendar nomClose = Calendar.getInstance();
		this.transcribe("Nominations closed at " + nomClose.get(Calendar.HOUR_OF_DAY) + ":" + nomClose.get(Calendar.MINUTE) + "\nx"); // record on the transcript when the nominations were closed
		if(this.entrants.getLength() < 7)
			this.transcript += "-- CANDIDATES --x\n||";
		else
		{
			int i = 0;
			for(; i < (this.entrants.getLength() * 3 - 12) / 2 ; i++)
				this.transcript += "-";
			this.transcript += " CANDIDATES ";
			i += 12;
			for(; i < this.entrants.getLength() * 3 ; i++)
				this.transcript += "-";
			this.transcript += "x\n||";
		}
		for(int i = 0; i < this.entrants.getLength() ; i++)
		{
			this.transcript += (i+1) + " "; // correct for indexing starting at 0 or 1
			if(i < 10) this.transcript += " "; //correct for 1 or 2 digit numbers
		}
		this.transcript += "\n||";
		for(int i = 0; i< this.entrants.getLength() * 3 ; i++)
			this.transcript+="-";
		this.transcript+="\n";
	}
	
	public Candidate tabulate(Q2 eliminees) // pass a list of candidates to kill (revive before exit); return a winner, or null for no winner
	{
		Candidate winner = null; // winner of the sub-election the method is currently tabulating; if no winner, will ultimately return null
	//	if we want to print something about when the election was closed, we can uncomment the next two lines:
	//	Calendar elClose = Calendar.getInstance();
	//	this.transcript += "Election closed and tabulation begun at " + elClose.get(Calendar.HOUR_OF_DAY) + ":" + elClose.get(Calendar.MINUTE) + "\n"; // record on the transcript when the nominations were closed
		if(eliminees != null) // kill off eliminees (revived at end of method)
			for(Binode temp = eliminees.getFront(); temp != null && temp.getData() != null; temp = temp.getNext())
			{
				Candidate eliminated = (Candidate) temp.getData();
				eliminated.kill();
				// For "black-box" elections, comment out the following:
				transcript += "[" + eliminated.getName() + " eliminated]\n";
				//*****************
			}
				
		this.accrueVotes(); // figure out where candidates stand after the eliminations directed above
		
		int smallest = this.ballots.getLength(); // smallest vote total
		int secsm = smallest; // second smallest
		Q2 lowests = new Q2(); // Q2 of candidates with lowest vote totals
		int numliving = 0; // we record the number of living candidates so we can easily deal with some special cases below
	
		
		for(Binode current = this.entrants.getFront(); current != null && current.getData() != null; current = current.getNext()) // go through the candidates
			if(((Candidate) current.getData()).isAlive()) // only consider living candidates (we can't put this criterion above, for then a dead candidate would terminate our loop)
			{
				numliving++;
				// now we see if we have a winner:
				//criterion for majority of ALL BALLOTS:
				//if(((double) ((Candidate)current.getData()).getTally()) / ((double) this.ballots.getLength()) > 0.5 ) // not >= because this would be a problem in the case of a final 2-way tie
				//criterion for majority of LIVE BALLOTS:
				if(((double) ((Candidate)current.getData()).getTally()) / ((double) this.liveBallots) > 0.5 )
					winner = (Candidate) current.getData();
				else if(((Candidate) current.getData()).getTally() < smallest)
				{
					secsm = smallest; // update secsm
					smallest = ((Candidate) current.getData()).getTally(); // and smallest.
					lowests.clearQ();				// we have a new group of lowest vote-getters,
					lowests.joinQ(current.getData()); // consisting for now of just this one candidate
				}
				else if(((Candidate) current.getData()).getTally() == smallest)
					lowests.joinQ(current.getData());
				else if(((Candidate) current.getData()).getTally() < secsm)
					secsm = ((Candidate) current.getData()).getTally();
				// else do nothing, because the candidate is staying alive for the time being, and isn't too close to being eliminated right now				
			}
		if(lowests.getLength() > 1 && secsm == this.ballots.getLength() && winner == null) transcript += "All remaining candidates are tied... reballot necessary.\n";
		
		//if there are fewer than 1 remaining, no winner will have been identified above, and the null result will be correct
		//if there is one remaining, with enough votes to win, that candidate will have been identified as the winner.
		//if there is one remaining without enough votes, no winner will have been identified, and the null result is correct
		//otherwise, if we don't have a winner, we can/need-to do an elimination
		if(numliving > 1 && winner == null) // i.e. if this sub-election doesn't have a winner
		{
			if(smallest * lowests.getLength() < secsm) // this covers all the eliminations which would happen under the current bylaws
			{
				if(lowests.getLength() > 1)
				{
					this.transcript += "Tie-break of collective impotence invoked for " + lowests.getLength() + "-way tie\n";
					// if you want a "black-box" election where the transcript does not prejudice future, eliminate the following:
					this.transcript += "[Tie is between: ";
					for(Binode current = lowests.getFront(); current != null && current.getData() != null; current = current.getNext())
					{
						this.transcript += ((Candidate)current.getData()).getName();
						if(current.getNext() != null) this.transcript += ", ";
						else this.transcript += ".]\n";
					}
					//*************************
				}
				winner = tabulate(lowests);	
			}
			else if(lowests.getFront() != null && lowests.getFront().getData() != null) // here we have a tie which is not easily breakable; the if statement is a mere needless precaution
			{// so winner remains null if we never get past the `if' in the `else-if'
				this.transcript += "Tie-break of all-the-same-in-the-end attempted for "+lowests.getLength() + "-way tie\n";
				// if you want a "black-box" election where the transcript does not prejudice future, eliminate the following:
				this.transcript += "[Tie is between: ";
				for(Binode current = lowests.getFront(); current != null && current.getData() != null; current = current.getNext())
				{
					this.transcript += ((Candidate)current.getData()).getName();
					if(current.getNext() != null) this.transcript += ", ";
					else this.transcript += ".]\n";
				}
				//*************************
				Q2 eliminstance = new Q2();
				eliminstance.joinQ(lowests.getFront().getData());
				winner = tabulate(eliminstance); // for now, winner is the winner with a first random tie-break (may be null)	
				for(Binode current = lowests.getFront().getNext(); winner != null && (current != null && current.getData() != null); current = current.getNext())
				{// go through the candidates with the lowest number of votes and try eliminating them one by one
					eliminstance.clearQ();
					eliminstance.joinQ(current.getData());
					if(winner != tabulate(eliminstance)) winner = null; // if we get a different (or null) winner with a different elimination, the winner becomes null and we exit the for-loop with sad news
					if(winner == null) transcript += "Multiple possible winners found... reballot necessary.\n";
				}
			}
		}
		
		if(numliving == 1 && winner == null)
		{
			this.transcript += "No one obtained a majority of votes...\n";
			for(Binode current = this.entrants.getFront(); current != null && current.getData() != null; current = current.getNext()) // go through the candidates
				if(((Candidate) current.getData()).isAlive()) winner = (Candidate) current.getData();
		}
		if(eliminees != null) // revive eliminees before returning stream of control to earlier call of tabulate
			for(Binode temp = eliminees.getFront(); temp != null && temp.getData() != null; temp = temp.getNext())
			{
				Candidate Lazarus = (Candidate) temp.getData();
				Lazarus.activate();
				// For "black-box" elections, comment out the following:
				transcript += "[" + Lazarus.getName() + " revived]\n";
				//*****************
			}
		
		return winner;
	}

	private void accrueVotes()
	{ // first, zero the vote totals
		for(Binode current = this.entrants.getFront(); current != null && current.getData() != null; current = current.getNext())
			((Candidate) current.getData()).zero();
		this.liveBallots = 0;
		// go through each ballot, find the first (highest ranked) live candidate, and increment that candidate's tally
		for(Binode currBalNode = this.ballots.getFront(); currBalNode != null && currBalNode.getData() != null ; currBalNode = currBalNode.getNext()) // current Ballot Node
			if(((Ballot) currBalNode.getData()).recordVote()) this.liveBallots++; // this will try to record a vote for the ballot and return true if the ballot is still live
	}
	
/*	private void declareWinner(Candidate winner)
*	{
*		this.transcript += "The winner is " + winner.getName();
*		System.out.println(this.transcript);
*	}
*	
*	private void declareNoWinner(String messageText)
*	{
*		this.transcript += "There was no winner, due to:\n" + messageText;
*		System.out.println(this.transcript);
*	}
*/
	
	
}