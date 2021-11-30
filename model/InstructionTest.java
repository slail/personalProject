package proj01.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstructionTest {
	private CPU cpu;
	private Memory mem;
	private int[] memCpy;
	private Random rand;
	private Program prog;

	@BeforeEach
	void setUp() throws Exception {
		mem = new Memory(100);
		rand = new Random();
		cpu = new CPU(mem);
		memCpy = new int[100];
		for (int i=0;i<100;i++) {
			int r = rand.nextInt();
			mem.set(i, r);
			memCpy[i]=r;
		}
		prog = new Program("UnitTest");
	}
	
	void checkMem() {
		// mem[0] contains instruction
		for(int i=1;i<100;i++) {
			try {
				assertEquals(memCpy[i],mem.get(i),"Memory cell " + i + " incorrect.");
			} catch (PippinMemoryException e) {
				fail("mem.get error in checkMem " + e.getMessage());
			}
		}
	}
	
	void loadUTprogram(String ... instList) { 
		try {
			for(String inst : instList) {
				prog.add(Instruction.factory(inst));
			}
			prog.load(cpu);
		} catch (PippinException e) {
			fail("Error creating unit test program: "  + e.getMessage());
		}
	}
	
	void checkInvalidInstruction(String instString) {
		Instruction inst;
		try {
			inst = Instruction.factory(instString);
			inst.isValid();
			fail("Expect instruction " + instString + " to be Invalid.");
		} catch (PippinInstructionException e) {
			// If an exception was thrown, this instruction is invalid
		}
	}
	
	void utSetData(int loc,int val) {
		// WARNING: Assumes a single instruction!
		try {
			cpu.setData(loc,val);
		} catch (PippinMemoryException e) {
			fail("cpu.setData failed " + e.getMessage());
		} 
		memCpy[loc+1]=val;
	}
	
	void setIndirect(int loc,int loc2,int val) {
		try {
			cpu.setData(loc,loc2);
			cpu.setData(loc2,val);
		} catch (PippinMemoryException e) {
			fail("Indirect cpu.setData failed " + e.getMessage());
		}
		memCpy[loc+1]=loc2;
		memCpy[loc2+1]=val;
	}

	@Test
	void testADDIMM() { 
		loadUTprogram("ADD IMM 12");
		cpu.setAccumulator(11);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(23,cpu.getAccumulator(),"Add imm 11+12=23");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test
	void testADDIND() { 
		loadUTprogram("ADD IND 12");
		cpu.setAccumulator(11);
		setIndirect(12,14,20);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(31,cpu.getAccumulator(),"Add ind @12");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test
	void testADDDIR() { 
		loadUTprogram("ADD DIR 12");
		cpu.setAccumulator(11);
		int sum=0;
		try {
			sum = cpu.getData(12)+11;
		} catch (PippinMemoryException e) {
			fail("cpu.getData(12) failed: " + e.getMessage());
		}
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(sum,cpu.getAccumulator(),"Add dir 11+@12");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test
	void testADDNOM() {
		checkInvalidInstruction("ADD NOM 12");
	}
	
	@Test
	void testSUBIMM() { 
		loadUTprogram("SUB IMM 12");
		cpu.setAccumulator(26);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(14,cpu.getAccumulator(),"SUB imm 26-12=14");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test
	void testSUBDIR() { 
		loadUTprogram("SUB DIR 12");
		cpu.setAccumulator(11);
		int sum=0;
		try {
			sum = 11-cpu.getData(12);
		} catch (PippinMemoryException e) {
			fail("cpu.getData(12) failed: " + e.getMessage());
		}
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(sum,cpu.getAccumulator(),"SUB dir 11-@12");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test
	void testSUBIND() { 
		loadUTprogram("SUB IND 12");
		cpu.setAccumulator(11);
		setIndirect(12,14,20);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(-9,cpu.getAccumulator(),"SUB IND @12");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test void testSUBNOM() { checkInvalidInstruction("SUB NOM 12"); }
	
	@Test
	void testMULIMM() { 
		loadUTprogram("MUL IMM 3");
		cpu.setAccumulator(11);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(33,cpu.getAccumulator(),"MUL imm 3*11=33");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test
	void testMULDIR() { 
		loadUTprogram("MUL DIR 12");
		cpu.setAccumulator(2);
		int sum=0;
		try {
			sum = cpu.getData(12)*2;
		} catch (PippinMemoryException e) {
			fail("cpu.getData(12) failed " + e.getMessage());
		}
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(sum,cpu.getAccumulator(),"MUL dir 2*@12");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test
	void testMULIND() { 
		loadUTprogram("MUL IND 12");
		cpu.setAccumulator(-3);
		setIndirect(12,14,6);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(-18,cpu.getAccumulator(),"MUL IND @12");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test void testMULNOM() { checkInvalidInstruction("MUL NOM 12");}
	
	@Test
	void testDIVIMM() { 
		loadUTprogram("DIV IMM 3");
		cpu.setAccumulator(11);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(3,cpu.getAccumulator(),"DIV imm 11/3=3");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test
	void testDIVDIR() { 
		loadUTprogram("DIV DIR 12");
		try {
			cpu.setAccumulator(cpu.getData(12)*5);
		} catch (PippinMemoryException e) {
			fail("cpu.getData failed " + e.getMessage());
		}
		int sum=0;
		try {
			sum = cpu.getAccumulator()/cpu.getData(12);
		} catch (PippinMemoryException e) {
			fail("cpu.getData failed " + e.getMessage());
		}
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(sum,cpu.getAccumulator(),"DIV dir @12/4");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test
	void testDIVIND() { 
		loadUTprogram("DIV IND 12");
		cpu.setAccumulator(100);
		setIndirect(12,14,3);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(33,cpu.getAccumulator(),"Add ind @12");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test void testDIVNOM() { checkInvalidInstruction("DIV NOM 12"); }
	

	@Test void testNOPIMM() { checkInvalidInstruction("NOP IMM 12"); }
	
	@Test 
	void testNOPNOM() {
		loadUTprogram("NOP NOM 12");
		cpu.setAccumulator(235);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(235,cpu.getAccumulator(),"NOP NOM");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test
	void testLODIMM() {
		loadUTprogram("LOD IMM 12");
		cpu.setAccumulator(235);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(12,cpu.getAccumulator(),"LOD IMM");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test
	void testLODDIR() {
		loadUTprogram("LOD DIR 12");
		cpu.setAccumulator(235);
		cpu.setHalted(false);
		cpu.execute();
		try {
			assertEquals(cpu.getData(12),cpu.getAccumulator(),"LOD DIR");
		} catch (PippinMemoryException e) {
			fail("cpu.getData failed " + e.getMessage());
		}
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test
	void testLODIND() { 
		loadUTprogram("LOD IND 12");
		cpu.setAccumulator(11);
		setIndirect(12,14,20);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(20,cpu.getAccumulator(),"LOD IND @12");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test void testLODNOM() { checkInvalidInstruction("LOD NOM 12"); }
	
	@Test void testSTOIMM() { checkInvalidInstruction("STO IMM 12"); }
	
	@Test
	void testSTODIR() {
		loadUTprogram("STO DIR 12");
		cpu.setAccumulator(235);
		cpu.setHalted(false);
		cpu.execute();
		memCpy[13]=235;
		try {
			assertEquals(235,cpu.getData(12),"STO DIR");
		} catch (PippinMemoryException e) {
			fail("cpu.getData failed " + e.getMessage());
		}
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test
	void testSTOIND() { 
		loadUTprogram("STO IND 12");
		cpu.setAccumulator(99);
		utSetData(12,7);
		cpu.setHalted(false);
		cpu.execute();
		memCpy[8]=99; 
		try {
			assertEquals(99,cpu.getData(7),"STO IND");
		} catch (PippinMemoryException e) {
			fail("cpu.getData failed " + e.getMessage());
		}
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	@Test void testSTONOM() { checkInvalidInstruction("STO NOM 12"); }

	@Test void testHLTIMM() { checkInvalidInstruction("HLT IMM 12"); }
	
	@Test
	void testHLTNOM() {
		loadUTprogram("HLT NOM 12","ADD IMM 300");
		try {
			memCpy[1]=mem.get(1);
		} catch (PippinMemoryException e) {
			fail("mem.get failed " + e.getMessage());
		}
		cpu.setAccumulator(235);
		cpu.setHalted(false);
		cpu.execute();
		cpu.execute();
		assertEquals(235,cpu.getAccumulator(),"HLT NOM");
		assertTrue(cpu.isHalted()); // HLT instruction should halt
		checkMem();
	}
	
	// Test AND using immediate mode that results in true
	@Test
	void testANDIMM_T() { 
		loadUTprogram("AND IMM 12");
		cpu.setAccumulator(11);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(1,cpu.getAccumulator(),"AND imm 11 & 12= True");
		checkMem();
	}
	
	// Test AND using immediate mode when accumulator is false
	@Test
	void testANDIMM_F1() { 
		loadUTprogram("AND IMM 0");
		cpu.setAccumulator(11);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(0,cpu.getAccumulator(),"AND imm 0 & 12= False");
		checkMem();
	}
	
	// Test AND using immediate mode when immediate value is false
	@Test
	void testANDIMM_F2() { 
		loadUTprogram("AND IMM 12");
		cpu.setAccumulator(0);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(0,cpu.getAccumulator(),"AND imm 0 & 12= True");
		checkMem();
	}
	
	// Test AND using direct mode that results in true
	@Test
	void testANDDIR() { 
		loadUTprogram("AND DIR 12");
		cpu.setAccumulator(11);
		utSetData(12,31); // Make sure there is a non-zero value @12
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(1,cpu.getAccumulator(),"AND DIR 11 & @12= True");
		checkMem();
	}
	
	@Test
	void testANDIND() { 
		loadUTprogram("AND IND 5");
		cpu.setAccumulator(100);
		setIndirect(5,10,0);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(0,cpu.getAccumulator(),"AND IND @@5");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	// Test AND using NOM mode to make sure it is invalid
	@Test void testANDNOM() { checkInvalidInstruction("AND NOM 12"); }

	// Test NOT using IMM mode to make sure it is invalid
	@Test void testNOTIMM() { checkInvalidInstruction("NOT IMM 12"); }
	
	// Test NOT using DIR mode to make sure it is invalid
	@Test void testNOTDIR() { checkInvalidInstruction("NOT DIR 12"); }
	
	// Test NOT using DIR mode to make sure it is invalid
	@Test void testNOTIND() { checkInvalidInstruction("NOT IND 12"); }
	
	// Test NOT using NOM mode that results in true
	@Test void testNOTNOM_TRU() { 
		loadUTprogram("NOT NOM 3");
		cpu.setAccumulator(0);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(1,cpu.getAccumulator(),"NOT 0=1");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	// Test NOT using NOM mode that results in false
	@Test
	void testNOTNOM_FLS() { 
		loadUTprogram("NOT NOM 3");
		cpu.setAccumulator(76);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(0,cpu.getAccumulator(),"NOT 76=0");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	// Test CML using IMM mode to make sure it is invalid
	@Test void testCMLIMM() { checkInvalidInstruction("CML IMM 12"); }
	
	// Test CML using DIR mode that results in true
	@Test
	void testCMLDIR_T() {
		loadUTprogram("CML DIR 12"); // Create a CML instruction with DIR mode and argument=12
		// add that instruction as the only instruction in the prog Program
		// and loads it into the CPU
		cpu.setAccumulator(11); // Initialize the accumulator to 11 (just so you know what it was)
		utSetData(12,-31); // Make sure there is a negative value @12
		cpu.setHalted(false); // Set the halted flag in the CPU to false
		cpu.execute(); // Execute the CML instruction
		assertFalse(cpu.isHalted(),"CML instruction didnt halt");
		assertEquals(1,cpu.getAccumulator(),"CML DIR @12= True"); // Check to make sure the accumulator has been updated to 1
		// It should be 1 because the operand is the value at data location 12, which is -31
		// and -31 is less than zero.
		checkMem(); // Check to make sure the CML instruction did NOT change any memory values
	}
	
	// Test CML using DIR mode that results in false
	@Test
	void testCMLDIR_F() { 
		loadUTprogram("CML DIR 12"); // Same as above
		cpu.setAccumulator(11); // Same as above
		utSetData(12,0); // Make sure there is a non-negative value @12
		cpu.setHalted(false);
		cpu.execute();
		assertFalse(cpu.isHalted(),"CML worked for false");
		assertEquals(0,cpu.getAccumulator(),"CML DIR @12= False");
		// Since the operand is the value at location 12, or 0, and 0 is not less than zero,
		// the accumulator should be set to "false" or 0.
		checkMem();
	}
	
	@Test
	void testCMLIND() { 
		loadUTprogram("CML IND 5");
		cpu.setAccumulator(100);
		setIndirect(5,10,0);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(0,cpu.getAccumulator(),"CML IND @@5");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	// Test CML using NOM mode to make sure it is invalid
	@Test void testCMLNOM() { checkInvalidInstruction("CML NOM 12"); }
	
	// Test CMZ using IMM mode to make sure it is invalid
	@Test void testCMZIMM() { checkInvalidInstruction("CMZ IMM 12"); }
	
	// Test CMZ using DIR mode that results in true
	@Test
	void testCMZDIR_T() { 
		loadUTprogram("CMZ DIR 12");
		cpu.setAccumulator(11);
		utSetData(12,0); // Make sure there is a zero value @12
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(1,cpu.getAccumulator(),"CMZ DIR @12= True");
		checkMem();
	}
	
	// Test CMZ using DIR mode that results in false
	@Test
	void testCMZDIR_F() { 
		loadUTprogram("CMZ DIR 12");
		cpu.setAccumulator(11);
		utSetData(12,1342); // Make sure there is a non-zero value @12
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(0,cpu.getAccumulator(),"CMZ DIR @12= True");
		checkMem();
	}
	
	@Test
	void testCMZIND() { 
		loadUTprogram("CMZ IND 5");
		cpu.setAccumulator(100);
		setIndirect(5,10,0);
		cpu.setHalted(false);
		cpu.execute();
		assertEquals(1,cpu.getAccumulator(),"CMZ IND @@5");
		assertFalse(cpu.isHalted()); // If an error occurred, CPU will be halted
		checkMem();
	}
	
	// Test CMZ using NOM mode to make sure it is invalid
	@Test void testCMZNOM() { checkInvalidInstruction("CMZ NOM 12"); 	}	
	
	// Test JMP using IMM
	@Test
	void testJMPIMM() { 
		loadUTprogram("JMP IMM 10");
		cpu.setHalted(false);
		int seqIp=cpu.getInstructionPointer();
		cpu.execute();
		assertEquals(seqIp+10,cpu.getInstructionPointer(),"JMP IMM 10");
		checkMem();
	}
	

	// Test JMP using DIR mode to make sure it is invalid
	@Test void testJMPDIR() { checkInvalidInstruction("JMP DIR 12"); }
	
	// Test JMP using IND mode to make sure it is invalid
	@Test void testJMPIND() { checkInvalidInstruction("JMP IND 12"); }
	
	// Test JMP using NOM mode to make sure it is invalid
	@Test void testJMPNOM() { checkInvalidInstruction("JMP NOM 12"); }	
	
	// Test JMZ using IMM when branch is taken
	@Test
	void testJMZIMM_BR() { 
		loadUTprogram("JMZ IMM 10");
		cpu.setAccumulator(0);
		cpu.setHalted(false);
		int seqIp=cpu.getInstructionPointer();
		cpu.execute();
		assertEquals(seqIp+10,cpu.getInstructionPointer(),"JMZ IMM 10");
		checkMem();
	}
	
	// Test JMZ using IMM when branch is not taken
	@Test
	void testJMZIMM_NOBR() { 
		loadUTprogram("JMZ IMM 10");
		cpu.setAccumulator(-421);
		cpu.setHalted(false);
		int seqIp=cpu.getInstructionPointer()+1;
		cpu.execute();
		assertEquals(seqIp,cpu.getInstructionPointer(),"JMZ IMM 10");
		checkMem();
	}
	

	// Test JMZ using DIR mode to make sure it is invalid
	@Test void testJMZDIR() { checkInvalidInstruction("JMZ DIR 12"); }	
	
	// Test JMZ using IND mode to make sure it is invalid
	@Test void testJMZIND() { checkInvalidInstruction("JMZ IND 12"); }
	
	// Test JMZ using NOM mode to make sure it is invalid
	@Test void testJMZNOM() { checkInvalidInstruction("JMZ NOM 12"); }
	
}
