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

import java.io.Serializable;
import java.util.*;

/**
 * The Push language interpreter.
 */

public class Interpreter implements Serializable {
	private static final long serialVersionUID = 1L;

	protected HashMap<String, Instruction> _instructions = new HashMap<String, Instruction>();

	// All generators

	protected HashMap<String, AtomGenerator> _generators = new HashMap<String, AtomGenerator>();
	protected ArrayList<AtomGenerator> _randomGenerators = new ArrayList<AtomGenerator>();

	protected intStack _intStack;
	protected floatStack _floatStack;
	protected booleanStack _boolStack;
	protected ObjectStack _codeStack;
	protected ObjectStack _nameStack;
	protected ObjectStack _execStack = new ObjectStack();

	protected ObjectStack _inputStack = new ObjectStack(); // Since the
	// _inputStack will not change after initialization, it will not need
	// a frame stack.

	protected ObjectStack _intFrameStack = new ObjectStack();
	protected ObjectStack _floatFrameStack = new ObjectStack();
	protected ObjectStack _boolFrameStack = new ObjectStack();
	protected ObjectStack _codeFrameStack = new ObjectStack();
	protected ObjectStack _nameFrameStack = new ObjectStack();

	protected boolean _useFrames;

	protected int _effort;

	protected int _maxRandomInt;
	protected int _minRandomInt;
	protected int _randomIntResolution;

	protected float _maxRandomFloat;
	protected float _minRandomFloat;
	protected float _randomFloatResolution;

	protected Random _RNG = new Random();

	protected InputPusher _inputPusher = new InputPusher();
	
	private static long _evaluationExecutions = 0;

	public Interpreter() {
		
		_useFrames = false;
		PushStacks();

		DefineInstruction("integer.+", new IntegerAdd());
		DefineInstruction("integer.-", new IntegerSub());
		DefineInstruction("integer./", new IntegerDiv());
		DefineInstruction("integer.%", new IntegerMod());
		DefineInstruction("integer.*", new IntegerMul());
		DefineInstruction("integer.=", new IntegerEquals());
		DefineInstruction("integer.>", new IntegerGreaterThan());
		DefineInstruction("integer.<", new IntegerLessThan());
		DefineInstruction("integer.abs", new IntegerAbs());
		DefineInstruction("integer.neg", new IntegerNeg());

		DefineInstruction("float.+", new FloatAdd());
		DefineInstruction("float.-", new FloatSub());
		DefineInstruction("float./", new FloatDiv());
		DefineInstruction("float.%", new FloatMod());
		DefineInstruction("float.*", new FloatMul());
		DefineInstruction("float.=", new FloatEquals());
		DefineInstruction("float.>", new FloatGreaterThan());
		DefineInstruction("float.<", new FloatLessThan());
		DefineInstruction("float.min", new FloatMin());
		DefineInstruction("float.max", new FloatMin());
		DefineInstruction("float.sin", new FloatSin());
		DefineInstruction("float.cos", new FloatCos());
		DefineInstruction("float.tan", new FloatTan());
		DefineInstruction("float.abs", new FloatAbs());
		DefineInstruction("float.neg", new FloatNeg());

		DefineInstruction("boolean.=", new BoolEquals());
		DefineInstruction("boolean.not", new BoolNot());
		DefineInstruction("boolean.and", new BoolAnd());
		DefineInstruction("boolean.or", new BoolOr());
		DefineInstruction("boolean.xor", new BoolXor());

		DefineInstruction("code.quote", new Quote());

		DefineInstruction("exec.do*times", new ExecDoTimes(this));
		DefineInstruction("code.do*times", new CodeDoTimes(this));
		DefineInstruction("exec.do*count", new ExecDoCount(this));
		DefineInstruction("code.do*count", new CodeDoCount(this));
		DefineInstruction("exec.do*range", new ExecDoRange(this));
		DefineInstruction("code.do*range", new CodeDoRange(this));
		DefineInstruction("code.=", new ObjectEquals(_codeStack));
		DefineInstruction("exec.=", new ObjectEquals(_execStack));
		DefineInstruction("code.if", new If(_codeStack));
		DefineInstruction("exec.if", new If(_execStack));

		DefineInstruction("true", new BooleanConstant(true));
		DefineInstruction("false", new BooleanConstant(false));

		DefineInstruction("input.index", new InputIndex(_inputStack));
		DefineInstruction("input.inall", new InputInAll(_inputStack));
		DefineInstruction("input.inallrev", new InputInRev(_inputStack));
		DefineInstruction("input.stackdepth", new Depth(_inputStack));

		DefineStackInstructions("integer", _intStack);
		DefineStackInstructions("float", _floatStack);
		DefineStackInstructions("boolean", _boolStack);
		DefineStackInstructions("name", _nameStack);
		DefineStackInstructions("code", _codeStack);
		DefineStackInstructions("exec", _execStack);

		DefineInstruction("frame.push", new PushFrame());
		DefineInstruction("frame.pop", new PopFrame());

		_generators.put("float.erc", new FloatAtomGenerator());
		_generators.put("integer.erc", new IntAtomGenerator());
	}

