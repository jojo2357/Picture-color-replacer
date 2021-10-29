package com.github.jojo2357.rendering.typeface;

public class JojoFont {

    private static final FontCharacter[] fontCharacters = new FontCharacter[41];
    public static FontCharacter FNF;

    public static void init() {
        loadNumbers();
        loadLetters();
        FNF = new FontCharacter("FNF");
    }

    public static void loadNumbers() {
        for (char asciiValue = 48; asciiValue < 58; asciiValue++) {
            fontCharacters[asciiValue - 48] = new FontCharacter(asciiValue, Colors.WHITE);
        }
        fontCharacters[36] = new FontCharacter('-', Colors.WHITE);
    }

    private static void loadLetters() {
        for (char asciiValue = 65; asciiValue < 91; asciiValue++) {
            fontCharacters[asciiValue - 55] = new FontCharacter(asciiValue, Colors.WHITE);
        }
        fontCharacters[37] = new FontCharacter('.', Colors.WHITE);
        fontCharacters[38] = new FontCharacter('(', Colors.WHITE);
        fontCharacters[39] = new FontCharacter(')', Colors.WHITE);
        fontCharacters[40] = new FontCharacter('/', "fs", Colors.WHITE);
    }

    public static FontCharacter getCharacter(char charRepresentation) {
        if (charRepresentation <= '9' && charRepresentation >= '0')
            if (fontCharacters[charRepresentation - 48] == null)
                throw new IllegalStateException("Probs wrong color. " + charRepresentation + "_" + Colors.WHITE.name + " DNE");
            else
                return fontCharacters[charRepresentation - 48];
        if (charRepresentation <= 'Z' && charRepresentation >= 'A')
            if (fontCharacters[charRepresentation - 55] == null)
                throw new IllegalStateException("Probs wrong color. " + charRepresentation + "_" + Colors.WHITE.name + " DNE");
            else
                return fontCharacters[charRepresentation - 55];
        if (charRepresentation == '-')
            if (fontCharacters[36] == null)
                throw new IllegalStateException("Probs wrong color. " + charRepresentation + "_" + Colors.WHITE.name + " DNE");
            else
                return fontCharacters[36];
        if (charRepresentation == '.')
            if (fontCharacters[37] == null)
                throw new IllegalStateException("Probs wrong color. " + charRepresentation + "_" + Colors.WHITE.name + " DNE");
            else
                return fontCharacters[37];
        if (charRepresentation == '(')
            if (fontCharacters[38] == null)
                throw new IllegalStateException("Probs wrong color. " + charRepresentation + "_" + Colors.WHITE.name + " DNE");
            else
                return fontCharacters[38];
        if (charRepresentation == ')')
            if (fontCharacters[39] == null)
                throw new IllegalStateException("Probs wrong color. " + charRepresentation + "_" + Colors.WHITE.name + " DNE");
            else
                return fontCharacters[39];
        if (charRepresentation == '/')
            if (fontCharacters[40] == null)
                throw new IllegalStateException("Probs wrong color. " + charRepresentation + "_" + Colors.WHITE.name + " DNE");
            else
                return fontCharacters[40];
        return FNF;
    }
}
