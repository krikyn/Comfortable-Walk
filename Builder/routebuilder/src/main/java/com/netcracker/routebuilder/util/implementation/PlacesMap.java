package com.netcracker.routebuilder.util.implementation;

import com.netcracker.commons.service.CityMapService;
import com.netcracker.routebuilder.properties.AlgorithmParameters;
import com.netcracker.routebuilder.util.enums.PlacesType;
import com.netcracker.routebuilder.util.enums.RouteProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.netcracker.routebuilder.util.implementation.Utils.combineFields;
import static com.netcracker.routebuilder.util.implementation.Utils.fieldNormalization100;
import static com.netcracker.routebuilder.util.implementation.Utils.initField;

/**
 * Class describing a places potential map
 *
 * @author Kirill.Vakhrushev
 */
@Component
@RequiredArgsConstructor
public class PlacesMap extends AbstractPotentialMap {

    private final AlgorithmParameters params;
    private final CityMapService cityMapService;
    private Map<String, int[][]> fields;

    /**
     * Places potential map initialization
     */
    @PostConstruct
    public void init() {
        fields = new HashMap<>();

        for (PlacesType type : PlacesType.values()) {
            fields.put(type.toString(), initField(params.getScale()));
        }

        updateMaps();
    }

    @Scheduled(cron = "0 0 1 * * MON")
    private void update() {
        updateMaps();
    }

    private void updateMaps() {
        for (PlacesType place : PlacesType.values()) {
            fields.replace(place.toString(), cityMapService.loadCityMapByType("POTENTIAL_" + place.toString()).getGrid());
        }
    }


    /**
     * Method for collecting a potential map with the desired properties
     *
     * @param includedProperties route properties
     * @return potential map with plotted objects
     */
    public int[][] getField(ArrayList<RouteProperty> includedProperties) {
        int[][] baseMap = initField(params.getScale());

        for (RouteProperty type : includedProperties) {
            if (!type.equals(RouteProperty.GOOD_WEATHER)) {
                int[][] curField = fields.get(type.toString());
                fieldNormalization100(curField);
                combineFields(baseMap, curField, 1);
            }
        }

        return baseMap;
    }
}
