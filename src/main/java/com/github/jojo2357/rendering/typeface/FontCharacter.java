package com.github.jojo2357.rendering.typeface;

import com.github.jojo2357.util.Texture;

public class FontCharacter {
    private final char stringRep;
    private final Colors color;
    private final Texture image;

    public FontCharacter(char strRepresentation, Colors color) {
        this(strRepresentation, "" + strRepresentation, color);
    }

    public FontCharacter(char strRepresentation, String filename, Colors color) {
        this.stringRep = strRepresentation;
        this.color = color;
        this.image = Texture.create("FontAssets/" + filename + "_" + color.name);
    }

    public FontCharacter(String texture) {
        this.stringRep = 0;
        this.color = null;
        this.image = Texture.create("FontAssets/" + texture);
    }

    public char getStringRep() {
        return stringRep;
    }

    public Colors getColor() {
        return color;
    }

    public Texture getImage() {
        return image;
    }
}
