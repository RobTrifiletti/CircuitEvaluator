import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
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
		byte[] inputs = getInputs();
		List<List<Gate>> layersOfGates = parseStrategy.getParsedCircuit();
		byte[] result = evaluateCircuit(layersOfGates, inputs);
	}

	private byte[] getInputs(){
		byte[] result = null;
		BufferedInputStream input;
		try {
			input = new BufferedInputStream(new FileInputStream(inputFile));
			result = new byte[(int)inputFile.length()];
			int totalBytesRead = 0;
			while(totalBytesRead < result.length) {
				int bytesRemaining = result.length - totalBytesRead;
				//input.read() returns -1, 0, or more :
				
				int bytesRead = input.read(result, totalBytesRead, bytesRemaining); 
				if (bytesRead > 0){
					totalBytesRead = totalBytesRead + bytesRead;
				}
			}
			input.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	

	private byte[] evaluateCircuit(List<List<Gate>> layersOfGates, byte[] inputs) {
		byte[] result = new byte[inputs.length/2];
		
		HashMap<Integer, Boolean> evals = new HashMap<Integer, Boolean>();
		
		int pos = 0;
		for(byte b: inputs){
			evals.put(pos++, (b & 0x1) != 0);
			evals.put(pos++, (b & 0x2) != 0);
			evals.put(pos++, (b & 0x4) != 0);
			evals.put(pos++, (b & 0x8) != 0);
			evals.put(pos++, (b & 0x10) != 0);
			evals.put(pos++, (b & 0x20) != 0);
			evals.put(pos++, (b & 0x40) != 0);
			evals.put(pos++, (b & 0x80) != 0);	
		}
		
		
		for(List<Gate> layer: layersOfGates){
			for(Gate g: layer){
				String gate = g.getGate();
				char[] gateArray = gate.toCharArray();
				
				System.out.println(g);
				System.out.println(layersOfGates.indexOf(layer));
				boolean leftInput = evals.get(g.getLeftWireIndex());
				boolean rightInput = evals.get(g.getRightWireIndex());
				
				if (leftInput == false &&
						rightInput == false){
					if(gateArray.length < 4){
						evals.put(g.getOutputWireIndex(), false);
						continue;
					}
					else if (gateArray[0] == '1'){
						evals.put(g.getOutputWireIndex(), true);
					}
					else evals.put(g.getOutputWireIndex(), false);
				}
				else if(leftInput == false
						&& rightInput == true){
					if(gateArray.length < 3){
						evals.put(g.getOutputWireIndex(), false);
						continue;
					}
					else if (gateArray[0] == '1'){
						evals.put(g.getOutputWireIndex(), true);
					}
					else evals.put(g.getOutputWireIndex(), false);
					
				}
				else if(leftInput == true &&
						rightInput == false){
					if(gateArray.length < 2){
						evals.put(g.getOutputWireIndex(), false);
						continue;
					}
					else if (gateArray[0] == '1'){
						evals.put(g.getOutputWireIndex(), true);
					}
					else evals.put(g.getOutputWireIndex(), false);
					
				}
				else if(leftInput == true &&
						rightInput == true){
					if(gateArray.length < 1){
						evals.put(g.getOutputWireIndex(), false);
						continue;
					}
					else if (gateArray[0] == '1'){
						evals.put(g.getOutputWireIndex(), true);
					}
					else evals.put(g.getOutputWireIndex(), false);
				}
			}
		}
		System.out.println(evals.size());
		
		return null;
	}
}
