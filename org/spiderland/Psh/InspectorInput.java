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

import java.io.*;

/**
 * A utility class to help read PshInspector input files.
 */

public class InspectorInput {

	Program _program;
	int _executionLimit;
	Interpreter _interpreter;

	/**
	 * Constructs an InspectorInput from a filename string
	 * 
	 * @param inFilename
	 *            The file to input from.
	 */
	public InspectorInput(String inFilename) throws Exception {
		InitInspectorInput(new File(inFilename));
	}

	/**
	 * Constructs an InspectorInput from a filename string
	 * 
	 * @param inFile
	 *            The file to input from.
	 */
	public InspectorInput(File inFile) throws Exception {
		InitInspectorInput(inFile);
	}

	/**
	 * Initializes an InspectorInput
	 * 
	 * @param inFile
	 *            The file to input from.
	 */
	private void InitInspectorInput(File inFile) throws Exception {
		_interpreter = new Interpreter();

		// Read fileString
		String fileString = Params.ReadFileString(inFile);

		// Get programString
		int indexNewline = fileString.indexOf("\n");
		String programString = fileString.substring(0, indexNewline);
		fileString = fileString.substring(indexNewline + 1);

		// Get _executionLimit
		indexNewline = fileString.indexOf("\n");
		if (indexNewline != -1) {
			_executionLimit = Integer.parseInt(fileString.substring(0,
					indexNewline));
			fileString = fileString.substring(indexNewline + 1);
		} else {
			// If here, no inputs to be pushed were included
			_executionLimit = Integer.parseInt(fileString);
			fileString = "";
		}

		// Get inputs and push them onto correct stacks. If fileString = ""
		// at this point, then can still do the following with correct result.
		indexNewline = fileString.indexOf("\n");
		if (indexNewline != -1)
			fileString = fileString.substring(0, indexNewline);

		// Check for input.inN instructions
		checkForInputIn(programString);

		// Parse the inputs and load them into the interpreter
		parseAndLoadInputs(fileString);

		// Load the program
		_program = new Program(_interpreter, programString);
		_interpreter.LoadProgram(_program); // Initializes program
	}

	/**
	 * Returns the initialized interpreter
	 * 
	 * @return The initialized interpreter
	 */
	public Interpreter getInterpreter() {
		return _interpreter;
	}

	public Program getProgram() {
		return _program;
	}
	
	/**
	 * Returns the execution limit
	 * 
	 * @return The execution limit
	 */
	public int getExecutionLimit() {
		return _executionLimit;
	}

	private void parseAndLoadInputs(String inputs) throws Exception {
		String inputTokens[] = inputs.split("\\s+");

		for (int i = 0; i < inputTokens.length; i++) {
			String token = inputTokens[i];

			if (token.equals("")) {
				continue;
			} else if (token.equals("true")) {
				_interpreter.boolStack().push(true);
				_interpreter.inputStack().push(true);
			} else if (token.equals("false")) {
				_interpreter.boolStack().push(false);
				_interpreter.inputStack().push(false);
			} else if (token.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
				if (token.indexOf('.') != -1) {
					_interpreter.floatStack().push(Float.parseFloat(token));
					_interpreter.inputStack().push(Float.parseFloat(token));
				} else {
					_interpreter.intStack().push(Integer.parseInt(token));
					_interpreter.inputStack().push(Integer.parseInt(token));
				}
			} else {
				throw new Exception(
						"Inputs must be of type int, float, or boolean. \""
								+ token + "\" is none of these.");
			}
		}
	}

	private void checkForInputIn(String programString) {
		String added = "";
		String numstr = "";
		int index = 0;
		int numindex = 0;
		int spaceindex = 0;
		int parenindex = 0;
		int endindex = 0;

		while (true) {

			index = programString.indexOf("input.in");
			if (index == -1)
				break;

			// System.out.println(programString + "    " + index);

			numindex = index + 8;
			if (!Character.isDigit(programString.charAt(numindex))) {
				programString = programString.substring(numindex);
				continue;
			}

			spaceindex = programString.indexOf(' ', numindex);
			parenindex = programString.indexOf(')', numindex);
			if (spaceindex == -1)
				endindex = parenindex;
			else if (parenindex == -1)
				endindex = spaceindex;
			else
				endindex = Math.min(spaceindex, parenindex);

			numstr = programString.substring(numindex, endindex);

			// Check for doubles in added
			if (added.indexOf(" " + numstr + " ") == -1) {
				added = added + " " + numstr + " ";
				_interpreter.AddInstruction("input.in" + numstr, new InputInN(
						Integer.parseInt(numstr)));
			}

			programString = programString.substring(numindex);
		}

	}

}