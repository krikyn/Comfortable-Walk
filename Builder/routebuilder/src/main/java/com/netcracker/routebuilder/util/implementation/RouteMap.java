package com.netcracker.routebuilder.util.implementation;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import com.netcracker.routebuilder.util.interfaces.AbstractPotentialMap;
import com.netcracker.routebuilder.properties.AlgorithmParameters;

import com.netcracker.datacollector.util.implementation.MapUtil.decreaseValue
import com.netcracker.datacollector.util.implementation.MapUtil.findNeighbours

import com.netcracker.datacollector.util.implementation.MapUtil.convertGeoToFieldCoordinates

import static com.netcracker.routebuilder.util.implementation.Utils.initField;


@Slf4j
@Component
@RequiredArgsConstructor
public class RouteMap extends AbstractPotentialMap {

    private final AlgorithmParameters params;
    private ArrayList<FieldCoordinates> route_list;


    @PostConstruct
    public void init(ArrayList<GeoCoordinates> route_list) {
        field = initField(params.getScale()); //Получаем пустой массив
        this.route_list = ConvertRouteListToFieldList(route_list);
        MakeRoutePotentialMap();

    }

    private void MakeRoutePotentialMap() {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                int value = AddvalueOfCell(i, j, field);
                field[i][j] = value;
                if (value != 0) {
                    List<Integer> values = decreaseValue(value);//На основе значения ячейки, вычисляется диапазон убывания
                    int maxRadius = values.get(values.size() - 1); // Устанавливается радиус убывания
                    for (int rad = 1; rad <= maxRadius; rad++) {
                        findNeighbours(i, j, field.length, field[i].length, rad, field, values); // Поиск всех соседних ячеек в указанном радиусе
                    }
                }

            }
        }

    }

    private int AddvalueOfCell(int x, int y, int[][] field) {
        for (FieldCoordinates fieldR : route_list) {
            if ((fieldR.getX() == x) && (fieldR.getY() == y)) {
                return 100;
            }

        }
        return 0;
    }

    private ArrayList<FieldCoordinates> ConvertRouteListToFieldList(ArrayList<GeoCoordinates> GeoList) {
        ArrayList<FieldCoordinates> field = new ArrayList<>();
        for (GeoCoordinates geo : GeoList) {
            field.add(convertGeoToFieldCoordinates(geo, params.getScale()));

        }
        return field;


    }


}



