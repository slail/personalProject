package proj01.model;

public class Trace {
	private static boolean trace=false;
	
	static public void startTrace() { trace=true; }
	static public void stopTrace() { trace=false; }
	
	public static void message(String msg) {
		if (trace) System.out.println("Trace: " + msg);
	}
	
	public static void instMsg(Instruction inst,String desc) {
		message(inst.toString() + " :: " + desc);
	}
	
	public static boolean getTrace() { return trace; }

}
