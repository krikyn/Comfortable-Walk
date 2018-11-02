package com.netcracker.datacollector.util.implementation;

import com.google.maps.model.LatLng;
import com.netcracker.datacollector.data.model.Place;
import com.netcracker.datacollector.util.MapBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MapBuilderImpl implements MapBuilder {
    private LatLng startCoord1km = new LatLng(60.02781, 30.18035);
    private LatLng startCoord50m = new LatLng(60.0318555, 30.1722815); //lat-широта-y; lng-долгота-x 60.02781, 30.18035
    private double latKm = 0.00899;
    private double lngKm = 0.01793;
    private double lat50m = 0.0004495;
    private double lng50m = 0.0008965;

    @Override
    public LatLng[][] buildBaseMap() {
        double lat = startCoord1km.lat;
        double lng = startCoord1km.lng;
        LatLng[][] map = new LatLng[21][20];

        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 20; j++) {
                if (i == 0 && j == 0) {
                    map[i][j] = startCoord1km;
                } else {
                    map[i][j] = new LatLng(lat, lng);
                }
                lng += lngKm;
            }
            lat -= latKm;
            lng = startCoord1km.lng;
        }
        return map;
    }

    /**
     *  Строит потенциальную карту мест на основе базовой карты
     *  @param baseMap - базовая карта
     *  @param places - список мест
     *  @param scale - масштаб (при масштабе 1 - строится карта мест с ячейками 1 на 1 километр,
     *               при 2 - карта с ячейками 500 на 500 метров и т.д.)
     **/
    @Override
    public int[][] buildPlaceMap(final LatLng[][] baseMap, final List<Place> places, final int scale) {
        double halfLatKm = (latKm / 2) / scale;
        double halfLngKm = (lngKm / 2) / scale;
        int row = 21 * scale;
        int col = 20 * scale;
        int[][] placeMap = new int[row][col];

        for (Place place : places) {
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    if ((baseMap[i][j].lat - halfLatKm <= place.getLatitude() && place.getLatitude() < baseMap[i][j].lat + halfLatKm)   //Проверка попадания места в клетку карты
                            && (baseMap[i][j].lng - halfLngKm <= place.getLongitude() && place.getLongitude() < baseMap[i][j].lng + halfLngKm)) {
                        placeMap[i][j] += 100;
                    }
                }
            }
        }
        return placeMap;
    }

    private void potentialMapBuilder(final int[][] placeMap, final int scale) {
        int row = 21 * scale;
        int col = 20 * scale;
        int[][] result = new int[row][col];

        for(int i = 0; i < row; i++) {
            for(int j = 0; j < col; j++) {
                if(placeMap[i][j] != 0) {
                    result[i][j] = placeMap[i][j];
                    for(int x = Math.max(0, i-1); x <= Math.min(i+1, row); x++) {
                        for(int y = Math.max(0, j-1); j <= Math.min(j+1, col); y++) {
                            if(x != i || y != j) {
                                System.out.print("res[" + x + "]" + "[" + y + "]");
                            }
                        }
                    }
                }
            }
        }
    }
}