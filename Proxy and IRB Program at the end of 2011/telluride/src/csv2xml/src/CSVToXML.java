import  java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.File;

/**
 * Main class functionality is to convert a M$ CSV file to XML as defined by props file.
 *
 *
 *
 */
public class CSVToXML
{
/**
 *The writer object is used for all output.
 */
    protected myWriter writer;

    /**
     *Set level of debug.
     **/
    public static int debug = 0;

    /**
     *Root method.
     *@param  csvFile csv input file
     *@see <a href="CSVFile.html">CSVFile</a>
     *@param  details , property file
     *@see <a href="propertyFile.html">propertyFile</a>
     *@param  writer , output file handle
     *
     **/
public static void ProduceXML(CSVFile csvFile, propertyFile details, myWriter writer)
{
    /**
     *buffer into which output data is built prior to writing
     *
     **/
    StringBuffer xmlFileData = new StringBuffer();

    // Add document node
    xmlFileData.append( "<!-- " + details.comment() + " -->\n");

    // Document root node
    xmlFileData.append( "<" + details.XMLRootName() + ">\n" );


    // Loop through csv records
    for (int i = 0; i < csvFile.count(); i++ )
	{
	    // New record
	    xmlFileData.append( "<" + details.recordName() + ">\n" );
	    // Loop through fields in current record
	    for (int x = 0; x < csvFile.getRecord(i).count(); x++) {
		if (debug > 2){
		    System.out.print("csv2xml: record size is: "+ csvFile.getRecord(i).count()+"\n");
		    System.out.print("csv2xml:[" + i+" "+ + x +"] " );
		    System.out.print("[ " + csvFile.getRecord(i).getFields(x)+"]");
		}
		    // Write the field to the file
		    xmlFileData.append( "<" + details.fieldNames(x) + ">"
					+ (csvFile.getRecord(i)).getFields(x)
					+ "</"
					+ details.fieldNames(x) + ">" );

	    } //end of record
	    if (debug > 2) System.err.println();
	    xmlFileData.append( "</" + details.recordName() + ">\n" );
	}//end of document
    xmlFileData.append( "</" + details.XMLRootName() + ">" );


	// ----- Now take the String and write to file
	String st = xmlFileData.toString();
	writer.write(st);
	writer.quit();



}
    /**
     *null constructor.
     *
     **/
public  CSVToXML()
{
	//  This is here to prevent an instance of this class being instantiated, as that would be pointless
}


/**
 *Standard Entry point: Calls doMain for action.
 *@param argv
 *@see <a href="#doMain">doMain</a> 
 **/
public static void main(String[] argv){
  // the real work is delegated to another routine so that it can be used in a subclass
    (new CSVToXML()).doMain(argv, new CSVToXML(), "java CSVToXML");

}

/**
 * Resolves command line parameters and calls draw class.
 * @param args Command line arguments
 * @param app class to be constructed
 * @param name Application name
 * <a name='doMain' />
 */

  protected void doMain(String args[], CSVToXML app, String name) {


        File outputFile = null;
        //ParameterSet params = new ParameterSet();
        //Properties outputProperties = new Properties();
        String outputFileName = null;
	String propertyFileName=null;
	String CSVFileName=null;

				// Check the command-line arguments.

        try {
            int i = 0;
            while (true) {
                if (i>=args.length) break;
		if (debug > 2)
		    System.err.println("i: "+ i + args.length+" args[i]="+args[i]);


                if (args[i].charAt(0)=='-') {
		    if (args[i].equals("-t")) {
                        System.err.println(Version.getProductName());
                        System.err.println("Java version " + System.getProperty("java.version"));
                        i++;
                    }
		    else if (args[i].equals("-p")) {
                        i++;
                        if (args.length < i+1) badUsage(name, "No Property  file Specified");
                        propertyFileName = args[i++];
			if (debug > 1)
			    System.err.println("props file: "+propertyFileName);


                    }
		    else if (args[i].equals("-i")) {
                        i++;
                        if (args.length < i+1) badUsage(name, "No input  file Specified");
                        CSVFileName = args[i++];
			if (debug > 1)
			    System.err.println("ip file: "+CSVFileName);

                    }

		    else if (args[i].equals("-o")) {
                        i++;
                        if (args.length < i+1) badUsage(name, "No output file name");
                        outputFileName = args[i++];
			if (debug > 1)
			    System.err.println("op file: "+outputFileName);


                    }
		    else badUsage(name, "Unknown option " + args[i]);
		}
		else break;
            } //end while
	    if (outputFileName!=null ) {
		outputFile = new File(outputFileName);
		if (outputFile.isDirectory()) {
		    quit("Output is a directory");
		}
            }
	}catch (Exception err2) {
            err2.printStackTrace();
        }
	//Check for files available: input, property and output
	if (propertyFileName == null)badUsage(name, "No property File available; Quitting");
	if (CSVFileName == null) badUsage(name, "No Input File available; Quitting");
	if (outputFileName == null) badUsage(name, "No Output File available; Quitting");

	writer = new myWriter();
	writer.openFile (outputFile);
	if (debug > 1){
	    System.err.println("CSVToXML: Producing "
			       + outputFileName
			       + " from " + CSVFileName
			       + " Using " + propertyFileName);
	}
	//Create new properties file
	propertyFile pf = new propertyFile(propertyFileName);
	 //go Convert it
	CSVFile iFile = new CSVFile( pf,CSVFileName);

	ProduceXML (iFile, pf, writer);



	//end the root calls

	writer.quit();
        System.exit(0);
  } //end of domain.

   /**
    * Exit with a message
    *@param message Message to be output prior to quitting.
    */

    protected static void quit(String message) {
        System.err.println(message);
	// Need to call svgOutput.closeFile
        System.exit(2);
    }
/**
 * Output the command line help.
 *@param name option in errror
 *@param message associated message
 *
 */

  protected void badUsage(String name, String message) {
        System.err.println(message);
        System.err.println(Version.getProductName());
        System.err.println("Usage: " + name + " [options] {param=value}...");
        System.err.println("Options: ");
        System.err.println("  -p filename     Take properties from named file  ");
        System.err.println("  -o filename     Send output to named file  ");
        System.err.println("  -i filename     Take CSV input from named file  ");

        System.err.println("  -t              Display version and timing information ");
	//System.err.println("  -w0             Recover silently from recoverable errors ");
        //System.err.println("  -w1             Report recoverable errors and continue (default)");
        //System.err.println("  -w2             Treat recoverable errors as fatal");
       System.err.println("  -?              Display this message ");
       System.exit(2);
    }


}
