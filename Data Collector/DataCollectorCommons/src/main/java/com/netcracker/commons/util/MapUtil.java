package com.netcracker.commons.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that presents utility methods for map building.
 *
 * @author Али
 */
public class MapUtil {

    /**
     * Find neighbours cells in specified radius.
     *
     * @param cellRow - base cell row.
     * @param cellCol - base cell column.
     * @param maxRow - vertical maximum of the map.
     * @param maxCol - horizontal maximum of the map.
     * @param radius - radius of searching neighbour cells.
     * @param result - map where put result of search.
     * @param values - value list of decreasing range.
     *
     * */
    public static void findNeighbours(final int cellRow, final int cellCol, final int maxRow, final int maxCol, int radius, int[][] result, List<Integer> values) {
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
     * Calculating decreasing range.
     *
     * @param value - Base value from which decreasing occurs.
     *
     * @return List of decrease range and the radius.
     * */
    public static List<Integer> decreaseValue(int value) {
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

    private static boolean checkBounds(int rowNum, int colNum, int maxRow, int maxCol) {
        if(rowNum < 0 || colNum < 0) {
            return false;
        }
        return rowNum < maxRow && colNum < maxCol;
    }
}
