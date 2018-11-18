package com.netcracker.datacollector.util;

import com.google.maps.model.LatLng;
import com.netcracker.datacollector.data.model.Place;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Grout.
 *
 * Строит базовую карту города, а так же базовую и потенциальную карты мест.
 */

@Component
public class MapBuilder {
    private LatLng startCoord1km = new LatLng(60.02781, 30.18035); //lat-широта-y; lng-долгота-x 60.02781, 30.18035
    private LatLng startCoord50m = new LatLng(60.03208025, 30.17183325);
    private double latitudeInKm = 0.00899;
    private double longitudeInKm = 0.01793;

    /**
     * Строит базовую карту города.
     * @param scale - масштаб карты.
     * ВАЖНО! Метод корректно строит только карты с масштабом 1 и 20, так как только для этих масштабов известны
     *              начальные координаты.
     */

    public LatLng[][] buildBaseMap(int scale) {
        double lat;
        double lng;
        int maxRow = 21 * scale;
        int maxCol = 20 * scale;
        LatLng[][] map = new LatLng[maxRow][maxCol];
        switch (scale) {
            case 1:
                lat = startCoord1km.lat;
                lng = startCoord1km.lng;
                break;
            case 20:
                lat = startCoord50m.lat;
                lng = startCoord50m.lng;
                break;
            default:
                lat = startCoord1km.lat;
                lng = startCoord1km.lng;
                break;
        }

        //Установка координат в базовой карте
        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                setCoordinate(row, col, map, scale, lat, lng);
                lng += longitudeInKm / scale;
            }
            lat -= latitudeInKm / scale;
            if(scale == 20) {
                lng = startCoord50m.lng;
            } else {
                lng = startCoord1km.lng;
            }
        }
        return map;
    }

    private void setCoordinate(int row, int col, LatLng[][] map, int scale, double lat, double lng) {
        if (row == 0 && col == 0) {
            if(scale == 20){
                map[row][col] = startCoord50m;
            } else {
                map[row][col] = startCoord1km;
            }
        } else {
            map[row][col] = new LatLng(lat, lng);
        }
    }

    /**
     *  Строит карту мест на основе базовой карты
     *  @param baseMap - базовая карта
     *  @param places - список мест
     *  @param scale - масштаб (при масштабе 1 - строится карта мест с ячейками 1 на 1 километр,
     *               при 2 - карта с ячейками 500 на 500 метров и т.д.)
     **/
    public int[][] buildPlaceMap(final LatLng[][] baseMap, final List<Place> places, final int scale) {
        double halfLat = (latitudeInKm / 2) / scale;
        double halfLng = (longitudeInKm / 2) / scale;
        int defaultRowsInMap = 21;
        int defaultColsInMap = 20;

        int row = defaultRowsInMap * scale;
        int col = defaultColsInMap * scale;
        int[][] placeMap = new int[row][col];

        for (Place place : places) {
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    if(checkCell(baseMap[i][j], place, halfLat, halfLng)){
                        placeMap[i][j] += 100;
                    }
                }
            }
        }
        return placeMap;
    }

    /**
     * Метод для построения потенциальной карты.
     * @param placeMap - карта мест, на основе которой будет построен потенциальная карта.
     * @param scale - масштаб карты (масштаб 1 - карта размером 21 на 20 ячеек, масштаб 2 - карта размером 42 на 40 ячеек и т.д.)
     *
     * */
    public int[][] buildPotentialMap(final int[][] placeMap, final int scale) {
        int minRowInMap = 21;
        int minColInMap = 20;

        // Границы карты
        int maxRow = minRowInMap * scale;
        int maxCol = minColInMap * scale;

        int[][] result = new int[maxRow][maxCol];

        // Проход по массиву (карте), если находится ячейка с местом, то относительно этой ячейки строится карта потенциалов
        for(int row = 0; row < maxRow; row++) {
            for(int col = 0; col < maxCol; col++) {
                if(placeMap[row][col] != 0) {
                    result[row][col] += placeMap[row][col]; // Запись ячейки с местом в результат
                    List<Integer> values = decreaseValue(placeMap[row][col]); // На основе значения ячейки, вычисляется диапазон убывания
                    int maxRadius = values.get(values.size()-1); // Устанавливается радиус убывания
                    for(int rad = 1; rad <= maxRadius; rad++) {
                        findNeighbours(row, col, maxRow, maxCol, rad, result, values); // Поиск всех соседних ячеек в указанном радиусе
                    }
                }
            }
        }
        return result;
    }

    /**
     * Функция для вычисления диапазона убывания.
     * Возвращает список с диапазоном убывания и радиусом.
     * @param value - Число, на основе которого вычисляется диапазон убывания.
     *
     * */
    private static List<Integer> decreaseValue(int value) {
        int numberOfDecreasing = 0;
        int resultValue = value;
        int decreaseCoefficient = 3;
        List<Integer> result = new ArrayList<>();
        while(resultValue > 0) {
            resultValue -= Math.pow(decreaseCoefficient, 2);
            if(resultValue > 0) {
                result.add(resultValue);
                decreaseCoefficient++;
                numberOfDecreasing++;
            }
        }
        result.add(numberOfDecreasing);
        return result;
    }

    /**
     * Функция поиска соседних ячеек в указанном радиусе.
     * @param cellRow - строка, на которой находится основная ячейка.
     * @param cellCol - столбец, в которой находится основная ячейка.
     * @param maxRow - максимум карты по вертикали.
     * @param maxCol - максимум карты по горизонтали.
     * @param radius - радиус поиска соседних ячеек.
     * @param result - карта в которую записывается результат построения.
     * @param values - список с дипазоном убывания.
     *
     * */
    private void findNeighbours(final int cellRow, final int cellCol, final int maxRow, final int maxCol, int radius, int[][] result, List<Integer> values) {
        //Проход по заданному радиусу вокруг основной ячейки
        for(int rowNum = cellRow-radius; rowNum <= (cellRow+radius); rowNum++) {
            for(int colNum = cellCol-radius; colNum <= (cellCol+radius); colNum++) {
                if(!((rowNum == cellRow) &&(colNum == cellCol))) {
                    if(checkBounds(rowNum, colNum, maxRow, maxCol)){ //Проверка границ карты
                        //Запись значений из диапазона убывания во внешний радиус
                        if((rowNum == cellRow-radius) || (rowNum == cellRow+radius)) {
                            result[rowNum][colNum] += values.get(radius-1);
                        } else if(colNum == cellCol-radius || colNum == cellCol+radius) {
                            result[rowNum][colNum] += values.get(radius-1);
                        }
                    }
                }
            }
        }
    }

    /**
     * Метод для проверки границ карты
     * @param rowNum - проверяемая строка.
     * @param colNum - проверямый столбец.
     * @param maxRow - максимальное значение карты по вертикали.
     * @param maxCol - максимальное значение карты по горизонтали.
     *
     * */
    private boolean checkBounds(int rowNum, int colNum, int maxRow, int maxCol) {
        if(rowNum < 0 || colNum < 0) {
            return false;
        }
        return rowNum < maxRow && colNum < maxCol;
    }

    /**
     * Метод для проверки попадания места в ячейку базовой карты
     * @param baseCell - ячейка базовой карты.
     * @param place - место которое надо проверить
     * @param halfLat - расстояние по широте для проверки поподания в ячейку карты.
     * @param halfLng - расстояние по долготе для проверки попадания в ячейку карты.
     *
     * */
    private boolean checkCell(LatLng baseCell, Place place, double halfLat, double halfLng) {
        return (baseCell.lat - halfLat <= place.getLatitude() && place.getLatitude() < baseCell.lat + halfLat)
                && (baseCell.lng - halfLng <= place.getLongitude() && place.getLongitude() < baseCell.lng + halfLng);
    }

}