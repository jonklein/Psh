
package org.spiderland.Psh;

/**
 * An abstract container for a GATestCase containing an input and output object.
 */

public class GATestCase {
    public GATestCase( Object inInput, Object inOutput ) { _input = inInput; _output = inOutput; }
    
    public Object _input;
    public Object _output;
}
