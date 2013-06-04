import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RankPanel implements ChangeListener {

	private Q2 candidates; // the list of candidates to plot out
	private JPanel panel; // the outputly panel
	private JRadioButton[][] buttons;
	private boolean built = false;
	
	public RankPanel(Q2 cands)
	{
		this.candidates = cands;
	
		this.panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		buttons = new JRadioButton[this.candidates.getLength()][this.candidates.getLength() + 1];
		for(int i = 0; i < buttons.length; i++)
		{
			for(int j = 0; j < buttons[i].length - 1; j++)
			{
				buttons[i][j] = new JRadioButton(""+(j+1));
				buttons[i][j].addChangeListener(this);
				c.gridy = i;
				c.gridx = j+1;
				this.panel.add(buttons[i][j],c);
			}
			buttons[i][buttons[i].length - 1] = new JRadioButton("no vote");
			buttons[i][buttons[i].length - 1].addChangeListener(this);
			buttons[i][buttons[i].length - 1].setSelected(true);
			c.gridy = i;
			c.gridx = buttons[i].length;
			this.panel.add(buttons[i][buttons[i].length - 1],c);
		}
		
		c.gridx = 0;
		int i=0;
		for(Binode temp = this.candidates.getFront(); temp != null && temp.getData() != null; temp = temp.getNext(), i++)
		{
			c.gridy = i;
			this.panel.add(new JLabel("<html><font color=#68228b><b>" + ((Candidate)temp.getData()).getName() + "</b></font></html>"),c);
		}
		this.built = true;
	}
	
	
	// accessors
	public JPanel getPane()
	{
		return this.panel;
	}
	
	// methods for extracting or giving information
	
	public void resetPanel()
	{
		for(int i=0; i<buttons.length; i++) // turn on all the "no vote" buttons
			buttons[i][buttons[i].length - 1].setSelected(true);
		// the state change listener will take care of the rest
	}
	
	public int [] extractRanks()
	{
		int [] ranks = new int[buttons.length];
		
		for(int i = 0; i < ranks.length; i++)
		{
			if(buttons[i][buttons[i].length - 1].isSelected())
				ranks[i] = 0; // no vote
			else
			{
				int j=0;
				for(;(j < buttons[i].length-1) && !buttons[i][j].isSelected();j++);
				ranks[i] = j+1;
			}
		}
		
		return ranks;
	}
	
	// the stateChanged method
	public void stateChanged(ChangeEvent e) {
		if(this.built) // don't do this during the constructor's operations
		{
			JRadioButton src = (JRadioButton) e.getSource();
			if(src.isSelected()) // if the source is selected, i.e. if it was turned on, do our thing (this will be turning buttons off a lot, and will stall if it runs at each instance of that)
			{
				// first, figure out where the source is
				for(int i=0; i < buttons.length; i++)
					for(int j=0; j < buttons[i].length; j++)
						if(buttons[i][j] == src)
						{// second, turn off everything else in its row and (if not "no vote") its column
							for(int k=0; k<j; k++) // row
								buttons[i][k].setSelected(false);
							for(int k=j+1; k < buttons[i].length; k++)
								buttons[i][k].setSelected(false);
							if(j != buttons[0].length - 1) // now its column, if it isn't at the end of its row
							{
								for(int k=0; k<i; k++)
									buttons[k][j].setSelected(false);
								for(int k=i+1; k<buttons.length; k++)
									buttons[k][j].setSelected(false);
							}
						}
			}
			// now, make sure something in each row is selected. we check after EVERY SINGLE CHANGE since at some point this sub-method won't be making any change
	
			for(int i=0; i < buttons.length; i++) // go through the rows
			{
				int j=0;
				boolean somethingOn = false;
				for(;j<buttons[i].length; j++)
					if(buttons[i][j].isSelected()) somethingOn = true;
				if(!somethingOn) buttons[i][buttons[i].length - 1].setSelected(true);
			}
		}
	}	
	
}