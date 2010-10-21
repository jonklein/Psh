package org.spiderland.Psh.PushBrush;

import java.util.ArrayList;

import org.spiderland.Psh.*;

import org.spiderland.Psh.PushBrush.BrushAttributes;

/**
 * The PushBrush problem class
 * @author Tom
 *
 */
public class PushBrushPC extends PushGP{
	private static final long serialVersionUID = 1L;

	int attributeStackIndex;

	boolean newGeneration; // Tells RunUntilHumanEvaluation() whether a new
	                       // generation has just started.
	int bestIndividualPreviousGeneration;
	
	// Variables for evaluation
	double totalFitness;
	GAIndividual currentIndividual;
	public int currentIndividualIndex;
	

	@Override
	protected void InitFromParameters() throws Exception {
		newGeneration = true;

		super.InitFromParameters();
	}
	
	@Override
	protected void InitInterpreter(Interpreter inInterpreter) throws Exception {
		
		attributeStackIndex = inInterpreter.addCustomStack(new intStack());
		//intStack attributeStack = (intStack) inInterpreter.getCustomStack(_attributeStackIndex);
		
		/* Attribute indices:
		 * 0 = x;
		 * 1 = y;
		 * 2 = radius;
		 * 3 = red;
		 * 4 = green;
		 * 5 = blue;
		 */
		inInterpreter.AddInstruction("brush.x.increment", new BrushIncrement(0));
		inInterpreter.AddInstruction("brush.x.decrement", new BrushDecrement(0));
		inInterpreter.AddInstruction("brush.x.input", new BrushInput(0));
		
		inInterpreter.AddInstruction("brush.y.increment", new BrushIncrement(1));
		inInterpreter.AddInstruction("brush.y.decrement", new BrushDecrement(1));
		inInterpreter.AddInstruction("brush.y.input", new BrushInput(1));
		
		inInterpreter.AddInstruction("brush.radius.increment", new BrushIncrement(2));
		inInterpreter.AddInstruction("brush.radius.decrement", new BrushDecrement(2));
		inInterpreter.AddInstruction("brush.radius.input", new BrushInput(2));
		
		inInterpreter.AddInstruction("brush.red.increment", new BrushIncrement(3));
		inInterpreter.AddInstruction("brush.red.decrement", new BrushDecrement(3));
		inInterpreter.AddInstruction("brush.red.input", new BrushInput(3));
		
		inInterpreter.AddInstruction("brush.green.increment", new BrushIncrement(4));
		inInterpreter.AddInstruction("brush.green.decrement", new BrushDecrement(4));
		inInterpreter.AddInstruction("brush.green.input", new BrushInput(4));
		
		inInterpreter.AddInstruction("brush.blue.increment", new BrushIncrement(5));
		inInterpreter.AddInstruction("brush.blue.decrement", new BrushDecrement(5));
		inInterpreter.AddInstruction("brush.blue.input", new BrushInput(5));
		
	}

	/**
	 * This method replaces GA's normal Run() method, since this problem
	 * requires human evaluations. It will run until an evaluation from the
	 * human is required, at which point it will return. The individual that
	 * needs to be evaluated will be stored as currentIndividualIndex.
	 * 
	 * Note: This method should be run once at the beginning of evolution
	 * to prime the individual to be tested.
	 * 
	 * @param inFitness The fitness of the previous individual. If no previous
	 *                  individual, feel free to call with bogus value (or 0).
	 * @return True if GA is finished, false otherwise (likely because a human
	 *         evaluation is required, or just because the GA is not yet
	 *         finished).
	 * @throws Exception
	 */
	public boolean RunUntilHumanEvaluation(float inFitness) throws Exception{
		
		if(newGeneration){
			BeginGeneration();
		}
		
		newGeneration = HumanEvaluate(inFitness);
		
		if(!newGeneration){
			return false;
		}
		
		Reproduce();
		EndGeneration();
		
		Print(Report());

		Checkpoint();
		System.gc();
		
		_currentPopulation = (_currentPopulation == 0 ? 1 : 0);
		_generationCount++;
		
		if(Terminate()){
			// Since this value was changed after termination conditions were
			// set, revert back to previous state.
			_currentPopulation = (_currentPopulation == 0 ? 1 : 0);
		
			Print(FinalReport());
			return true;
		}
		
		// We need to re-call this method, since a new individual from a new
		// generation needs to be fetched. We can pass a bogus fitness, since
		// it won't be used (since the individual will be the first of the
		// generation).
		return RunUntilHumanEvaluation(-2999);
	}
	
	protected void BeginGeneration() throws Exception {
		totalFitness = 0;
		_bestMeanFitness = Float.MAX_VALUE;
		
		currentIndividualIndex = 0;
		
		super.BeginGeneration();
	}
	
	protected void EndGeneration(){
		_populationMeanFitness = totalFitness / _populations[_currentPopulation].length;
		bestIndividualPreviousGeneration = _bestIndividual;
		super.EndGeneration();
	}
	
	/**
	 * Terminate if we have gone past the maximum generations or success.
	 */
	public boolean Terminate() {
		return (_generationCount >= _maxGenerations || Success());
	}

	/**
	 * As for now, we will never return success.
	 */
	protected boolean Success() {
		return false;
	}

