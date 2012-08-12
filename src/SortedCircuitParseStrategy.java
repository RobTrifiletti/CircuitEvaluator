import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class SortedCircuitParseStrategy<E> implements CircuitParseStrategy<Gate> {

	private File circuitFile;

	public SortedCircuitParseStrategy(String circuitFilename){
		this.circuitFile = new File(circuitFilename);
	}

	public List<List<Gate>> getParsedGates() {
		List<List<Gate>> layersOfGates = new ArrayList<List<Gate>>();

		try {
			BufferedReader fbr = new BufferedReader(new InputStreamReader(
					new FileInputStream(circuitFile), Charset.defaultCharset()));
			String line = fbr.readLine();
			//hack to skip first line
			List<Gate> currentLayer = null;
			while((line = fbr.readLine()) != null) {
				if (line.isEmpty()){
					continue;
				}

				if(line.startsWith("*")){
					currentLayer = new ArrayList<Gate>();
					layersOfGates.add(currentLayer);
					continue;
				}

				/*
				 * Parse each gate line and count numberOfNonXORGates
				 */
				GateEval g = new GateEval(line);
				currentLayer.add(g);
			}
			fbr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return layersOfGates;
	}

	public String getHeader() {
		BufferedReader fbr = null;
		String line = null;
		try {
			fbr = new BufferedReader(new InputStreamReader(
					new FileInputStream(circuitFile), Charset.defaultCharset()));
			line = fbr.readLine();
			fbr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return line;

	}

}
