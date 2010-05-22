package org.spiderland.Psh;

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

	protected float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
			Object inOutput) {
		
		int timeSteps = 1000;
		float timeDiscritized = 0.02f;
		float maxTime = timeSteps * timeDiscritized;
		
		float captureRadius = 0.1f;
		
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
			fStack.push(velocity);
			fStack.push(position);

			// Must be included in order to use the input stack. Uses same order
			// as inputs on Float stack.
			iStack.push(velocity);
			iStack.push(position);

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

}