	/**
	 * Gets the next individual ready to be evaluated by the human user.
	 * 
	 * @return true if the current generation is finished being evaluated.
	 */
	protected boolean HumanEvaluate(float inFitness) {
		if (currentIndividualIndex != 0) {
			// If here, the previous individual's fitness needs to be set based
			// on the fitness passed to this method. Also update other
			// variables.
			_averageSize += ((PushGPIndividual) currentIndividual)._program
					.programsize();

			currentIndividual.SetFitness(inFitness);
			ArrayList<Float> errors = new ArrayList<Float>();
			errors.add(inFitness);
			currentIndividual.SetErrors(errors);
			
			// Other system variables that need to be updated.
			totalFitness += inFitness;
			if (currentIndividual.GetFitness() < _bestMeanFitness) {
				_bestMeanFitness = currentIndividual.GetFitness();
				_bestIndividual = currentIndividualIndex - 1;
				_bestErrors = currentIndividual.GetErrors();
				_bestSize = ((PushGPIndividual) currentIndividual)._program.programsize();
			}
		}

		if(currentIndividualIndex >= _populations[_currentPopulation].length){
			// If here, we are done with this generation
			return true;
		}
		
		currentIndividual = _populations[_currentPopulation][currentIndividualIndex];
		currentIndividualIndex++;

		return false;
	}

	/**
	 * Uses the current individual to get the brush attributes for the next time
	 * step. Note that the returned brush is not constrained, in that some
	 * of its values may be out of the available ranges (e.g. x = -4)
	 * 
	 * @return new brush attributes
	 */
	public BrushAttributes getNextBrush(BrushAttributes inBrush) {
		BrushAttributes nextBrush = new BrushAttributes();
		PushGPIndividual i = (PushGPIndividual) currentIndividual;
		
		// Prepare the interpreter
		_interpreter.ClearStacks();
		
		intStack attributeStack = (intStack) _interpreter.getCustomStack(attributeStackIndex);
		intStack integerStack = _interpreter.intStack();
		ObjectStack inputStack = _interpreter.inputStack();
		
		// Push things on stacks
		attributeStack.push(inBrush.x);
		attributeStack.push(inBrush.y);
		attributeStack.push(inBrush.radius);
		attributeStack.push(inBrush.red);
		attributeStack.push(inBrush.green);
		attributeStack.push(inBrush.blue);
		
		integerStack.push(inBrush.t);
		inputStack.push(inBrush.t);
		
		// Execute the individual
		_interpreter.Execute(i._program, _executionLimit);
		
		// Retrieve the attributes
		nextBrush.x = attributeStack.peek(0);
		nextBrush.y = attributeStack.peek(1);
		nextBrush.radius = attributeStack.peek(2);
		nextBrush.red = attributeStack.peek(3);
		nextBrush.green = attributeStack.peek(4);
		nextBrush.blue = attributeStack.peek(5);

		return nextBrush;
	}

	@Override
	public float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
			Object inOutput) {
		// This isn't being used at this time
		return 0;
	}
	
	protected String Report() {
		String report = "\n";
		report += ";;--------------------------------------------------------;;\n";
		report += ";;---------------";
		report += " Report for Generation " + _generationCount + " ";
		
		if(_generationCount < 10)
			report += "-";
		if(_generationCount < 100)
			report += "-";
		if(_generationCount < 1000)
			report += "-";
		
		report += "-------------;;\n";
		report += ";;--------------------------------------------------------;;\n";

		if (Double.isInfinite(_populationMeanFitness))
			_populationMeanFitness = Double.MAX_VALUE;

		report += ";; Best Program:\n  "
				+ _populations[_currentPopulation][_bestIndividual] + "\n\n";

		report += ";; Best Program Fitness (higher is better, out of 100): "
				+ (100 - _bestMeanFitness) + "\n";
		report += ";; Best Program Size: " + _bestSize + "\n\n";

		report += ";; Mean Fitness: " + _populationMeanFitness + "\n";
		report += ";; Mean Program Size: " + _averageSize + "\n";

		report += ";; Total Timesteps Thus Far: "
				+ _interpreter.GetEvaluationExecutions() + "\n";
		String mem = String
				.valueOf(Runtime.getRuntime().totalMemory() / 10000000.0f);
		report += ";; Memory usage: " + mem + "\n\n";

		return report;
	}
	
	
	class BrushIncrement extends Instruction {
		private static final long serialVersionUID = 1L;
		
		int _attribute;
		
		BrushIncrement(int inAttribute){
			_attribute = inAttribute;
		}
		
		public void Execute(Interpreter inInterpreter) {
			intStack attributeStack = (intStack) inInterpreter
					.getCustomStack(attributeStackIndex);
			attributeStack.set(_attribute, attributeStack.peek(_attribute) + 1);
		}	
	}
	
	class BrushDecrement extends Instruction {
		private static final long serialVersionUID = 1L;
		
		int _attribute;
		
		BrushDecrement(int inAttribute){
			_attribute = inAttribute;
		}
		
		public void Execute(Interpreter inInterpreter) {
			intStack attributeStack = (intStack) inInterpreter
					.getCustomStack(attributeStackIndex);
			attributeStack.set(_attribute, attributeStack.peek(_attribute) - 1);
		}	
	}
	
	class BrushInput extends Instruction {
		private static final long serialVersionUID = 1L;
		
		int _attribute;
		
		BrushInput(int inAttribute){
			_attribute = inAttribute;
		}
		
		public void Execute(Interpreter inInterpreter) {
			intStack attributeStack = (intStack) inInterpreter
					.getCustomStack(attributeStackIndex);
			intStack integerStack = inInterpreter.intStack();
			int val = attributeStack.peek(_attribute);
			integerStack.push(val);
		}	
	}
	
}
