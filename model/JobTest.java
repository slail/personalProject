package proj01.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JobTest {
	static Program gcdPgm;
	static Pippin model;
	Job job;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		gcdPgm = new Program("gcd");
		model = new Pippin();
	}

	@BeforeEach
	void setUp() throws Exception {
		job = new Job("test",model,gcdPgm,5);
	}

	@Test
	void testJob() { 
		assertNotNull(job,"job created OK");
		assertEquals(gcdPgm.size(),job.getProgramSize(),"Created job has the right size");
	}

	@Test
	void testSwapOutSwapIn() { 
		Job job1 = new Job("test1",model,gcdPgm,5);
		Job job2 = new Job("test2",model,gcdPgm,5);
		try {
			job1.swapIn();
			fail("swapIn should throw an exception before load");
		} catch(PippinMemoryException e) { }
		try {
			job1.swapOut();
			fail("swapOut should throw an exception before load");
		} catch(PippinMemoryException e) { }
		try {
			job1.load(0);
			job2.load(100);
			job1.swapIn();
			CPU cpu = model.getCpu();
			cpu.execute();
			int saveIP=cpu.getInstructionPointer();
			int saveDMB=cpu.getDataMemoryBase();
			job1.swapOut();
			job2.swapIn();
			assertFalse(saveIP==cpu.getInstructionPointer(),"Ensure swap in changes IP");
			assertFalse(saveDMB==cpu.getDataMemoryBase(),"Ensure swap in changed data memory base");
			cpu.execute();
			job2.swapOut();
			job1.swapIn();
			assertEquals(saveIP,cpu.getInstructionPointer(),"Make sure IP is saved by swapOut");
			assertEquals(saveDMB,cpu.getDataMemoryBase(),"Make sure data memory base is restored");
		} catch (PippinMemoryException e) {
			fail("Pippin memory exception: " + e.getMessage());
		}
	}
	
	@Test
	void testLoad() { 
		CPU cpu = model.getCpu();
		assertTrue(cpu.isHalted(),"Ensure cpu starts out halted.");
		try {
			job.load(20);
			job.swapIn();
			assertFalse(cpu.isHalted(),"Ensure cpu is no longer halted.");
			assertEquals(20,cpu.getInstructionPointer(),"Job is loaded starting at location 20");
			Instruction tryInst = Instruction.factory(cpu.getMemory().get(25)); 
			assertEquals(tryInst.getOpcode(),Operation.STO,"Correct opcode at mem location 25");
			assertEquals(tryInst.getMode(),Mode.DIR,"Correct mode at mem location 25");
			assertEquals(tryInst.getArgument(),2,"Correct argument at location 25");
			assertEquals(20+gcdPgm.size(),cpu.getDataMemoryBase(),"load/swapin updated data memory base");
		} catch (PippinException e1) {
			fail("Pippin exception: " + e1.getMessage());
		}
		try {
			job.load(40);
			fail("cannot load the same job twice.");
		} catch(PippinMemoryException e) {}
	}

	@Test
	void testInitData() { 
		try {
			job.initData(3, -462);
			fail("initData should throw an exception before load");
		} catch(PippinMemoryException e) { }
		try {
			job.load(20);
			job.swapIn();
			CPU cpu = model.getCpu();
			int mb=cpu.getDataMemoryBase();
			job.swapOut();
			job.initData(3, -462);
			assertEquals(-462,cpu.getMemory().get(mb+3));
		} catch (PippinMemoryException e) {
			fail("Pippin memory exception: " + e.getMessage());
		}
	}

	@Test
	void testGetData() { 
		try {
			job.getData(3);
			fail("getData should throw an exception before load");
		} catch(PippinMemoryException e) { }
		try {
			job.load(20);
			job.swapIn();
			CPU cpu = model.getCpu();
			int mb=cpu.getDataMemoryBase();
			job.swapOut();
			cpu.getMemory().set(mb+3, 7742);
			assertEquals(7742,job.getData(3));
		} catch (PippinMemoryException e) {
			fail("Pippin memory exception: " + e.getMessage());
		}
	}

	@Test
	void testGetJobEndMemoryLoc() {
		try {
			job.getJobEndMemoryLoc();
			fail("getJobEndMemoryLoc should throw an exception before load");
		} catch(PippinMemoryException e) { }
		try {
			job.load(20);
			assertEquals(20+gcdPgm.size()+5,job.getJobEndMemoryLoc(),"Correct job end memory loc");
		} catch (PippinMemoryException e) {
			fail("Pippin memory exception: " + e.getMessage());
		}
	}

	@Test
	void testGetJobSize() { 
		assertEquals(gcdPgm.size()+5,job.getJobSize(),"Correct job size");
	}

	@Test
	void testGetDataSize() { 
		assertEquals(5,job.getDataSize(),"Correct job data size");
	}

	@Test
	void testGetCodeStart() { 
		try {
			job.getCodeStart();
			fail("getCodeStart should throw an exception before load");
		} catch(PippinMemoryException e) {
			
		}
		try {
			job.load(134);
			assertEquals(134,job.getCodeStart(),"Code starts where specified");
		} catch (PippinMemoryException e) {
			fail("Pippin memory exception: " + e.getMessage());
		}
	}

	@Test
	void testGetProgramSize() { 
		assertEquals(gcdPgm.size(),job.getProgramSize(),"Program size is correct");
	}

}
