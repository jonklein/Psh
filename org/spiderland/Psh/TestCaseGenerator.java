
package org.spiderland.Psh;

import java.util.*;

/**
 * A class allowing for the runtime creation of custom test cases.
 *
 * Each test case is a dictionary of HashMap< String, Object >.  
 * Each entry in the dictionary corresponds to a problem input,
 * except for the special token "output", which is reserved for 
 * the problem output.
 */

abstract public class TestCaseGenerator {
	/**
	 * @returns The number of cases the generator will create.
	 */

	abstract int				TestCaseCount();

	/**
	 * @returns A test case...
	 */

	abstract HashMap< String, Object >	TestCase( int inIndex );
}