	/**
	 * Enables experimental Push "frames"
	 * 
	 * When frames are enabled, each Push subtree is given a fresh set of stacks
	 * (a "frame") when it executes. When a frame is pushed, the top value from
	 * each stack is passed to the new frame, and likewise when the frame pops,
	 * allowing for input arguments and return values.
	 */

	public void SetUseFrames(boolean inUseFrames) {
		_useFrames = inUseFrames;
	}

	/**
	 * Defines the instruction set used for random code generation in this Push
	 * interpreter.
	 * 
	 * @param inInstructionList
	 *            A program consisting of a list of string instruction names to
	 *            be placed in the instruction set.
	 */

	public void SetInstructions(Program inInstructionList)
			throws RuntimeException {
		_randomGenerators.clear();

		for (int n = 0; n < inInstructionList.size(); n++) {
			Object o = inInstructionList.peek(n);
			String name = null;

			if (o instanceof Instruction) {
				String keys[] = _instructions.keySet().toArray(
						new String[_instructions.size()]);

				for (String key : keys)
					if (_instructions.get(key) == o) {
						name = key;
						break;
					}
			} else if (o instanceof String)
				name = (String) o;
			else
				throw new RuntimeException(
						"Instruction list must contain a list of Push instruction names only");

			// Check for registered
			if (name.indexOf("registered.") == 0) {
				String registeredType = name.substring(11);

				if (!registeredType.equals("integer")
						&& !registeredType.equals("float")
						&& !registeredType.equals("boolean")
						&& !registeredType.equals("exec")
						&& !registeredType.equals("code")
						&& !registeredType.equals("name")
						&& !registeredType.equals("input")
						&& !registeredType.equals("frame")) {
					System.err.println("Unknown instruction \"" + name
							+ "\" in instruction set");
				} else {
					// Legal stack type, so add all generators matching
					// registeredType to _randomGenerators.
					Object keys[] = _instructions.keySet().toArray();

					for (int i = 0; i < keys.length; i++) {
						String key = (String) keys[i];
						if (key.indexOf(registeredType) == 0) {
							AtomGenerator g = _generators.get(key);
							_randomGenerators.add(g);
						}
					}

					if (registeredType.equals("boolean")) {
						AtomGenerator t = _generators.get("true");
						_randomGenerators.add(t);
						AtomGenerator f = _generators.get("false");
						_randomGenerators.add(f);
					}
					if (registeredType.equals("integer")) {
						AtomGenerator g = _generators.get("integer.erc");
						_randomGenerators.add(g);
					}
					if (registeredType.equals("float")) {
						AtomGenerator g = _generators.get("float.erc");
						_randomGenerators.add(g);
					}

				}
			} else if (name.indexOf("input.makeinputs") == 0) {
				String strnum = name.substring(16);
				int num = Integer.parseInt(strnum);

				for (int i = 0; i < num; i++) {
					DefineInstruction("input.in" + i, new InputInN(i));
					AtomGenerator g = _generators.get("input.in" + i);
					_randomGenerators.add(g);
				}
			} else {
				AtomGenerator g = _generators.get(name);

				if (g == null) {
					System.out.println("Unknown instruction \"" + name
							+ "\" in instruction set");
				} else {
					_randomGenerators.add(g);
				}
			}
		}
	}

