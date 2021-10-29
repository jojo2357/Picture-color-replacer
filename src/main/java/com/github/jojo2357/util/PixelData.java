package com.github.jojo2357.util;

public class PixelData {
    private final byte r, g, b, a;
    public final Point imageLocation;

    public PixelData(int rgba, Point imageLocation) {
        this((rgba >> 24) & 0xFF, (rgba >> 16) & 0xFF, (rgba >> 8) & 0xFF, rgba & 0xFF, imageLocation);
    }

    public PixelData(byte r, byte g, byte b, byte a, Point imageLocation) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.imageLocation = imageLocation;
    }

    public PixelData(int r, int g, int b, int a, Point imageLocation) {
        this((byte) r, (byte) g, (byte) b, (byte) a, imageLocation);
    }

    public int getR() {
        return r & 0xFF;
    }

    public int getG() {
        return g & 0xFF;
    }

    public int getB() {
        return b & 0xFF;
    }

    public int getA() {
        return a & 0xFF;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d, %d) @ (%d, %d)", getR(), getG(), getB(), getA(), (int)imageLocation.getX(), (int)imageLocation.getY() );
    }
}
