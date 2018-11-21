package com.netcracker.routebuilder.util.implementation;

import com.netcracker.routebuilder.data.bean.Cell;
import com.netcracker.routebuilder.data.bean.FieldCoordinates;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;

import java.util.ArrayList;

public class Utils {

    private final static double lat1KM = 0.00898; //1 км в градусах широты
    private final static double lon1KM = 0.01440; //1 км в градусах долготы

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

    public static Integer recountWithNewScale(int original, int scale) {
        return (original - 1) * scale + 1;
    }

    public static FieldCoordinates convertGeoToFieldCoordinates(GeoCoordinates point, int scale) {
        final Double lat1 = lat1KM / (double) scale;
        final Double lon1 = lon1KM / (double) scale;

        Double fromFieldStartX = point.getX() - 30.18035d;
        Double fromFieldStartY = 60.02781d - point.getY();

        return new FieldCoordinates((int) Math.floor(fromFieldStartY / lat1), (int) Math.floor(fromFieldStartX / lon1));
    }
}
