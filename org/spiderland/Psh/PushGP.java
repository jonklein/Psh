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

import java.util.*;

/**
 * The Push Genetic Programming core class.
 */
abstract public class PushGP extends GA {
	private static final long serialVersionUID = 1L;
	
	protected Interpreter _interpreter;
	protected int _maxRandomCodeSize;
	protected int _maxPointsInProgram;
	protected float _fairMutationRange;
	protected int _executionLimit;
	protected boolean _useFairMutation;

	protected float _averageSize;
	protected int _bestSize;
	
	protected float _simplificationPercent;
	protected float _simplifyFlattenPercent;
	protected int _reproductionSimplifications;
	protected int _reportSimplifications;
	protected int _finalSimplifications;
	
	protected void InitFromParameters() throws Exception {
		// Default parameters to be used when optional parameters are not
		// given.
		float defaultFairMutationRange = 0.3f;
		float defaultsimplifyFlattenPercent = 20f;
		String defaultInterpreterClass = "org.spiderland.Psh.Interpreter";
		String defaultInputPusherClass = "org.spiderland.Psh.InputPusher";
		
		_maxRandomCodeSize = (int) GetFloatParam("max-random-code-size");
		_executionLimit = (int) GetFloatParam("execution-limit");
		_maxPointsInProgram = (int) GetFloatParam("max-points-in-program");
		
		_useFairMutation = "fair".equals(GetParam("mutation-mode", true));
		_fairMutationRange = GetFloatParam("fair-mutation-range", true);
		if(Float.isNaN(_fairMutationRange)){
			_fairMutationRange = defaultFairMutationRange;
		}

		_simplificationPercent = GetFloatParam("simplification-percent");
		_simplifyFlattenPercent = GetFloatParam("simplify-flatten-percent", true);
		if(Float.isNaN(_simplifyFlattenPercent)){
			_simplifyFlattenPercent = defaultsimplifyFlattenPercent;
		}
		
		_reproductionSimplifications = (int) GetFloatParam("reproduction-simplifications");
		_reportSimplifications = (int) GetFloatParam("report-simplifications");
		_finalSimplifications = (int) GetFloatParam("final-simplifications");

		// Setup ERC parameters
		int minRandomInt;
		int defaultMinRandomInt = -10;
		int maxRandomInt;
		int defaultMaxRandomInt = 10;
		int randomIntResolution;
		int defaultRandomIntResolution = 1;
		
		if(Float.isNaN(GetFloatParam("min-random-integer", true))){
			minRandomInt = defaultMinRandomInt;
		}
		else {
			minRandomInt = (int) GetFloatParam("min-random-integer", true);
		}
		if(Float.isNaN(GetFloatParam("max-random-integer", true))){
			maxRandomInt = defaultMaxRandomInt;
		}
		else {
			maxRandomInt = (int) GetFloatParam("max-random-integer", true);
		}
		if(Float.isNaN(GetFloatParam("random-integer-resolution", true))){
			randomIntResolution = defaultRandomIntResolution;
		}
		else {
			randomIntResolution = (int) GetFloatParam("random-integer-resolution", true);
		}
		
		float minRandomFloat;
		float defaultMinRandomFloat = -10.0f;
		float maxRandomFloat;
		float defaultMaxRandomFloat = 10.0f;
		float randomFloatResolution;
		float defaultRandomFloatResolution = 0.01f;
		
		if(Float.isNaN(GetFloatParam("min-random-float", true))){
			minRandomFloat = defaultMinRandomFloat;
		}
		else {
			minRandomFloat = GetFloatParam("min-random-float", true);
		}
		if(Float.isNaN(GetFloatParam("max-random-float", true))){
			maxRandomFloat = defaultMaxRandomFloat;
		}
		else {
			maxRandomFloat = GetFloatParam("max-random-float", true);
		}
		if(Float.isNaN(GetFloatParam("random-float-resolution", true))){
			randomFloatResolution = defaultRandomFloatResolution;
		}
		else {
			randomFloatResolution = GetFloatParam("random-float-resolution", true);
		}
		
		// Setup our custom interpreter class based on the params we're given
		String interpreterClass = GetParam("interpreter-class", true);
		if(interpreterClass == null){
			interpreterClass = defaultInterpreterClass;
		}
		Class<?> iclass = Class.forName(interpreterClass);
		Object iObject = iclass.newInstance();
		if (!(iObject instanceof Interpreter))
			throw (new Exception(
					"interpreter-class must inherit from class Interpreter"));

		_interpreter = (Interpreter) iObject;
		_interpreter.SetInstructions(new Program(_interpreter,
				GetParam("instruction-set")));
		_interpreter.SetRandomParameters(minRandomInt, maxRandomInt,
				randomIntResolution, minRandomFloat, maxRandomFloat,
				randomFloatResolution);

		String framemode = GetParam("push-frame-mode", true);

		String inputpusherClass = GetParam("inputpusher-class", true);
		if(inputpusherClass == null){
			inputpusherClass = defaultInputPusherClass;
		}
		
		iclass = Class.forName(inputpusherClass);

		iObject = iclass.newInstance();

		if (!(iObject instanceof InputPusher))
			throw new Exception(
					"inputpusher-class must inherit from class InputPusher");

		_interpreter.setInputPusher((InputPusher) iObject);

		InitInterpreter(_interpreter);

		if (framemode != null && framemode.equals("pushstacks"))
			_interpreter.SetUseFrames(true);

		super.InitFromParameters();
		
		// Print important parameters
		Print("  Important Parameters\n");
		Print(" ======================\n");
		Print("Population Size: " + (int)GetFloatParam("population-size") + "\n");
		Print("Generations: " + _maxGenerations + "\n");
		Print("Execution Limit: " + _executionLimit + "\n\n");
		Print("Crossover Percent: " + _crossoverPercent + "\n");
		Print("Mutation Percent: " + _mutationPercent + "\n");
		Print("Simplification Percent: " + _simplificationPercent + "\n");
		Print("Clone Percent: "+ (100 - _crossoverPercent - _mutationPercent -
				_simplificationPercent) + "\n");
		Print("\n");
	}

