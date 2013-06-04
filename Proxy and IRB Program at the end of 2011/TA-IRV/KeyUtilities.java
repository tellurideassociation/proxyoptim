import java.util.Random;

public final class KeyUtilities
{
	// '0' = 48, '9' = 57, 'A' = 65, 'Z' = 90, 'a' = 97, 'z' = 122
	private final static String [] fancy = {"  /====================\\  " , " /    Your personal     \\ ", "/     voting key is:     \\", "|                        |","|                        |","\\   (keep it private,    /", " \\   and vote wisely!)  / ","  \\====================/  "};
	private final static Random rng = new Random();
	private final static int numNums = (int) '9' - (int) '0' + 1;
	private final static int numCHARS = (int) 'Z' - (int) 'A' + 1;
	private final static int numchars = (int) 'z' - (int) 'a' + 1;
	private final static int numChars = KeyUtilities.numNums + KeyUtilities.numCHARS + KeyUtilities.numchars;
	public final static int CODELENGTH = 3;
	
	public static void main(String [] args)
	{
		String [] codes = new String [30];
		for(int i = 0; i < codes.length ; i++)
			codes[i] = KeyUtilities.makeKeyString(rng.nextInt(6) + 5);
		for(int i = 0; i < codes.length ; i++)
			System.out.println(codes[i]);
		KeyUtilities.fancyPrint(codes);
		
	}
	
	
	
	public static boolean tryKey(String pw, Q2 keys)
	{
		boolean success = false;
		
		if(keys != null && ! keys.isEmpty() && pw != null && pw.length() > 0)
			for(Binode current = keys.getFront(); !success && current != null && current.getData() != null; current = current.getNext())
				success = ((Key) current.getData()).tryKey(pw);

		return success;
	}
	
	public static String makeKeyString(int length)
	{
		String thekey = "";
		for(int i = 0; i < length; i++)
			thekey += KeyUtilities.randomChar();
		
		return thekey;
	}
	
	private static char randomChar()
	{
		int pos = rng.nextInt(KeyUtilities.numChars); // get a random number
		if(pos < KeyUtilities.numNums)
			return (char) ((int) '0' + pos);
		else
			pos -= KeyUtilities.numNums;
		if(pos < KeyUtilities.numCHARS)
			return (char) ((int) 'A' + pos);
		else
			pos -= KeyUtilities.numCHARS;
		return (char) ((int) 'a' + pos);
	}
	
	public static void fancyPrint(String [] codes)
	{
		for(int i = 0 ; i < codes.length ;)
		{
			int j = 0;
			for(; j < 4 ; j++)
				System.out.println(fancy[j] + "    " + fancy[j] + "    " + fancy[j]);

			if(i < codes.length)
					System.out.print(KeyUtilities.keyWithSpace(codes[i]));
			else System.out.print(KeyUtilities.keyWithSpace(""));
			i++;
			System.out.print("    ");
			if(i < codes.length)
				System.out.print(KeyUtilities.keyWithSpace(codes[i]));
			else System.out.print(KeyUtilities.keyWithSpace(""));
			i++;
			System.out.print("    ");
			if(i < codes.length)
				System.out.println(KeyUtilities.keyWithSpace(codes[i]));
			else System.out.println(KeyUtilities.keyWithSpace(""));
			i++;
			
			for(; j < fancy.length ; j++)
				System.out.println(fancy[j] + "    " + fancy[j] + "    " + fancy[j]);
			System.out.println();
		}
		
	}
	
	private static String keyWithSpace(String key)
	{
		String output = "";
		output += "|  ";
		int k = 0;
		for(; k < (int) (20 - key.length()) / 2 ; k++) output += " ";
		output += key;
		k += key.length();
		for(; k < 20 ; k++) output += " ";
		output += "  |";
		return output;
	}
	
}