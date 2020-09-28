import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

class MultipleFilesMultipleColorsExample{
	//args: 0: ext no .
	public static void main(String args[]) throws Exception{
		File colorInput = new File("ColorReplaceData.txt");
		Scanner parser = new Scanner(colorInput);
		ArrayList<String> commands = new ArrayList<String>();
		while(parser.hasNextLine()){
			commands.add(parser.nextLine());
			if (commands.get(commands.size() - 1).split(" ").length < 6)
				throw new RuntimeException("Line " + commands.size() + " in ColorReplaceData.txt is not valid!");
		}
		File folder = new File(System.getProperty("user.dir"));
		for (final File fileEntry : folder.listFiles()) {
			String lineIn = fileEntry.getName();
			if (!lineIn.contains("." + args[0]))
				continue;
			for (String command : commands){
				Runtime.getRuntime().exec("java ColorSeparation " + lineIn + " " + lineIn.substring(0, lineIn.indexOf(".")) + " " + args[0].substring(1) + " " + command);
				Thread.sleep(50);
			}
		}
	}
}