	public void AddInstruction(String inName, Instruction inInstruction) {
		DefineInstruction(inName, inInstruction);
		_randomGenerators.add(new InstructionAtomGenerator(inName));
	}

	protected void DefineInstruction(String inName, Instruction inInstruction) {
		_instructions.put(inName, inInstruction);
		_generators.put(inName, new InstructionAtomGenerator(inName));
	}

	protected void DefineStackInstructions(String inTypeName, Stack inStack) {
		DefineInstruction(inTypeName + ".pop", new Pop(inStack));
		DefineInstruction(inTypeName + ".swap", new Swap(inStack));
		DefineInstruction(inTypeName + ".rot", new Rot(inStack));
		DefineInstruction(inTypeName + ".flush", new Flush(inStack));
		DefineInstruction(inTypeName + ".dup", new Dup(inStack));
		DefineInstruction(inTypeName + ".stackdepth", new Depth(inStack));
	}
	
	/**
	 * Sets the parameters for the ERCs.
	 * 
	 * @param minRandomInt
	 * @param maxRandomInt
	 * @param randomIntResolution
	 * @param minRandomFloat
	 * @param maxRandomFloat
	 * @param randomFloatResolution
	 */
	public void SetRandomParameters(int minRandomInt, int maxRandomInt,
			int randomIntResolution, float minRandomFloat,
			float maxRandomFloat, float randomFloatResolution) {
		
		_minRandomInt = minRandomInt;
		_maxRandomInt = maxRandomInt;
		_randomIntResolution = randomIntResolution;

		_minRandomFloat = minRandomFloat;
		_maxRandomFloat = maxRandomFloat;
		_randomFloatResolution = randomFloatResolution;
	}

	/**
	 * Executes a Push program with no execution limit.
	 * 
	 * @return The number of instructions executed.
	 */

	public int Execute(Program inProgram) {
		return Execute(inProgram, -1);
	}

	/**
	 * Executes a Push program with a given instruction limit.
	 * 
	 * @param inMaxSteps
	 *            The maximum number of instructions allowed to be executed.
	 * @return The number of instructions executed.
	 */

	public int Execute(Program inProgram, int inMaxSteps) {
		_evaluationExecutions++;
		LoadProgram(inProgram); // Initializes program
		return Step(inMaxSteps);
	}

	/**
	 * Loads a Push program into the interpreter's exec and code stacks.
	 * 
	 * @param inProgram
	 *            The program to load.
	 */

	public void LoadProgram(Program inProgram) {
		_codeStack.push(inProgram);
		_execStack.push(inProgram);
	}

	/**
	 * Steps a Push interpreter forward with a given instruction limit.
	 * 
	 * This method assumes that the intepreter is already setup with an active
	 * program (typically using \ref Execute).
	 * 
	 * @param inMaxSteps
	 *            The maximum number of instructions allowed to be executed.
	 * @return The number of instructions executed.
	 */

	public int Step(int inMaxSteps) {
		int executed = 0;
		while (inMaxSteps != 0 && _execStack.size() > 0) {
			ExecuteInstruction(_execStack.pop());
			inMaxSteps--;
			executed++;
		}

		_effort += executed;

		return executed;
	}

	public int ExecuteInstruction(Object inObject) {

		if (inObject instanceof Program) {
			Program p = (Program) inObject;

			if (_useFrames) {
				_execStack.push("frame.pop");
			}

			p.PushAllReverse(_execStack);

			if (_useFrames) {
				_execStack.push("frame.push");
			}

			return 0;
		}

		if (inObject instanceof Integer) {
			_intStack.push((Integer) inObject);
			return 0;
		}

		if (inObject instanceof Number) {
			_floatStack.push(((Number) inObject).floatValue());
			return 0;
		}

		if (inObject instanceof Instruction) {
			((Instruction) inObject).Execute(this);
			return 0;
		}

		if (inObject instanceof String) {
			Instruction i = _instructions.get(inObject);

			if (i != null) {
				i.Execute(this);
			} else {
				_nameStack.push(inObject);
			}

			return 0;
		}

		return -1;
	}

