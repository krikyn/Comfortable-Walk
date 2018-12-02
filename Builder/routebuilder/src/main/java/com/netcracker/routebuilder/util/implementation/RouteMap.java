package com.netcracker.routebuilder.util.implementation;


import com.netcracker.routebuilder.algorithm.implementation.GoogleRouteBuilder;
import com.netcracker.routebuilder.data.bean.FieldCoordinates;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


import static com.netcracker.commons.util.MapUtil.decreaseValue;
import static com.netcracker.commons.util.MapUtil.findNeighbours;
import static com.netcracker.routebuilder.util.implementation.Utils.convertGeoToFieldCoordinates;
import static com.netcracker.routebuilder.util.implementation.Utils.initField;

/**
 * Class of Routing Map with properties  <b>PARAMS</b>.
 *
 * @author Kisliakov Grigori
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RouteMap {
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
        ArrayList<GeoCoordinates> googleRoutePoints = new GoogleRouteBuilder(PARAMS).buildRoute(start, end);
        ArrayList<FieldCoordinates> routePoints = ConvertRouteListToFieldList(googleRoutePoints);
        MakeRoutePotentialMap(routePoints, field);
        return field;

    }

    /**
     * Build potential map,put it in list of  route_list
     *
     * @param routePoints - list of route points with type of potential cells coordinates
     * @param field       - rout potential map
     */

    private void MakeRoutePotentialMap(ArrayList<FieldCoordinates> routePoints, int[][] field) {
        int value ;
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                value = AddValueOfCell(i, j, routePoints);

                if (value != 0) {
                    if (field[i][j] != 100) {
                        field[i][j] = 100;
                        CheckField(field, routePoints, value, 100);

                    } else {
                        field[i][j] = 100;
                        continue;
                    }
                    field[i][j] = 100;


                }
                if (value != 0) {
                    value = 100;
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
     * Build potential map,put it in list of route_list
     *
     * @param x           - Horizontal coordinate of route point on potential map
     * @param y           -  Vertical coordinate of route point on potential map
     * @param routePoints - list of route points with type of potential cells coordinates
     * @return value of point on potential route map
     */
    private int AddValueOfCell(int x, int y, ArrayList<FieldCoordinates> routePoints) {
        for (int i = 0; i < routePoints.size(); i++) {
            if ((routePoints.get(i).getX() == x) && (routePoints.get(i).getY() == y)) {
                return i;
            }

        }
        return 0;
    }

    /**
     * Convert list of route's coordinates into list of route potential map's coordinate
     *
     * @param geoList - list of route's coordinates
     * @return list of route potential map's coordinate
     */

    private ArrayList<FieldCoordinates> ConvertRouteListToFieldList(ArrayList<GeoCoordinates> geoList) {
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
    private void CheckField(int[][] field, ArrayList<FieldCoordinates> googleRoutePoints, int point, int count) {
        FieldCoordinates next;
        FieldCoordinates prev;
        boolean type1;
        boolean type2;
        boolean type3;
        boolean type4;
        if (point != 0) {
            next = googleRoutePoints.get(point);
            prev = googleRoutePoints.get(point - 1);
            type1 = (Math.abs(next.getX() - prev.getX()) > 1) && (next.getY() - prev.getY() == 0);
            type2 = (Math.abs(next.getY() - prev.getY()) > 1) && (next.getX() - prev.getX() == 0);
            type4 = (Math.abs(next.getX() - prev.getX()) > 1) || ((Math.abs(next.getY() - prev.getY()) > 1));
            type3 = type4 && (Math.abs(next.getY() - prev.getY()) == Math.abs(next.getX() - prev.getX()));
            if (type1) {
                AddStraightLine(field, next.getX(), prev.getX(), next.getY(), count, true);
            } else if (type2) {
                AddStraightLine(field, next.getY(), prev.getY(), next.getX(), count, false);
            } else if (type3) {
                AddDiagonalOnMap(field, next, prev, count);
            } else if (type4) {
                AddCurvedLineOnMap(field, next, prev, count);
            }
        }

    }

    /**
     * Draw the straight line of coordinates on route potencial map
     *
     * @param field       - array of potential route map
     * @param next        - next point of route
     * @param prev        - previous point of route
     * @param XY          - value of constant X or Y (dependence of boolean value)
     * @param count       - value for route point on route potential map
     * @param horVertical - draw horizontal or vertical line)
     */
    private void AddStraightLine(int[][] field, int next, int prev, int XY, int count, boolean horVertical) {
        int min  ;
        int max ;
        if (next > prev) {
            max = next;
            min = prev;
        } else {
            max = prev;
            min = next;
        }

        if (horVertical) {
            for (int i = min + 1; i < max; i++) {
                field[i][XY] = count;
            }
        } else {
            for (int j = min + 1; j < max; j++) {
                field[XY][j] = count;
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
    private void AddDiagonalOnMap(int[][] field, FieldCoordinates next, FieldCoordinates prev, int count) {
        int maxX ;
        int minX ;
        int maxY ;
        int minY ;
        if (next.getX() > prev.getX()) {
            maxX = next.getX();
            minX = prev.getX();
        } else {
            minX = next.getX();
            maxX = prev.getX();

        }
        if (next.getY() > prev.getY()) {
            maxY = next.getY();
            minY = prev.getY();
        } else {
            minY = next.getY();
            maxY = prev.getY();
        }
        int Y = minY + 1;
        for (int i = minX + 1; i < maxX; i++) {
            if (Y != maxY) {
                field[i][Y] = count;
                Y++;
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
    private void AddCurvedLineOnMap(int[][] field, FieldCoordinates next, FieldCoordinates prev, int count) {
        int maxX ;
        int minX ;
        int maxY ;
        int minY ;
        if (next.getX() > prev.getX()) {
            maxX = next.getX();
            minX = prev.getX();
        } else {
            minX = next.getX();
            maxX = prev.getX();

        }
        if (next.getY() > prev.getY()) {
            maxY = next.getY();
            minY = prev.getY();
        } else {
            minY = next.getY();
            maxY = prev.getY();
        }


        for (int j = minY + 1; j <= maxY; j++) {
            field[minX][j] = count;
        }
        for (int i = minX + 1; i < maxX; i++) {
            field[i][maxY] = count;
        }

    }


}