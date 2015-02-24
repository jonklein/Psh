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

/**
 * A Push program.
 */

public class Program extends ObjectStack implements Serializable {
	private static final long serialVersionUID = 1L;

	Interpreter _interpreter = null;

	public Interpreter getInterpreter() {
		return _interpreter;
	}

	public void setInterpreter(Interpreter _interpreter) {
		this._interpreter = _interpreter;
	}

	/**
	 * Constructs an empty Program.
	 * 
	 * @deprecated
	 */

	public Program() {
	}

	/**
	 * Constructs an empty Program with an associated Interpreter.
	 */

	public Program(Interpreter inInterpreter) {
		_interpreter = inInterpreter;
	}

	/**
	 * Constructs a copy of an existing Program.
	 * 
	 * @param inOther
	 *            The Push program to copy.
	 */

	public Program(Program inOther) {
		inOther.CopyTo(this);
		_interpreter = inOther._interpreter;
	}

	/**
	 * Constructs a Push program by parsing a String.
	 * 
	 * @param inString
	 *            The Push program string to parse.
	 * 
	 */

	public Program(String inString) throws Exception {
		Parse(inString);
	}

	/**
	 * Constructs a Push program by parsing a String.
	 * 
	 * @param inString
	 *            The Push program string to parse.
	 */

	public Program(Interpreter _interpreter, String inString) throws Exception {
		this._interpreter = _interpreter;
		Parse(inString);
	}

	/**
	 * Sets this program to the parsed program string.
	 * 
	 * @param inString
	 *            The Push program string to parse.
	 * @return The point size of the new program.
	 */

	public int Parse(String inString) throws Exception {
		clear();

		inString = inString.replace("(", " ( ");
		inString = inString.replace(")", " ) ");

		String tokens[] = inString.split("\\s+");

		Parse(tokens, 0);

		return programsize();
	}

	private int Parse(String inTokens[], int inStart) throws Exception {
		boolean first = (inStart == 0);

		for (int n = inStart; n < inTokens.length; n++) {
			String token = inTokens[n];

			if (!token.equals("")) {
				if (token.equals("(")) {

					// Found an open paren -- begin a recursive Parse, though
					// the very first
					// token in the list is a special case -- no need to create
					// a sub-program

					if (!first) {
						Program p = new Program(_interpreter);

						n = p.Parse(inTokens, n + 1);

						push(p);
					}
				} else if (token.equals(")")) {
					// End of the program -- return the advanced token index to
					// the caller

					return n;

				} else if (Character.isLetter(token.charAt(0))) {

					push(token);
					
					// This makes printing stacks very ugly. For now, will store
					// program instructions as strings, as was done before.
					/*
					Instruction i = _interpreter._instructions.get(token);
					if (i != null)
						push(i);
					else
						push(token);
					*/

				} else {
					Object number;

					if (token.indexOf('.') != -1)
						number = Float.parseFloat(token);
					else
						number = Integer.parseInt(token);

					push(number);
				}

				first = false;
			}
		}

		// If we're here, there was no closing brace for one of the programs

		throw new Exception("no closing brace found for program");
	}

	/**
	 * Returns the size of the program and all subprograms.
	 * 
	 * @return The size of the program.
	 */
	public int programsize() {
		int size = _size;

		for (int n = 0; n < _size; n++) {
			Object o = _stack[n];
			if (o instanceof Program)
				size += ((Program) o).programsize();
		}

		return size;
	}

	/**
	 * Returns the size of a subtree.
	 * 
	 * @param inIndex
	 *            The index of the requested subtree.
	 * @return The size of the subtree.
	 */

	public int SubtreeSize(int inIndex) {
		Object sub = Subtree(inIndex);

		if (sub == null)
			return 0;

		if (sub instanceof Program)
			return ((Program) sub).programsize();

		return 1;
	}

	/**
	 * Returns a subtree of the program.
	 * 
	 * @param inIndex
	 *            The index of the requested subtree.
	 * @return The program subtree.
	 */

	public Object Subtree(int inIndex) {
		if (inIndex < _size) {
			return _stack[inIndex];
		} else {
			int startIndex = _size;

			for (int n = 0; n < _size; n++) {
				Object o = _stack[n];

				if (o instanceof Program) {
					Program sub = (Program) o;
					int length = sub.programsize();

					if (inIndex - startIndex < length)
						return sub.Subtree(inIndex - startIndex);

					startIndex += length;
				}
			}
		}

		return null;
	}

	/**
	 * Replaces a subtree of this Program with a new object.
	 * 
	 * @param inIndex
	 *            The index of the subtree to replace.
	 * @param inReplacement
	 *            The replacement for the subtree
	 * @return True if a replacement was made (the index was valid).
	 */

	public boolean ReplaceSubtree(int inIndex, Object inReplacement) {
		if (inIndex < _size) {
			_stack[inIndex] = cloneforprogram(inReplacement);
			return true;
		} else {
			int startIndex = _size;

			for (int n = 0; n < _size; n++) {
				Object o = _stack[n];

				if (o instanceof Program) {
					Program sub = (Program) o;
					int length = sub.programsize();

					if (inIndex - startIndex < length)
						return sub.ReplaceSubtree(inIndex - startIndex,
								inReplacement);

					startIndex += length;
				}
			}
		}

		return false;
	}

	public void Flatten(int inIndex) {
		if (inIndex < _size) {
			// If here, the index to be flattened is in this program. So, push
			// the rest of the program onto a new program, and replace this with
			// that new program.

			Program replacement = new Program(this);
			clear();

			for (int i = 0; i < replacement._size; i++) {
				if (inIndex == i) {

					if (replacement._stack[i] instanceof Program) {
						Program p = (Program) replacement._stack[i];
						for (int j = 0; j < p._size; j++)
							this.push(p._stack[j]);
					} else {
						this.push(replacement._stack[i]);
					}
				} else {
					this.push(replacement._stack[i]);
				}
			}
		} else {
			int startIndex = _size;

			for (int n = 0; n < _size; n++) {
				Object o = _stack[n];

				if (o instanceof Program) {
					Program sub = (Program) o;
					int length = sub.programsize();

					if (inIndex - startIndex < length) {
						sub.Flatten(inIndex - startIndex);
						break;
					}

					startIndex += length;
				}
			}
		}
	}

	/**
	 * Copies this program to another.
	 * 
	 * @param inOther
	 *            The program to receive the copy of this program
	 */

	public void CopyTo(Program inOther) {
		for (int n = 0; n < _size; n++)
			inOther.push(_stack[n]);
	}

	public String toString() {
		String result = "(";

		for (int n = 0; n < _size; n++) {
			if (result.charAt(result.length() - 1) == '(')
				result += _stack[n];
			else
				result += " " + _stack[n];
		}

		result += ")";

		return result;
	}

	/**
	 * Creates a copy of an object suitable for adding to a Push Program. Java's
	 * clone() is unfortunately useless for this task.
	 */

	private Object cloneforprogram(Object inObject) {
		// Java clone() is useless :(

		if (inObject instanceof String)
			return new String((String) inObject);

		if (inObject instanceof Integer)
			return new Integer((Integer) inObject);

		if (inObject instanceof Float)
			return new Float((Float) inObject);

		if (inObject instanceof Program)
			return new Program((Program) inObject);

		if (inObject instanceof Instruction)
			return inObject; // no need to copy; instructions are singletons

		return null;
	}

}
