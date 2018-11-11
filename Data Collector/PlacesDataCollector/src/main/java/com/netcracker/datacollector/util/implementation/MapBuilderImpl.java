package com.netcracker.datacollector.util.implementation;

import com.google.maps.model.LatLng;
import com.netcracker.datacollector.data.model.Place;
import com.netcracker.datacollector.util.MapBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MapBuilderImpl implements MapBuilder {
    private LatLng startCoord1km = new LatLng(60.02781, 30.18035); //lat-широта-y; lng-долгота-x 60.02781, 30.18035
    private LatLng startCoord50m = new LatLng(60.03208025, 30.17183325);
    private double latKm = 0.00899;
    private double lngKm = 0.01793;
    //private double lat50m = 0.0004495; 25m = 0.00022475
    //private double lng50m = 0.0008965; 25m = 0.00044825

    @Override
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

        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < maxCol; j++) {
                if (i == 0 && j == 0) {
                    if(scale == 20){
                        map[i][j] = startCoord50m;
                    } else {
                        map[i][j] = startCoord1km;
                    }
                } else {
                    map[i][j] = new LatLng(lat, lng);
                }
                lng += lngKm / scale;
            }
            lat -= latKm / scale;
            if(scale == 20) {
                lng = startCoord50m.lng;
            } else {
                lng = startCoord1km.lng;
            }
        }
        return map;
    }

    /**
     *  Строит карту мест на основе базовой карты
     *  @param baseMap - базовая карта
     *  @param places - список мест
     *  @param scale - масштаб (при масштабе 1 - строится карта мест с ячейками 1 на 1 километр,
     *               при 2 - карта с ячейками 500 на 500 метров и т.д.)
     **/
    @Override
    public int[][] buildPlaceMap(final LatLng[][] baseMap, final List<Place> places, final int scale) {
        double halfLat = (latKm / 2) / scale;
        double halfLng = (lngKm / 2) / scale;
        int row = 21 * scale;
        int col = 20 * scale;
        int[][] placeMap = new int[row][col];

        for (Place place : places) {
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    if(cellCheck(baseMap[i][j], place, halfLat, halfLng)){
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
     * @param scale - масштаб карты (1 - 21 на 20, 2 - 42 на 40 и т.д.)
     *
     * */
    public int[][] buildPotentialMap(final int[][] placeMap, final int scale) {
        // Границы карты
        int maxRow = 21 * scale;
        int maxCol = 20 * scale;
        // Результат построения
        int[][] result = new int[maxRow][maxCol];

        // Проход по массиву (карте), если находится ячейка с местом, то относительно этой ячейки строится карта потенциалов
        for(int i = 0; i < maxRow; i++) {
            for(int j = 0; j < maxCol; j++) {
                if(placeMap[i][j] != 0) {
                    result[i][j] += placeMap[i][j]; // Запись ячейки с местом в результат
                    List<Integer> values = decreaseFunction(placeMap[i][j]); // На основе значения ячейки, вычисляется диапазон убывания
                    int maxRadius = values.get(values.size()-1); // Устанавливается радиус убывания
                    for(int rad = 1; rad <= maxRadius; rad++) {
                        findNeighbours(i, j, maxRow, maxCol, rad, result, values); // Поиск всех соседних ячеек в указанном радиусе
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
    private static List<Integer> decreaseFunction(int value) {
        int iter = 0;
        int resultValue = value;
        int x = 3;
        List<Integer> result = new ArrayList<>();
        while(resultValue > 0) {
            resultValue -= Math.pow(x, 2);
            if(resultValue > 0) {
                result.add(resultValue);
                x++;
                iter++;
            }
        }
        result.add(iter);
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
                    if(boundsCheck(rowNum, colNum, maxRow, maxCol)){ //Проверка границ карты
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
    private boolean boundsCheck(int rowNum, int colNum, int maxRow, int maxCol) {
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
    private boolean cellCheck(LatLng baseCell, Place place, double halfLat, double halfLng) {
        return (baseCell.lat - halfLat <= place.getLatitude() && place.getLatitude() < baseCell.lat + halfLat)
                && (baseCell.lng - halfLng <= place.getLongitude() && place.getLongitude() < baseCell.lng + halfLng);
    }

}