package proj01.model;

import java.util.ArrayList;
import java.util.List;

public class Pippin {

	public static final int MEMORY_SIZE = 4096;
	private Memory memory;
	private CPU cpu;
	private List<Job> jobs;
	
	public static final String PASMPATH = "src/" + Pippin.class.getPackageName().replace(".model", "/pasm") + "/";
	public static final String PEXEPATH = "src/" + Pippin.class.getPackageName().replace(".model", "/pexe") + "/";

	// Move job list into this level... makes more sense

	public Pippin() {
		memory = new Memory(MEMORY_SIZE);
		cpu = new CPU(memory);
		jobs = new ArrayList<Job>(); // Start off with an empty job list
	}

	public void addJob(Job newJob) throws PippinMemoryException {
		// Find the max space used by other jobs in memory
		int minStart = 0;
		for (Job j : jobs) {
			int tend = j.getJobEndMemoryLoc();
			if (tend >= minStart) minStart = tend + 1;
		}
		// Does this job fit in memory?
		if (minStart + newJob.getJobSize() > memory.size()) {
			throw new PippinMemoryException(
					"Not enough memory to add another job");
		}
		// Load the job in memory
		newJob.load(minStart);
		jobs.add(newJob);
	}
	
	public void removeJob(Job remJob) throws PippinMemoryException {
		remJob.unload();
		jobs.remove(remJob);
	}
	
	

	/**
	 * @return the jobs
	 */
	public List<Job> getJobs() { return jobs; }

	public void runJobs(int timeSlice) throws PippinMemoryException {
		boolean jobsRunning = true;
		while (jobsRunning) {
			jobsRunning = false; // This will get reset to true if any of the jobs
										// are still running
			for (Job j : jobs) {
				j.swapIn();
				try {
					cpu.run(timeSlice);
				} catch (Exception e) {
					//TODO cpu.run never returns a PippinException, but I want to
					//     catch ONLY Pippin exceptions. Why?
					cpu.setHalted(true);
					Trace.message("Job halted because it threw exception: "
							+ e.getMessage());
				}
				j.swapOut();
				if (!cpu.isHalted()) jobsRunning = true;
			}
		}
	}

	/**
	 * @return the cpu
	 */
	public CPU getCpu() { return cpu; }	
	

	/**
	 * @return the memory
	 */
	public Memory getMemory() { return memory; }

	/**
	 * @param title
	 * @see proj01.model.Memory#dump(java.lang.String)
	 */
	public void dumpMemory(String title) { memory.dump(title); }

	public static void main(String[] args) {

		Pippin model = new Pippin();
		int nextArg = 0;
		if (args.length > nextArg && args[nextArg].equals("-trace")) {
			Trace.startTrace();
			nextArg++;
		}

		if (nextArg >= args.length) {
			System.out.println(
					"Please invoke as [-trace] pgm1 [ pgm2 [ pgm3 ... ]]");
			return;
		}
		
		Job[] jobs = new Job[args.length-nextArg];
		
		for(int i=0;i<jobs.length; i++) {
			try {
				Program prog = new Program(args[nextArg+i]);
				jobs[i] = new Job(args[nextArg+i],model,prog,10);
				model.addJob(jobs[i]);
			} catch (PippinException e) {
				System.out.println("Got a Pippin Exception trying to add a job");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}

		// if (Trace.getTrace()) model.dumpMemory("Memory Before execution");

		try {
			model.runJobs(10);
		} catch (PippinMemoryException e) {
			System.out.println("Got a Pippin Memory Exception trying to run all jobs");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		for(Job job : jobs) {
			System.out.println("Result of job: " + job.getName() + " = " + job.getAccumulator());
		}
		
		// if (Trace.getTrace()) model.dumpMemory("Memory After execution");
	}

}
