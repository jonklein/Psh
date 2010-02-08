To Do List
==========

Done Major Changes
------------------
- Added problem classes for integer symbolic regression (IntSymbolicRegression.java) and integer symbolic regression without an input instruction (IntSymbolicRegressionNoInput.java).
- Make PshGP display the errors for best program
- Implement Psh runtime examiner to see why Schush and Psh are running code and getting different results. It likely has to do with what they push on the stacks at the beginning of a run.
- Fix CODE.DO*RANGE. Issues: 1. Recrusive call is not surrounded by parentheses. 2. Does not leave a copy of second integer on int stack.
- Check that non-CODE.DO*RANGE  code stack iteration instructions are working correctly, i.e. have same results as with Schush
- Fix Psh to make sure EXEC.DO*RANGE and other iterative instructions are working correctly.
- Allow the inclusion of all instructions for a certain type using REGISTERED.TYPE (e.g. REGISTERED.INTEGER or REGISTERED.EXEC).
- Implement Autosimplification function. Have Psh use Autosimplification after evolution. Autosimplify during reports. Make optional Autosimplification genetic operator.

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
- Make changes to README to reflect Lee's suggestions.
- Announce major changes on i3ci blog.
- In README, make Major Changes section read better.


To Do Still
-----------

- Add information about config (.pushgp) files, and about PshInspector files (.push) to README.

- Add GitHub site to my UMass homepage.




- Look into input instructions in Psh - currently lacking - add auxilary stack



- Add new problem classes (such as IntSymbolicRegression.java) for other problem types.


- During Auto-simplification, instead of only flattening or removing random points, could run a subprogram for a bit, and then replace it by whatever constants are left on the stacks. This would be useful when code like "BOOLEAN.STACKDEPTH" is used only to get a 0 on the int stack.






Jon's TODO
----------

Some things TODO before a 1.0 release:

- x improved reporting
- x output to files
- x trivial GP problem sets
- x unfair mutation

- documentation //trh//important
- x trivial geography
- CSV reader
- classification problems //trh//what are these?
- parity problem //trh//important
- boolean instructions //trh//important
- instruction review //trh//important
