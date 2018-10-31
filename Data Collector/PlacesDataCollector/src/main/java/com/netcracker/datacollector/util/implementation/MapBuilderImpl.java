package com.netcracker.datacollector.util.implementation;

import com.google.maps.model.LatLng;
import com.netcracker.datacollector.data.model.Place;
import com.netcracker.datacollector.util.MapBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MapBuilderImpl implements MapBuilder {
    private LatLng startCoord = new LatLng(60.02781, 30.18035); //lat-широта-y; lng-долгота-x
    private double latKm = 0.00899;
    private double lngKm = 0.01793;

    @Override
    public LatLng[][] buildBaseMap() {
        double lat = startCoord.lat;
        double lng = startCoord.lng;
        LatLng[][] map = new LatLng[21][20];

        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 20; j++) {
                if (i == 0 && j == 0) {
                    map[i][j] = startCoord;
                } else {
                    map[i][j] = new LatLng(lat, lng);
                }
                lng += lngKm;
            }
            lat -= latKm;
            lng = 30.18035;
        }
        return map;
    }

    @Override
    public int[][] buildPlaceMap(final LatLng[][] baseMap, final List<Place> places, final int scale) {

        double halfLatKm = (latKm / 2) / scale;
        double halfLngKm = (lngKm / 2) / scale;
        int[][] coord = new int[21 * scale][21 * scale];

        for (Place place : places) {
            for (int i = 0; i < 21; i++) {
                for (int j = 0; j < 20; j++) {
                    if ((baseMap[i][j].lat - halfLatKm <= place.getLatitude() && place.getLatitude() < baseMap[i][j].lat + halfLatKm)   //Проверка попадания места в клетку карты
                            && (baseMap[i][j].lng - halfLngKm <= place.getLongitude() && place.getLongitude() < baseMap[i][j].lng + halfLngKm)) {
                        coord[i][j] += 100;
                    }
                }
            }
        }
        return coord;
    }

}