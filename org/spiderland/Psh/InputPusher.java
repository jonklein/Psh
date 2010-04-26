package org.spiderland.Psh;

import java.io.Serializable;

public class InputPusher implements Serializable {
	private static final long serialVersionUID = 1L;

	public void pushInput(Interpreter inI, int n) {
		ObjectStack _stack = inI.inputStack();

		if (_stack.size() > n) {
			Object inObject = _stack.peek(n);

			if (inObject instanceof Integer) {
				intStack istack = inI.intStack();
				istack.push((Integer) inObject);
			} else if (inObject instanceof Number) {
				floatStack fstack = inI.floatStack();
				fstack.push(((Number) inObject).floatValue());
			} else if (inObject instanceof Boolean) {
				booleanStack bstack = inI.boolStack();
				bstack.push((Boolean) inObject);

			} else {
				System.err.println("Error during input.index - object "
						+ inObject.getClass()
						+ " is not a legal object according to "
						+ this.getClass() + ".");
			}
		}
	}

}
