/*
 * Copyright 2009-2010 Jon Klein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.ArrayList;

import org.spiderland.Psh.*;

/**
 * Used to print equations from Psh programs
 */
public class PshEquationBuilder {
	public static void main(String args[]) throws Exception {		

		if (args.length != 1) {
			System.out.println("Usage: PshEquationBuilder inputfile");
			System.exit(0);
		}
		
		File inFile = new File(args[0]);
		
		// Read fileString
		String fileString = Params.ReadFileString(inFile);

		// Get programString
		String programString;
		int indexNewline = fileString.indexOf("\n");
		
		if (indexNewline == -1) {
			programString = fileString;
		}
		else{
			programString = fileString.substring(0, indexNewline).trim();
		}
		
		//Get rid of parentheses
		programString = programString.replace('(', ' ');
		programString = programString.replace(')', ' ').trim();
		
		String instructions[] = programString.split("\\s+");
		
		ArrayList<String> stringStack = new ArrayList<String>();
		stringStack.add("x");
		for(String instruction : instructions){
			
			// (input.in0 float.+ float.- float.* float./ float.exp float.sin float.cos float.2pi)
			if(instruction.equals("input.in0")){
				stringStack.add("x");
			}
			else if(instruction.equals("float.+")){
				if(stringStack.size() > 1){
					String top = stringStack.remove(stringStack.size() - 1);
					String next = stringStack.remove(stringStack.size() - 1);
					
					String result = "(" + top + " + " + next + ")";
					stringStack.add(result);
				}
			}
			else if(instruction.equals("float.-")){
				if(stringStack.size() > 1){
					String top = stringStack.remove(stringStack.size() - 1);
					String next = stringStack.remove(stringStack.size() - 1);
					
					String result = "(" + top + " - " + next + ")";
					stringStack.add(result);
				}
			}
			else if(instruction.equals("float.*")){
				if(stringStack.size() > 1){
					String top = stringStack.remove(stringStack.size() - 1);
					String next = stringStack.remove(stringStack.size() - 1);
					
					String result = "(" + top + " * " + next + ")";
					stringStack.add(result);
				}
			}
			else if(instruction.equals("float./")){
				if(stringStack.size() > 1){
					String top = stringStack.remove(stringStack.size() - 1);
					String next = stringStack.remove(stringStack.size() - 1);
					
					String result = "(" + top + " / " + next + ")";
					stringStack.add(result);
				}
			}
			else if(instruction.equals("float.exp")){
				if(stringStack.size() > 0){
					String top = stringStack.remove(stringStack.size() - 1);
					
					String result = "(e^" + top + ")";
					stringStack.add(result);
				}
			}
			else if(instruction.equals("float.sin")){
				if(stringStack.size() > 0){
					String top = stringStack.remove(stringStack.size() - 1);
					
					String result = "sin(" + top + ")";
					stringStack.add(result);
				}
			}
			else if(instruction.equals("float.cos")){
				if(stringStack.size() > 0){
					String top = stringStack.remove(stringStack.size() - 1);
					
					String result = "cos(" + top + ")";
					stringStack.add(result);
				}
			}
			else if(instruction.equals("float.2pi")){
				stringStack.add("2 * pi");
			}
			else{
				throw new Exception("Unrecognized Psh instruction " +
						instruction +" in program.");
			}
			
			
		}

		System.out.println(stringStack.get(stringStack.size() - 1));
		
	}
}