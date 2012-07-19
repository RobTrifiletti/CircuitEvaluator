
public class GateEval implements Gate {
	
	private static final int numberOfInputWires = 2;
	
	private int counter;
	private int time;
	private int leftWireIndex;
	private int rightWireIndex;
	private int outputWireIndex;
	private String gate;
	private int gateNumber;
	private int layer;
	
	public GateEval(String s){
		
		//Example string: 2 1 96 99 256 0110 
		String[] split = s.split(" ");
		counter = numberOfInputWires;
		time = 0;
		layer = Integer.parseInt(split[0]);
		gateNumber = Integer.parseInt(split[1]);
		leftWireIndex = Integer.parseInt(split[2]);
		rightWireIndex = Integer.parseInt(split[3]);
		outputWireIndex = Integer.parseInt(split[4]);
		
		gate = split[5].replaceFirst("^0*", ""); //Removes leading 0's
	}

	@Override
	public int getLeftWireIndex(){
		return leftWireIndex;
	}
	
	@Override
	public int getRightWireIndex(){
		return rightWireIndex;
	}

	@Override
	public int getOutputWireIndex(){
		return outputWireIndex;
	}
	
	@Override
	public int getCounter(){
		return counter;
	}

	@Override
	public void decCounter(){
		counter--;
	}

	@Override
	public int getTime(){
		return time;
	}

	@Override
	public void setTime(int time){
		this.time = Math.max(this.time, time);
	}
	
	@Override
	public String getGate(){
		return gate;
	}
	
	@Override
	public String toString(){
		return getGateNumber() + " " + getLeftWireIndex() + " " + getRightWireIndex() +
				" " + getOutputWireIndex() + " " + getGate();
	}

	@Override
	public boolean isXOR(){
		if (gate.matches("110")){
			return true;
		}
		else return false;
	}
	
	@Override
	public void setGateNumber(int gateNumber){
		this.gateNumber = gateNumber;
	}
	
	@Override
	public int getGateNumber(){
		return gateNumber;
	}
	
	public int getGateLayer(){
		return layer;
	}
}
