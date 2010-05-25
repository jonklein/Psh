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
	protected float _averageSize;
	protected boolean _useFairMutation;

	protected float _simplificationPercent;
	protected float _simplifyFlattenPercent;
	protected int _reproductionSimplifications;
	protected int _reportSimplifications;
	protected int _finalSimplifications;

	protected void BeginGeneration() {
		_averageSize = 0;
	}

	protected void EndGeneration() {
		_averageSize /= _populations[0].length;
	}

	public void RunTestProgram(Program p, int inTestCaseIndex) {
		PushGPIndividual i = new PushGPIndividual(p);
		GATestCase test = _testCases.get(inTestCaseIndex);

		System.out.println("Executing program: " + p);

		EvaluateTestCase(i, test._input, test._output);

		System.out.println(_interpreter);
	}

	protected void InitIndividual(GAIndividual inIndividual) {
		PushGPIndividual i = (PushGPIndividual) inIndividual;

		int randomCodeSize = _RNG.nextInt(_maxRandomCodeSize) + 2;
		Program p = _interpreter.RandomCode(randomCodeSize);

		i.SetProgram(p);
	}

	protected int EvaluateIndividual(GAIndividual inIndividual) {
		return EvaluateIndividual(inIndividual, false);
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

	protected String Report() {
		String report = super.Report();
		PushGPIndividual simplified = Autosimplify(
				(PushGPIndividual) _populations[_currentPopulation][_bestIndividual],
				_reportSimplifications);

		report += ";; Mean Program Size: " + _averageSize + "\n\n";

		report += ";; Partial Simplification (may beat best):\n  ";
		report += simplified._program + "\n";
		report += ";; Partial Simplification Size: ";
		report += simplified._program.programsize() + "\n\n";

		return report;
	}

	protected String FinalReport() {
		String report = super.FinalReport() + "\n";

		PushGPIndividual simplified = Autosimplify(
				(PushGPIndividual) _populations[_currentPopulation][_bestIndividual],
				_finalSimplifications);

		report += "<<<<<<<<<< After Simplification >>>>>>>>>>\n";
		report += ">> Best Program: ";
		report += simplified._program + "\n";
		report += ">> Size: ";
		report += simplified._program.programsize() + "\n\n";

		return report;
	}

	protected void InitFromParameters() throws Exception {
		_maxRandomCodeSize = (int) GetFloatParam("max-random-code-size");
		_executionLimit = (int) GetFloatParam("execution-limit");
		_fairMutationRange = GetFloatParam("fair-mutation-range");
		_maxPointsInProgram = (int) GetFloatParam("max-points-in-program");
		_useFairMutation = "fair".equals(GetParam("mutation-mode", true));

		_simplificationPercent = GetFloatParam("simplification-percent");
		_simplifyFlattenPercent = GetFloatParam("simplify-flatten-percent");
		_reproductionSimplifications = (int) GetFloatParam("reproduction-simplifications");
		_reportSimplifications = (int) GetFloatParam("report-simplifications");
		_finalSimplifications = (int) GetFloatParam("final-simplifications");

		// Setup our custom interpreter class based on the params we're given

		Class<?> iclass = Class.forName(GetParam("interpreter-class"));

		Object iObject = iclass.newInstance();

		if (!(iObject instanceof Interpreter))
			throw (new Exception(
					"interpreter-class must inherit from class Interpreter"));

		String framemode = GetParam("push-frame-mode", true);

		_interpreter = (Interpreter) iObject;
		_interpreter.SetInstructions(new Program(_interpreter,
				GetParam("instruction-set")));

		iclass = Class.forName(GetParam("inputpusher-class"));

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

	abstract protected void InitInterpreter(Interpreter inInterpreter)
			throws Exception;

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

}