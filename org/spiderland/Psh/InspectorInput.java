
package org.spiderland.Psh;

import java.io.*;

import org.spiderland.Psh.*;

/**
 * A utility class to help read PshInspector input files.
 */

public class InspectorInput{

    Program _program;
    int _executionLimit;
    Interpreter _interpreter;

    /**
     * Constructs an InspectorInput from a filename string
     * @param inFilename The file to input from.
     */
    public InspectorInput(String inFilename) throws Exception{
	InitInspectorInput(new File(inFilename));
    }

    /**
     * Constructs an InspectorInput from a filename string
     * @param inFile The file to input from.
     */
    public InspectorInput(File inFile) throws Exception{
	InitInspectorInput(inFile);
    }

    /**
     * Initializes an InspectorInput
     * @param inFile The file to input from.
     */
    private void InitInspectorInput(File inFile) throws Exception{
	_interpreter = new Interpreter();

	//Read fileString
	String fileString = Params.ReadFileString(inFile);

	//Get programString
	int indexNewline = fileString.indexOf("\n");
	String programString = fileString.substring(0, indexNewline);
	fileString = fileString.substring(indexNewline + 1);

	//Get _executionLimit
	indexNewline = fileString.indexOf("\n");
	if(indexNewline != -1){
	    _executionLimit = Integer.parseInt(fileString.substring(0, indexNewline));
	    fileString = fileString.substring(indexNewline + 1);
	}
	else{
	    //If here, no inputs to be pushed were included
	    _executionLimit = Integer.parseInt(fileString);
	    fileString = "";
	}

	//Get inputs and push them onto correct stacks. If fileString = ""
	//at this point, then can still do the following with correct result.
	indexNewline = fileString.indexOf("\n");
	if(indexNewline != -1)
	    fileString = fileString.substring(0, indexNewline);

	//Parse the inputs and load them into the interpreter
	parseAndLoadInputs(fileString);

	//Load the program
	_program = new Program(programString);
	_interpreter.codeStack().push(_program);
	_interpreter.LoadProgram(_program); //Initiallizes program
    }

    /**
     * Returns the initialized interpreter
     * @return The initialized interpreter
     */
    public Interpreter getInterpreter(){
	return _interpreter;
    }

    /**
     * Returns the execution limit
     * @return The execution limit
     */
    public int getExecutionLimit(){
	return _executionLimit;
    }

    private void parseAndLoadInputs(String inputs) throws Exception{
	String inputTokens[] = inputs.split( "\\s+" );

	for(int i = 0; i < inputTokens.length; i++){
	    String token = inputTokens[i];

	    if(token.equals("")){
		continue;
	    }
	    else if(token.equals("true")){
		_interpreter.boolStack().push(true);
	    }
	    else if(token.equals("false")){
		_interpreter.boolStack().push(false);
	    }
	    else if (token.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")){
		if(token.indexOf('.') != - 1)
		    _interpreter.floatStack().push(Float.parseFloat(token));
		else
		    _interpreter.intStack().push(Integer.parseInt(token));
	    }
	    else{
		throw new Exception("Inputs must be of type int, float, or boolean. \"" + token + "\" is none of these.");
	    } 
	}
    }



}