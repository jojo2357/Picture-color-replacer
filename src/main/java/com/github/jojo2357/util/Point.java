package com.github.jojo2357.util;

import com.github.jojo2357.Main;

import java.io.Serializable;

public class Point implements Serializable {
    public static final long serialVersionUID = Main.serialVersionUID;

    private float x;
    private float y;

    public Point() {
        this(0, 0);
    }

    public Point(int x, int y) {
        this((float) x, (float) y);
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point(Dimensions dims) {
        this(dims.getWidth(), dims.getHeight());
    }

    public Point(double x, double y) {
        this((float) x, (float) y);
    }

    public Point subtract(Point other) {
        return new Point(this.x - other.getX(), this.y - other.getY());
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public Point subtract(Dimensions amounts) {
        return new Point(this.x - amounts.getWidth(), this.y - amounts.getHeight());
    }

    public Point add(Point other) {
        return new Point(this.x + other.getX(), this.y + other.getY());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Point)) return false;
        return ((Point) other).x == this.x && ((Point) other).y == this.y;
    }

    @Override
    public String toString() {
        return "(" + this.x + " ," + this.y + ")";
    }

    public Point step(int stepAmt) {
        this.stepX(stepAmt);
        this.stepY(stepAmt);
        return this;
    }

    public Point stepX(int stepAmt) {
        this.x += stepAmt;
        return this;
    }

    public Point stepY(int stepAmt) {
        this.y += stepAmt;
        return this;
    }

    public Point stepX(double stepAmt) {
        this.x += stepAmt;
        return this;
    }

    public Point step(int xAmt, int yAmt) {
        this.stepX(xAmt);
        this.stepY(yAmt);
        return this;
    }

    public boolean isInBoundingBox(Point coordinates, Dimensions buttonDimensions, float correctionFactor) {
        return this.x > correctionFactor * (coordinates.x - buttonDimensions.getWidth() / 2.0) && this.x < correctionFactor * (coordinates.x + buttonDimensions.getWidth() / 2.0) && this.y > correctionFactor * (coordinates.y - buttonDimensions.getHeight() / 2.0) && this.y < correctionFactor * (coordinates.y + buttonDimensions.getHeight() / 2.0);
    }

    public Point multiply(float factor) {
        this.x *= factor;
        this.y *= factor;
        return this;
    }

    public Point getUpperLeft(Dimensions dims) {
        return new Point(this.x - dims.getWidth() / 2.0, this.y - dims.getHeight() / 2.0);
    }

    public Point getClosest(Point... points) {
        double temp;
        double closest = this.distanceFrom(points[0]);
        Point closestPoint = points[0];
        for (Point point : points)
            if ((temp = distanceFrom(point)) < closest) {
                closest = temp;
                closestPoint = point;
            }
        return closestPoint.copy();
    }

    public double distanceFrom(Point otherPosition) {
        return Math.sqrt(Math.pow(this.x - otherPosition.x, 2) + Math.pow(this.y - otherPosition.y, 2));
    }

    public Point copy() {
        return new Point(this.x, this.y);
    }
}