	/**
	 * Fetch the active integer stack.
	 */

	public intStack intStack() {
		return _intStack;
	}

	/**
	 * Fetch the active float stack.
	 */

	public floatStack floatStack() {
		return _floatStack;
	}

	/**
	 * Fetch the active exec stack.
	 */

	public ObjectStack execStack() {
		return _execStack;
	}

	/**
	 * Fetch the active code stack.
	 */

	public ObjectStack codeStack() {
		return _codeStack;
	}

	/**
	 * Fetch the active bool stack.
	 */

	public booleanStack boolStack() {
		return _boolStack;
	}

	/**
	 * Fetch the active name stack.
	 */

	public ObjectStack nameStack() {
		return _nameStack;
	}

	/**
	 * Fetch the active input stack.
	 */

	public ObjectStack inputStack() {
		return _inputStack;
	}

	protected void AssignStacksFromFrame() {
		_floatStack = (floatStack) _floatFrameStack.top();
		_intStack = (intStack) _intFrameStack.top();
		_boolStack = (booleanStack) _boolFrameStack.top();
		_codeStack = (ObjectStack) _codeFrameStack.top();
		_nameStack = (ObjectStack) _nameFrameStack.top();
	}

	public void PushStacks() {
		_floatFrameStack.push(new floatStack());
		_intFrameStack.push(new intStack());
		_boolFrameStack.push(new booleanStack());
		_codeFrameStack.push(new ObjectStack());
		_nameFrameStack.push(new ObjectStack());

		AssignStacksFromFrame();
	}

	public void PopStacks() {
		_floatFrameStack.pop();
		_intFrameStack.pop();
		_boolFrameStack.pop();
		_codeFrameStack.pop();
		_nameFrameStack.pop();

		AssignStacksFromFrame();
	}

	public void PushFrame() {
		if (_useFrames) {
			boolean boolTop = _boolStack.top();
			int intTop = _intStack.top();
			float floatTop = _floatStack.top();
			Object nameTop = _nameStack.top();
			Object codeTop = _codeStack.top();

			PushStacks();

			_floatStack.push(floatTop);
			_intStack.push(intTop);
			_boolStack.push(boolTop);

			if (nameTop != null)
				_nameStack.push(nameTop);
			if (codeTop != null)
				_codeStack.push(codeTop);
		}
	}

	public void PopFrame() {
		if (_useFrames) {
			boolean boolTop = _boolStack.top();
			int intTop = _intStack.top();
			float floatTop = _floatStack.top();
			Object nameTop = _nameStack.top();
			Object codeTop = _codeStack.top();

			PopStacks();

			_floatStack.push(floatTop);
			_intStack.push(intTop);
			_boolStack.push(boolTop);

			if (nameTop != null)
				_nameStack.push(nameTop);
			if (codeTop != null)
				_codeStack.push(codeTop);
		}
	}

	/**
	 * Prints out the current stack states.
	 */

	public void PrintStacks() {
		System.out.println(this);
	}

	/**
	 * Returns a string containing the current Interpreter stack states.
	 */

	public String toString() {
		String result = "";
		result += "exec stack: " + _execStack + "\n";
		result += "code stack: " + _codeStack + "\n";
		result += "int stack: " + _intStack + "\n";
		result += "float stack: " + _floatStack + "\n";
		result += "boolean stack: " + _boolStack + "\n";
		result += "name stack: " + _nameStack + "\n";
		result += "input stack: " + _inputStack + "\n";

		return result;
	}

	/**
	 * Resets the Push interpreter state by clearing all of the stacks.
	 */

	public void ClearStacks() {
		_intStack.clear();
		_floatStack.clear();
		_execStack.clear();
		_nameStack.clear();
		_boolStack.clear();
		_codeStack.clear();
		_inputStack.clear();
	}

	/**
	 * Returns a string list of all instructions enabled in the interpreter.
	 */

	public String GetInstructionString() {
		Object keys[] = _instructions.keySet().toArray();
		Arrays.sort(keys);
		String list = "";

		for (int n = 0; n < keys.length; n++)
			list += keys[n] + " ";

		return list;
	}

