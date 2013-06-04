public class Candidate {
	
	private String name;
	private int tally; // election tallies are run on the candidates, with each candidate containing her "pile"
	private boolean active; // for election tallying: whether candidate is still "alive"
	
	//constructors
	public Candidate(String thename) {
		this.name = thename;
		this.reset(); // start "alive" and with no votes in candidate's pile
	}
	
	//accessors
	public String getName() {
		return this.name;
	}
	
	public boolean isAlive() {
		return this.active;
	}
	
	public int getTally() {
		return this.tally;
	}
	
	//mutators
	public void activate() {
		this.active = true;
	}
	
	public void kill() {
		this.active = false;
	}
	
	public void increment() {
		this.tally++; // increment candidate's tally ; no situation where decrement would be needed
	}
	
	public void reset() { // set tally to zero and reactivate candidate if it has been deactivated
		this.zero();
		this.activate();
	}
	
	public void zero() {
		this.tally = 0; // zero out the vote tally
	}
		
}