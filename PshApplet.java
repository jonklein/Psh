

//import java.util.*;
import java.applet.*;

import org.spiderland.Psh.*;

public class PshApplet extends Applet {
	public static final long serialVersionUID = 2L;


	Interpreter _interpreter = new Interpreter();

	public void init() {
		try {
			System.out.println( Run( getParameter( "program" ) ) );
		} catch( Exception e ) {};
	}

	public String Run( String inValue ) {
		_interpreter.ClearStacks();

		try {
			Program p;
			p = new Program( _interpreter, inValue );

			_interpreter.Execute( p );

		} catch( Exception e ) {

		};

		return _interpreter.toString();
	}

	public String GetInstructionString() {
		return "=> " + _interpreter.GetInstructionString();
	}
}
