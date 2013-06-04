import java.util.Random;

// A Q2 is a double-linked queue, like this:
//        |-> d         |-> d          |-> d
//   <----*0-----> <----*1----> <------*2----->
//        ^-- front                    ^-- back
// the Q2 is served from the front and entries are added to the back
// the name comes from Q= queue, 2= doubly-linked
// note that this implementation contains a lot of info we don't actually use, but it's not CPU-costly so no big deal
// we could get away with a singly-linked Q with only a pointer at the front

public class Q2 {
	
	private Binode front, back;
	private int length;
	
	// constructor
	public Q2()
	{
		this.front = null;
		this.back = null;
		this.length = 0;
	}
	
	// accessors
	public boolean isEmpty()
	{
		return length == 0;
	}
	public boolean isFull()
	{
		return false; // never full, because pointer-based!
	}
	public int getLength()
	{
		return this.length;
	}
	public Binode getFront()
	{
		return this.front;
	}
	public Binode getBack()
	{
		return this.back;
	}
	public Object getPos(int i) // searchable Q, so can get object at position i; indexing starts at 0
	{
		if(!isEmpty())
		{
			Binode pos = this.getNode(i);
			if(pos != null)
				return pos.getData();
			return null;
		}
//		System.out.println("Q2's length is"+this.length+"and I tried to find"+i);
		return null;
	}
	public Binode getNode(int i) // searchable Q, so can get node at position i; indexing starts at 0
	{
		if(!isEmpty())
		{
			Binode pos = this.front; // start pointing to the front, which is position 0
			if(i>=0 && i < this.length)
			{
				for(int j = 0; j<i ;j++) // if i=0, we're in the right place; if i=1, we do one getNext and are in the right place, etc.  will return null if out of bounds
					pos = pos.getNext();
				return pos;
			}
		}
		return null;
	}
	
	//mutators
	public void joinQ(Object thing)
	{
		Binode temp = new Binode(thing); // create a node called temp for the new thing
		if(isEmpty())
		{
			this.front = temp;
			this.back = temp;
		}
		else // not empty
		{
			temp.setPrev(this.back); // insert temp after the back
			this.back.setNext(temp); // so temp is back's new next
			this.back = temp; // and temp is the new back
		}		
		this.length++; // increment length of Q
	//	System.out.println("Object added to Q, length="+this.length);
	}
	public Object leaveQ() // returns object at front of Q, removing it from the Q, if the Q is non-empty; otherwise, returns null
	{
		if(!isEmpty())
		{
			Object temp = this.front.getData();
			this.front = this.front.getNext(); // front may not get garbage collected yet if length = 1, but it will be when someone else joins the Q
			this.length--;
			//System.out.println("Object removed from Q, length="+this.length);
			return temp;
		}
		//else
		return null;
	}
	
	public Q2 copy() // returns a copy of the current Q, but DOES NOT COPY THE OBJECTS IN THE Q, JUST THE Q ITSELF!
	{
		Q2 copy = new Q2();
		for(Binode temp = this.front; temp != null ; temp = temp.getNext())
			copy.joinQ(temp.getData());		
		return copy;
	}
	
	public Q2 randomizedCopy() // returns a copy of the current Q with a new, randomized order, but DOES NOT COPY THE OBJECTS IN THE Q, JUST THE Q ITSELF! 
	{
		Q2 tempcopy = this.copy();
		Q2 randomized = new Q2();
		Random rng = new Random(); // random number generator
		while(! tempcopy.isEmpty()) // we work by emptying tempcopy into randomized in a random sort of way
		{
			int next = rng.nextInt(tempcopy.getLength()); // next is an int from 0 to tempcopy's length - 1.
			Binode current = tempcopy.getFront();
			for(int i = 0; i < next ; i++)
				current = current.getNext(); // moves current to the (i+1)st position in tempcopy if i is not equal to next yet
			randomized.joinQ(current.getData()); // add the entry from current (the "next"th entry in tempcopy) to the randomized Q
			if(current.getPrev() != null) // take current out of tempcopy Q...
				current.getPrev().setNext(current.getNext()); // may be set to null
			else // at front of Q
				tempcopy.setFront(current.getNext());
			if(current.getNext() != null)
				current.getNext().setPrev(current.getPrev()); // may be set to null
			else // at back of Q
				tempcopy.setBack(current.getPrev());
			tempcopy.decrementLength();
		}
		
		return randomized;
	}
	
	private void setFront(Binode frnt) // needed to solve some pointer problems with random Q
	{
		this.front = frnt;
	}
	private void setBack(Binode bck) // needed to solve some pointer problems with random Q
	{
		this.back = bck;
	}
	
	
	private void decrementLength()
	{
		this.length--; // for use by methods which need to do shifty things to the inside of the Q without messing up the length arithmetic
	}
	
	public void clearQ()
	{
		this.front = null;
		this.back = null;
		this.length = 0;
	}

	
}