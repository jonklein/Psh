package tools;

import java.io.File;

import org.spiderland.Psh.Params;

public class InstructionListCleaner {

	/** Cleans the file PushInstructionSet.text from a single line of
	 * instructions to a list of instructions.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			File f = new File("tools/PushInstructionSet.txt");
			String line = Params.ReadFileString(f);
			
			String out = line.replace(' ', '\n');
			System.out.println(out);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
