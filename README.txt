README

1.
	Brief Summary:
		- The program is made to read a post fixed regular expression from a text file inputted through 			standard input/command line.
		- The text file needs to be formatted where in each line is a single post fixed regular expression.
		- The valid alphabet for the PFRegEx are {a,b,c,d,e}
		- The valid operators are; '|' for union; '&' for concatenation; and '*' for kleene star.
		- The program will then convert the post fixed regular expression into a NFA using stack.
		- The program will print the states of the NFA in ascending order
			e.g.
				RE: aa&
				Start: q1
				Accpet: q4
				(q1, a) -> q2
				(q2, E) -> q3
				(q3, a) -> q4
				
				**q1 is a state, has 'a" transtion, and connected to state q2**
2.
	Files in Archive:
		REtoNFA.java - A java file that contains the process of converting post-fixed regular expression
				to NFA using stack
		README.txt - information about the program
	
3.
	Compiling Instruction:
		In order to compile the java file in the command line, java is needed to be installed.
	
	How to Compile: (case sensitive)
		javac REtoNFA.java
	
4.
	How to Run:
		java REtoNFA input.txt
		
