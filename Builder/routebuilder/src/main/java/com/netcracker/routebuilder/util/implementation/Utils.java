package com.netcracker.routebuilder.util.implementation;

import com.netcracker.routebuilder.data.bean.FieldCoordinates;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;

import java.util.Arrays;

public class Utils {

    private final static double lat1KM = 0.00898; //1 км в градусах широты
    private final static double lon1KM = 0.01793; //1 км в градусах долготы

    public static boolean checkBorders(int x, int y, int scale) {

        assert scale >= 1;
        assert scale % 2 == 0;

        //default field with cells 1x1 km
        int numX = 21;
        int numY = 20;

        for (int i = 0; i < scale / 2; i++) {
            numX = numX * 2 - 1;
            numY = numY * 2 - 1;
        }


        return x >= 0 && y >= 0 && x < numX && y < numY;
    }

    private static int normalize100(int value, int maxValue) {
        return (int) (((double) value / maxValue) * 100);
    }

    public static void fieldNormalization100(int[][] a) {
        int maxValue = getMaxValue(a);

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                a[i][j] = normalize100(a[i][j], maxValue);
            }
        }
    }

    public static void combineFields(int[][] a, int[][] b, double factor) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                a[i][j] += factor * (double) b[i][j];
            }
        }
    }

    private static int getMaxValue(int[][] b) {
        int maxValue = 0;

        for (int[] aB : b) {
            for (int anAB : aB) {
                maxValue = Math.max(maxValue, anAB);
            }
        }

        return maxValue;
    }

    public static int[][] initField(int scale) {
        final int defaultNumPointsX = 21;
        final int defaultNumPointsY = 20;

        final int numPointsX = recountWithNewScale(defaultNumPointsX, scale);
        final int numPointsY = recountWithNewScale(defaultNumPointsY, scale);

        return new int[numPointsX][numPointsY];
    }

    public static Integer recountWithNewScale(int originalSize, int newScale) {
        return (originalSize - 1) * newScale + 1;
    }

    public static FieldCoordinates convertGeoToFieldCoordinates(GeoCoordinates point, int scale) {
        final Double lat1 = lat1KM / (double) scale;
        final Double lon1 = lon1KM / (double) scale;

        Double fromFieldStartX = point.getX() - 30.18035d;
        Double fromFieldStartY = 60.02781d - point.getY();

        return new FieldCoordinates((int) Math.round(fromFieldStartY / lat1), (int) Math.round(fromFieldStartX / lon1));
    }
}
