package erkang;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TxtWriter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			FileWriter outFile = new FileWriter(args[0]);
			PrintWriter out = new PrintWriter(outFile);
			
			// Also could be written as follows on one line
			// Printwriter out = new PrintWriter(new FileWriter(args[0]));
		
			// Write text to file
			out.println("Map:");
			for(int i=1; i<1001; i++){
				for(int j=1; j<1001; j++){
						out.println(i+" "+j);
				}
			}
			out.println("Avoid:");
			out.println(93+" "+175+" "+225+" "+486+" "+894);
			out.println("Peggy");
			out.println(98+" "+157+" "+595+" "+736+" "+440);
			out.println("Sam");
			out.println(105+" "+376+" "+639+" "+963+" "+280);
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

}
