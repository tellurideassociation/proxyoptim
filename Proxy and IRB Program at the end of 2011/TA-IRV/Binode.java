// this is a basic 2-pointer node for use in our double-linked queue Q2.  It consists of an element of type E, called data,
// and the adresses of two other nodes, named prev(ious) and next (or pointing to null nodes).  It's like this:
//  prev  <----- this ------> next
//                |--> data

public class Binode {
	
	private Object data;
	private Binode prev,next;
	
	//Constructors
	public Binode(Object data,Binode prev,Binode next)
	{
		this.data=data;
		this.prev=prev;
		this.next=next;
	}
	public Binode(Object data)
	{
		this(data,null,null);
	}
	public Binode()
	{
		this(null,null,null);
	}
	
	//Accessors
	public Binode getPrev()
	{
		return prev;
	}
	public Binode getNext()
	{
		return next;
	}
	public Object getData()
	{
		return data;
	}
	public boolean noData()
	{
		return data == null;
	}
	//Mutators
	public void setData(Object data)
	{
		this.data = data;
	}
	public void setPrev(Binode prev)
	{
		this.prev = prev;
	}
	public void setNext(Binode next)
	{
		this.next = next;
	}	
	
}