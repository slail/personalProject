package proj01.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;

public class Program {
	
	private List<Instruction> program;
	private Map<Integer,Integer> initializations;
	private String name;
	
	/**
	 * Constructor - creates a new empty program (no instructions).
	 */
	public Program() {
		program=new ArrayList<Instruction>();
		initializations=new HashMap<Integer,Integer>();
		this.name = "?";
	}
	
	public Program(String name) throws PippinException {
		this.name=name;
		program=new ArrayList<Instruction>();
		initializations=new HashMap<Integer,Integer>();
		readPexe();
	}
	
	public void readPexe() throws PippinException {
		String objName = Pippin.PEXEPATH + name + ".pexe";
		File objFile = new File(objName);
		try(Scanner objScan = new Scanner(objFile)) {
			while(objScan.hasNextInt()) {
				int instr = objScan.nextInt();
				add(Instruction.factory(instr));
			}
			objScan.nextLine(); // Get to the end of the last line
			if (objScan.hasNextLine() && objScan.nextLine().equals("---init---")) {
				while( objScan.hasNextLine() ) {
					objScan.findInLine("(\\d+)\\=(\\d+)");
					MatchResult result = objScan.match();
					int loc = Integer.parseInt(result.group(1));
					int val = Integer.parseInt(result.group(2));
					addInit(loc,val);
					objScan.nextLine();
				}
			}
			Trace.message("Program read from " + objName);
		} catch (FileNotFoundException e) {
			throw new PippinException("Unable to load program " + name + ". File not found.");
		}
	}
	
	public void writePexe() {	
		String objFileName = Pippin.PEXEPATH + name + ".pexe";
		File objFile = new File(objFileName);
		try {
			objFile.createNewFile(); // Will create the file if it's not there
		} catch (IOException e1) {
			System.out.println("Unable to create output file: " + objFileName + " : " + e1.getMessage());
			return;
		} 
		try(PrintStream objStream = new PrintStream(objFile)) {
			for(Instruction inst : program) {
				objStream.println(inst.toInt());
			}
			if (!initializations.isEmpty()) {
				objStream.println("---init---");
				for(int loc : initializations.keySet()) {
					objStream.println(loc + "=" + initializations.get(loc));
				}
			}
			Trace.message("Program Object Code written to " + objFileName);
		} catch (FileNotFoundException e) {
			System.out.println("Should never get here.");
			e.printStackTrace();
		} 
	}

	public boolean add(Instruction e) { return program.add(e); }
	
	public void setArgAtLine(int ln, int arg) {
		program.get(ln).setArgument(arg);
	}

	public void clear() { program.clear(); }

	public int size() { return program.size(); }
	
	public void load(CPU cpu) throws PippinMemoryException {
		load(cpu,0); // Default is to load the program at location 0
	}
	
	public void addInit(int loc,int value) {
		initializations.put(loc, value);
	}
	
	public void loadData(Memory mem,int dataMemoryBase) throws PippinMemoryException {
		for(int loc : initializations.keySet()) {
			mem.set(dataMemoryBase+loc,initializations.get(loc));
		}
	}
	
	public void load(CPU cpu, int codeStart) throws PippinMemoryException {
		int instructionCounter=codeStart;
		cpu.setInstructionPointer(instructionCounter);
		Memory mem = cpu.getMemory();
		for(Instruction inst : program) {
			inst.store(mem,instructionCounter);
			instructionCounter ++;
		}
		cpu.setDataMemoryBase(instructionCounter);
		loadData(mem,instructionCounter);
	}

	public String getName() { return name; }
	
	public void setName(String pgmName) { name=pgmName; }	

}
