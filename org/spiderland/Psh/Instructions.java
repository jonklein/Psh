
package org.spiderland.Psh;

//
// All instructions 
//

/**
 * Abstract instruction class for instructions which operate on any of the
 * built-in stacks.
 */

abstract class StackInstruction extends Instruction {
    protected Stack _stack;

    StackInstruction( Stack inStack ) { _stack = inStack; }
}

/**
 * Abstract instruction class for instructions which operate on one of the
 * standard ObjectStacks (code & exec).
 */

abstract class ObjectStackInstruction extends Instruction {

    protected ObjectStack _stack;

    ObjectStackInstruction( ObjectStack inStack ) { _stack = inStack; }
}


class Quote extends Instruction {
    Quote() {}

    public void Execute( Interpreter inI ) {
	ObjectStack cstack = inI.codeStack();
	ObjectStack estack = inI.execStack();

	if( estack.size() > 0 )
	    cstack.push( estack.pop() );
    }
}



class Pop extends StackInstruction {
    Pop( Stack inStack ) { super( inStack ); }

    public void Execute( Interpreter inI ) {
	if( _stack.size() > 0 )
	    _stack.popdiscard();
    }
}

class Flush extends StackInstruction {
    Flush( Stack inStack ) { super( inStack ); }

    public void Execute( Interpreter inI ) {
	_stack.clear();
    }
}
		
class Dup extends StackInstruction {
    Dup( Stack inStack ) { super( inStack ); }

    public void Execute( Interpreter inI ) {
	_stack.dup();
    }
}
		
class Rot extends StackInstruction {
    Rot( Stack inStack ) { super( inStack ); }

    public void Execute( Interpreter inI ) {
	if( _stack.size() > 2 ) 
	    _stack.rot();
    }
}

class Swap extends StackInstruction {
    Swap( Stack inStack ) { super( inStack ); }

    public void Execute( Interpreter inI ) {
	if( _stack.size() > 1 ) 
	    _stack.swap();
    }
}

class Depth extends StackInstruction {
    Depth( Stack inStack ) { super( inStack ); }

    public void Execute( Interpreter inI ) {
	intStack stack = inI.intStack();
	stack.push( _stack.size() );
    }
}

class IntegerDefine extends Instruction {
    public void Execute( Interpreter inI ) {
	intStack istack = inI.intStack();
	//ObjectStack nstack = inI.nameStack();

	int value = istack.pop();
	//String name = (String)nstack.pop();

	new IntegerConstant( value );
    }
}

class IntegerConstant extends Instruction {
    int _value;

    public IntegerConstant( int inValue ) { _value = inValue; }

    public void Execute( Interpreter inI ) {
	inI.intStack().push( _value );
    }
}

class FloatConstant extends Instruction {
    float _value;

    public FloatConstant( float inValue ) { _value = inValue; }

    public void Execute( Interpreter inI ) {
	inI.floatStack().push( _value );
    }
}

class BooleanConstant extends Instruction {
    boolean _value;

    public BooleanConstant( boolean inValue ) { _value = inValue; }

    public void Execute( Interpreter inI ) {
	inI.boolStack().push( _value );
    }
}

class ObjectConstant extends ObjectStackInstruction {
    Object _value;

    public ObjectConstant( ObjectStack inStack, Object inValue ) { super( inStack ); _value = inValue; }

    public void Execute( Interpreter inI ) {
	_stack.push( _value );
    }
}

//
//
// Binary integer instructions
//

abstract class BinaryIntegerInstruction extends Instruction {
    abstract int BinaryOperator( int inA, int inB );

    public void Execute( Interpreter inI ) {
	intStack stack = inI.intStack();

	if( stack.size() > 1 ) {
	    int a, b;
	    a = stack.pop();
	    b = stack.pop();
	    stack.push( BinaryOperator( b, a ) );
	}
    }
}

class IntegerAdd extends BinaryIntegerInstruction {
    int BinaryOperator( int inA, int inB ) { return inA + inB; }
}

class IntegerSub extends BinaryIntegerInstruction {
    int BinaryOperator( int inA, int inB ) { return inA - inB; }
}

class IntegerDiv extends BinaryIntegerInstruction {
    int BinaryOperator( int inA, int inB ) { return inB != 0 ? ( inA / inB ) : 0; }
}

class IntegerMul extends BinaryIntegerInstruction {
    int BinaryOperator( int inA, int inB ) { return inA * inB; }
}

class IntegerMod extends BinaryIntegerInstruction {
    int BinaryOperator( int inA, int inB ) { return inB != 0 ? ( inA % inB ) : 0; }
}

