package proj01.model;

import java.util.SortedMap;

public class Assembler {
	
	public boolean assemble(String pgmName,SortedMap<Integer,String> errors) {
		//TODO: Read the .pasm file and produce a .pexe file for the requested program
		return false;
		
		// The following error messages should be used if the error is present:
		// E: File <pasmFileName> not found
		// E: Illegal Second data delimiter - the remainder of the file is ignored
		// W: Extra tokens on data delimiter line: <line>
		// E: Illegal opcode: <token>
		// W: Extra tokens for instruction ignored: <badTokens>
		// E: Missing argument specification
		// E: Invalid argument specification: <token>
		// E: invalid data line: <line>
	}

}
