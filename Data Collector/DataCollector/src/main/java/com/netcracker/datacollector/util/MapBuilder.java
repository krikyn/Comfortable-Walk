package com.netcracker.datacollector.util;

import com.google.maps.model.LatLng;
import com.netcracker.datacollector.data.model.Place;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Grout.
 *
 * Строит базовую карту города, а так же базовую и потенциальную карты мест.
 */

@Component
public class MapBuilder {
    private LatLng startCoord1km = new LatLng(60.02781, 30.18035); //lat-широта-y; lng-долгота-x 60.02781, 30.18035
    //private LatLng startCoord50m = new LatLng(60.03208025, 30.17183325);
    private double latitudeInKm = 0.00899;
    private double longitudeInKm = 0.01793;

    /**
     * Строит базовую карту города.
     * @param scale - масштаб карты.
     *
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

        //Установка координат в базовой карте
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

    private void setCoordinate(int row, int col, LatLng[][] map, double lat, double lng) {
        if (row == 0 && col == 0) {
            map[row][col] = startCoord1km;
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
        //double halfLat = (latitudeInKm / 2) / scale;
        //double halfLng = (longitudeInKm / 2) / scale;
        int baseRow = 21;
        int baseCol = 20;

        int maxRow = (baseRow - 1) * scale + 1;
        int maxCol = (baseCol - 1) * scale + 1;
        int[][] placeMap = new int[maxRow][maxCol];

        for (Place place : places) {
            setPlaceInMap(placeMap, startCoord1km, place, scale);
            /*for (int i = 0; i < maxRow; i++) {
                for (int j = 0; j < maxCol; j++) {
                    if(checkCell(baseMap[i][j], place, halfLat, halfLng)){
                        placeMap[i][j] += 100;
                    }
                }
            }*/
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

    /*
      Функция для вычисления диапазона убывания.
      Возвращает список с диапазоном убывания и радиусом.
      @param value - Число, на основе которого вычисляется диапазон убывания.
     *
     * */
    /*private static List<Integer> decreaseValue(int value) {
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
    }*/

    /*
      Функция поиска соседних ячеек в указанном радиусе.
      @param cellRow - строка, на которой находится основная ячейка.
     * @param cellCol - столбец, в которой находится основная ячейка.
     * @param maxRow - максимум карты по вертикали.
     * @param maxCol - максимум карты по горизонтали.
     * @param radius - радиус поиска соседних ячеек.
     * @param result - карта в которую записывается результат построения.
     * @param values - список с дипазоном убывания.
     *
     * */
    /*private void findNeighbours(final int cellRow, final int cellCol, final int maxRow, final int maxCol, int radius, int[][] result, List<Integer> values) {
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
    }*/

    /*
      Метод для проверки границ карты
      @param rowNum - проверяемая строка.
     * @param colNum - проверямый столбец.
     * @param maxRow - максимальное значение карты по вертикали.
     * @param maxCol - максимальное значение карты по горизонтали.
     *
     * */
    /*private boolean checkBounds(int rowNum, int colNum, int maxRow, int maxCol) {
        if(rowNum < 0 || colNum < 0) {
            return false;
        }
        return rowNum < maxRow && colNum < maxCol;
    }*/

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

    private void setPlaceInMap(int[][] placeMap, LatLng baseMapStartCoordinates, Place place, int scale) {
        final Double lat1 = latitudeInKm / (double) scale;
        final Double lon1 = longitudeInKm / (double) scale;
        double placeRelativeLat = baseMapStartCoordinates.lat - place.getLatitude();
        double placeRelativeLng = place.getLongitude() - baseMapStartCoordinates.lng;

        if(placeRelativeLat < baseMapStartCoordinates.lat && placeRelativeLat > 0 || placeRelativeLng >= 0 && placeRelativeLng < place.getLongitude()) {
            int row = (int) Math.floor(placeRelativeLat / lat1);
            int col = (int) Math.floor(placeRelativeLng / lon1);
            placeMap[row][col] += 100;
        }
    }
}