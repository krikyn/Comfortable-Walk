package com.netcracker.datacollector.data.model;

// daba хорошим тоном является разделять Entity и просто бины по пакетам внутри model
public class Point {
    private double x;
    private double y;

    // daba get/set + toString переопределить ломбоком
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return y + ", " + x;
    }
}
