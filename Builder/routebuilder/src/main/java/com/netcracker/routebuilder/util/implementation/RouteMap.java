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

import com.netcracker.routebuilder.util.interfaces.AbstractPotentialMap;

import static com.netcracker.datacollector.util.MapUtil.decreaseValue;
import static com.netcracker.datacollector.util.MapUtil.findNeighbours;
import static com.netcracker.routebuilder.util.implementation.Utils.convertGeoToFieldCoordinates;
import static com.netcracker.routebuilder.util.implementation.Utils.initField;

/**
 * Class of Routing Map with properties  <b>route_list</b> и <b>PARAMS</b>.
 *
 * @author Kisliakov Grigori
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RouteMap extends AbstractPotentialMap {
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

    public int [] [] buildMap(GeoCoordinates start, GeoCoordinates end) {
        field = initField(PARAMS.getScale());
        ArrayList<FieldCoordinates> routeMap = ConvertRouteListToFieldList(new GoogleRouteBuilder(PARAMS).buildRoute(start, end));
        MakeRoutePotentialMap(routeMap);
        return field;

    }

    /**
     * Build potential map,put it in list of  route_list
     * @param routeMap - list of route points with type of potential cells coordinates
     */

    private void MakeRoutePotentialMap(ArrayList<FieldCoordinates> routeMap ) {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                int value = AddvalueOfCell(i, j,routeMap);
                field[i][j] = value;
                if (value != 0) {
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
     * @param x - Horizontal coordinate of route point on potential map
     * @param y -  Vertical coordinate of route point on potential map
     * @param routeMap - list of route points with type of potential cells coordinates
     * @return value of point on potential route map
     */
    private int AddvalueOfCell(int x, int y,ArrayList<FieldCoordinates> routeMap) {
        for (FieldCoordinates fieldR : routeMap) {
            if ((fieldR.getX() == x) && (fieldR.getY() == y)) {
                return 100;
            }

        }
        return 0;
    }

    /**
     * Convert list of route's coordinates into list of route potential map's coordinate
     *
     * @param GeoList - list of route's coordinates
     * @return list of route potential map's coordinate
     */

    private ArrayList<FieldCoordinates> ConvertRouteListToFieldList(ArrayList<GeoCoordinates> GeoList) {
        ArrayList<FieldCoordinates> field = new ArrayList<>();
        for (GeoCoordinates geo : GeoList) {
            field.add(convertGeoToFieldCoordinates(geo, PARAMS.getScale()));

        }
        return field;


    }


}