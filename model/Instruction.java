package proj01.model;

import java.util.Scanner;

public class Instruction {
	
	// Fields
	Operation opcode;
	Mode mode;
	int argument;
	
	static public Instruction factory(String asmInst) throws PippinInstructionException {
		Scanner instructionScanner = new Scanner(asmInst);
		String opName=instructionScanner.next();
		String modeName=instructionScanner.next();
		int argument=instructionScanner.nextInt();
		instructionScanner.close();
		return factory(opName,modeName,argument);
	}
	
	static public Instruction factory(String opName,String modeName,int argument) throws PippinInstructionException {
		return new Instruction(findOpcode(opName),findMode(modeName),argument);
	}
	
	static public Instruction factory(int binInstr) throws PippinInstructionException {
		return factory(getEncodedOpcode(binInstr),
							getEncodedMode(binInstr),
							getEncodedArg(binInstr));
	}

		
	static public Instruction factory(int opcode,int mode,int argument) throws PippinInstructionException {
		return new Instruction(Operation.values()[opcode],
				Mode.values()[mode],
				argument);
	}
	
	public Instruction(Operation opcode,Mode mode,int argument) {
		this.opcode = opcode;
		this.mode = mode;
		this.argument = argument;
	}
	
	public void store(Memory mem,int loc) throws PippinMemoryException {
		mem.set(loc,encodeOpModeArg(opcode.ordinal(),mode.ordinal(),argument));
	}
	
	public String getOpName() throws PippinInstructionException { 
		return opcode.name(); }

	public int getOpcode() { return opcode.ordinal(); }

	public String getModeName() throws PippinInstructionException {
		return mode.name(); 
	}

	public Mode getMode() { return mode; }

	public int getArgument() { return argument; }
	
	public int fetchOperand(CPU cpu) throws PippinException {
		// Note... override this method for special opcodes (like STO)
		if (mode==Mode.IMM) return argument;
		if (mode==Mode.DIR) return cpu.getData(argument);
		if (mode==Mode.IND) return cpu.getData(cpu.getData(argument));
		throw new PippinInstructionException("fetchOperand expected a mode of either IMM or DIR or IND");
	}
	
	public void isValid() throws PippinInstructionException {
		if (!isModeValid()) {
			throw new PippinInstructionException("Mode " + mode + " is not a valid mode for this instruction.");
		}
	}
	
	public boolean isModeValid() {
		return opcode.isModeValid(mode);
	}
	
	public void execute(CPU cpu) throws PippinException {
		opcode.execute(cpu,this);
	}

	public static Operation findOpcode(String name) throws PippinInstructionException {
		try {
			return Operation.valueOf(name);
		} catch(IllegalArgumentException e) {
			throw new PippinInstructionException("findOpcode: " + 
				name + " is not a valid opcopde name");
		}
	}
	
	public static Mode findMode(String name) throws PippinInstructionException {
		try {
			return Mode.valueOf(name);
		} catch(IllegalArgumentException e) {
			throw new PippinInstructionException("findMode: " + 
				name + " is not a valid mode name");
		}
	}
	
	public static int encodeOpModeArg(int opcode,int mode,int arg) {
		int opMode= (opcode*10 + mode) << 24;
		int binInst = opMode | (arg & 0xFFFFFF);
		return binInst;
	}
	
	public static int getEncodedOpcode(int binInstr) {
		int opMode=(binInstr>>24) & 0xFF; // Get last 8 bits
		return opMode/10;
	}
	
	public static int getEncodedMode(int binInstr) {
		int opMode=(binInstr>>24) & 0xFF;
		return opMode%10; // Remainder operation discards the opcode
	}
	
	public static int getEncodedArg(int binInstr) {
		int arg = binInstr<<8; // Shift off leftmost 8 bits
		arg = arg>>8; // And shift back, propagating sign bit
		return arg;
	}

	@Override
	public String toString() {
		try {
			return getOpName() + " " + getModeName()
					+ " arg=" + argument;
		} catch (PippinInstructionException e) {
			return "[Invalid instruction]";
		}
	}

	public int toInt() { return encodeOpModeArg(opcode.ordinal(),mode.ordinal(),argument); }

	public void setArgument(int arg) { argument=arg; }
	
}