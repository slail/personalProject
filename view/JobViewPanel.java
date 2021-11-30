package proj01.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import proj01.model.Assembler;
import proj01.model.CPU;
import proj01.model.Job;
import proj01.model.Memory;
import proj01.model.Pippin;
import proj01.model.PippinException;
import proj01.model.PippinMemoryException;
import proj01.model.Program;


public class JobViewPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private PippinGUI gui;
	private Pippin model;
	private CPU cpu;
	private Memory mem;
	private Job job;
	
	private ProcessorViewPanel cpuView;
	private JFrame frame;
	private CodeViewPanel codeViewPanel;
	private DataViewPanel dataViewPanel;
	JButton reloadButton;
	JButton clearButton;
	JTextField jobName;
	Map<Integer,Integer> breakPoints;

	public JobViewPanel(PippinGUI gui, Job job) {
		super();
		this.gui = gui;
		this.model = gui.getModel();
		this.job = job;
		this.cpu = model.getCpu();
		this.mem = model.getMemory();
		this.cpuView = gui.getProcessorPanel();
		this.breakPoints = new HashMap<Integer,Integer>();

		reloadButton = new JButton("Reload");
		reloadButton.setBackground(Color.WHITE);
		reloadButton.addActionListener(e -> reload());

		clearButton = new JButton("Remove");
		clearButton.setBackground(Color.WHITE);
		clearButton.addActionListener(e -> gui.removeJob());

		JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
		
		buttonPanel.add(reloadButton);
		buttonPanel.add(clearButton);
		
		JPanel jobNamePanel = new JPanel();
		jobNamePanel.add(new JLabel("Loaded Code "));
		jobName = new JTextField(20);
		jobName.setEditable(false);
		jobNamePanel.add(jobName);
		jobName.setText(job.getName());
		
		JPanel jobControl = new JPanel(new GridLayout(0,1));
		jobControl.add(buttonPanel);
		jobControl.add(jobNamePanel);

		codeViewPanel = new CodeViewPanel(model, job,this);
		dataViewPanel = new DataViewPanel(model, job);
		setLayout(new BorderLayout(1, 1));
		add(jobControl, BorderLayout.NORTH);
		add(codeViewPanel, BorderLayout.WEST);
		add(dataViewPanel, BorderLayout.CENTER);

	
		if(job.isLoaded()) { 
			codeViewPanel.loadCode(job);
			dataViewPanel.load(job);
			jobName.setText(job.getProgramName());
		}
	}

	public void loadJob() throws PippinException {
		File ldir = new File(Pippin.PEXEPATH);
		List<String> programs = new ArrayList<String>();
		for(String file : ldir.list()) {
			if (file.endsWith(".pexe")) programs.add(file.replace(".pexe", ""));
		}
		String[] progList = programs.toArray(new String[0]);
		String program = (String) JOptionPane.showInputDialog(null, "Choose a program...",
		        "Choose a program:", JOptionPane.QUESTION_MESSAGE, null, // Use
		                                                                        // default
		                                                                        // icon
		        progList, // Array of choices
		        progList[0]); // Initial choice
		loadJob(program);
	}
	
	public void loadJob(String pgmName) throws PippinException {
		Program pgm = new Program(pgmName);
		job = new Job(pgmName,model,pgm,10); // Space hard-coded to 10 for now
		model.addJob(job);
		job.swapIn();
		jobName.setText(pgmName);
		codeViewPanel.loadCode(job);
		dataViewPanel.load(job);
		update();
	}
	
	public void assembleAndLoadJob() throws PippinException {
		File ldir = new File(Pippin.PASMPATH);
		List<String> programs = new ArrayList<String>();
		for(String file : ldir.list()) {
			if (file.endsWith(".pasm")) programs.add(file.replace(".pasm", ""));
		}
		String[] progList = programs.toArray(new String[0]);
		String pgm = (String) JOptionPane.showInputDialog(null, "Choose a program...",
		        "Choose a program to assemble:", JOptionPane.QUESTION_MESSAGE, null, 
		        progList, // Array of choices
		        progList[0]); // Initial choice
		Assembler asm = new Assembler();
		
		if (pgm != null) {
			SortedMap<Integer,String> errors = new TreeMap<>();
			boolean assembled = asm.assemble(pgm, errors);
			if (!errors.isEmpty()) {
				StringBuilder sb = new StringBuilder(pgm + " has one or more errors\n");
				for(Integer key : errors.keySet()) {
					sb.append(errors.get(key)); sb.append("\n");
				}
				JOptionPane.showMessageDialog(
					null, 
					sb.toString(),
					"Source code error(s)",
					JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(
						null, // null, 
						"Program " + pgm + " was assembled to an executable with no errors",
						"Success",
						JOptionPane.INFORMATION_MESSAGE);
			}
			if (assembled) loadJob(pgm);
		}
		update();
		
	}

	public void reload() {
		try {
			job.reload();
			job.swapIn();
		} catch (PippinMemoryException e) {
			JOptionPane.showMessageDialog(
					null, 
					"Error reloading program: " + e.getMessage(),
					"Program Load Error",
					JOptionPane.INFORMATION_MESSAGE);
		}
		gui.update();
	}
	
	public void enableReloadClear(boolean en) {
		reloadButton.setEnabled(en);
		clearButton.setEnabled(en);
	}

	public void update() {
		codeViewPanel.update();
		dataViewPanel.update();
		cpuView.update();
		if (job.isLoaded() && !cpu.isHalted()) {
			reloadButton.setEnabled(true);
			clearButton.setEnabled(true);
		} else if (job.isLoaded()) {
			reloadButton.setEnabled(true);
			clearButton.setEnabled(true);
		} else {
			reloadButton.setEnabled(false);
			clearButton.setEnabled(false);
		}
		
	}


	
	public boolean toggleBreakpoint(int ip) {
		// Toggle break point
		if (breakPoints.containsKey(ip)) {
			try {
				mem.set(ip, breakPoints.get(ip));
				breakPoints.remove(ip);
				codeViewPanel.clearBreakPoint(ip);
			} catch (PippinMemoryException e) {
				JOptionPane.showMessageDialog(
						null, 
						"Unable to turn off breakpoint at " + ip + " : " + e.getMessage(),
						"Toggle Breakpoint Problem",
						JOptionPane.ERROR_MESSAGE);
			}
			return false;	
		}
		try {
			breakPoints.put(ip,mem.get(ip));
			mem.set(ip, -1); // Put an invalid instruction at that memory
			codeViewPanel.setBreakPoint(ip);
		} catch (PippinMemoryException e) {
			JOptionPane.showMessageDialog(
					null, 
					"Unable to turn on breakpoint at " + ip + " : " + e.getMessage(),
					"Toggle Breakpoint Problem",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;			
	}
	
	public boolean atBreakpoint() {
		int ip=cpu.getInstructionPointer();
		if (!breakPoints.containsKey(ip)) return false;
		JOptionPane.showMessageDialog(frame, "Breakpoint at " + ip + " reached", ""
				+ "Run time error", JOptionPane.OK_OPTION);
		try {
			mem.set(ip, breakPoints.get(ip));
			breakPoints.remove(ip);
			codeViewPanel.clearBreakPoint(ip);
		} catch (PippinMemoryException e) {
			JOptionPane.showMessageDialog(
					null, 
					"Unable to turn off breakpoint at " + ip + " : " + e.getMessage(),
					"Remove Breakpoint Problem",
					JOptionPane.ERROR_MESSAGE);
		}	
		cpu.setHalted(false);
		return false;
	}
	
	public void resetBreakPoints() {
		for(int ip : breakPoints.keySet()) {
			codeViewPanel.setBreakPoint(ip);
		}
	}

	public Job getJob() { return job; }

	public static void main(String[] args) throws PippinException {
		Pippin model = new Pippin();
		Program gcdPgm = new Program("gcd");
		Job job = new Job("gcd",model,gcdPgm,10);
		model.addJob(job);
		job.swapIn();
		PippinGUI gui = new PippinGUI();
		gui.setModel(model);
		JobViewPanel panel = new JobViewPanel(gui,job);
		JFrame frame = new JFrame("TEST JOBVIEW");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 700);
		frame.setLocationRelativeTo(null);
		frame.add(panel);
		frame.setVisible(true);
		panel.update();	
	}
}
