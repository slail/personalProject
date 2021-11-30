# Project Part 1 - Pippin Assembler

## Introduction

The final project will be a team project that will be divided into two parts, both dealing with Pippin. The first part (this part) consists of converting Pippin man-readable assembler code (.pasm files) into "binary" machine readable Pippin object code (.pexe files).

In order to make this all work, there have been several updates to the Pippin code since we last worked on it on lab 08. The highlights are as follows:

- I have repackaged the Pippin code by making package sub-directories. These are as follows:
   - **model** - This is where all the code we have been working with resides.
   - **view** - This is the GUI wrapper around Pippin so we can easily visualize what is there.
   - **pasm** - This contains some sample Pippin man readable assembly files.
   - **pexe** - This contains some sample Pippin object code files.
   
- I have provided two new classes to the Pippin model package. One is the `Job` class, which is used to keep track of a running job in Pippin. The other is a `Pippin` class, which creates an entire Pippin model.

- I have added a new mode called IND for indirect, that is valid everywhere a direct mode is valid. In indirect mode, we use the argument to retrieve an index from memory, and then use that index to retrieve a second actual value from memory. (This allows us to do things like walk through arrays in Pippin code.)

I have also created a [web page reference manual](https://www.cs.binghamton.edu/~tbartens/HowTo/PippinReference.html) for our Pippin simulation. 


## Repository Sub-directories

The default mechanism in Eclipse for handling sub-directories is not very satisfying. Eclipse will show a "proj01" package with just this README, and then, at the same level, proj01.model, proj01.pasm, proj01.pexe, and proj01.view. Find the three vertical dots icon to he right of the Package Explorer tab, select that element, and then select **Package Presentation** in the popup, and select **Hierarchical** instead of Flat. Now you will see a single "proj01" package with sub-packages of "model", "pasm", "pexe" and "view". This looks much better.

### The model sub-package

The proj01.model sub-package contains all of the Java code that simulates a Pippin machine. This is all the code we've been working on in lab for all of the Pippin labs. I have added a top level class called "Pippin" that brings everything together - the memory and the CPU, and simplifies the creation of a "Pippin" simulation.

### The pasm sub-package

The pasm sub-package contains all of the Pippin "assembly" code files that have a file type of .pasm. I have provided several programs, some of which are completely correct, some which fail to assemble, and some which assemble, but do not run correctly.

### The pexe sub-package

The pexe sub-package contains all of the Pippin "executable" code files that have a file type of .pexe. I have provided a couple of example .pexe files that demonstrate the correct translation of the .pasm file with the same name to the .pexe file.

### The view sub-package

The view sub-package contains all of the code to make a Graphical User Interface that supports the Pippin simulator. To run the GUI, execute the PippinGUI main method. When the GUI comes up, there are more directions on how to run the GUI in the help panel.


## Other Pippin Changes

I have made a few other changes to the Pippin code, as follows:

### The Pippin Class

I have created a new class called "Pippin" which is intended as a high level collector. The Pippin class creates a CPU and a Memory, and ties those together. The Pippin class also contains a list of jobs that are currently loaded. The model class also has a `runJobs` method which has a timeSlice parameter. The timeSlice parameter is the number of instructions to execute on a single job before swapping that job out, and swapping another job in. The `runJobs` method allows each non-halted job in the job list to run timeSlice instructions in a round-robin fashion until all jobs are halted.

The `main` method in the Pippin class accepts a list of program names as a run-time argument. These programs are all loaded from .pexe files, and run using the `runJobs` method.

One other change was to create two public static final fields in the Pippin class, one called `PASMPATH` and the other called `PEXEPATH`. These are initialized to src/proj01/pasm and src/proj01/pexe respectively, assuming you are running from the Eclipse project directory (which is where Eclipse runs from.) Every other reference to either Pippin Assembler (.pasm) files or Pippin Executable (.pexe) files should use these public fields to access .pasm or .pexe files.

### Program class Enhancements

First, I added a map for data initializations to the Program class fields. This map contains the index in the program's data memory, and the value to put in that memory before running the program.

I have also added a `readPexe` method which takes the program name, prefixes it with `PEXEPATH` and adds the `.pexe` suffix on the end, and reads the resulting file to create the instructions and data initializations.

The constructor now takes a single argument - the program name, and invokes the `readPexe` method using that name. A no-argument constructor can still be used to create an empty Program object.

When a job that specifies a program is loaded by the Pippin `addJob` method, the binary instructions are written into memory and the initializations are actually performed. 

### The Indirect Mode

I have added a fourth Instruction mode to Pippin, an "indirect" addressing mode or "IND". When an instruction has an indirect mode, the argument represents a location in memory that contains a value that is *also* a location in memory.  For example, for a LOD instruction that has a mode of IND and an argument of 7, the CPU will first read data location 7 (and that's 7 from the current value of the dataMemorybase register). Suppose data location 7 has a value of 12. Then the LOD instruction will read the contents of data location 12, and put that into the accumulator register. An indirect mode allows Pippin to do things like deal with arrays.

In order to implement the indirect mode, I added a new value to the Mode enumeration, updated the  Operation `isModevalid` methods, updating the Instruction `fetchOperand` method and added several test cases to the `InstructionTest` JUnit tester

### Miscellaneous Modifications

I have added some getters and setters in various places, as required, to make the GUI work.

## Coding an Assembler class

The main goal of part one of the project is to write the code for the `assemble` method in the Assembler class. You should not need changes in any of the other classes in order to implement your assembler, and your Assembler class must be written in Java, but otherwise, the implementation is up to you.

### The assemble Method

I have provided an Assembler class in the proj01.model package that has an `assemble` method with the signature:

```
public boolean assemble(String name,SortedMap<Integer, String> errors)
```

The assemble method should take the `name` parameter, prefix it with a Pippin `PASMPATH`, and add a ".pasm" suffix to come up with the fully qualified assembly file path..

The method should take the `name` parameter, prefix it with a Pippin `PEXEPATH`, and add a ".pexe" suffix to come up with the associated executable file path..

The assemble method then needs to convert the program in the assembly file into a list of Pippin binary instructions and data initialization specifications, and write the result to the executable file. The specifications for both the .pasm and .pexe files are in the [Pippin Reference](https://www.cs.binghamton.edu/~tbartens/HowTo/PippinReference.html) web page.

If any errors or warnings are found, the error or warning should be added to the `errors` map, with the key being the line number of the `name`.pasm file on which the error or warning was found. Only one error or warning is expected per line. You will need to keep track of how many lines you read from the `name`.pasm file so you know the correct line number for the `errors` map. 

Some assemblers or compilers adopt the "SOFE" or "Stop On First Error" policy. Users often find this policy less than helpful, because you need to fix the first error and recompile to find the next error. Our assembler will attempt to find and report *all* of the errors it can find in a single pass through the compiler.

If any error is encountered, the executable file should **not** be written, but the assemble method should return a `false` result. If there are no errors, the executable file should be written, and a `true` result should be returned.

If the `name`.pasm file cannot be found, an error should be added to the `errors` map with a key of `-1`, and a file not found error message. The assemble method should return `false`.

The easiest way to perform the conversion from .pasm to .pexe is to create an empty Program object. As you read through the .pasm file, add instructions and initializations to the Program object, keeping track of whether any errors occurred or not. If no errors occur, use the Program `writePexe` method to write the .pexe file. 

### General Assembler Method Processing

See the Pippin Reference Web Page [Assembly Code](https://www.cs.binghamton.edu/~tbartens/HowTo/PippinReference.html#pasm) section for a detailed description of what is expected and what is allowed in a .pasm file. Here are many hints on how to read through that file and convert it to a Pippin Program object.

 - I chose to read the `name`.pasm file using the Java Library Scanner class. You can use the `hasNextLine()` and `getNextLine()` methods in the Scanner class to read a single line out of the .pasm file at a time.

- If your line from the `name`.pasm file is in variable `line`, comments can be removed by the Java code: `line=line.replaceFirst("#.*", ""); // Remove comments`.
  
- Empty lines or lines that contain only comments should be counted as an assembler file line, but otherwise may be ignored.
  
- Keep a flag to keep track of whether you have encountered a "---data---" delimiter or not. If you have not yet encountered a data line, you are still processing instructions. If you have encountered a delimiter, you are processing data initializations. If the flag is on and you encounter a second delimiter, that's an error and should be reported. Everything after a second data delimiter line can be ignored. 

### Instruction Line Processing
  
- Instruction lines should be split into blank delimited tokens, or a list of "words" with one or more blanks or tabs between each word. The best way to split a line into an array of tokens in Java is: `String token[] = line.split("\\s+");`

- Use the instruction line tokens to determine the Operation, Mode, and argument of an instruction. You can then create a new Instruction using these values, and add the instruction to the program using the Program `add` method. You can also use the Program `size()` method to figure out how many instructions are already in the program.

- In Java, you can use the specification `arg=Integer.parseInt(token);` to determine if `token` is a symbol or a number. If `token` is not a number (and therefore a label), `Integer.parseInt` will throw a `NumberFormatException`. (Of course, you need to remove the leading "@" or trailing ":" from the token when they have special meanings.)

- You can use a map from variable name to memory location to keep track of variables. The first time the variable name is encountered in the assembly file, it won't be in the map, so you will need to assign a new location in memory, and add that variable to the map. Each time the variable is used after that, it will be in the map. That also means you have to keep track of the next available data memory index, which should start at zero, and get incremented every time you run into a new variable.

- The assemble method must also keep track of all labels so that the when a label is used in a jump instruction, you know where to jump to. You can keep a label map that contains the label name, and the location of the next instruction, which will just be the program size plus one. 

- **Warning** labels may be referenced *before* they are defined. For instance, the 8th instruction in the program may be something like "JMP loop3". Since the "loop3" label has not been defined yet, we cannot determine the argument value for this line of code. You can either keep track of all the instructions that need to be fixed, and fix them when you do find the label, or you can make two passes through the .pasm file - one to set the label values and a second pass to actually write the instructions.
  
- You should be able to determine the opcode, mode, and argument value for each instruction by reading the .pasm file. Then, you can add that instruction to the program. If there are missing or incorrect tokens, you should issue an error message. If there are extra tokens, you should issue a warning message and ignore those tokens.
  
### Data Line Syntax

After the "---data---" delimiter, the syntax rules are slightly different.

- As above, comments should be removed, and empty lines should be ignored.

- Any non-empty line must contain three blank delimited tokens. The first of the three tokens may contain either an integer that specifies the location directly, or a data label. If a data label is specified, the location is the value of that data label. The second token must contain an equals sign (**=**). The third token must contain an integer value. 

Once you know the location and value in the data line, you can use the Program class `addInit` method to add that location and value to the program initializations.

### Testing Your Assembler

When you run the assemble command (especially if you run from the GUI using the "Assemble and Load" button) the binary .pexe file you created will overwrite the pexe/*program*.pexe file that is already there. The files in pexe/*program*.pexe that I have provided to you have been created by a correct assembler. You might want to save copies of these .pexe files somewhere so you can check them against the .pexe files you produce to make sure yours are also correct.

You might want to create a `main` method in the Assemble class so you can run your assembler directly, without having to load the GUI. The main method should pick the name of a program (a valid .pasm file) invoke the `assemble` method, and print out any errors that result.

I have provided several .pasm files that you can use to test your Assembler. Make sure you can produce a valid .pexe file that can then be run in the GUI and produce correct results; or you can detect any Pippin Assembly syntax errors and report on them correctly. You might want to copy and edit the .pasm files to create new tests to cover cases I haven't covered. You might even want to create a JUnit test to test your Assembler.
  
## Submitting your Project

Only one member of the team needs to submit. The grade will be shared by all team members.
  
Get your hash code with: 

`git rev-parse HEAD`
  
or by using the Eclipse Git perspective. Copy the paste the hash code to Brightspace under proj01.

## Project Part 1 Extensions

I realize this is a difficult time of the semester, which lots of papers due and exams to study for and travel plans to organize. The Project Part 1 is due on Monday, December 6, 2021 at 11:59 PM. I am willing to be somewhat flexible with this deadline, but this flexibility comes with some responsibility on your part.

- I will grant short extensions only when I get an email from your group. When I grant an extension, I will send an email and copy all the members of the group. If you have not received an email from me confirming your extension, you don't have an extension!

- I will not grant extensions **after** the due date. If you need an extension, you must ask for it before the project is due.

- Early requests for extensions represent good planning, and will be much easier to get. The closer the due date gets, the harder it will be to get an extension.

- An extension on part 1 of the project *reduces* the time you have to work on part 2 of the project. There will be **no** extensions granted for part 2 of the project because part 2 will be due on the last day of exams (and I need to quickly grade exams and projects to turn in final grades.)

## Grading Criteria

This assignment is worth a total of 50 points. Those 50 points are broken down as follows:

- +10 points for submitting anything
- +15 points if everything in proj01.model, including Assembler.java compiles correctly.
- + 5 points if everything in proj01.model compiles with no warning messages
- +15 points if you correctly report all 14 errors in asmErrors.pasm (1 point per error caught plus 1 bonus points)
- + 5 points if you do not report any extra errors in the .pasm files in proj01.pasm

There will be deductions as follows:
- -5 if there are problems with the commit hash code in Brightspace.
- -5 if your repository is not configured correctly, as it appeared when you accepted the invitation.
- -5 for each 24 hour period that this part of the project is late without an extension.

Part 2 of the project will be worth 30 points, and the team interview will be worth 20 points. The main purpose of the team interview is to ensure that everyone on the team knows how all the code you submit works for both part 1 and part 2.
