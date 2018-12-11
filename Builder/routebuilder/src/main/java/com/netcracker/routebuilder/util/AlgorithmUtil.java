package com.netcracker.routebuilder.util;

import com.netcracker.routebuilder.data.bean.FieldCoordinates;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;

/**
 * bean tools for algorithm
 *
 * @author Kirill.Vakhrushev
 */
public class AlgorithmUtil {

    /**
     * 1 km in degrees of latitude
     */
    private final static double lat1KM = 0.00898;
    /**
     * 1 km in degrees of longitude
     */
    private final static double lon1KM = 0.01793;

    private static int normalize100(int value, int maxValue) {
        return (int) (((double) value / maxValue) * 100);
    }

    /**
     * Method for normalization of values in all cells of the field
     *
     * @param a potential field
     */
    public static void fieldNormalization100(int[][] a) {
        int maxValue = getMaxValue(a);

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                a[i][j] = normalize100(a[i][j], maxValue);
            }
        }
    }

    /**
     * Method for combine fields
     *
     * @param a      base field
     * @param b      field is added
     * @param factor add factor
     */
    public static void combineFields(int[][] a, int[][] b, double factor) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                a[i][j] += factor * (double) b[i][j];
            }
        }
    }

    /**
     * Method for getting max cell value
     *
     * @param b potential field
     * @return max cell value
     */
    private static int getMaxValue(int[][] b) {
        int maxValue = 0;

        for (int[] aB : b) {
            for (int anAB : aB) {
                maxValue = Math.max(maxValue, Math.abs(anAB));
            }
        }

        return maxValue;
    }

    /**
     * Method for initialize field
     *
     * @param scale required scale
     * @return field
     */
    public static int[][] initField(int scale) {
        final int defaultNumPointsX = 21;
        final int defaultNumPointsY = 20;

        final int numPointsX = recountWithNewScale(defaultNumPointsX, scale);
        final int numPointsY = recountWithNewScale(defaultNumPointsY, scale);

        return new int[numPointsX][numPointsY];
    }

    /**
     * Method for converting size
     *
     * @param originalSize original size
     * @param newScale     new scale
     * @return new size
     */
    public static Integer recountWithNewScale(int originalSize, int newScale) {
        return (originalSize - 1) * newScale + 1;
    }

    /**
     * Method for converting GeoCoordinates to FieldCoordinates
     *
     * @param point geo coordinates
     * @param scale scale
     * @return field coordinates
     */
    public static FieldCoordinates convertGeoToFieldCoordinates(GeoCoordinates point, int scale) {
        final Double lat1 = lat1KM / (double) scale;
        final Double lon1 = lon1KM / (double) scale;

        Double fromFieldStartX = point.getX() - 30.18035d;
        Double fromFieldStartY = 60.02781d - point.getY();

        return new FieldCoordinates((int) Math.round(fromFieldStartY / lat1), (int) Math.round(fromFieldStartX / lon1));
    }
}
