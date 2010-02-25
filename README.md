Psh
===

Psh is a Java implementation of the Push programming language and of PushGP. Push is a stack-based language designed for evolutionary computation, specifically genetic programming. PushGP is a genetic programming system that evolves programs in Push. More information about Push and PushGP can be found [here](http://hampshire.edu/lspector/push.html).

Psh is licensed under the GNU General Public License.

Getting Started with Git
========================

To Get Psh
----------
    $ git clone git://github.com/jonklein/Psh.git
    $ cd Psh

To Update Psh
-------------
    $ cd Psh
    $ git pull

Git References
--------------
- [Pro Git](http://progit.org/book/) - A wonderful source for those new to git.
- [GitHub Help](http://help.github.com/) - GitHub help pages.

Getting Started with Psh
========================

Building Psh
------------
After getting Psh with get, build the package:

    $ make

Using PshGP
----------
To run PshGP on a sample problem:

    $ java PshGP gpsamples/intreg1.pushgp

This problem uses integer symbolic regression to solve the equation y = 12x^2 + 5. Other sample problems are available, with descriptions, in 'gpsamples/'. For example, 'intreg.2.pushgp' uses integer symbolic regression to solve the factorial function, and 'regression1.pushgp' uses float symbolic regression to solve y = 12x^2 + 5.

Using PshInspector
------------------
PshInspector allows you to examine every step of a Psh program as it executes. To run PshInspector on a sample psh program:

    $ java PshInspector pushsamples/exampleProgram1.push

This push file runs the psh program '(2994 5 integer.+)' for 100 steps after pushing the inputs '44, 22, true, 17.76'. Other sample psh programs are available in 'pushsamples/'.

Psh In More Detail
==================

Configuration Files
-------------------
PshGP runs are setup using configuration files which have the extension '.pushgp'. These files contain a list of parameters in the form of 

    param-name = value

The following parameters must be defined in the configuration file, given with example values:

    interpreter-class = org.spiderland.Psh.Interpreter
    individual-class = org.spiderland.Psh.PushGPIndividual
    problem-class = org.spiderland.Psh.IntSymbolicRegression
    
    max-generations = 200
    population-size = 1000
    execution-limit = 150
    max-points-in-program = 100
    
    tournament-size = 7
    trivial-geography-radius = 10
    
    mutation-percent = 30
    crossover-percent = 55
    simplification-percent = 5
    simplify-flatten-percent= 20
    
    reproduction-simplifications = 25
    report-simplifications = 100
    final-simplifications = 1000
    
    fair-mutation-range = .3
    max-random-code-size = 40
    
    test-cases = ((1 1) (2 2) (3 6) (4 24) (5 120) (6 720))
    instruction-set = (registered.exec registered.boolean integer.% integer.* integer.+ integer.- integer./ integer.< integer.= integer.> integer.dup integer.flush integer.pop integer.rot integer.stackdepth integer.swap)

The following parameters are optional:

    output-file = out.txt
    mutation-mode = fair
    push-frame-mode = pushstacks

PshInspector Files
------------------
In order to inspect the execution of a program, PshInspector takes a push program file with the extension '.push'. After every step of the program, the stacks of the interpreter are displayed. The input file contains the following, separated by new lines:

- Program: The psh program to run
- ExecutionLimit: Maximum execution steps
- Input(optional): Any inputs to be pushed before execution, separated by spaces. The inputs are pushed in the order in which they are given. Note: Only int, float, and boolean inputs are accepted.

Problem Classes
---------------
See [Jon Klein's Instructions](http://www.spiderland.org/Psh/docs.html). More coming soon...

Converting Psh Programs to Schush Programs
------------------------------------------
You may have noticed that Psh programs are not executable in Shcush, since Psh instructions use all capitals, and Schush instructions use all lower case. In order to convert a Psh program into a Schush program, we have provided a C++ program that converts the standard input file from a Psh program into a Schush program, which is sent to standard output. The converter is located at 'tools/converter/PshToSchush'.

Major Changes since v0.1:
=========================

Psh
---
- Added problem classes for integer symbolic regression (IntSymbolicRegression.java) and integer symbolic regression without an input instruction (IntSymbolicRegressionNoInput.java).
- Fixed 'code' and 'exec' stack iteration functions, which were not executing correctly according to Push 3.0 standards.
- All instructions have been converted into lower case to match Schush and other implementations.
- An input stack was added, which holds all inputs. It has the following instructions:
1. input.index - Pops n off of the integer stack and pushes input[n] onto corresponding stack. If integer stack is empty, acts as a no-op.
2. input.makeinputsN - Creates N instructions called 'input.in0', 'input.in1', ..., 'input.in(N-1)'
3. input.inall - For all n in 0 to input.size, push input[n] onto the corresponding stack.
4. input.inallrev - For all n in input.size to 9, push input[n] onto the corresponding stack.
5. input.stackdepth - Puts size of stack on integer stack.


PshGP
-----
- PshGP now displays the error values for the best program during the generation report.
- In config files, you can now include all instructions for a certain type using 'registered.type' (e.g. 'registered.integer' or 'registered.stack').
- Implemented auto-simplification, which is used during generation and final reports. Auto-simplification may also be used as a genetic operator along with mutation and crossover.

PshInspector
------------
- PshInspector was created to inspect interpreter stacks of push programs as they execute. This can be used to catch errors and trace executions. To run, see Using PshInspector section above.
