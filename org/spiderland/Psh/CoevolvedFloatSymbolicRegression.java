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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This problem class implements symbolic regression for floating point numbers
 * using co-evolved prediction. The class must keep track of the
 * amount of effort it takes compared to the effort of the co-evolving
 * predictor population, and use about 95% of the effort. Effort based on the
 * number of evaluation executions thus far, which is tracked by the
 * interpreter.
 */
public class CoevolvedFloatSymbolicRegression extends PushGP {
	private static final long serialVersionUID = 1L;

	protected float _currentInput;
	
	protected long _effort;
	protected GA _predictorGA;

	protected void InitFromParameters() throws Exception {
		super.InitFromParameters();
		
		_effort = 0;

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
		
		//Create and initialize predictors
		_predictorGA = GA.GAWithParameters(GetPredictorParameters(_parameters));
		
		
	}

	protected void InitInterpreter(Interpreter inInterpreter) {
	}
	
	protected int EvaluateIndividual(GAIndividual inIndividual,
			boolean duringSimplify) {
		ArrayList<Float> errors = new ArrayList<Float>();

		if (!duringSimplify)
			_averageSize += ((PushGPIndividual) inIndividual)._program
					.programsize();

		long t = System.currentTimeMillis();

		for (int n = 0; n < _testCases.size(); n++) {
			GATestCase test = _testCases.get(n);
			float e = EvaluateTestCase(inIndividual, test._input, test._output);
			errors.add(e);
		}
		t = System.currentTimeMillis() - t;

		inIndividual.SetFitness(AbsoluteSumOfErrors(errors));
		inIndividual.SetErrors(errors);

		//System.out.println("Evaluated individual in " + t + " msec: fitness "
		//		+ inIndividual.GetFitness());
		
		return 0;
	}

	protected float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
			Object inOutput) {
		_effort++;
		
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
		
		/*
		System.out.println("\nevaluations according to interpreter " + Interpreter.GetEvaluationExecutions());
		System.out.println("evaluations according to effort " + _effort);
		*/
		
		return result - ((Float) inOutput);
	}
	
	protected boolean Success() {
		return _bestFitness <= 0.1 * _testCases.size();
	}

	private HashMap<String, String> GetPredictorParameters(
			HashMap<String, String> parameters) {
		
		HashMap<String, String> predictorParameters = new HashMap<String, String>();
		
		predictorParameters.put("alphabet", "soup");/////////////obviously change later
		
		return predictorParameters;
	}
	
}