	/**
	 * Returns the Instruction whose name is given in instr.
	 * 
	 * @param instr
	 * @return the Instruction or null if no such Instruction.
	 */
	public Instruction GetInstruction(String instr) {
		return _instructions.get(instr);
	}

	/**
	 * Returns the number of evaluation executions so far this run.
	 * 
	 * @return The number of evaluation executions during this run.
	 */
	public static long GetEvaluationExecutions(){
		return _evaluationExecutions;
	}
	
	public InputPusher getInputPusher() {
		return _inputPusher;
	}

	public void setInputPusher(InputPusher _inputPusher) {
		this._inputPusher = _inputPusher;
	}
	
	/**
	 * Generates a single random Push atom (instruction name, integer, float,
	 * etc) for use in random code generation algorithms.
	 * 
	 * @return A random atom based on the interpreter's current active
	 *         instruction set.
	 */

	public Object RandomAtom() {
		int index = _RNG.nextInt(_randomGenerators.size());

		return _randomGenerators.get(index).Generate(this);
	}

	/**
	 * Generates a random Push program of a given size.
	 * 
	 * @param inSize
	 *            The requested size for the program to be generated.
	 * @return A random Push program of the given size.
	 */

	public Program RandomCode(int inSize) {
		Program p = new Program(this);

		List<Integer> distribution = RandomCodeDistribution(inSize - 1,
				inSize - 1);

		for (int i = 0; i < distribution.size(); i++) {
			int count = distribution.get(i);

			if (count == 1) {
				p.push(RandomAtom());
			} else {
				p.push(RandomCode(count));
			}
		}

		return p;
	}

	/**
	 * Generates a list specifying a size distribution to be used for random
	 * code.
	 * 
	 * Note: This method is called "decompose" in the lisp implementation.
	 * 
	 * @param inCount
	 *            The desired resulting program size.
	 * @param inMaxElements
	 *            The maxmimum number of elements at this level.
	 * @return A list of integers representing the size distribution.
	 */

	public List<Integer> RandomCodeDistribution(int inCount, int inMaxElements) {
		ArrayList<Integer> result = new ArrayList<Integer>();

		RandomCodeDistribution(result, inCount, inMaxElements);

		Collections.shuffle(result);

		return result;
	}

	/**
	 * The recursive worker function for the public RandomCodeDistribution.
	 * 
	 * @param ioList
	 *            The working list of distribution values to append to.
	 * @param inCount
	 *            The desired resulting program size.
	 * @param inMaxElements
	 *            The maxmimum number of elements at this level.
	 */

	private void RandomCodeDistribution(List<Integer> ioList, int inCount,
			int inMaxElements) {
		if (inCount < 1)
			return;

		int thisSize = inCount < 2 ? 1 : (_RNG.nextInt(inCount) + 1);

		ioList.add(thisSize);

		RandomCodeDistribution(ioList, inCount - thisSize, inMaxElements - 1);
	}

	abstract class AtomGenerator implements Serializable {
		private static final long serialVersionUID = 1L;

		abstract Object Generate(Interpreter inInterpreter);
	}

	private class InstructionAtomGenerator extends AtomGenerator {
		private static final long serialVersionUID = 1L;

		InstructionAtomGenerator(String inInstructionName) {
			_instruction = inInstructionName;
		}

		Object Generate(Interpreter inInterpreter) {
			return _instruction;
		}

		String _instruction;
	}

	private class FloatAtomGenerator extends AtomGenerator {
		private static final long serialVersionUID = 1L;

		Object Generate(Interpreter inInterpreter) {
			float r = _RNG.nextFloat() * (_maxRandomFloat - _minRandomFloat);

			r -= (r % _randomFloatResolution);

			return r + _minRandomFloat;
		}
	}

	private class IntAtomGenerator extends AtomGenerator {
		private static final long serialVersionUID = 1L;

		Object Generate(Interpreter inInterpreter) {
			int r = _RNG.nextInt(_maxRandomInt - _minRandomInt);

			r -= (r % _randomIntResolution);

			return r + _minRandomInt;
		}
	}

}