	protected void InitIndividual(GAIndividual inIndividual) {
		PushGPIndividual i = (PushGPIndividual) inIndividual;

		int randomCodeSize = _RNG.nextInt(_maxRandomCodeSize) + 2;
		Program p = _interpreter.RandomCode(randomCodeSize);

		i.SetProgram(p);
	}
	
	protected void BeginGeneration() {
		_averageSize = 0;
	}

	protected void EndGeneration() {
		_averageSize /= _populations[0].length;
	}
	
	protected void Evaluate() {
		float totalFitness = 0;
		_bestFitness = Float.MAX_VALUE;

		for (int n = 0; n < _populations[_currentPopulation].length; n++) {
			GAIndividual i = _populations[_currentPopulation][n];

			EvaluateIndividual(i);

			totalFitness += i.GetFitness();

			if (i.GetFitness() < _bestFitness) {
				_bestFitness = i.GetFitness();
				_bestIndividual = n;
				_bestSize = ((PushGPIndividual) i)._program.programsize();
				_bestErrors = i.GetErrors();
			}
		}

		_meanFitness = totalFitness / _populations[_currentPopulation].length;
	}

	protected void EvaluateIndividual(GAIndividual inIndividual) {
		EvaluateIndividual(inIndividual, false);
	}

	protected void EvaluateIndividual(GAIndividual inIndividual,
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
	}

	abstract protected void InitInterpreter(Interpreter inInterpreter)
			throws Exception;
	
	protected void EvaluateTrainerExactFitness(PushGPIndividual inTrainer){
	}

	protected String Report() {
		String report = super.Report();

		report += ";; Best Program:\n  "
				+ _populations[_currentPopulation][_bestIndividual] + "\n\n";

		report += ";; Best Program Fitness: " + _bestFitness + "\n";
		report += ";; Best Program Mean Fitness: "
				+ (_bestFitness / _testCases.size()) + "\n";
		report += ";; Best Program Errors: (";
		for (int i = 0; i < _testCases.size(); i++) {
			if (i != 0)
				report += " ";
			report += "(" + _testCases.get(i)._input + " ";
			report += Math.abs(_bestErrors.get(i)) + ")";
		}
		report += ")\n";
		report += ";; Best Program Size: " + _bestSize + "\n\n";

		report += ";; Mean Fitness: " + _meanFitness + "\n";
		report += ";; Mean Program Size: " + _averageSize + "\n";

		PushGPIndividual simplified = Autosimplify(
				(PushGPIndividual) _populations[_currentPopulation][_bestIndividual],
				_reportSimplifications);

		report += ";; Number of Evaluations Thus Far: "
				+ _interpreter.GetEvaluationExecutions() + "\n";
		String mem = String
				.valueOf(Runtime.getRuntime().totalMemory() / 10000000.0f);
		report += ";; Memory usage: " + mem + "\n\n";

		report += ";; Partial Simplification (may beat best):\n  ";
		report += simplified._program + "\n";
		report += ";; Partial Simplification Size: ";
		report += simplified._program.programsize() + "\n\n";

		return report;
	}

	protected String FinalReport() {
		String report = super.FinalReport();

		PushGPIndividual simplified = Autosimplify(
				(PushGPIndividual) _populations[_currentPopulation][_bestIndividual],
				_finalSimplifications);

		// Note: The number of evaluations here will likely be higher than that
		// given during the last generational report, since evaluations made
		// during simplification count towards the total number of
		// simplifications.
		report += ">> Number of Evaluations: "
			+ _interpreter.GetEvaluationExecutions() + "\n";

		report += ">> Best Program: "
				+ _populations[_currentPopulation][_bestIndividual] + "\n";
		report += ">> Fitness: " + _bestFitness + "\n";
		report += ">> Mean Fitness of Best Program Over Test Cases: "
				+ (_bestFitness / _testCases.size()) + "\n";
		report += ">> Errors: (";
		for (int i = 0; i < _testCases.size(); i++) {
			if (i != 0)
				report += " ";
			report += "(" + _testCases.get(i)._input + " ";
			report += Math.abs(_bestErrors.get(i)) + ")";
		}
		report += ")\n";
		report += ">> Size: " + _bestSize + "\n\n";
		
		report += "<<<<<<<<<< After Simplification >>>>>>>>>>\n";
		report += ">> Best Program: ";
		report += simplified._program + "\n";
		report += ">> Size: ";
		report += simplified._program.programsize() + "\n\n";

		return report;
	}
	
