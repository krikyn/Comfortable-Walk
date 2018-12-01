package com.netcracker.routebuilder.util.implementation;


import com.netcracker.routebuilder.algorithm.implementation.GoogleRouteBuilder;
import com.netcracker.routebuilder.data.bean.FieldCoordinates;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
     * list of route's positions on potential map
     */
    private ArrayList<FieldCoordinates> route_list;
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

    public ArrayList<FieldCoordinates> buildMap(GeoCoordinates start, GeoCoordinates end) {
        field = initField(PARAMS.getScale());
        this.route_list = ConvertRouteListToFieldList(new GoogleRouteBuilder(PARAMS).buildRoute(start, end));
        MakeRoutePotentialMap();
        return route_list;

    }

    /**
     * Build potential map,put it in list of  route_list
     */

    private void MakeRoutePotentialMap() {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                int value = AddvalueOfCell(i, j);
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
     * @return value of point on potential route map
     */
    private int AddvalueOfCell(int x, int y) {
        for (FieldCoordinates fieldR : route_list) {
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