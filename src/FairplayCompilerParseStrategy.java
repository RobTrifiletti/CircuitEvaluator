import java.io.File;
import java.util.List;


public class FairplayCompilerParseStrategy<E> implements CircuitParseStrategy<Gate> {

	private CircuitSorter circuitSorter;
	
	public FairplayCompilerParseStrategy(String circuitFilename){
		this.circuitSorter = new CircuitSorter(new File(circuitFilename), null);
	}
	
	@Override
	public List<List<Gate>> getCircuit() {
		List<Gate> gates= circuitSorter.getParsedGates();
		List<List<Gate>> sortedGates = circuitSorter.getTimestampedGates(gates);
		
		return sortedGates;
	}
	
	public String getHeader(List<List<Gate>> sortedGates){
		return circuitSorter.getHeader(sortedGates);
	}

}
