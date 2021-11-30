package proj01.model;

public class CPU {
	
	private int accumulator;
	private int instructionPointer;
	private int dataMemoryBase;
	private Memory memory;
	private boolean halted;
	private Job currentJob;

	public CPU(Memory memory) {
		accumulator=0; // Not required, but included for clarity
		dataMemoryBase=0; 
		instructionPointer=0; // Start programs at memory location 0
		this.memory=memory;
		halted=true;
	}
	
	public void run() {
		halted=false;
		while(!halted) execute();
	}
	
	public void run(int timeSlice) { 
		for(int i=0; i<timeSlice && !halted;i++) execute();
	 }
	
	public void execute() {
		
		if (halted) return; 
		
		try {
			// Fetch instruction from memory
			Instruction inst = Instruction.factory(memory.get(instructionPointer));
			instructionPointer++;
			inst.isValid(); // If not, an exception will be thrown
			inst.execute(this);
		} catch(PippinException e) {
			halted=true;
			System.out.println("Program Halted, Pippin Exception occured: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public int getAccumulator() { return accumulator; }
	public void setAccumulator(int accumulator) { this.accumulator = accumulator; }

	public void setDataMemoryBase(int dataMemoryBase) {
		this.dataMemoryBase = dataMemoryBase;
	}
	
	public Memory getMemory() { return memory; }

	public int getInstructionPointer() { return instructionPointer; }

	public void setInstructionPointer(int instructionPointer) {
		this.instructionPointer = instructionPointer;
	}

	public int getData(int loc) throws PippinMemoryException {
		return memory.get(loc+dataMemoryBase);
	}
	
	public void setData(int loc,int value) throws PippinMemoryException {
		memory.set(loc+dataMemoryBase, value);
	}
	
	public boolean isHalted() { return halted; }

	public void setHalted(boolean halted) { this.halted = halted; }

	public int getDataMemoryBase() { return dataMemoryBase; }

	public void setCurrentJob(Job job) { currentJob=job; }

	public Job getCurrentJob() { return currentJob; }

}