//
// Integer instructions with boolean output
//

abstract class BinaryIntegerBoolInstruction extends Instruction {
    abstract boolean BinaryOperator( int inA, int inB );

    public void Execute( Interpreter inI ) {
	intStack istack = inI.intStack();
	booleanStack bstack = inI.boolStack();

	if( istack.size() > 1 ) {
	    int a, b;
	    a = istack.pop();
	    b = istack.pop();
	    bstack.push( BinaryOperator( b, a ) );
	}
    }
}

class IntegerGreaterThan extends BinaryIntegerBoolInstruction {
    boolean BinaryOperator( int inA, int inB ) { return inA > inB; }
}

class IntegerLessThan extends BinaryIntegerBoolInstruction {
    boolean BinaryOperator( int inA, int inB ) { return inA < inB; }
}

class IntegerEquals extends BinaryIntegerBoolInstruction {
    boolean BinaryOperator( int inA, int inB ) { return inA == inB; }
}


//
// Binary float instructions with float output
//

abstract class BinaryFloatInstruction extends Instruction {
    abstract float BinaryOperator( float inA, float inB );

    public void Execute( Interpreter inI ) {
	floatStack stack = inI.floatStack();

	if( stack.size() > 1 ) {
	    float a, b;
	    a = stack.pop();
	    b = stack.pop();
	    stack.push( BinaryOperator( b, a ) );
	}
    }
}

class FloatAdd extends BinaryFloatInstruction {
    float BinaryOperator( float inA, float inB ) { return inA + inB; }
}

class FloatSub extends BinaryFloatInstruction {
    float BinaryOperator( float inA, float inB ) { return inA - inB; }
}

class FloatMul extends BinaryFloatInstruction {
    float BinaryOperator( float inA, float inB ) { return inA * inB; }
}

class FloatDiv extends BinaryFloatInstruction {
    float BinaryOperator( float inA, float inB ) { return inB != 0.0f ? ( inA / inB ) : 0.0f; }
}

class FloatMod extends BinaryFloatInstruction {
    float BinaryOperator( float inA, float inB ) { return inB != 0.0f ? ( inA % inB ) : 0.0f; }
}

class FloatMin extends BinaryFloatInstruction {
    float BinaryOperator( float inA, float inB ) { return Math.min( inA, inB ); }
}

class FloatMax extends BinaryFloatInstruction {
    float BinaryOperator( float inA, float inB ) { return Math.max( inA, inB ); }
}

//
// Binary float instructions with boolean output
//

abstract class BinaryFloatBoolInstruction extends Instruction {
    abstract boolean BinaryOperator( float inA, float inB );

    public void Execute( Interpreter inI ) {
	floatStack fstack = inI.floatStack();
	booleanStack bstack = inI.boolStack();

	if( fstack.size() > 1 ) {
	    float a, b;
	    b = fstack.pop();
	    a = fstack.pop();
	    bstack.push( BinaryOperator( a, b ) );
	}
    }
}

class FloatGreaterThan extends BinaryFloatBoolInstruction {
    boolean BinaryOperator( float inA, float inB ) { return inA > inB; }
}

class FloatLessThan extends BinaryFloatBoolInstruction {
    boolean BinaryOperator( float inA, float inB ) { return inA < inB; }
}

class FloatEquals extends BinaryFloatBoolInstruction {
    boolean BinaryOperator( float inA, float inB ) { return inA == inB; }
}

//
// Unary float instructions
//

abstract class UnaryFloatInstruction extends Instruction {
    abstract float UnaryOperator( float inValue );

    public void Execute( Interpreter inI ) {
	floatStack stack = inI.floatStack();

	if( stack.size() > 0 )
	    stack.push( UnaryOperator( stack.pop() ) );
    }
}

class FloatSin extends UnaryFloatInstruction {
    float UnaryOperator( float inValue ) { return (float)Math.sin( inValue ); }
}

class FloatCos extends UnaryFloatInstruction {
    float UnaryOperator( float inValue ) { return (float)Math.cos( inValue ); }
}

class FloatTan extends UnaryFloatInstruction {
    float UnaryOperator( float inValue ) { return (float)Math.tan( inValue ); }
}

//
// Instructions for input stack
//

class InputInN extends Instruction{
    protected int index;

    InputInN(int inIndex) {index = inIndex;}

    @Override
    public void Execute(Interpreter inI){
        inI.getInputPusher().pushInput(inI, index);
    }
}

class InputInAll extends ObjectStackInstruction{
    InputInAll(ObjectStack inStack) {super( inStack );}

