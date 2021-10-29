package com.github.jojo2357.rendering.typeface;

import com.github.jojo2357.rendering.ScreenManager;
import com.github.jojo2357.util.Point;

public class TextRenderer {
    public static void render(String charSequenceToPrint, Point renderStart, int lineWidth, Colors color) {
        charSequenceToPrint = charSequenceToPrint.toUpperCase();
        Point currentSpot = renderStart.copy();
        int onThisLine = 0;
        charSequenceToPrint += ' ';
        for (int i = 0; i < charSequenceToPrint.length(); i++) {
            char renderChar = charSequenceToPrint.charAt(i);
            if (renderChar != ' ') {
                onThisLine++;
                if (JojoFont.getCharacter(renderChar) == JojoFont.FNF)
                    ScreenManager.renderTexture(JojoFont.getCharacter(renderChar).getImage(), currentSpot, 1.1f);
                else
                    ScreenManager.renderTextureWithOverlay(JojoFont.getCharacter(renderChar).getImage(), currentSpot, 1.1f, color);
                //ScreenManager.renderTexture(JojoFont.getCharacter(renderChar, color).getImage(), currentSpot, 2.1f);
                currentSpot.stepX(9);
            } else {
                if (charSequenceToPrint.indexOf(' ', i + 1) - i + onThisLine >= lineWidth) {
                    currentSpot = new Point(renderStart.getX(), currentSpot.getY() + 20);
                    onThisLine = 0;
                } else
                    currentSpot.stepX(9);
            }
        }
    }

    public static void renderRightJustified(String charSequenceToPrint, Point renderStart, Colors... colors) {
        render(charSequenceToPrint, renderStart.stepX((int) -getStringWidth(charSequenceToPrint)), 1, 1000, colors);
    }

    public static void render(String charSequenceToPrint, Point renderStart, int lineWidth, Colors... colors) {
        render(charSequenceToPrint, renderStart, 1, lineWidth, colors);
    }

    public static void render(String charSequenceToPrint, Point renderStart, float size, int lineWidth, Colors... colors) {
        int colodex = 0;
        charSequenceToPrint = charSequenceToPrint.toUpperCase();
        Point currentSpot = renderStart.copy();
        int onThisLine = 0;
        charSequenceToPrint += ' ';
        for (int i = 0; i < charSequenceToPrint.length(); i++) {
            char renderChar = charSequenceToPrint.charAt(i);
            if (renderChar == '\b')
                colodex++;
            else if (renderChar != ' ') {
                onThisLine++;
                ScreenManager.renderTextureWithOverlay(JojoFont.getCharacter(renderChar).getImage(), currentSpot, 1.1f * size, colors[colodex]);
                currentSpot.stepX(9 * size);
            } else {
                if (charSequenceToPrint.indexOf(' ', i + 1) - i + onThisLine >= lineWidth) {
                    currentSpot = new Point(renderStart.getX(), currentSpot.getY() + 20);
                    onThisLine = 0;
                } else
                    currentSpot.stepX(9 * size);
            }
        }
    }

    public static void renderJustified(String charSequenceToPrint, Point heightToPrintAt, int lineWidth, Colors... colors) {
        render(charSequenceToPrint, heightToPrintAt.stepX((int) Math.round(-getStringWidth(charSequenceToPrint) / 2)), 1, lineWidth, colors);
    }

    private static double getStringWidth(String charSequence) {
        return (charSequence.replace("\b", "").trim().length() - 1) * 9;
    }
}
