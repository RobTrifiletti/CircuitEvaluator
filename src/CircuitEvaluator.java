import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CircuitEvaluator implements Runnable {

	private File inputFile;
	private File circuitFile;
	private File outputFile;
	private CircuitParseStrategy<Gate> parseStrategy;

	public CircuitEvaluator(File inputFile, File circuitFile,
			File outputFile, CircuitParseStrategy<Gate> parseStrategy){
		this.inputFile = inputFile;
		this.circuitFile = circuitFile;
		this.outputFile = outputFile;
		this.parseStrategy = parseStrategy;
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
			else if (args[param].equals("-f")){
				parseStrategy = 
						new FairplayCompilerParseStrategy<Gate>(circuitFilename);
			}
			else if (outputFilename == null) {
				outputFilename = args[param];
			}

			else System.out.println("Unparsed: " + args[param]); 
		}

		if(outputFilename == null) {
			outputFilename = "out.txt";
		}
		if(parseStrategy == null){
			parseStrategy = new SortedParseStrategy<Gate>(circuitFilename);
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

		CircuitEvaluator eval = new CircuitEvaluator(inputFile, circuitFile,
				outputFile, parseStrategy);
		eval.run();
	}

	@Override
	public void run() {
		List<char[]> inputs = getInputs();
		List<List<Gate>> layersOfGates = parseStrategy.getParsedCircuit();
	}

	private List<char[]> getInputs(){
		List<char[]> inputs = new ArrayList<char[]>();

		BufferedReader fbr;
		try {
			fbr = new BufferedReader(new InputStreamReader(
					new FileInputStream(inputFile), Charset.defaultCharset()));
			String line = "";
			while((line = fbr.readLine()) != null) {
				if (line.isEmpty()){
					continue;
				}
				inputs.add(line.toCharArray());
			}
			fbr.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputs;
	}
}