    @Override
    public void Execute(Interpreter inI){

	if(_stack.size() > 0){
	    for(int index = 0; index < _stack.size(); index++){
                inI.getInputPusher().pushInput(inI, index);
	    }
	}
    }
}

class InputInRev extends ObjectStackInstruction{
    InputInRev(ObjectStack inStack) {super( inStack );}

    @Override
    public void Execute(Interpreter inI){

	if(_stack.size() > 0){
	    for(int index = _stack.size() - 1; index >= 0; index--){
                inI.getInputPusher().pushInput(inI, index);
	    }
	}
    }
}

class InputIndex extends ObjectStackInstruction{
    InputIndex(ObjectStack inStack) {super( inStack );}

    @Override
    public void Execute(Interpreter inI){
	intStack istack = inI.intStack();

	if(istack.size() > 0 && _stack.size() > 0){
	    int index = istack.pop();

	    if(index < 0)
		index = 0;
	    if(index >= _stack.size())
		index = _stack.size() - 1;

            inI.getInputPusher().pushInput(inI, index);
	}
    }
}

//
// Instructions which can be applied to any code stack 
//


//trh//All code and exec stack iteration fuctions have been fixed to match the
//     specifications of Push 3.0


//Begin code iteration functions
class CodeDoRange extends ObjectStackInstruction {
    CodeDoRange(Interpreter inI) { 
	super(inI.codeStack()); 
    }

    public void Execute( Interpreter inI ) {
	intStack istack = inI.intStack();
	ObjectStack estack = inI.execStack();

	if( _stack.size() > 0 && istack.size() > 1 ) {
	    int stop  = istack.pop();
	    int start = istack.pop();
	    Object code = _stack.pop();

	    if( start == stop ) {
		istack.push( start );
		estack.push( code );
	    } else {
		istack.push(start);
		start = ( start < stop ) ? ( start + 1 ) : ( start - 1 );

		//trh//Made changes to correct errors with code.do*range
		//String recursiveCall = "(" + start + " " + stop + " ";
		//recursiveCall += "code.quote ";
		//recursiveCall += code + " code.do*range)";

                // ( start stop code.quote code code.do*range )
		
		try{
		    //Program recursiveCallProgram = new Program(inI, recursiveCall);
                    Program recursiveCallProgram = new Program(inI);
                    recursiveCallProgram.push(Integer.valueOf(start));
                    recursiveCallProgram.push(Integer.valueOf(stop));
                    recursiveCallProgram.push(inI._instructions.get("code.quote"));
                    recursiveCallProgram.push(code);
                    recursiveCallProgram.push(inI._instructions.get("code.do*range"));
		    estack.push(recursiveCallProgram);
		} catch(Exception e){
		    System.err.println("Error while initializing a program.");
		}

		estack.push( code );
	    }
	}
    }
}

class CodeDoTimes extends ObjectStackInstruction{
    CodeDoTimes(Interpreter inI) { 
	super(inI.codeStack()); 
    }

    public void Execute(Interpreter inI){
	intStack istack = inI.intStack();
	ObjectStack estack = inI.execStack();

	if(_stack.size() > 0 && istack.size() > 0) {
	    if(istack.top() > 0){
                Object bodyObj = _stack.pop();

                if (bodyObj instanceof Program) {
                    // insert integer.pop in front of program
                    ((Program)bodyObj).shove(
                            inI._instructions.get("integer.pop"),
                            ((Program)bodyObj)._size);
                }
                else
                {
                    // create a new program with integer.pop in front of
                    // the popped object
                    Program newProgram = new Program(inI);
                    newProgram.push(inI._instructions.get("integer.pop"));
                    newProgram.push(bodyObj);
                    bodyObj = newProgram;
                }

                /*
		String body = _stack.pop().toString();
		String bodyAndIntPop = "";

		if(body.charAt(0) == '('){
		    bodyAndIntPop = "(integer.pop " + body.substring(1);
		}
		else{
		    bodyAndIntPop = "(integer.pop " + body + ")";
		}*/

                int stop = istack.pop() - 1;

                //String doRangeMacroString = "(0 " + stop;
		//doRangeMacroString += " code.quote " + bodyAndIntPop;
		//doRangeMacroString += " code.do*range)";

		try{
		    // Program doRangeMacroProgram = new Program(inI, doRangeMacroString);
                    Program doRangeMacroProgram = new Program(inI);
                    doRangeMacroProgram.push(Integer.valueOf(0));
                    doRangeMacroProgram.push(Integer.valueOf(stop));
                    doRangeMacroProgram.push(inI._instructions.get("code.quote"));
                    doRangeMacroProgram.push(bodyObj);
                    doRangeMacroProgram.push(inI._instructions.get("code.do*range"));
		    estack.push(doRangeMacroProgram);
		} catch(Exception e){
		    System.err.println("Error while initializing a program.");
		}
		
	    }
	}

    }
}

