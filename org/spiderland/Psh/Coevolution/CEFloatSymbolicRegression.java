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

package org.spiderland.Psh.Coevolution;

import java.util.ArrayList;
import java.util.HashMap;

import org.spiderland.Psh.GA;
import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.GATestCase;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.Program;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;
import org.spiderland.Psh.floatStack;

/**
 * This problem class implements symbolic regression for floating point numbers
 * using co-evolved prediction. The class must keep track of the
 * amount of effort it takes compared to the effort of the co-evolving
 * predictor population, and use about 95% of the effort. Effort based on the
 * number of evaluation executions thus far, which is tracked by the
 * interpreter.
 */
public class CEFloatSymbolicRegression extends PushGP {
	private static final long serialVersionUID = 1L;

	protected float _currentInput;
	
	protected long _effort;
	protected float _predictorEffortPercent;
	protected PredictionGA _predictorGA;

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
		_predictorEffortPercent = GetFloatParam("PREDICTOR-effort-percent", true);
		_predictorGA = (PredictionGA) GA.GAWithParameters(GetPredictorParameters(_parameters));
		_predictorGA.SetGAandTrainers(this);
		
		
		//_predictorGA.Run();
		//System.exit(0);
		
		
	}

	protected void InitInterpreter(Interpreter inInterpreter) {
	}
	
	/**
	 * Evaluates a solution individual using the best predictor so far.
	 */
	protected void EvaluateIndividual(GAIndividual inIndividual,
			boolean duringSimplify) {
		ArrayList<Float> errors = new ArrayList<Float>();

		if (!duringSimplify)
			_averageSize += ((PushGPIndividual) inIndividual)._program
					.programsize();


		for (int n = 0; n < _testCases.size(); n++) {
			GATestCase test = _testCases.get(n);
			float e = EvaluateTestCase(inIndividual, test._input, test._output);
			errors.add(e);
		}

		inIndividual.SetFitness(AbsoluteSumOfErrors(errors));
		inIndividual.SetErrors(errors);

		
	}
	
	/**
	 * Evaluates a trainer's exact fitness and sets it.
	 */
	public void EvaluateTrainerExactFitness(PushGPIndividual inTrainer){
		ArrayList<Float> errors = new ArrayList<Float>();

		for (int n = 0; n < _testCases.size(); n++) {
			GATestCase test = _testCases.get(n);
			float e = EvaluateTestCase(inTrainer, test._input, test._output);
			errors.add(e);
		}

		inTrainer.SetFitness(AbsoluteAverageOfErrors(errors));
		inTrainer.SetErrors(errors);	
	}

	public float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
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
			HashMap<String, String> parameters) throws Exception {
		
		HashMap<String, String> predictorParameters = new HashMap<String, String>();
		
		predictorParameters.put("problem-class", GetParam("PREDICTOR-problem-class"));
		predictorParameters.put("population-size", GetParam("PREDICTOR-population-size"));
		predictorParameters.put("mutation-percent", GetParam("PREDICTOR-mutation-percent"));
		predictorParameters.put("crossover-percent", GetParam("PREDICTOR-crossover-percent"));
		predictorParameters.put("tournament-size", GetParam("PREDICTOR-tournament-size"));
		predictorParameters.put("trivial-geography-radius", GetParam("PREDICTOR-trivial-geography-radius"));
		predictorParameters.put("generations-between-trainers", GetParam("PREDICTOR-generations-between-trainers"));
		predictorParameters.put("trainer-population-size", GetParam("PREDICTOR-trainer-population-size"));
		
		
		
		///? remove below
		predictorParameters.put("problem-class", "org.spiderland.Psh.IntSymbolicRegression");
		predictorParameters.put("max-random-code-size","35");
		predictorParameters.put("execution-limit","150");
		predictorParameters.put("max-points-in-program","50");
		predictorParameters.put("simplification-percent","5");
		predictorParameters.put("reproduction-simplifications","20");
		predictorParameters.put("report-simplifications","100");
		predictorParameters.put("final-simplifications","1000");
		predictorParameters.put("max-generations","10000");
		predictorParameters.put("instruction-set","(registered.integer registered.input)");
		predictorParameters.put("test-cases","((1 1) (2 3) (3 5) (4 7) (5 9) (6 11) (7 13) (8 15) (9 17) (10 19))");
		
		return predictorParameters;
	}
	
}
