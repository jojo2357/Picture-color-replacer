package com.github.jojo2357.util;

import java.util.Objects;

public class PixelData {
    public final Point imageLocation;
    private byte r, g, b, a;

    public PixelData(int rgba, Point imageLocation) {
        this((rgba >> 24) & 0xFF, (rgba >> 16) & 0xFF, (rgba >> 8) & 0xFF, rgba & 0xFF, imageLocation);
    }

    public PixelData(int r, int g, int b, int a, Point imageLocation) {
        this((byte) r, (byte) g, (byte) b, (byte) a, imageLocation);
    }

    public PixelData(byte r, byte g, byte b, byte a, Point imageLocation) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.imageLocation = imageLocation;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d, %d) @ (%d, %d)", getR(), getG(), getB(), getA(), (int) imageLocation.getX(), (int) imageLocation.getY());
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

    public void stepR(int i) {
        if (getR() == 0xFF && i > 0)
            ;//pass
        else if (getR() == 0x00 && i < 0)
            ;//pass
        else r += i;
    }

    public PixelData copy() {
        return new PixelData(r, g, b, a, imageLocation);
    }

    public void stepG(int i) {
        if (getG() == 0xFF && i > 0)
            ;//pass
        else if (getG() == 0x00 && i < 0)
            ;//pass
        else g += i;
    }
    public void stepB(int i) {
        if (getB() == 0xFF && i > 0)
            ;//pass
        else if (getB() == 0x00 && i < 0)
            ;//pass
        else b += i;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PixelData) {
            PixelData other = (PixelData) obj;
            return other.r == r && other.g == g && other.b == b;
        }
        return false;
    }
}
