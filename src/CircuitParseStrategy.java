import java.util.List;


public interface CircuitParseStrategy<E> {

	public List<List<E>> getParsedCircuit();
	public String getHeader(List<List<E>> sortedGates);
}
