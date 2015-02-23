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

package org.spiderland.Psh.ProbClass;

import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.GATestCase;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.ObjectPair;
import org.spiderland.Psh.Program;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;
import org.spiderland.Psh.floatStack;
import org.spiderland.Psh.TestCase.TestCaseGenerator;

/**
 * This problem class implements symbolic regression for floating point numbers.
 * See also IntSymbolicRegression for integer symbolic regression.
 */
public class FloatSymbolicRegression extends PushGP {
	private static final long serialVersionUID = 1L;
	
	private float _noResultPenalty = 10000;

	protected void InitFromParameters() throws Exception {
		super.InitFromParameters();

		String cases = GetParam("test-cases", true);
		String casesClass = GetParam("test-case-class", true);
		if (cases == null && casesClass == null) {
			throw new Exception("No acceptable test-case parameter.");
		}

		if (casesClass != null) {
			// Get test cases from the TestCasesClass.
			Class<?> iclass = Class.forName(casesClass);
			Object iObject = iclass.newInstance();
			if (!(iObject instanceof TestCaseGenerator)) {
				throw (new Exception(
						"test-case-class must inherit from class TestCaseGenerator"));
			}

			TestCaseGenerator testCaseGenerator = (TestCaseGenerator) iObject;
			int numTestCases = testCaseGenerator.TestCaseCount();

			for (int i = 0; i < numTestCases; i++) {
				ObjectPair testCase = testCaseGenerator.TestCase(i);

				Float in = (Float) testCase._first;
				Float out = (Float) testCase._second;

				Print(";; Fitness case #" + i + " input: " + in + " output: "
						+ out + "\n");

				_testCases.add(new GATestCase(in, out));
			}
		} else {
			// Get test cases from test-cases.
			Program caselist = new Program(_interpreter, cases);

			for (int i = 0; i < caselist.size(); i++) {
				Program p = (Program) caselist.peek(i);

				if (p.size() < 2)
					throw new Exception(
							"Not enough elements for fitness case \"" + p
									+ "\"");

				Float in = new Float(p.peek(0).toString());
				Float out = new Float(p.peek(1).toString());

				Print(";; Fitness case #" + i + " input: " + in + " output: "
						+ out + "\n");

				_testCases.add(new GATestCase(in, out));
			}
		}

	}

	protected void InitInterpreter(Interpreter inInterpreter) {
	}

	public float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
			Object inOutput) {
		_interpreter.ClearStacks();

		float currentInput = (Float) inInput;

		floatStack stack = _interpreter.floatStack();

		stack.push(currentInput);

		// Must be included in order to use the input stack.
		_interpreter.inputStack().push(currentInput);

		_interpreter.Execute(((PushGPIndividual) inIndividual)._program,
				_executionLimit);

		float result = stack.top();
		
		// Penalize individual if there is no result on the stack.
		if(stack.size() == 0){
			return _noResultPenalty;
		}
		
		return result - ((Float) inOutput);
	}
	
	public float GetIndividualTestCaseResult(GAIndividual inIndividual, GATestCase inTestCase){
		_interpreter.ClearStacks();

		float currentInput = (Float) inTestCase._input;

		floatStack stack = _interpreter.floatStack();

		stack.push(currentInput);

		// Must be included in order to use the input stack.
		_interpreter.inputStack().push(currentInput);

		_interpreter.Execute(((PushGPIndividual) inIndividual)._program,
				_executionLimit);

		float result = stack.top();
		
		// If no result, return 0
		if(stack.size() == 0){
			return 0;
		}
		
		return result;
	}

	protected boolean Success() {
		return _bestMeanFitness <= 0.1;
	}

}
