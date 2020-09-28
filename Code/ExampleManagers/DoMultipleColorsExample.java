import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.lang.RuntimeException;

class DoMultipleColorsExample{
	/*args:
	0: filename
	1: ext (no ".")
	*/
	public static void main(String args[]) throws Exception{
		File colorInput = new File("ColorReplaceData.txt");
		Scanner parser = new Scanner(colorInput);
		ArrayList<String> commands = new ArrayList<String>();
		while(parser.hasNextLine()){
			commands.add(parser.nextLine());
			if (commands.get(commands.size() - 1).split(" ").length < 6)
				throw new RuntimeException("Line " + commands.size() + " in ColorReplaceData.txt is not valid!");
		}
		for (String command : commands){
			Runtime.getRuntime().exec("java ColorSeparation " + args[0] + "." + args[1] + " " + args[0] + " " + args[1] + " " + command);
			Thread.sleep(50);
		}
	}
}