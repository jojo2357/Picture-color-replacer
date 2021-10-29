package com.github.jojo2357.rendering.typeface;

public enum Colors {
    RED("red", 255, 0, 0),
    YELLOW("yellow", 255, 216, 0),
    WHITE("white", 255, 255, 255),
    GREEN("green", 0, 255, 0),
    BLUE("blue", 0, 32, 205),
    LIGHT_BLUE("light_blue", 0, 255, 255),
    ORANGE("orange", 255, 127, 0);

    public final String name;
    public final int R;
    public final int G;
    public final int B;

    Colors(String name, int r, int g, int b){
        this.name = name;
        this.R = r;
        this.G = g;
        this.B = b;
    }
}
