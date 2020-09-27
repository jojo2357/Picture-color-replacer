import java.io.*;

/*
This can be used to iterate over many different pictures to replace specific colors.
A demo is loaded up, but feel free to implement in any way that works for you.
*/

class RecolorerManager{
	public static void main(String args[]) throws Exception{
		//java ColorSeparation InputImage.ext OutputImage ext targetR targetG targetB convertR convertG convertB <optional-not shown>convert Alpha
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "java ColorSeparation Bolt.png WhiteLightning png 255 216 0 255 255 255");
        	builder.redirectErrorStream(true);
        	Process p = builder.start();
        	BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        	String line;
        	while (true) {// just gives notification when done
           		line = r.readLine();
           		if (line == null) { break; }
            		System.out.println(line);
        	}
	}
}