public class Ballot
{
	private Q2 cands; // Q2 of candidates which is the ballot itself
	private String key; // the key which was used to cast the ballot
	
	public Ballot(Q2 cands, String key)
	{
		this.cands = cands;
		this.key = key;
	}
	public Ballot(Q2 cands)
	{
		this(cands,"No Key Entered");
	}
	
	public boolean recordVote()
	{
		Binode current = this.cands.getFront();
		for(; (current != null && current.getData() != null) && !((Candidate) current.getData()).isAlive() ; current = current.getNext()); // go down the ballot until you find a live candidate
		if(current != null && current.getData() != null)
		{
			((Candidate) current.getData()).increment(); // if there is a live candidate on the ballot, we will have found the highest ranked one, and should increment that candidate's total accordingly
			return true; // record that the ballot had a live candidate and was counted
		}
		return false; // returns whether a vote was recorded
	}
	
	
	public String transcribeVotes(Q2 entrants)
	{
		String transcription = "";
		
		for(Binode current = entrants.getFront(); current != null && current.getData() != null ; current = current.getNext()) // the getData check is just an extra precaution,... the real work is in checking we haven't gone off the Q
		{ // go through the candidates in the master order and find out how they stack up, according to this ballot
			int rank = 1;
			Binode temp = this.cands.getFront(); // we need to check the value of temp outside of the for loop, so we declare it here
			for(; temp != null && temp.getData() != current.getData() ; temp = temp.getNext() ) // go down the current ballot and compare the entries to the current candidate, to find out where the current candidate ranks
				rank++; // will increment if we're still on the list and haven't found the current candidate yet
			if(temp == null) // i.e. if we have tested all the candidates on the ballot and haven't found the current one
				transcription += "n  ";
			else // i.e. if we found the candidate
			{
				transcription += rank + " ";
				if(rank < 10) transcription += " "; // add an extra space for 1-digit numbers
			}			
		}
		
		transcription += "<---#" + this.key;
		
		return transcription;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}