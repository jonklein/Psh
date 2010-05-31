package org.spiderland.Psh;

public class FloatSymbolicRegression extends PushGP {
	private static final long serialVersionUID = 1L;

	protected float _currentInput;

	protected void InitFromParameters() throws Exception {
		super.InitFromParameters();

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
	}

	protected void InitInterpreter(Interpreter inInterpreter) {
	}

	protected float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
			Object inOutput) {
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

		return result - ((Float) inOutput);
	}

}
