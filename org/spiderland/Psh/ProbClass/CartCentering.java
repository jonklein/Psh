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
import org.spiderland.Psh.ObjectStack;
import org.spiderland.Psh.Program;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;
import org.spiderland.Psh.booleanStack;
import org.spiderland.Psh.floatStack;

/**
 * A sample problem class for testing the cart centering problem. This solves
 * the cart centering problem as described in John Koza's Genetic Programming,
 * chapter 7.1. In this problem, a cart is placed on a 1-dimensional,
 * frictionless track. At every time, the cart has a position and velocity on
 * the track. The problem is to stop the cart at the origin (within reasonable
 * approximations) by applying a fixed-magnitude force to accelerate the cart
 * in the forward or backward direction.
 * 
 * Note: Cart centering does not yet support test case generators.
 */
public class CartCentering extends PushGP {
	private static final long serialVersionUID = 1L;

	protected void InitFromParameters() throws Exception {
		super.InitFromParameters();

		String cases = GetParam("test-cases");

		Program caselist = new Program(_interpreter, cases);

		for (int i = 0; i < caselist.size(); i++) {
			Program singleCase = (Program) caselist.peek(i);

			if (singleCase.size() < 2)
				throw new Exception("Not enough elements for fitness case \""
						+ singleCase + "\"");

			Float x = new Float(singleCase.peek(0).toString());
			Float v = new Float(singleCase.peek(1).toString());

			Print(";; Fitness case #" + i + " position: " + x + " velocity: " + v
					+ "\n");

			ObjectPair xv = new ObjectPair(x,v);
			
			_testCases.add(new GATestCase(xv, null));
		}
	}

	protected void InitInterpreter(Interpreter inInterpreter) {
	}

	public float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
			Object inOutput) {
		
		int timeSteps = 1000;
		float timeDiscritized = 0.01f;
		float maxTime = timeSteps * timeDiscritized;
		
		float captureRadius = 0.01f;
		
		ObjectPair xv = (ObjectPair) inInput;
		float position = (Float) xv._first;
		float velocity = (Float) xv._second;
		
		for(int step = 1; step <= timeSteps; step++){
			_interpreter.ClearStacks();

			floatStack fStack = _interpreter.floatStack();
			booleanStack bStack = _interpreter.boolStack();
			ObjectStack iStack = _interpreter.inputStack();
			
			// Position will be on the top of the stack, and velocity will be
			// second.
			fStack.push(position);
			fStack.push(velocity);

			// Must be included in order to use the input stack. Uses same order
			// as inputs on Float stack.
			iStack.push(position);
			iStack.push(velocity);
			
			_interpreter.Execute(((PushGPIndividual) inIndividual)._program,
					_executionLimit);
			
			// If there is no boolean on the stack, the program has failed to
			// return a reasonable output. So, return a penalty fitness of
			// twice the maximum time.
			if(bStack.size() == 0){
				return 2 * maxTime;
			}

			// If there is a boolean, use it to compute the next position and
			// velocity. Then, check for termination conditions.
			// NOTE: If result == True, we will apply the force in the positive
			// direction, and if result == False, we will apply the force in
			// the negative direction.
			boolean positiveForce = bStack.top();
			float acceleration;
			
			if(positiveForce){
				acceleration = 0.5f;
			}
			else{
				acceleration = -0.5f;
			}
			
			velocity = velocity + (timeDiscritized * acceleration);
			position = position + (timeDiscritized * velocity);
			
			// Check for termination conditions
			if(position <= captureRadius && position >= -captureRadius && 
					velocity <= captureRadius && velocity >= -captureRadius){
				//Cart is centered, so return time it took.
				return step * timeDiscritized;
			}
		
		}
		
		// If here, the cart failed to come to rest in the allotted time. So,
		// return the failed error of maxTime.

		return maxTime;
	}
	
	protected boolean Success() {
		return _generationCount >= _maxGenerations;
	}

}
