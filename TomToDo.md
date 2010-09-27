To Do List
==========

Done Major Changes
------------------
- Added problem classes for integer symbolic regression (IntSymbolicRegression.java) and integer symbolic regression without an input instruction (IntSymbolicRegressionNoInput.java).
- Make PshGP display the errors for best program
- Implement Psh runtime examiner to see why Schush and Psh are running code and getting different results. It likely has to do with what they push on the stacks at the beginning of a run.
- Fix code.do*range. Issues: 1. Recrusive call is not surrounded by parentheses. 2. Does not leave a copy of second integer on int stack.
- Check that non-code.do*range  code stack iteration instructions are working correctly, i.e. have same results as with Schush
- Fix Psh to make sure exec.do*range and other iterative instructions are working correctly.
- Allow the inclusion of all instructions for a certain type using registered.type (e.g. registered.integer or registered.exec).
- Implement Autosimplification function. Have Psh use Autosimplification after evolution. Autosimplify during reports. Make optional Autosimplification genetic operator.
- Make all instructions lower case.
- Implement input stack including the following instructions:
    1. input.index - Pops n off of the integer stack and pushes input[n] onto corresponding stack. If integer stack is empty, acts as a no-op.
    2. input.makeinputsN - Creates N instructions called 'input.in0', 'input.in1', ..., 'input.in(N-1)'
    3. input.inall - For all n in 0 to input.size, push input[n] onto the corresponding stack.
    4. input.inallrev - For all n in input.size to 9, push input[n] onto the corresponding stack.
    5. input.stackdepth - Puts size of stack on integer stack.
- Implement absolute value and negation instructions for both int and float.
- Implement new problem class CartCentering.java, that is used for the Cart Centering problem described by Koza in Genetic Programming.
- Change Psh over to Apache 2.0 license.
- Implement new instructions: integer.pow, integer.min, integer.max, float.exp, float.pow
- Create more difficult float regression example that uses many test cases, and to do so creates it's test cases using TestCaseGenerator.java.


Done Minor Changes
------------------
- Fiddle with integer symbolic regression to test a different function and different input
- Make list of available instructions
- Implement symbolic regression for a different example (see Schush) [i.e. run trial of Factorial]
- Make sure Psh prints stacks (int and float in particular) in same order as Schush
- Fix parentheses pushed onto stack issue in Psh.
- In PshInspector, when printing generation number, print last executed instruction on same line.
- Run Factorial and make sure the returned programs are correct
- Make sure trivial-geography is actually being used. It's parameter appears only in the function TournamentSelectionIndex, which is never being called.
- Get working version uploaded to GitHub
- Remove "//trh//" type comments, clean up things for release v0.2
- Announce major changes on i3ci blog.
- In README, make Major Changes section read better.
- Add population, generations, and other statistics to the pre-run report of PushGP.
- Add to "Problem Classes" section in README
- Made many parameters of .pushgp files optional. This should make creating .pushgp files for new users much simpler, as many parameters are rarely (if ever) changed.
- Add float.erc and integer.erc to their respective registered instructions (i.e. for registered.float)
- The number of fitness evaluations is now displayed during reports.
- Release v1.0 of Psh, including changing the README version number.
- The parameters that affect Ephemeral Random Constant creation, such as the minimum random integer, are now available as optional .pushgp parameters. 
- Fixed a bug in float.max
- Added optional parameter `target-function-string`, which specifies a human-readable version of the target function, which is only used in I/O.


To Do Still
-----------
- Make way to conduct multiple runs and get statistics about those runs, to compare things such as population, generations, or use of ERCs.
- Add co-evolution fitness predictors. This will be a big project (make a git branch).
  - Many GA an PushGP methods are can be overwritten in order to implement co-evolution.
- Add instructions for integer.fromfloat, float.frominteger, etc.
- During Auto-simplification, instead of only flattening or removing random points, could run a subprogram for a bit, and then replace it by whatever constants are left on the stacks. This would be useful when code like "boolean.stackdepth" is used only to get a 0 on the int stack.


Jon's TODO
----------

Some things TODO before a 1.0 release:

- x improved reporting
- x output to files
- x trivial GP problem sets
- x unfair mutation

- documentation
- x trivial geography
- CSV reader
- classification problems //trh//what are these? - check out Koza
- parity problem //trh//semi-important
- x boolean instructions //trh//important
- instruction review
