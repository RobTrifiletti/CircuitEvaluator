import java.util.List;


public interface CircuitEvaluatorParseStrategy<E> {

	public List<List<E>> getParsedGates();
	public String getHeader();
}
