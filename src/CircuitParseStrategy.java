import java.util.List;


public interface CircuitParseStrategy<E> {

	public List<List<E>> getParsedGates();
	public String getHeader();
}
