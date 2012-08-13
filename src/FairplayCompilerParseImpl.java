import java.io.File;
import java.util.List;

/*
 * Stub if needed to evaluate a fairplay compiled circuit
 */
public class FairplayCompilerParseImpl<E> implements CircuitEvaluatorParseStrategy<Gate> {

	private CircuitConverter cc;
	private List<List<Gate>> layersOfGates;
	
	public FairplayCompilerParseImpl(String circuitFilename){
		File circuitFile = new File(circuitFilename);
		cc = new CircuitConverter(circuitFile, circuitFile, false);
	}
	
	@Override
	public List<List<Gate>> getParsedGates() {
		List<Gate> gates = cc.getParsedGates();
		
		layersOfGates = cc.getLayersOfGates(gates);
		
		return layersOfGates;
	}

	@Override
	public String getHeader() {
		int[] intHeaders = cc.getOutputHeader(layersOfGates);
		String header = "";
		
		for (int i = 0; i < intHeaders.length; i++){
			header += (intHeaders[i] + "");
			if (i != intHeaders.length - 1){
				header += " ";
			}
		}
		
		return header;
	}

}
