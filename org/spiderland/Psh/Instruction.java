package org.spiderland.Psh;

import java.io.Serializable;

/**
 * Abstract instruction base for all instructions.
 */

public abstract class Instruction implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public abstract void Execute(Interpreter inI);
}
