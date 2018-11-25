package com.netcracker.datacollector.util;

import java.util.ArrayList;
import java.util.List;

public class MapUtil {

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
     * Метод для проверки границ карты
     * @param rowNum - проверяемая строка.
     * @param colNum - проверямый столбец.
     * @param maxRow - максимальное значение карты по вертикали.
     * @param maxCol - максимальное значение карты по горизонтали.
     *
     * */
    private static boolean checkBounds(int rowNum, int colNum, int maxRow, int maxCol) {
        if(rowNum < 0 || colNum < 0) {
            return false;
        }
        return rowNum < maxRow && colNum < maxCol;
    }

    /**
     * Функция для вычисления диапазона убывания.
     * Возвращает список с диапазоном убывания и радиусом.
     * @param value - Число, на основе которого вычисляется диапазон убывания.
     *
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
}
