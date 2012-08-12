import java.io.File;


public class Driver {
	/** Input format: inputfile (binary file) circuitfile.txt outputfile.txt
	 * Both inputfile and circuitfile.txt must exist, else error.
	 * If no output filename is supplied, data/out.txt is chosen by default
	 * Optional: add a -f argument if you supply a circuit in the Fairplay compiled
	 * format.
	 * Optional: add a -v argument if you are testing the evaluator 
	 * (kinda hardcoded, for debug use only).
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length < 2){
			System.out.println("Incorrect number of arguments, please specify inputfile");
			return;
		}

		String inputFilename = null;
		String circuitFilename = null;
		String outputFilename = null;
		CircuitParseStrategy<Gate> parseStrategy = null;
		boolean verify = false;

		for(int param = 0; param < args.length; param++){
			if (inputFilename == null) {
				inputFilename = args[param];
			}
			else if (circuitFilename == null){
				circuitFilename = args[param];
			}
			else if (args[param].equals("-f")){
				parseStrategy = 
						new FairplayCompilerParseStrategy<Gate>(circuitFilename);
			}
			else if (args[param].equals("-v")){
				verify = true;
			}
			else if (outputFilename == null) {
				outputFilename = args[param];
			}

			else System.out.println("Unparsed: " + args[param]); 
		}

		if(outputFilename == null) {
			outputFilename = "data/out.bin";
		}
		if(parseStrategy == null){
			parseStrategy = new SortedCircuitParseStrategy<Gate>(circuitFilename);
		}

		File inputFile = new File(inputFilename);
		File circuitFile = new File(circuitFilename);
		File outputFile = new File(outputFilename);

		if (!inputFile.exists()){
			System.out.println("Inputfile: " + inputFile.getName() + " not found");
			return;
		}
		else if (!circuitFile.exists()){
			System.out.println("Inputfile: " + circuitFile.getName() + " not found");
			return;
		}


		CircuitEvaluator eval = new CircuitEvaluator(inputFile,
				outputFile, parseStrategy, verify);
		eval.run();
	}
}
