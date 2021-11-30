package proj01.model;

public class Memory {
	
	/**
		Under the covers, we will simulate a RAM memory using a Java
		array. I have declared a field called "data" which
		is an array of integers.
	**/
	
	int data[];
	
	public Memory(int length) {
		data = new int[length];
	}
	
	public void set(int loc,int val) throws PippinMemoryException {
		if (loc<0 || loc>data.length) 
			throw new PippinMemoryException("Error... Invalid memory address: " + loc + " during set");
		else data[loc]=val;
	}
	
	public int get(int loc) throws PippinMemoryException {
		if (loc<0 || loc>data.length) { 
			throw new PippinMemoryException("Error... Invalid memory address: " + loc + " during get");
		}
		else return data[loc];
	}
	
	public void dump(String title) {
		dump(title,0,data.length-1);
	}

	public void dump(String title,int start,int stop) {
		boolean in0=false;
		int start0=start,stop0=start;
		System.out.println(title);
		for(int i=start;i<=stop;i++) {
			if (data[i]==0) {
				if (in0) { stop0=i; }
				else {
					in0=true;
					start0=stop0=i;
				}
			} else {
				if (in0) {
					if (start0==stop0) {
						System.out.println(String.format("   %08d           ",start0) + " : 0x00000000 =          0 = [NOP]");
					} else {
						System.out.println(String.format("   %08d",start0) + " - " + String.format("%08d",stop0) + " : 0x00000000 =        0 = [NOP]");
					}
					in0=false;
				}
				try {
					System.out.println(String.format("   %08d",i) + "            : " +String.format("0x%08x = %10d = %s",data[i],data[i],Instruction.factory(data[i]).toString()));
				} catch (PippinInstructionException e) {
					System.out.println(String.format("   %08d",i) + "            : " +String.format("0x%08x = %10d = INVALID INSTRUCTION",data[i],data[i]));
				}
			}
		}
		if (in0) {
			if (start0==stop0) {
				System.out.println(String.format("   %08d",start0) + "           : 0x00000000 =          0 = [NOP]");
			} else {
				System.out.println(String.format("   %08d",start0) + " - " + String.format("%08d",stop0) + " : 0x00000000 =          0 = [NOP]");
			}
		}
	}

	public int size() { return data.length; }


}