class CodeDoCount extends ObjectStackInstruction{
    CodeDoCount(Interpreter inI) { 
	super(inI.codeStack()); 
    }

    public void Execute(Interpreter inI){
	intStack istack = inI.intStack();
	ObjectStack estack = inI.execStack();

	if(_stack.size() > 0 && istack.size() > 0) {
	    if(istack.top() > 0){
                int stop = istack.pop() - 1;
                Object bodyObj = _stack.pop();

		//String doRangeMacroString = "(0 " + stop;
		//doRangeMacroString += " code.quote " + bodyObj;
		//doRangeMacroString += " code.do*range)";

		try{
		    //Program doRangeMacroProgram = new Program(inI, doRangeMacroString);
                    Program doRangeMacroProgram = new Program(inI);
                    doRangeMacroProgram.push(Integer.valueOf(0));
                    doRangeMacroProgram.push(Integer.valueOf(stop));
                    doRangeMacroProgram.push(inI._instructions.get("code.quote"));
                    doRangeMacroProgram.push(bodyObj);
                    doRangeMacroProgram.push(inI._instructions.get("code.do*range"));
		    estack.push(doRangeMacroProgram);
		} catch(Exception e){
		    System.err.println("Error while initializing a program.");
		}
		
	    }
	}

    }
}
//End code iteration functions

//Begin exec iteration functions

class ExecDoRange extends ObjectStackInstruction {
    ExecDoRange(Interpreter inI) { 
	super(inI.execStack()); 
    }

    public void Execute( Interpreter inI ) {
	intStack istack = inI.intStack();
	ObjectStack estack = inI.execStack();

	if( _stack.size() > 0 && istack.size() > 1 ) {
	    int stop  = istack.pop();
	    int start = istack.pop();
	    Object code = _stack.pop();

	    if( start == stop ) {
		istack.push( start );
		estack.push( code );
	    } else {
		istack.push(start);
		start = ( start < stop ) ? ( start + 1 ) : ( start - 1 );

		//trh//Made changes to correct errors with code.do*range
		//String recursiveCall = "(" + start + " " + stop + " ";
		//recursiveCall += "exec.do*range " + code + ")";
		
		try{
		    //Program recursiveCallProgram = new Program(inI, recursiveCall);
                    Program recursiveCallProgram = new Program(inI);
                    recursiveCallProgram.push(Integer.valueOf(start));
                    recursiveCallProgram.push(Integer.valueOf(stop));
                    recursiveCallProgram.push(inI._instructions.get("exec.do*range"));
                    recursiveCallProgram.push(code);
		    estack.push(recursiveCallProgram);
		} catch(Exception e){
		    System.err.println("Error while initializing a program.");
		}

		estack.push( code );
	    }
	}
    }
}

class ExecDoTimes extends ObjectStackInstruction{
    ExecDoTimes(Interpreter inI) { 
	super(inI.execStack()); 
    }

    public void Execute(Interpreter inI){
	intStack istack = inI.intStack();
	ObjectStack estack = inI.execStack();

	if(_stack.size() > 0 && istack.size() > 0) {
	    if(istack.top() > 0){
                Object bodyObj = _stack.pop();

                if (bodyObj instanceof Program) {
                    // insert integer.pop in front of program
                    ((Program)bodyObj).shove(
                            inI._instructions.get("integer.pop"),
                            ((Program)bodyObj)._size);
                }
                else
                {
                    // create a new program with integer.pop in front of
                    // the popped object
                    Program newProgram = new Program(inI);
                    newProgram.push(inI._instructions.get("integer.pop"));
                    newProgram.push(bodyObj);
                    bodyObj = newProgram;
                }

                int stop = istack.pop() - 1;

                /*
		String body = _stack.pop().toString();
		String bodyAndIntPop = "";

		if(body.charAt(0) == '('){
		    bodyAndIntPop = "(integer.pop " + body.substring(1);
		}
		else{
		    bodyAndIntPop = "(integer.pop " + body + ")";
		}

		String doRangeMacroString = "(0 " + stop;
		doRangeMacroString += " exec.do*range " + bodyAndIntPop + ")";*/


		try{
		    //Program doRangeMacroProgram = new Program(inI, doRangeMacroString);
                    Program doRangeMacroProgram = new Program(inI);
                    doRangeMacroProgram.push(Integer.valueOf(0));
                    doRangeMacroProgram.push(Integer.valueOf(stop));
                    doRangeMacroProgram.push(inI._instructions.get("exec.do*range"));
                    doRangeMacroProgram.push(bodyObj);
		    estack.push(doRangeMacroProgram);
		} catch(Exception e){
		    System.err.println("Error while initializing a program.");
		}
		
	    }
	}

    }
}

