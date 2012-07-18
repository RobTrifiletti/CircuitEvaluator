import java.io.File;
import java.util.List;

public class CircuitEvaluator implements Runnable {

	private File inputFile;
	private File circuitFile;
	private File outputFile;
	private CircuitParseStrategy<Gate> parseStrategy;

	public CircuitEvaluator(String inputFilename, String circuitFilename,
			String outputFilename, CircuitParseStrategy<Gate> parseStrategy){
		this.inputFile = new File(inputFilename);
		this.circuitFile = new File(circuitFilename);
		this.outputFile = new File(outputFilename);
		this.parseStrategy = parseStrategy;

		if (!inputFile.exists()){
			System.out.println("Inputfile: " + inputFile.getName() + " not found");
			return;
		}
		else if (!circuitFile.exists()){
			System.out.println("Inputfile: " + circuitFile.getName() + " not found");
			return;
		}
	}

	/**
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

		for(int param = 0; param < args.length; param++){
			if (inputFilename == null) {
				inputFilename = args[param];
			}
			else if (circuitFilename == null){
				circuitFilename = args[param];
			}
			else if (outputFilename == null) {
				outputFilename = args[param];
			}
			else if (args[param].equals("-f")){
				parseStrategy = 
						new FairplayCompilerParseStrategy<Gate>(circuitFilename);
			}

			else System.out.println("Unparsed: " + args[param]); 
		}

		if(outputFilename == null) {
			outputFilename = "out.txt";
		}
		if(parseStrategy == null){
			parseStrategy = new SortedParseStrategy<Gate>(circuitFilename);
		}
				
		CircuitEvaluator eval = new CircuitEvaluator(inputFilename, circuitFilename,
				outputFilename, parseStrategy);
		eval.run();
	}

	@Override
	public void run() {
		int[][] inputs = getInputs();
		List<List<Gate>> sortedCircuit;
	}
	
	private int[][] getInputs(){
		return null;
	}

}
