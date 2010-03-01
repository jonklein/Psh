
package org.spiderland.Psh;

import java.io.Serializable;

/**
 * Abstract instruction base for all instructions.
 */

public abstract class Instruction implements Serializable {
	public abstract void Execute( Interpreter inI );
}

