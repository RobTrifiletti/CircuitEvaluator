import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

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
		if(inputFile.length() != inputSize/8){
			System.out.println("Input mismatch, check inputfile");
			return;
		}

		byte[] bytesRead = getBytesFromFile();

		BitSet bitset = bitsetToByteArray(bytesRead);

		List<List<Gate>> layersOfGates = parseStrategy.getParsedCircuit();
		BitSet result = evalCircuit(layersOfGates, bitset);

		writeCircuitOutput(result);
		
		// For testing purposes
		//verifyOutput();

	}

	// Returns the contents of the file in a byte array.
	public byte[] getBytesFromFile() {
		byte[] bytesRead = null;
		try {
			RandomAccessFile f = new RandomAccessFile(inputFile, "r");
			bytesRead = new byte[(int)f.length()];
			f.read(bytesRead);
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytesRead;
	}

	public BitSet bitsetToByteArray(byte[] bytes) {
		BitSet bits = new BitSet();
		for (int i=0; i<bytes.length*8; i++) {
			if ((bytes[bytes.length-i/8-1]&(1<<(i%8))) > 0) {
				bits.set((bytes.length*8 - 1) - i);
			}
		}
		return bits;
	}

	public BitSet evalCircuit(List<List<Gate>> layersOfGates, BitSet inputs) {
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
		for(int i =  startOfOutputGates; i <= maxGateNumber; i++){
			int outputIndex = i - startOfOutputGates;
			result.set(outputIndex, evals.get(i));
		}

		return result;
	}

	public void writeCircuitOutput(BitSet result) {
		//Convert to big endian for correct output format
		byte[] out = toByteArray(littleEndianToBigEndian(result));
		System.out.println(result.size());
		System.out.println(out.length);
		try {
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			outputStream.write(out);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public byte[] toByteArray(BitSet bits) {
		byte[] bytes = new byte[bits.size()/8];
		for (int i=0; i<bits.size(); i++) {
			if (bits.get(i)) {
				bytes[bytes.length-i/8-1] |= 1<<(i%8);
			}
		}
		return bytes;
	}

	public BitSet littleEndianToBigEndian(BitSet bitset){
		BitSet result = new BitSet(bitset.size());
		for(int i = 0; i < bitset.size(); i++){
			result.set((result.size() - 1) - i, bitset.get(i));
		}
		return result;
	}

	public void verifyOutput() {
		File expectedResultFile = null;
		if(inputFile.getName().equals("input0.bin")){
			expectedResultFile = new File("data/expected0.bin");
		}
		else if(inputFile.getName().equals("input1.bin")){
			expectedResultFile = new File("data/expected1.bin");
		}
		else if(inputFile.getName().equals("input2.bin")){
			expectedResultFile = new File("data/expected2.bin");
		}
		else if(inputFile.getName().equals("input3.bin")){
			expectedResultFile = new File("data/expected3.bin");
		}
		
		try {
			if(FileUtils.contentEquals(expectedResultFile, outputFile)){
				System.out.println("Circuit evaluated correctly");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Helper method for debugging
	public String bitsetToBitString(BitSet bitset) {
		String res = "";
		for(int i = 0; i < bitset.size(); i++){
			if (i != 0 && i % 8 == 0){
				res += " ";
			}
			if(bitset.get(i)){
				res += '1';
			}
			else res += '0';
		}
		return res;
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
