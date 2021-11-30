package proj01.model;

public enum Operation {
	NOP { 
		@Override
		public boolean isModeValid(Mode mode) {
			return mode==Mode.NOM;
		}
		@Override
		public void execute(CPU cpu, Instruction inst) {
			Trace.instMsg(inst,"");
		}
	}, 
	LOD {
		@Override
		public boolean isModeValid(Mode mode) {
			return isModeIMMorDIR(mode);
		}
		@Override
		public void execute(CPU cpu, Instruction inst) throws PippinException {
			cpu.setAccumulator(inst.fetchOperand(cpu));
			Trace.instMsg(inst," acc=" + cpu.getAccumulator());
		}
	}, 
	STO {
		public boolean isModeValid(Mode mode) {
			return (mode==Mode.DIR || mode==Mode.IND);
		}
		public void execute(CPU cpu, Instruction inst) throws PippinMemoryException {
			int target=inst.getArgument();
			if (inst.getMode()==Mode.IND) target = cpu.getData(target);
			cpu.setData(target,cpu.getAccumulator());
			Trace.instMsg(inst," data[" + target + "]=" + cpu.getAccumulator());
		}
	}, 
	ADD {
		@Override
		public boolean isModeValid(Mode mode) {
			return isModeIMMorDIR(mode);
		}
		@Override
		public void execute(CPU cpu, Instruction inst) throws PippinException {
			cpu.setAccumulator(inst.fetchOperand(cpu)+cpu.getAccumulator());
			Trace.instMsg(inst," acc=" + cpu.getAccumulator());
		}
	}, 
	SUB {
		@Override
		public boolean isModeValid(Mode mode) {
			return isModeIMMorDIR(mode);
		}
		@Override
		public void execute(CPU cpu, Instruction inst) throws PippinException {
			cpu.setAccumulator(cpu.getAccumulator()-inst.fetchOperand(cpu));
			Trace.instMsg(inst," acc=" + cpu.getAccumulator());
		}
	}
		
	, 
	MUL {
		@Override
		public boolean isModeValid(Mode mode) {
			return isModeIMMorDIR(mode);
		}
		@Override
		public void execute(CPU cpu, Instruction inst) throws PippinException {
			cpu.setAccumulator(cpu.getAccumulator()*inst.fetchOperand(cpu));
			Trace.instMsg(inst," acc=" + cpu.getAccumulator());
		}
	}, 
	DIV {
		@Override
		public boolean isModeValid(Mode mode) {
			return isModeIMMorDIR(mode);
		}
		@Override
		public void execute(CPU cpu, Instruction inst) throws PippinException {
			if (inst.fetchOperand(cpu)==0) throw new PippinDivideByZeroException();
			cpu.setAccumulator(cpu.getAccumulator()/inst.fetchOperand(cpu));
			Trace.instMsg(inst," acc=" + cpu.getAccumulator());
		}
	}, 
	AND {
		@Override
		public boolean isModeValid(Mode mode) {
			return isModeIMMorDIR(mode);
		}
		@Override
		public void execute(CPU cpu, Instruction inst) throws PippinException {
			int newValue = ((inst.fetchOperand(cpu) != 0) && (cpu.getAccumulator() != 0) ? 1 : 0);
			cpu.setAccumulator(newValue);
			Trace.instMsg(inst," acc=" + cpu.getAccumulator());
		}
	}, 
	NOT {
		@Override
		public boolean isModeValid(Mode mode) {
			return mode==Mode.NOM;
		}
		@Override
		public void execute(CPU cpu, Instruction inst) {
			int newValue = ((cpu.getAccumulator() != 0) ? 0 : 1);
			cpu.setAccumulator(newValue);
			Trace.instMsg(inst," acc=" + cpu.getAccumulator());
		}
	}, 
	CML {
		@Override
		public boolean isModeValid(Mode mode) {
			if (mode==Mode.DIR) return true;
			if (mode==Mode.IND) return true;
			return false;
		}
		@Override
		public void execute(CPU cpu, Instruction inst) throws PippinException {
			int newValue=(inst.fetchOperand(cpu)<0) ? 1 : 0;
			cpu.setAccumulator(newValue);
			Trace.instMsg(inst," acc=" + cpu.getAccumulator());
		}
	}, 
	CMZ{
		@Override
		public boolean isModeValid(Mode mode) {
			if (mode==Mode.DIR) return true;
			if (mode==Mode.IND) return true;
			return false;
		}
		@Override
		public void execute(CPU cpu, Instruction inst) throws PippinException {
			int newValue=(inst.fetchOperand(cpu)==0) ? 1 : 0;
			cpu.setAccumulator(newValue);
			Trace.instMsg(inst," acc=" + cpu.getAccumulator());
		}
	},
	JMP {
		@Override
		public boolean isModeValid(Mode mode) {
			return mode==Mode.IMM;
		}
		@Override
		public void execute(CPU cpu, Instruction inst) throws PippinException {
			int currentIp=cpu.getInstructionPointer();
			cpu.setInstructionPointer(currentIp - 1 + inst.fetchOperand(cpu));
			Trace.instMsg(inst," instructionPointer = " + cpu.getInstructionPointer()); 
		} 
	},
	JMZ {
		@Override
		public boolean isModeValid(Mode mode) {
			return mode==Mode.IMM;
		}
		@Override
		public void execute(CPU cpu, Instruction inst) throws PippinException {
			if (cpu.getAccumulator()==0) { 
				int currentIp=cpu.getInstructionPointer();
				cpu.setInstructionPointer(currentIp - 1 + inst.fetchOperand(cpu));
			}
			Trace.instMsg(inst," instructionPointer = " + cpu.getInstructionPointer()); 
		}
	},
	HLT { 
		@Override
		public boolean isModeValid(Mode mode) {
			return mode==Mode.NOM;
		}
		@Override
		public void execute(CPU cpu, Instruction inst) {
			cpu.setHalted(true);
			Trace.instMsg(inst," Program halted");
		}
	};
	
	public abstract boolean isModeValid(Mode mode);
	public abstract void execute(CPU cpu, Instruction inst) throws PippinException;
	
	private static boolean isModeIMMorDIR(Mode mode) {
		if (mode==Mode.IMM || mode==Mode.DIR || mode==Mode.IND) return true;
		return false; 
	}
	
}
