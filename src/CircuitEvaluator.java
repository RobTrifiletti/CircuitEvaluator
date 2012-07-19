import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

public class CircuitEvaluator implements Runnable {

	private File inputFile;
	private File outputFile;
	private CircuitParseStrategy<Gate> parseStrategy;

	private int inputSize;
	private int outputSize;
	private int numberOfWires;

	public CircuitEvaluator(File inputFile, File outputFile,
			CircuitParseStrategy<Gate> parseStrategy){
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.parseStrategy = parseStrategy;
		String[] split = parseStrategy.getHeader().split(" ");
		inputSize = Integer.parseInt(split[0]);
		outputSize = Integer.parseInt(split[1]);
		numberOfWires = Integer.parseInt(split[2]);

	}

	@Override
	public void run() {
		BitSet inputs = readInputs();
		if(inputFile.length() != inputSize/8){
			System.out.println("Input mismatch, check inputfile");
			return;
		}

		List<List<Gate>> layersOfGates = parseStrategy.getParsedCircuit();
		BitSet result = getCircuitOutput(layersOfGates, inputs);

		writeCircuitOutput(result);

		/*
		 * For visual output of the bits to standard out
		 */
//		for(int i = 0; i < result.size(); i++){
//			if(result.get(i)){
//				System.out.print('1');
//			}
//			else System.out.print('0');
//		}
	}

	private BitSet readInputs() {
		BitSet result = new BitSet(inputSize);
		BufferedInputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream(inputFile));
			int buf = -1;
			int j = 0;
			while((buf = in.read()) > -1) {
				buf &= 0xFF;
				int cursor = 0x80;
				for(int i = 0;i < 8;i++) {
					if((buf & cursor) > 0){
						result.set(j);
					}
					cursor >>= 1;
					j++;
				}
			}
			in.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private BitSet getCircuitOutput(List<List<Gate>> layersOfGates, BitSet inputs) {
		BitSet result = new BitSet();

		// Construct and fill up initial evaluation map with the inputs
		HashMap<Integer, Boolean> evals = new HashMap<Integer, Boolean>();
		for(int i = 0; i < inputSize; i++){
			evals.put(i, inputs.get(i));
		}

		int maxGateNumber = 0;
		for(List<Gate> layer: layersOfGates){
			for(Gate g: layer){
				maxGateNumber = Math.max(maxGateNumber, g.getOutputWireIndex());
				String gate = g.getGate();
				char[] gateArray = gate.toCharArray();

				boolean leftInput = evals.get(g.getLeftWireIndex());
				boolean rightInput = evals.get(g.getRightWireIndex());

				if (leftInput == false &&
						rightInput == false){
					if(gateArray.length < 4){
						evals.put(g.getOutputWireIndex(), false);
						continue;
					}
					if (gateArray[gateArray.length - 4] == '1'){
						evals.put(g.getOutputWireIndex(), true);
					}
					else evals.put(g.getOutputWireIndex(), false);
				}
				if(leftInput == false
						&& rightInput == true){
					if(gateArray.length < 3){
						evals.put(g.getOutputWireIndex(), false);
						continue;
					}
					if (gateArray[gateArray.length - 3] == '1'){
						evals.put(g.getOutputWireIndex(), true);
					}
					else evals.put(g.getOutputWireIndex(), false);

				}
				if(leftInput == true &&
						rightInput == false){
					if(gateArray.length < 2){
						evals.put(g.getOutputWireIndex(), false);
						continue;
					}
					if (gateArray[gateArray.length - 2] == '1'){
						evals.put(g.getOutputWireIndex(), true);
					}
					else evals.put(g.getOutputWireIndex(), false);
				}
				if(leftInput == true &&
						rightInput == true){
					if(gateArray.length < 1){
						evals.put(g.getOutputWireIndex(), false);
						continue;
					}
					if (gateArray[gateArray.length - 1] == '1'){
						evals.put(g.getOutputWireIndex(), true);
					}
					else evals.put(g.getOutputWireIndex(), false);
				}
			}
		}
		int startOfOutputGates = maxGateNumber - outputSize + 1;
		for(int i =  startOfOutputGates; i < maxGateNumber; i++){
			int outputIndex = i - startOfOutputGates;
			result.set(outputIndex, evals.get(i));
		}

		return result;
	}

	private void writeCircuitOutput(BitSet result) {
		byte[] out = getByteArray(result);

		try {
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			outputStream.write(out);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public byte[] getByteArray(BitSet bits) {
		byte[] bytes = new byte[(bits.length() + 7) / 8];
		for (int i=0; i<bits.length(); i++) {
			if (bits.get(i)) {
				bytes[bytes.length-i/8-1] |= 1<<(i%8);
			}
		}
		return bytes;
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
			 outputFilename = "data/out.bin";
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


		 CircuitEvaluator eval = new CircuitEvaluator(inputFile,
				 outputFile, parseStrategy);
		 eval.run();
	 }
}
