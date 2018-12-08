package com.netcracker.datacollector.mapcollector.maputil;

import com.google.maps.model.LatLng;
import com.netcracker.commons.data.model.Place;
import com.netcracker.commons.util.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class that presents methods for map building. Builds base city map, base place maps and potential place maps.
 *
 * @author Ali
 */
@Component
@Slf4j
public class MapBuilder {
    private LatLng startCoord1km = new LatLng(60.02781, 30.18035); //lat-широта-y; lng-долгота-x
    private double latitudeInKm = 0.00899;
    private double longitudeInKm = 0.01793;

    /**
     * Builds base city map based on given scale. Return {@link LatLng} two-dimensional array, where cells represents
     * sectors of the city. In each cell stored coordinate of the sector center.
     *
     * @param scale - scale of a built map.
     * @return two-dimensional array of coordinates
     */
    public LatLng[][] buildBaseMap(int scale) {
        int baseRow = 21;
        int baseCol = 20;
        double lat;
        double lng;
        int maxRow = (baseRow - 1) * scale + 1;
        int maxCol = (baseCol - 1) * scale + 1;
        LatLng[][] map = new LatLng[maxRow][maxCol];

        lat = startCoord1km.lat;
        lng = startCoord1km.lng;

        //Setting coordinates in base map
        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                setCoordinate(row, col, map, lat, lng);
                lng += longitudeInKm / scale;
            }
            lat -= latitudeInKm / scale;
            lng = startCoord1km.lng;
        }
        return map;
    }

    /**
     *  Builds base place map based on base city map. Return two-dimensional array og integers, where cells represents
     *  sectors of the city. In cells stored only place potential (default potential of one place is 100). If in one
     *  city sector are several places then their potentials are summarised.
     *
     *  @param places - list of places.
     *  @param scale - scale of built map.
     *  @return two-dimensional array of integers.
     **/
    public int[][] buildPlaceMap(final List<Place> places, final int scale) {
        int baseRow = 21;
        int baseCol = 20;

        int maxRow = (baseRow - 1) * scale + 1;
        int maxCol = (baseCol - 1) * scale + 1;
        int[][] placeMap = new int[maxRow][maxCol];

        for (Place place : places) {
            setPlaceInMap(placeMap, startCoord1km, place, scale, maxRow, maxCol);
        }
        return placeMap;
    }

    /**
     * Builds potential place maps. Return two-dimensional array og integers, where cells represents
     * sectors of the city. In cells stored place potential and range of decreasing potentials around place cell.
     *
     * @param placeMap - base place map on the basis of which will be built potential map.
     * @param scale - map scale of built map.
     * @return two-dimensional array of integers.
     * */
    public int[][] buildPotentialMap(final int[][] placeMap, final int scale) {
        int baseRow = 21;
        int baseCol = 20;

        // Границы карты
        int maxRow = (baseRow - 1) * scale + 1;
        int maxCol = (baseCol - 1) * scale + 1;

        int[][] result = new int[maxRow][maxCol];

        // Проход по массиву (карте), если находится ячейка с местом, то относительно этой ячейки строится карта потенциалов
        for(int row = 0; row < maxRow; row++) {
            for(int col = 0; col < maxCol; col++) {
                if(placeMap[row][col] != 0) {
                    result[row][col] += placeMap[row][col]; // Запись ячейки с местом в результат
                    List<Integer> values = MapUtil.decreaseValue(placeMap[row][col]); // На основе значения ячейки, вычисляется диапазон убывания
                    int maxRadius = values.get(values.size()-1); // Устанавливается радиус убывания
                    for(int rad = 1; rad <= maxRadius; rad++) {
                        MapUtil.findNeighbours(row, col, maxRow, maxCol, rad, result, values); // Поиск всех соседних ячеек в указанном радиусе
                    }
                }
            }
        }
        return result;
    }

    private void setCoordinate(int row, int col, LatLng[][] map, double lat, double lng) {
        if (row == 0 && col == 0) {
            map[row][col] = startCoord1km;
        } else {
            map[row][col] = new LatLng(lat, lng);
        }
    }

    private void setPlaceInMap(int[][] placeMap, LatLng baseMapStartCoordinates, Place place, int scale,
                               int maxRow, int maxCol) {
        final Double lat1 = latitudeInKm / (double) scale;
        final Double lon1 = longitudeInKm / (double) scale;
        double placeRelativeLat = baseMapStartCoordinates.lat - place.getLatitude();
        double placeRelativeLng = place.getLongitude() - baseMapStartCoordinates.lng;
        int row = (int) Math.round(placeRelativeLat / lat1);
        int col = (int) Math.round(placeRelativeLng / lon1);

        if(row < maxRow
                && row >= 0
                && col >= 0
                && col < maxCol) {
            placeMap[row][col] += 100;
        }
    }
}