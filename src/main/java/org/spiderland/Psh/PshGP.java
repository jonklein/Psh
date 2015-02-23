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

import java.io.*;

import org.spiderland.Psh.*;

/**
 * PshGP executes a genetic programming run using the given parameter file. More
 * information about parameter files can be found in the README.
 */
public class PshGP {
	public static void main(String args[]) throws Exception {

		if (args.length != 1 && args.length != 3) {
			System.out.println("Usage: PshGP paramfile|checkpointfile.gz [testprogram testcasenumber]");
			System.exit(0);
		}

		GA ga = null;
		if (args[0].endsWith(".gz"))
			ga = GA.GAWithCheckpoint(args[0]);
		else
			ga = GA.GAWithParameters(Params.ReadFromFile(new File(args[0])));

		if (args.length == 3) {
			// Execute a test program

			Program p = new Program(args[1]);
			((PushGP) ga).RunTestProgram(p, Integer.parseInt(args[2]));
		} else {
			// Execute a GP run.

			ga.Run();
		}
	}
}
