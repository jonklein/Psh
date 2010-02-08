
package org.spiderland.Psh;

import org.spiderland.Psh.*;

public class IntSymbolicRegressionNoInput extends PushGP {
    protected int			_currentInput;
    protected int 			_inputCount;
    
    protected void InitFromParameters() throws Exception {
	super.InitFromParameters();

	String cases = GetParam( "test-cases" );

	Program caselist = new Program( cases );

	for( int i = 0; i < caselist.size(); i++ ) {
	    Program p = (Program)caselist.peek( i );

	    if( p.size() < 2 ) 
		throw new Exception( "Not enough elements for fitness case \"" + p + "\"" );

	    Integer in  = new Integer( p.peek( 0 ).toString() );
	    Integer out = new Integer( p.peek( 1 ).toString() );

	    Print( ";; Fitness case #" + i + " input: " + in + " output: " + out + "\n" );

	    _testCases.add( new GATestCase( in, out ) );
	}
    }

    protected void InitInterpreter( Interpreter inInterpreter ) {

    }

    protected float EvaluateTestCase( GAIndividual inIndividual, Object inInput, Object inOutput ) {
	_interpreter.ClearStacks();

	_currentInput = (Integer)inInput;

	intStack stack = _interpreter.intStack();

	stack.push( _currentInput );

	_interpreter.Execute( ((PushGPIndividual)inIndividual)._program, _executionLimit );

	int result = stack.top();
	// System.out.println( _interpreter + " " + result );

	return result - ( (Integer)inOutput );
    }
}
