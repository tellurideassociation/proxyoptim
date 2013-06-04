public class Key
{
	private String pw;
	private boolean used;
	
	// a Key is a password which can only be used once
	
	public Key(String keytext)
	{
		this.pw = keytext;
		this.used = false;
	}
	public boolean tryKey(String attempt) // try to use the key
	{
		if(this.used) // if it's already been used, it won't work
			return false;
		else if(! attempt.equals(this.pw))
			return false;
		else
		{
			this.used = true;
			return true;
		}
	}
}