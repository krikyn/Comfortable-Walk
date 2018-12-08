package com.netcracker.routebuilder.service;


import com.netcracker.routebuilder.algorithm.GoogleRouteBuilder;
import com.netcracker.routebuilder.data.bean.FieldCoordinates;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


import static com.netcracker.commons.util.MapUtil.decreaseValue;
import static com.netcracker.commons.util.MapUtil.findNeighbours;
import static com.netcracker.routebuilder.util.AlgorithmUtil.convertGeoToFieldCoordinates;
import static com.netcracker.routebuilder.util.AlgorithmUtil.initField;

/**
 * Class of Routing Map with properties  <b>PARAMS</b>.
 *
 * @author Kisliakov Grigori
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RouteMapService {
    /**
     * final field of algorithm's parameters
     */
    private final AlgorithmParameters PARAMS;

    /**
     * Convert start,end position in route and build  potential map.
     *
     * @param start - start position of route
     * @param end   - end position of route
     * @return return building potential map
     */

    public int[][] buildMap(GeoCoordinates start, GeoCoordinates end) {
        int[][] field = initField(PARAMS.getScale());
        ArrayList<GeoCoordinates> geoCord = new GoogleRouteBuilder(PARAMS).buildRoute(start, end);
        ArrayList<FieldCoordinates> routeMap = convertRouteListToFieldList(geoCord);
        makeRoutePotentialMap(routeMap, field);
        return field;

    }

    public int[][] buildMap2(GeoCoordinates start, GeoCoordinates end) {
        int[][] field = initField(PARAMS.getScale());
        ArrayList<GeoCoordinates> geoCord = new GoogleRouteBuilder(PARAMS).buildRoute(start, end);
        ArrayList<FieldCoordinates> routeMap = convertRouteListToFieldList(geoCord);
        makeRoutePotentialMap2(routeMap, field);
        return field;

    }

    /**
     * Build potential map,put it in list of  route_list
     *
     * @param routeMap - list of route points with type of potential cells coordinates
     * @param field    - rout potential map
     */

    private void makeRoutePotentialMap(ArrayList<FieldCoordinates> routeMap, int[][] field) {
        int value;
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                value = addValueOfCell(i, j, routeMap);
                if (value != 0) {
                    if (field[i][j] != 100) {
                        field[i][j] = 100;
                        checkField(field, routeMap, value, 100);
                    }

                }
            }

        }
        addDecreasingValuesOnMap(field, 100);

    }

    private void makeRoutePotentialMap2(ArrayList<FieldCoordinates> routeMap, int[][] field) {
        int value;
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                value = addValueOfCell(i, j, routeMap);
                if (value != 0) {
                    if (field[i][j] != 100) {
                        field[i][j] = 100;
                        checkField(field, routeMap, value, 100);
                    }

                }
            }

        }
        addDecreasingValuesOnMap2(field, 100);

    }

    /**
     * Build potential map,put it in list of route_list
     *
     * @param x        - Horizontal coordinate of route point on potential map
     * @param y        -  Vertical coordinate of route point on potential map
     * @param routeMap - list of route points with type of potential cells coordinates
     * @return value of point on potential route map
     */
    private int addValueOfCell(int x, int y, ArrayList<FieldCoordinates> routeMap) {
        for (int i = 0; i < routeMap.size(); i++) {
            if ((routeMap.get(i).getX() == x) && (routeMap.get(i).getY() == y)) {
                return i;
            }

        }
        return 0;
    }

    private void findNeighbours2(final int cellRow, final int cellCol, final int maxRow, final int maxCol, int radius, int[][] result, List<Integer> values) {
        //Проход по заданному радиусу вокруг основной ячейки
        for (int rowNum = cellRow - radius; rowNum <= (cellRow + radius); rowNum++) {
            for (int colNum = cellCol - radius; colNum <= (cellCol + radius); colNum++) {
                if (!((rowNum == cellRow) && (colNum == cellCol))) {
                    if (rowNum < maxRow && colNum < maxCol) { //Проверка границ карты
                        //Запись значений из диапазона убывания во внешний радиус
                        if ((rowNum == cellRow - radius) || (rowNum == cellRow + radius)) {
                            if ((result[rowNum][colNum] != 100) && (values.get(radius - 1)) > result[rowNum][colNum])
                                result[rowNum][colNum] = values.get(radius - 1);
                        } else if (colNum == cellCol - radius || colNum == cellCol + radius) {
                            if ((result[rowNum][colNum] != 100) && (values.get(radius - 1)) > result[rowNum][colNum])
                                result[rowNum][colNum] = values.get(radius - 1);
                        }
                    }
                }
            }
        }
    }

    /**
     * Add decreasing values around route's cells on potential route map
     *
     * @param field - array of route potential map
     * @param value - value of route point's potential
     */
    private void addDecreasingValuesOnMap(int[][] field, int value) {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] == value) {
                    List<Integer> values = decreaseValue(value);
                    int maxRadius = values.get(values.size() - 1); // decreasing radius
                    for (int rad = 1; rad <= maxRadius; rad++) {
                        findNeighbours(i, j, field.length, field[i].length, rad, field, values); // find all neighboring cells
                    }
                }
            }
        }
    }

    /**
     * Add decreasing values around route's cells on potential route map
     *
     * @param field - array of route potential map
     * @param value - value of route point's potential
     */
    private void addDecreasingValuesOnMap2(int[][] field, int value) {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] == value) {
                    List<Integer> values = decreaseValue(value);
                    int maxRadius = values.get(values.size() - 1); // decreasing radius
                    for (int rad = 1; rad <= maxRadius; rad++) {
                        findNeighbours2(i, j, field.length, field[i].length, rad, field, values); // find all neighboring cells
                    }
                }
            }
        }
    }

    /**
     * Convert list of route's coordinates into list of route potential map's coordinate
     *
     * @param geoList - list of route's coordinates
     * @return list of route potential map's coordinate
     */

    private ArrayList<FieldCoordinates> convertRouteListToFieldList(ArrayList<GeoCoordinates> geoList) {
        ArrayList<FieldCoordinates> field = new ArrayList<>();
        for (GeoCoordinates geo : geoList) {
            FieldCoordinates fieldCoordinate = convertGeoToFieldCoordinates(geo, PARAMS.getScale());
            if (!field.isEmpty()) {
                if (field.get(field.size() - 1).equals(fieldCoordinate)) {
                    continue;

                }

            }
            field.add(fieldCoordinate);

        }
        return field;


    }

    /**
     * Fills empty cells of route between two neighboring cells,that Google API Derection doesn't send
     *
     * @param field             - array of potential route map
     * @param googleRoutePoints - list of route points with FieldCoordinates type
     * @param point             - list of route's coordinates
     * @param count             - value for route point on route potential map
     */
    private void checkField(int[][] field, ArrayList<FieldCoordinates> googleRoutePoints, int point, int count) {
        FieldCoordinates next;
        FieldCoordinates prev;
        boolean horizontalStraightLine;
        boolean verticalStraightLine;
        boolean diagonalLine;
        boolean curvedLine;
        if (point != 0) {
            next = googleRoutePoints.get(point);
            prev = googleRoutePoints.get(point - 1);
            horizontalStraightLine = (Math.abs(next.getX() - prev.getX()) > 1) && (next.getY() - prev.getY() == 0);
            verticalStraightLine = (Math.abs(next.getY() - prev.getY()) > 1) && (next.getX() - prev.getX() == 0);
            curvedLine = (Math.abs(next.getX() - prev.getX()) > 1) || ((Math.abs(next.getY() - prev.getY()) > 1));
            diagonalLine = curvedLine && (Math.abs(next.getY() - prev.getY()) == Math.abs(next.getX() - prev.getX()));
            if (horizontalStraightLine) {
                addStraightLine(field, next.getX(), prev.getX(), next.getY(), count, true);
            } else if (verticalStraightLine) {
                addStraightLine(field, next.getY(), prev.getY(), next.getX(), count, false);
            } else if (diagonalLine) {
                addDiagonalOnMap(field, next, prev, count);
            } else if (curvedLine) {
                addCurvedLineOnMap(field, next, prev, count);
            }
        }

    }

    /**
     * Draw the straight line of coordinates on route potencial map
     *
     * @param field        - array of potential route map
     * @param next         - next point of route
     * @param prev         - previous point of route
     * @param constValue   - value of constant X or Y (dependence of boolean value)
     * @param count        - value for route point on route potential map
     * @param isHorizontal - if true -draw horizontal line. if false -draw vertical line )
     */
    private void addStraightLine(int[][] field, int next, int prev, int constValue, int count, boolean isHorizontal) {
        int[] varible = identifyMaxAndMinCoordinate(next, prev);

        if (isHorizontal) {
            for (int i = varible[0] + 1; i < varible[1]; i++) {
                field[i][constValue] = count;
            }
        } else {
            for (int j = varible[0] + 1; j < varible[1]; j++) {
                field[constValue][j] = count;
            }
        }

    }

    /**
     * Draw the diagonal line of coordinates on route potencial map
     *
     * @param field - array of potential route map
     * @param next  - next point of route
     * @param prev  - previous point of route
     * @param count - value for route point on route potential map* @param horVertical - draw horizontal or vertical line)
     */
    private void addDiagonalOnMap(int[][] field, FieldCoordinates next, FieldCoordinates prev, int count) {
        int[] x = identifyMaxAndMinCoordinate(next.getX(), prev.getX());
        int[] y = identifyMaxAndMinCoordinate(next.getY(), prev.getY());
        int yCounter = y[0] + 1;
        for (int i = x[0] + 1; i < x[1]; i++) {
            if (yCounter != y[1]) {
                field[i][yCounter] = count;
                yCounter++;
            }
        }

    }

    /**
     * Draw the curved line of coordinates on route potencial map
     *
     * @param field - array of potential route map
     * @param next  - next point of route
     * @param prev  - previous point of route
     * @param count - value for route point on route potential map* @param horVertical - draw horizontal or vertical line)
     */
    private void addCurvedLineOnMap(int[][] field, FieldCoordinates next, FieldCoordinates prev, int count) {
        int[] x = identifyMaxAndMinCoordinate(next.getX(), prev.getX());
        int[] y = identifyMaxAndMinCoordinate(next.getY(), prev.getY());

        for (int j = y[0] + 1; j <= y[1]; j++) {
            field[x[0]][j] = count;
        }
        for (int i = x[0] + 1; i < x[1]; i++) {
            field[i][y[1]] = count;
        }

    }

    /**
     * Method return sorted array of two elements(coordinates)
     *
     * @param coord1 - first x coordinate for compare
     * @param coord2 - second x coordinate for compare
     * @return
     */
    private int[] identifyMaxAndMinCoordinate(int coord1, int coord2) {
        int[] coordinates = new int[2];
        if (coord1 > coord2) {
            coordinates[1] = coord1;
            coordinates[0] = coord2;
        } else {
            coordinates[0] = coord1;
            coordinates[1] = coord2;

        }

        return coordinates;

    }
}