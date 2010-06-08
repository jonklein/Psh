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

package org.spiderland.Psh;

/**
 * This problem class implements symbolic regression for floating point numbers.
 * See also IntSymbolicRegression for integer symbolic regression.
 */
public class FloatSymbolicRegression extends PushGP {
	private static final long serialVersionUID = 1L;

	protected float _currentInput;

	protected void InitFromParameters() throws Exception {
		super.InitFromParameters();

		String cases = GetParam("test-cases");

		Program caselist = new Program(_interpreter, cases);

		for (int i = 0; i < caselist.size(); i++) {
			Program p = (Program) caselist.peek(i);

			if (p.size() < 2)
				throw new Exception("Not enough elements for fitness case \""
						+ p + "\"");

			Float in = new Float(p.peek(0).toString());
			Float out = new Float(p.peek(1).toString());

			Print(";; Fitness case #" + i + " input: " + in + " output: " + out
					+ "\n");

			_testCases.add(new GATestCase(in, out));
		}
	}

	protected void InitInterpreter(Interpreter inInterpreter) {
	}

	protected float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
			Object inOutput) {
		_interpreter.ClearStacks();

		_currentInput = (Float) inInput;

		floatStack stack = _interpreter.floatStack();

		stack.push(_currentInput);

		// Must be included in order to use the input stack.
		_interpreter.inputStack().push(_currentInput);

		_interpreter.Execute(((PushGPIndividual) inIndividual)._program,
				_executionLimit);

		float result = stack.top();
		// System.out.println( _interpreter + " " + result );

		return result - ((Float) inOutput);
	}
	
	protected boolean Success() {
		return _bestFitness <= 0.1 * _testCases.size();
	}

}
