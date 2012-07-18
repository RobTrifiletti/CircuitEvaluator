
public class CircuitEvaluator {

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length < 2){
			System.out.println("Incorrect number of arguments, please specify inputfile");
			return;
		}

		String inputfileName = null;
		String circuitfileName = null;
		String outputfileName = null;
		
		for(int param = 0; param < args.length; param++){
			if (inputfileName == null) {
				inputfileName = args[param];
			}
			else if (circuitfileName == null){
				circuitfileName = args[param];
			}
			else if (outputfileName == null) {
				outputfileName = args[param];
			}

			else System.out.println("Unparsed: " + args[param]); 
		}
		
		if(outputfileName == null) {
			System.out.println("hej");
			outputfileName = "out.txt";
		}
	}

}
