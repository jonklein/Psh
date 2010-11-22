> Copyright 2009-2010 Jon Klein
>
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
>    http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.

Psh
===

Psh is a Java implementation of the Push programming language and of PushGP. Push is a stack-based language designed for evolutionary computation, specifically genetic programming. PushGP is a genetic programming system that evolves programs in Push. More information about Push and PushGP can be found [here](http://hampshire.edu/lspector/push.html).

This is v1.0 of Psh.

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

This problem uses integer symbolic regression to solve the equation y = 12x^2 + 5. Other sample problems are available, with descriptions, in `gpsamples/`. For example, `intreg2.pushgp` uses integer symbolic regression to solve the factorial function, and `regression1.pushgp` uses float symbolic regression to solve y = 12x^2 + 5.

Using PshInspector
------------------
PshInspector allows you to examine every step of a Psh program as it executes. To run PshInspector on a sample psh program:

    $ java PshInspector pushsamples/exampleProgram1.push

This push file runs the psh program `(2994 5 integer.+)` for 100 steps after pushing the inputs `44, 22, true, 17.76`. Other sample psh programs are available in `pushsamples/`.

Using Psh in Eclipse
====================

Getting Psh
-----------
Use _Getting Started with Git_ above, or use [Egit](http://www.eclipse.org/egit/) with help [on this page](http://wiki.eclipse.org/EGit/User_Guide). Note on installing Egit: the link provided on the download page does not download any files directly; instead, it is meant to be pasted in Eclipse under `Help > Install New Software`.

Loading Psh in Eclipse
----------------------
Start a new Java project by clicking `File > New > Java Project`. From there, select `Create project from existing source` and browse for and select the Psh project folder. Click `Finish`.

Adding JUnit to the Build Path
------------------------------
JUnit, while not essential to using Psh, will allow for some automatic testing and will make Eclipse not complain about errors in Psh whenever you try to run it. JUnit can be downloaded [here](http://www.junit.org/), and should be placed wherever you want to store Java jar libraries. To add JUnit to your Psh project, click `Project > Properties`. Then select `Java Build Path` followed by the `Libraries` tab, and then click `Add External JARs...`. Select your JUnit jar file wherever you saved it, click `Open`, and then `Ok`. Now, JUnit should be added to your build path.

Using PshGP from Eclipse
------------------------
Since PshGP requires command-line arguments, you must first specify them in Eclipse by setting up a Run Configuration. To do so, click `Run > Run Configurations...`. From here, click `New launch configuration` in the upper left corner. Give your configuration a name, and select the project by browsing or by typing Psh in the project line. Then, select PshGP as the main class by clicking `Search > PshGP > Ok`. Now, click the `Arguments` tab, and type in the configuration file you wish to run, for example `gpsamples/intreg1.pushgp`. From here, you can click `Run` to directly run this configuration, or `Apply` then `Close` if you don't want to run it right away. To run a run configuration, click the arrow to the right of the `Run` arrow, and select the run configuration you wish to execute.

Psh In More Detail
==================

Configuration Files
-------------------
PshGP runs are setup using configuration files which have the extension `.pushgp`. These files contain a list of parameters in the form of 

    param-name = value

The following parameters must be defined in the configuration file, given with example values:

    problem-class = org.spiderland.Psh.IntSymbolicRegression
    
    max-generations = 200
    population-size = 1000
    execution-limit = 150
    max-points-in-program = 100
    max-random-code-size = 40
    
    tournament-size = 7
    mutation-percent = 30
    crossover-percent = 55
    simplification-percent = 5
    
    reproduction-simplifications = 25
    report-simplifications = 100
    final-simplifications = 1000
    
    test-cases = ((1 1) (2 2) (3 6) (4 24) (5 120) (6 720))
    instruction-set = (registered.exec registered.boolean integer.% integer.* integer.+ integer.- integer./ integer.dup)

The following parameters are optional. If not specified, the default values below will be used for these parameters, except for the parameters `mutation-mode`, `output-file`, and `push-frame-mode`, which significantly change the run when specified. Also, `target-function-string` defaults to not displaying a string, but a representative example is given below.

    trivial-geography-radius = 10
    simplify-flatten-percent = 20
    mutation-mode = fair
    fair-mutation-range = .3
    
    node-selection-mode = unbiased  (others available are leaf-probability and size-tournament)
	node-selection-leaf-probability = 10  (only used if node-selection-mode = leaf-probability)
	node-selection-tournament-size = 2  (only used if node-selection-mode = size-tournament)
    
    min-random-integer = -10
    max-random-integer = 10
    random-integer-resolution = 1
    min-random-float = -10.0
    max-random-float = 10.0
    random-float-resolution = 0.01
    
    target-function-string = "y = x^4 - 2x + 7"
    
    interpreter-class = org.spiderland.Psh.Interpreter
    individual-class = org.spiderland.Psh.PushGPIndividual
    inputpusher-class = org.spiderland.Psh.InputPusher
    
    output-file = out.txt
    push-frame-mode = pushstacks

PshInspector Files
------------------
In order to inspect the execution of a program, PshInspector takes a push program file with the extension `.push`. After every step of the program, the stacks of the interpreter are displayed. The input file contains the following, separated by new lines:

- Program: The Psh program to run
- ExecutionLimit: Maximum execution steps
- Input(optional): Any inputs to be pushed before execution, separated by spaces. The inputs are pushed in the order in which they are given. Note: Only int, float, and boolean inputs are accepted.

Problem Classes
---------------
PshGP uses problem classes, implemented as Java classes, to determine certain aspects of the run, such as how to compute fitness values. The choice of problem class determines how test case data is interpreted, and which stacks are used for test case input and output. In addition, certain inherited methods in both GA.java and PushGP.java may be overwritten for further customization.

Psh comes with a few standard problem classes. The following problem classes are currently implemented, and are in the ProbClass subpackage:

- FloatSymbolicRegression.java: Maps an input floating point value to an output floating point value. Error value is computed as the difference between the desired output value and the top value on the float stack.
- IntSymbolicRegression.java: Maps an input integer value to an output integer value. Error value is computed as the difference between the desired output value and the top value on the integer stack.
- CartCentering.java: Maps two input floats (position and velocity) to a boolean value that represents a forward or backward force applied to a cart. The error is the amount of time required to stop the cart at the origin. For more information, see the problem class file.

In order to perform runs for other types of problems, you can implement your own custom problem classes. Please note the following:

- You will likely want to implement the InitFromParamenters method, which can be used to set up test cases. If so, make sure to also call its parent method.
- In PshGP, the term fitness actually refers to error values, which means that lower values are considered more fit and that 0.0 represents no error. The EvaluateTestCase method must be implemented by any problem class, and should compute an individual's fitness, with lower values being better.
- The InitInterpreter method must be implemented by all problem classes though many times this method is simply left empty.
- There are other optional methods that can be overwritten or extended in the GA.java and PushGP.java classes. For example, the CartCentering.java problem class implements the Success method in order to override the conditions that GA uses to identify a successful run.

Changelog
=========

Major Changes since v1.0:
-------------------------
- The parameters that affect Ephemeral Random Constant creation, such as the minimum random integer, are now available as optional configuration parameters. See Configuration Files above for more details.
- Implement new instructions: integer.pow, integer.min, integer.max, float.exp, float.pow. Also, fixed a bug in float.max.
- Moved problem classes and test cases to their own packages to reduce clutter.
- Fixed holes in many integer and float instructions that could cause underflow, overflow, or NaN errors.
- Made FloatSymbolicRegression and IntSymbolicRegression as well as co-evolved FloatSymbolicRegression work with test-case generators.
- PshGP now primarily uses the mean of test case errors for an individual's error instead of the total sum of the errors.
- Added optional parameter `target-function-string`, which specifies a human-readable version of the target function, which is only used in I/O.
- Added many instructions that were missing from the Push 3.0 specification.
- Added node-selection-mode as an optional parameter, as well as node size tournaments for node selection.

Major Changes since v0.3:
-------------------------
- Added new integer and float instructions: abs, neg, sin, cos, tan, max, min.
- Added new boolean instructions: and, or, xor, not.
- Added problem class for the cart centering problem (CartCentering.java), an optimal control problem.
- Made many parameters of .pushgp files optional. This should make creating .pushgp files for new users much simpler, as many parameters are rarely (if ever) changed. Optional parameters are listed in this readme.
- Change Psh over to Apache 2.0 license.
- The number of fitness evaluations is now displayed during reports.

Major Changes since v0.2:
-------------------------
- All instructions have been converted into lower case to match Schush and other implementations.
- An input stack was added, which holds all inputs. It has the following instructions:
    1. input.index - Pops n off of the integer stack and pushes input[n] onto corresponding stack. If integer stack is empty, acts as a no-op.
    2. input.makeinputsN - Creates N instructions called 'input.in0', 'input.in1', ..., 'input.in(N-1)'
    3. input.inall - For all n in 0 to input.size, push input[n] onto the corresponding stack.
    4. input.inallrev - For all n in input.size to 9, push input[n] onto the corresponding stack.
    5. input.stackdepth - Puts size of stack on integer stack.
- In config files, you can now include all instructions for a certain type using 'registered.type' (e.g. 'registered.integer' or 'registered.stack').
- Implemented auto-simplification, which is used during generation and final reports. Auto-simplification may also be used as a genetic operator along with mutation and crossover.

Major Changes since v0.1:
-------------------------
- Added problem classes for integer symbolic regression (IntSymbolicRegression.java) and integer symbolic regression without an input instruction (IntSymbolicRegressionNoInput.java).
- Fixed 'code' and 'exec' stack iteration functions, which were not executing correctly according to Push 3.0 standards.
- PshGP now displays the error values for the best program during the generation report.
- PshInspector was created to inspect interpreter stacks of push programs as they execute. This can be used to catch errors and trace executions. To run, see Using PshInspector section above.

Acknowledgement
===============
This material is based upon work supported by the National Science Foundation under Grant No. 1017817. Any opinions, findings, and conclusions or recommendations expressed in this publication are those of the authors and do not necessarily reflect the views of the National Science Foundation.