class ExecDoCount extends ObjectStackInstruction{
    ExecDoCount(Interpreter inI) { 
	super(inI.execStack()); 
    }

    public void Execute(Interpreter inI){
	intStack istack = inI.intStack();
	ObjectStack estack = inI.execStack();

	if(_stack.size() > 0 && istack.size() > 0) {
	    if(istack.top() > 0){
                int stop = istack.pop() - 1;
                Object bodyObj = _stack.pop();

		//String doRangeMacroString = "(0 " + stop;
		//doRangeMacroString += " exec.do*range " + bodyObj + ")";

		try{
		    //Program doRangeMacroProgram = new Program(inI, doRangeMacroString);
                    Program doRangeMacroProgram = new Program(inI);
                    doRangeMacroProgram.push(Integer.valueOf(0));
                    doRangeMacroProgram.push(Integer.valueOf(stop));
                    doRangeMacroProgram.push(inI._instructions.get("exec.do*range"));
                    doRangeMacroProgram.push(bodyObj);
		    estack.push(doRangeMacroProgram);
		} catch(Exception e){
		    System.err.println("Error while initializing a program.");
		}
		
	    }
	}
    }
}
//End exec iteration functions.

class ObjectEquals extends ObjectStackInstruction {
    ObjectEquals( ObjectStack inStack ) { super( inStack ); }

    public void Execute( Interpreter inI ) {
	booleanStack bstack = inI.boolStack();

	if( _stack.size() > 1 ) {
	    Object o1 = _stack.pop();
	    Object o2 = _stack.pop();

	    bstack.push( o1.equals( o2 ) );
	}
    }
}

class If extends ObjectStackInstruction {
    If( ObjectStack inStack ) { super( inStack ); }

    public void Execute( Interpreter inI ) {
	booleanStack bstack = inI.boolStack();
	ObjectStack estack  = inI.execStack();

	if( _stack.size() > 1 && bstack.size() > 0 ) {
	    boolean istrue = bstack.pop();

	    Object iftrue  = _stack.pop();
	    Object iffalse = _stack.pop();

	    if( istrue )
		estack.push( iftrue );
	    else
		estack.push( iffalse );
	}
    }
}


//
// Instructions for the activation stack
//

class PopFrame extends Instruction {
    PopFrame() {}

    public void Execute( Interpreter inI ) {
	// floatStack fstack = inI.floatStack();
	// float total = fstack.accumulate();

	inI.PopFrame();

	// do the activation, and push the result on to the end of the previous frame
	// fstack = inI.floatStack();
	// fstack.push( 1.0f / ( 1.0f + (float)Math.exp( -10.0f * ( total - .5 ) ) ) );
    }
}

class PushFrame extends Instruction {
    PushFrame() {}

    public void Execute( Interpreter inI ) {
	inI.PushFrame();
    }
}

//
// Binary bool instructions with bool output
//

abstract class BinaryBoolInstruction extends Instruction {
    abstract boolean BinaryOperator( boolean inA, boolean inB );

    @Override
    public void Execute( Interpreter inI ) {
	booleanStack stack = inI.boolStack();

	if( stack.size() > 1 ) {
	    boolean a, b;
	    a = stack.pop();
	    b = stack.pop();
	    stack.push( BinaryOperator( b, a ) );
	}
    }
}

class BoolEquals extends BinaryBoolInstruction {
    @Override
    boolean BinaryOperator( boolean inA, boolean inB ) { return inA == inB; }
}

class BoolAnd extends BinaryBoolInstruction {
    @Override
    boolean BinaryOperator( boolean inA, boolean inB ) { return inA & inB; }
}

class BoolOr extends BinaryBoolInstruction {
    @Override
    boolean BinaryOperator( boolean inA, boolean inB ) { return inA | inB; }
}

class BoolXor extends BinaryBoolInstruction {
    @Override
    boolean BinaryOperator( boolean inA, boolean inB ) { return inA ^ inB; }
}

class BoolNot extends Instruction {
    BoolNot() {}

    public void Execute( Interpreter inI ) {
        if (inI.boolStack().size() > 0)
            inI.boolStack().push(!inI.boolStack().pop());
    }
}

