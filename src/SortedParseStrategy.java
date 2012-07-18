import java.util.List;


public class SortedParseStrategy<E> implements CircuitParseStrategy<Gate> {

	private String circuitFilename;
	
	public SortedParseStrategy(String circuitFilename){
		this.circuitFilename = circuitFilename;
	}
	
	@Override
	public List<List<Gate>> getCircuit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHeader(List<List<Gate>> sortedGates) {
		// TODO Auto-generated method stub
		return null;
	}

}
