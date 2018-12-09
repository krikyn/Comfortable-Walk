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
 * Class of Routing Map with properties  <b>route_list</b> и <b>PARAMS</b>.
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
        ArrayList<GeoCoordinates> geoCoord = new GoogleRouteBuilder(PARAMS).buildRoute(start,end);
        ArrayList<FieldCoordinates> routeMap = convertRouteListToFieldList(geoCoord);
        makeRoutePotentialMap(routeMap, field);
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
                    if (field[i][j]==0) {
                        field[i][j] = 3400;
                        checkField(field, routeMap, value, 3400);
                    }

                }
            }

        }
        addDecreasingValuesOnMap(field, 800);

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

    /**
     * Add decreasing values around route's cells on potential route map
     *
     * @param field - array of route potential map
     * @param decreaseValue - value of decreasing
     */
    private void addDecreasingValuesOnMap(int[][] field, int decreaseValue) {
        ArrayList<FieldCoordinates> fCoord= new ArrayList<>();
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] !=0) {
                    fCoord.add(new FieldCoordinates(i,j));
                }

            }
        }
        for (FieldCoordinates coordinate:fCoord) {
            int i=coordinate.getX();
            int j=coordinate.getY();
            List<Integer> values = decreaseValue(decreaseValue);
            int maxRadius = values.get(values.size() - 1); // decreasing radius
            for (int rad = 1; rad <= maxRadius; rad++) {
                findNeighbours(i, j, field.length, field[i].length, rad, field, values); // find all neighboring cells
            }

        }
    }


    /**
     * Convert list of route's coordinates into list of route potential map's coordinate and check doubles.
     *
     * @param geoList - list of route's coordinates
     * @return list of route potential map's coordinate
     */

    private ArrayList<FieldCoordinates> convertRouteListToFieldList(ArrayList<GeoCoordinates> geoList) {
        ArrayList<FieldCoordinates> fieldCoord = new ArrayList<>();
        for (GeoCoordinates geo : geoList) {
            FieldCoordinates fieldCoordinate = convertGeoToFieldCoordinates(geo, PARAMS.getScale());
            if (!fieldCoord.isEmpty()) {
                if (fieldCoord.get(fieldCoord.size() - 1).equals(fieldCoordinate)) {
                    continue;

                }

            }
            fieldCoord.add(fieldCoordinate);
            System.out.println(fieldCoordinate.getX()+":"+fieldCoordinate.getY());

        }
        return fieldCoord;


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
     * @param isHorizontal - if true - draw horizontal line. if false -draw vertical line )
     */
    private void addStraightLine(int[][] field, int next, int prev, int constValue, int count, boolean isHorizontal) {
        int[] variable = identifyMaxAndMinCoordinate(next, prev);

        if (isHorizontal) {
            for (int i = variable[0] + 1; i < variable[1]; i++) {
                field[i][constValue] = count;
            }
        } else {
            for (int j = variable[0] + 1; j < variable[1]; j++) {
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
        int yCounter;
        if((prev.getX()==x[0])){
            if((prev.getY()==y[0])) {
                yCounter = y[0] + 1;
                for (int i = prev.getX() + 1; i < x[1]; i++) {
                    if (yCounter != y[1]) {
                        field[i][yCounter] = count;
                        yCounter++;
                    }
                }
            }
            else{
                yCounter = y[1] - 1;
                for (int i = prev.getX() + 1; i < x[1]; i++) {
                    if (yCounter != y[1]) {
                        field[i][yCounter] = count;
                        yCounter--;
                    }
                }
            }
        }
        else{
            if((prev.getY()==y[0])) {
                yCounter = y[0] + 1;
                for (int i = prev.getX() - 1; i > x[0]; i++) {
                    if (yCounter != y[1]) {
                        field[i][yCounter] = count;
                        yCounter++;
                    }
                }
            }
            else{
                yCounter = y[1] - 1;
                for (int i = prev.getX() - 1; i > x[0]; i++) {
                    if (yCounter != y[1]) {
                        field[i][yCounter] = count;
                        yCounter--;
                    }
                }
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
        int k;
        if(prev.getX()!=x[0]){
            for (int i = prev.getX() - 1; i > x[0]; i--) {
                field[i][prev.getY()] = count;
            }
            k=x[0]-1;
        }
        else{
            for (int i = prev.getX() + 1; i < x[1]; i++) {
                field[i][prev.getY()] = count;
            }
            k=x[1]-1;
        }
        if(prev.getY()!=y[0]){
            for (int j = prev.getY() - 1; j >= y[0]; j--) {
                field[k][j] = count;
            }
        }
        else{
            for (int j = prev.getY() + 1; j <= y[1]; j++) {
                field[k][j] = count;
            }
        }
    }

    /**
     * Method return sorted array of two elements(coordinates)
     *
     * @param coord1 - first x coordinate for compare
     * @param coord2 - second x coordinate for compare
     * @return - sorted array
     */
    private int[] identifyMaxAndMinCoordinate(int coord1, int coord2) {
        int[] coordinates = new int[2];
        coordinates[0] = Math.min(coord1,coord2);
        coordinates[1] = Math.max(coord1,coord2);

        return coordinates;

    }

}