	protected PushGPIndividual Autosimplify(PushGPIndividual inIndividual,
			int steps) {

		PushGPIndividual simplest = (PushGPIndividual) inIndividual.clone();
		PushGPIndividual trial = (PushGPIndividual) inIndividual.clone();
		EvaluateIndividual(simplest, true);
		float bestError = simplest.GetFitness();

		boolean madeSimpler = false;

		for (int i = 0; i < steps; i++) {
			madeSimpler = false;
			float method = _RNG.nextInt(100);

			if (trial._program.programsize() <= 0)
				break;
			if (method < _simplifyFlattenPercent) {
				// Flatten random thing
				int pointIndex = _RNG.nextInt(trial._program.programsize());
				Object point = trial._program.Subtree(pointIndex);

				if (point instanceof Program) {
					trial._program.Flatten(pointIndex);
					madeSimpler = true;
				}
			} else {
				// Remove small number of random things
				int numberToRemove = _RNG.nextInt(3) + 1;

				for (int j = 0; j < numberToRemove; j++) {
					int trialSize = trial._program.programsize();

					if (trialSize > 0) {
						int pointIndex = _RNG.nextInt(trialSize);
						trial._program.ReplaceSubtree(pointIndex, new Program(
								_interpreter));
						trial._program.Flatten(pointIndex);
						madeSimpler = true;
					}
				}
			}

			if (madeSimpler) {
				EvaluateIndividual(trial, true);

				if (trial.GetFitness() <= bestError) {
					simplest = (PushGPIndividual) trial.clone();
					bestError = trial.GetFitness();
				}
			}

			trial = (PushGPIndividual) simplest.clone();
		}

		return simplest;
	}

	protected void Reproduce() {
		int nextPopulation = _currentPopulation == 0 ? 1 : 0;

		for (int n = 0; n < _populations[_currentPopulation].length; n++) {
			float method = _RNG.nextInt(100);
			GAIndividual next;

			if (method < _mutationPercent) {
				next = ReproduceByMutation(n);
			} else if (method < _crossoverPercent + _mutationPercent) {
				next = ReproduceByCrossover(n);
			} else if (method < _crossoverPercent + _mutationPercent
					+ _simplificationPercent) {
				next = ReproduceBySimplification(n);
			} else {
				next = ReproduceByClone(n);
			}

			_populations[nextPopulation][n] = next;
		}
	}

	protected GAIndividual ReproduceByCrossover(int inIndex) {
		PushGPIndividual a = (PushGPIndividual) ReproduceByClone(inIndex);
		PushGPIndividual b = (PushGPIndividual) TournamentSelect(
				_tournamentSize, inIndex);

		if (a._program.programsize() == 0) {
			return b;
		}
		if (b._program.programsize() == 0) {
			return a;
		}

		int aindex = _RNG.nextInt(a._program.programsize());
		int bindex = _RNG.nextInt(b._program.programsize());

		if (a._program.programsize() + b._program.SubtreeSize(bindex)
				- a._program.SubtreeSize(aindex) <= _maxPointsInProgram)
			a._program.ReplaceSubtree(aindex, b._program.Subtree(bindex));

		return a;
	}

	protected GAIndividual ReproduceByMutation(int inIndex) {
		PushGPIndividual i = (PushGPIndividual) ReproduceByClone(inIndex);

		int totalsize = i._program.programsize();
		int which = totalsize > 1 ? _RNG.nextInt(totalsize) : 0;
		int oldsize = i._program.SubtreeSize(which);
		int newsize = 0;

		if (_useFairMutation) {
			int range = (int) Math.max(1, _fairMutationRange * oldsize);			
			newsize = Math.max(1, oldsize + _RNG.nextInt(2 * range) - range);
		} else {
			newsize = _RNG.nextInt(_maxRandomCodeSize);
		}

		Object newtree;

		if (newsize == 1)
			newtree = _interpreter.RandomAtom();
		else
			newtree = _interpreter.RandomCode(newsize);

		if (newsize + totalsize - oldsize <= _maxPointsInProgram)
			i._program.ReplaceSubtree(which, newtree);

		return i;
	}

	protected GAIndividual ReproduceBySimplification(int inIndex) {
		PushGPIndividual i = (PushGPIndividual) ReproduceByClone(inIndex);

		i = Autosimplify(i, _reproductionSimplifications);

		return i;
	}

	public void RunTestProgram(Program p, int inTestCaseIndex) {
		PushGPIndividual i = new PushGPIndividual(p);
		GATestCase test = _testCases.get(inTestCaseIndex);

		System.out.println("Executing program: " + p);

		EvaluateTestCase(i, test._input, test._output);

		System.out.println(_interpreter);
	}
	
}