import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class ColorSeparation {
    /*
    args: 
    0: first name (input, include extension)
    1: last name (output, do not include extension here. put in next arg)
    2: output file image format (idk what is allowed, but png i have tested and it works. do not include . as in "png" not ".png"
    3: r1 RGB1 = color to replace
    4: g1 
    5: b1 
    6: r2 RGB2 = color to replace it with
    7: g2 
    8: b2
    9: opacity of dest (optional; setting this to 0 will make all replaced pixels transparent
    */
    public static void main(String args[]) throws IOException {
        assert(args.length > 8);
        if (args[1].indexOf(".") == args[1].length() - 4)
             throw new IllegalArgumentException("I think the output file argument has . something and i cba to make the program fix it for you.");
        int agnum = 0;
        separate(args[agnum++], args[agnum++], args[agnum++], Integer.parseInt(args[agnum++]), Integer.parseInt(args[agnum++]),
                Integer.parseInt(args[agnum++]), Integer.parseInt(args[agnum++]),
                Integer.parseInt(args[agnum++]), Integer.parseInt(args[agnum++]),
                args.length > 9? Integer.parseInt(args[9]) : 255);
	System.out.println(args[1] + "." + args[2] + " done");
    }

    public static void separate(String startingName, String endingName, String outFormat, int r1, int g1, int b1, 
            int r2, int g2, int b2, int a2) throws IOException {
        BufferedImage image = ImageIO.read(new File(startingName));
        for (int col = 0; col < image.getWidth(); col++) {
            for (int row = 0; row < image.getHeight(); row++) {
                if (image.getRGB(col, row) == (new Color(r1, g1, b1)).getRGB())
                    image.setRGB(col, row, (new Color(r2, g2, b2, a2)).getRGB());
            }
        }
        File saveAs = new File(endingName + "." + outFormat);
        ImageIO.write(image, outFormat, saveAs);
    }
}