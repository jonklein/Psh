/*
 * Copyright 2009-2010 Jon Klein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.spiderland.Psh.*;

/**
 * PshInspector pshFile
 * 
 * PshInspector can be used to inspect the execution of a Psh program.
 * After every step of the program, the stacks of the interpreter
 * are displayed. The Psh program is given to PshInspector through
 * the file pshFile. pshFile is an input file containing the
 * following, separated by new lines:
 *
 * - Program: The psh program to run
 * - ExecutionLimit: Maximum execution steps
 * - Input(optional): Any inputs to be pushed before execution,
 *                    separated by spaces. Note: Only int, float, and
 *                    boolean inputs are accepted.
 */
public class PshInspector {
	public static void main(String args[]) throws Exception {		

		if (args.length != 1) {
			System.out.println("Usage: PshInspector inputfile");
			System.exit(0);
		}

		// _input will allow easy initialization of the interpreter.
		InspectorInput _input = new InspectorInput(args[0]);
		Interpreter _interpreter = _input.getInterpreter();

		int _executionLimit = _input.getExecutionLimit();
		int executed = 0;
		int stepsTaken = 1;
		String stepPrint = "";

		// Print registered instructions
		System.out.println("Registered Instructions: "
				+ _interpreter.GetRegisteredInstructionsString() + "\n");

		// Run the Psh Inspector
		System.out.println("====== State after " + executed + " steps ======");
		_interpreter.PrintStacks();

		while (executed < _executionLimit && stepsTaken == 1) {
			executed += 1;

			// Create output string
			if (executed == 1)
				stepPrint = "====== State after " + executed + " step ";
			else
				stepPrint = "====== State after " + executed + " steps ";
			
			stepPrint += "(last step: ";
			Object execTop = _interpreter.execStack().top();
			
			if (execTop instanceof Program)
				stepPrint += "(...)";
			else
				stepPrint += execTop;
			
			stepPrint += ") ======";

			// Execute 1 instruction
			stepsTaken = _interpreter.Step(1);

			if (stepsTaken == 1) {
				System.out.println(stepPrint);
				_interpreter.PrintStacks();
			}
		}

	}
}