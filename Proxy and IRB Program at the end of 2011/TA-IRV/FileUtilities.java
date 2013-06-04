import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public final class FileUtilities
{
	/*Commands accepted (read in order):
	 * 
	 * C,Groucho   ---> enters a candidate named Groucho
	 * C,Harpo
	 * C,Gummo
	 * C,Zeppo
	 * C,Chico
	 * C,Karl
	 * B,3,2,0,1,4,0 ---> enters a ballot Zeppo then Harpo then Groucho etc. no vote for Gummo, Karl
	 * b,karl,Zeppo,GUMMO,HaRpO,Chico ----> enters a ballot for Karl then Zeppo etc.
	 * 
	 * 
	 * 
	 */
	public static void parseFile(String thefile, Pemby election)
	{
		String [] inputcmds = readfile(thefile,1500);
		for(int i=0 ; i < inputcmds.length ; i++)
		{
			String [] command = inputcmds[i].split(","); // split line i by commas
			if(command.length > 0)
			{
				if(command[0].equals("C"))
				{
					if(command.length > 1)
						election.enterCandidate(command[1]);
				}
				else if(command[0].equals("B"))
				{
					if(command.length > 1)
					{
						Q2 ranks = new Q2();
						boolean nfeed = false; // records whether there's been a number format exception
						for(int j=1; j < command.length && !nfeed; j++)
						{
							try {
								ranks.joinQ(new Integer(Integer.parseInt(command[j])));
							} catch(NumberFormatException nfe)
							{// we go until there's an error or we run out of numbers
								nfeed = true;
							}
						}
						election.enterBallotByNumbers(ranks);
					}
				}
				else if(command[0].equals("b"))
				{
					if(command.length > 1)
					{
						Q2 names = new Q2();
						for(int j=1; j < command.length; j++)
							names.joinQ(command[j]);
						election.enterBallotByName(names);
					}
				}
			}
		}
	}
	
	public static void parseKeys(String thefile, Q2 keys)
	{
		String [] inputcmds = readfile(thefile,1500);
		
		if(inputcmds != null)
		{
			for(int i=0 ; i < inputcmds.length ; i++)
			{
				if(inputcmds[i] != null && inputcmds[i].length() > 0)
					keys.joinQ(new Key(inputcmds[i]));
			}
		}
	}
	
	public static String [] getOutputLocationsAtConsole() //asks user to tell you where she or he wants outputs written
	{
//		 from homework 1, for output management
		InputStreamReader keyboard = new InputStreamReader(System.in); //read from the keyboard
		BufferedReader buffkb = new BufferedReader(keyboard); //a buffered reader from the keyboard
		int outputs = 1; // default number of output locations
		System.out.println("How many places do you want results to be written? (default: "+outputs+")");
		try {outputs = Integer.parseInt(buffkb.readLine());}
		catch(Exception nfe)
		{
			System.out.println("I didn't get an integer from you.  I'll use the default\n"+outputs+".");
		}
		String [] writes = new String[outputs];
		for(int i=0; i< outputs; i++)	
		{
			writes[i] = "console"; // initialize output array to write to the console, since that's the only output for which we definitely know the (non-existent) directory structure
			System.out.println("Where am I writing the full output (output destination "+(i+1)+")? (hit enter to use console;)");
			String str = null;
			try{str=buffkb.readLine();}
			catch(Exception e)
			{
				System.out.println("Aargh! Exception!"+e);
			}
			if(str.length() != 0) writes[i] = str; //if the user didn't just hit enter, have writes point to the user's input instead of the default
		}
		// close up the shop
		try{
		buffkb.close(); // when
		keyboard.close(); // finished
		}
		catch(Exception e){ System.out.println("Aaargh, exception!"+e);}
		return writes;
	}
	
	//	 here is a lovely filereader from the decoder homework problem
	public static String [] readfile(String nameoffile,int numlines)
	{
		FileReader file = null;      //declare the name of our FileReader and BufferedFile Reader and initialize to something silly.  we'll only try to use it if we've assigned to it something cool
		BufferedReader bfile = null; // Same here; we declare these out of the try catch statement so that we can make the try-catch short and use them outside of the try-catch.
		
		boolean filefound = false;
		
		try{	
			file = new FileReader(nameoffile);
			filefound = true;  // let us know you found the file
		}
		catch(FileNotFoundException fnfe){
			System.out.println("I couldn't find your file.\n" + fnfe +"\nInstead I will use a default string.");
			// filefound will remain false if the file wasn't found
		}
		
		if(filefound) // we do one of two things, depending on whether the file was found
		{
			bfile = new BufferedReader(file); //make a buffered reader
			String [] strarray = new String [numlines]; // we'll make a big array of strings, which will be shrunken for the output
			
			String thisline;
			
			//we have to do a try-catch here because java isn't totally sure our buffered reader was set up correctly
			try {
				thisline = bfile.readLine(); //will return the first line of the file, or null if file is empty
			} catch (IOException e) {
				thisline = null; //if there's an exception, we might as well pretend that we couldn't read anything
				System.out.println("There was a problem with the buffered file reader: "+e);
			} 
			
			int linecount = 0; //this will tell us what line we're on
			while(thisline != null && linecount < strarray.length) // while we're still getting interesting data from the file reader and we're not overflowing 
			{
				strarray[linecount++] = thisline; //copy our line into the array and increment the number of lines found
				try{
					thisline = bfile.readLine(); // get the next line, see above for try-catch explanation
				} catch (IOException e) {
					thisline = null;
					System.out.println("There was a problem with the buffered file reader");
				}
			}
			
			// we're done reading the file, so we close it, that is, if we were able to open it in the first place
			
			try {
				bfile.close(); // try closing it
			} catch (IOException e) { // or else don't
				System.out.println("Problem closing buffered reader");
			}
			try {
				file.close(); // try closing it
			} catch (IOException e) { // or else don't
				System.out.println("Problem closing file reader");
			}
			
			String [] nicearray = new String [linecount]; //make an array which only contains the interesting lines
			for(int i = 0; i < nicearray.length; i++)
				nicearray[i] = strarray[i]; // give the addresses of the strings from the big array to the nice array for the interesting strings

			return nicearray; // return the nice array
		}
		// if the file wasn't found, do something silly but inoffensive
		String [] sillyarray = new String [0];  //an array of length zero! how silly!  I guess we haven't read anything interesting!
		return sillyarray;	
	}	
	
	public static void writeOuts(String [] writes, String output) // write fulloutput to the locations in writes array
	{
	//	System.out.println("writeOuts invoked");
		if(writes != null && output != null)
		{
			if(output.length() == 0)
				output = "What a boring output!";
			for(int i=0; i<writes.length;i++)
			{
				if(writes[i].equals("console")) System.out.println(output);
				else
				{
					FileWriter results = null;
					try{results = new FileWriter(writes[i]);//write to the file pointed to by writes
					results.write(output);
					results.flush();
					System.out.println("Results have been written to "+writes[i]);
					results.close();
					}
					catch(Exception e) {
						System.out.println("Aargh, exception!"+e);}
					
				}
			}
		}
		else if (writes == null)
			System.out.println("writes was null");
		else if (output == null)
			System.out.println("output");
	//	System.out.println("writeOuts is outahere!");
		
	}
	
}